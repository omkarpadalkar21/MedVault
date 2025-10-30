package com.HackPro.MedVault.services.impl;

import com.HackPro.MedVault.domain.dtos.DocumentResponseDto;
import com.HackPro.MedVault.domain.dtos.DocumentUploadDto;
import com.HackPro.MedVault.domain.dtos.N8nProcessedDataDto;
import com.HackPro.MedVault.domain.entities.MedicalRecords.Document;
import com.HackPro.MedVault.domain.entities.MedicalRecords.ProcessingStatus;
import com.HackPro.MedVault.domain.entities.UserManagement.Patient;
import com.HackPro.MedVault.exceptions.ResourceNotFoundException;
import com.HackPro.MedVault.repositories.DocumentRepository;
import com.HackPro.MedVault.repositories.PatientRepository;
import com.HackPro.MedVault.services.DocumentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final PatientRepository patientRepository;
    private final RestTemplate restTemplate;

    @Value("${n8n.webhook.url}")
    private String n8nWebhookUrl;

    @Override
    @Transactional
    public DocumentResponseDto uploadDocument(DocumentUploadDto dto, UUID patientId) {
        log.info("Uploading document for patient: {}", patientId);

        // Find patient
        Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new ResourceNotFoundException("Patient not found"));

        // Create document entity
        Document document = Document.builder()
                .patient(patient)
                .title(dto.getTitle())
                .fileUrl(dto.getFileUrl())
                .fileName(dto.getFileName())
                .processingStatus(ProcessingStatus.PENDING)
                .build();

        // Save to database
        Document savedDocument = documentRepository.save(document);
        log.info("Document saved with ID: {}", savedDocument.getId());

        // Send to n8n asynchronously
        sendToN8nAsync(savedDocument);

        return mapToDto(savedDocument);
    }

    private void sendToN8nAsync(Document document) {
        new Thread(() -> {
            try {
                log.info("Sending document to n8n: {}", document.getId());

                // Update status to PROCESSING
                document.setProcessingStatus(ProcessingStatus.PROCESSING);
                documentRepository.save(document);

                // Prepare payload for n8n
                var payload = new N8nWebhookPayload(
                        document.getId(),
                        document.getTitle(),
                        document.getFileUrl(),
                        document.getFileName(),
                        document.getPatient().getId()
                );

                // Send POST request to n8n
                restTemplate.postForObject(n8nWebhookUrl, payload, String.class);

                log.info("Successfully sent document to n8n: {}", document.getId());
            } catch (Exception e) {
                log.error("Failed to send document to n8n: {}", document.getId(), e);
                document.setProcessingStatus(ProcessingStatus.FAILED);
                documentRepository.save(document);
            }
        }).start();
    }

    @Override
    @Transactional
    public void updateProcessedData(N8nProcessedDataDto dto) {
        log.info("Updating processed data for document: {}", dto.getDocumentId());

        Document document = documentRepository.findById(dto.getDocumentId())
                .orElseThrow(() -> new ResourceNotFoundException("Document not found"));

        // Update document with n8n processed data
        document.setSummary(dto.getSummary());
        document.setAnomalies(String.join(",", dto.getAnomalies())); // Store as CSV or JSON
        document.setCategory(dto.getCategory());
        document.setDocumentType(dto.getDocumentType());
        document.setProcessingStatus(ProcessingStatus.COMPLETED);

        documentRepository.save(document);
        log.info("Document processing completed: {}", dto.getDocumentId());
    }

    @Override
    @Transactional(readOnly = true)
    public List<DocumentResponseDto> getPatientDocuments(UUID patientId) {
        List<Document> documents = documentRepository.findByPatientIdOrderByCreatedAtDesc(patientId);
        return documents.stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private DocumentResponseDto mapToDto(Document document) {
        List<String> anomalies = document.getAnomalies() != null
                ? Arrays.asList(document.getAnomalies().split(","))
                : List.of();

        return DocumentResponseDto.builder()
                .id(document.getId())
                .title(document.getTitle())
                .fileUrl(document.getFileUrl())
                .category(document.getCategory())
                .documentType(document.getDocumentType())
                .summary(document.getSummary())
                .anomalies(anomalies)
                .processingStatus(document.getProcessingStatus())
                .createdAt(document.getCreatedAt())
                .build();
    }

    // Inner class for n8n payload
    private record N8nWebhookPayload(
            UUID documentId,
            String title,
            String fileUrl,
            String fileName,
            UUID patientId
    ) {}
}
