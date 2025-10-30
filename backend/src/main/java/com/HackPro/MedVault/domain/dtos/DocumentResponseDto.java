package com.HackPro.MedVault.domain.dtos;

import com.HackPro.MedVault.domain.entities.MedicalRecords.ProcessingStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentResponseDto {
    private UUID id;
    private String title;
    private String fileUrl;
    private String category;
    private String documentType;
    private String summary;
    private List<String> anomalies;
    private ProcessingStatus processingStatus;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;
}
