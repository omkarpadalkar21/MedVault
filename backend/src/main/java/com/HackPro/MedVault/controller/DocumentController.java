package com.HackPro.MedVault.controller;

import com.HackPro.MedVault.domain.dtos.DocumentResponseDto;
import com.HackPro.MedVault.domain.dtos.DocumentUploadDto;
import com.HackPro.MedVault.domain.dtos.N8nProcessedDataDto;
import com.HackPro.MedVault.security.MedVaultUserDetails;
import com.HackPro.MedVault.services.DocumentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
@Slf4j
public class DocumentController {

    private final DocumentService documentService;

    /**
     * Upload document (called by frontend after Supabase upload)
     */
    @PostMapping("/upload")
    public ResponseEntity<DocumentResponseDto> uploadDocument(
            @Valid @RequestBody DocumentUploadDto dto,
            @AuthenticationPrincipal MedVaultUserDetails userDetails
    ) {
        log.info("Document upload request from user: {}", userDetails.getUserId());
        DocumentResponseDto response = documentService.uploadDocument(dto, userDetails.getUserId());
        return ResponseEntity.ok(response);
    }

    /**
     * Receive processed data from n8n (webhook callback)
     */
    @PostMapping("/processed")
    public ResponseEntity<Void> receiveProcessedData(@RequestBody N8nProcessedDataDto dto) {
        log.info("Received processed data from n8n for document: {}", dto.getDocumentId());
        documentService.updateProcessedData(dto);
        return ResponseEntity.ok().build();
    }

    /**
     * Get all documents for authenticated patient
     */
    @GetMapping
    public ResponseEntity<List<DocumentResponseDto>> getMyDocuments(
            @AuthenticationPrincipal MedVaultUserDetails userDetails
    ) {
        List<DocumentResponseDto> documents = documentService.getPatientDocuments(userDetails.getUserId());
        return ResponseEntity.ok(documents);
    }
}
