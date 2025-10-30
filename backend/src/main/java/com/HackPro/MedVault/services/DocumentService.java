package com.HackPro.MedVault.services;

import com.HackPro.MedVault.domain.dtos.DocumentResponseDto;
import com.HackPro.MedVault.domain.dtos.DocumentUploadDto;
import com.HackPro.MedVault.domain.dtos.N8nProcessedDataDto;

import java.util.List;
import java.util.UUID;

public interface DocumentService {
    DocumentResponseDto uploadDocument(DocumentUploadDto dto, UUID patientId);

    void updateProcessedData(N8nProcessedDataDto dto);

    List<DocumentResponseDto> getPatientDocuments(UUID patientId);
}

