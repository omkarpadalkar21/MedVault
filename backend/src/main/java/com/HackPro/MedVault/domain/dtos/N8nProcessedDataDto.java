package com.HackPro.MedVault.domain.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class N8nProcessedDataDto {
    private UUID documentId;
    private String summary;
    private List<String> anomalies;
    private String category;
    private String documentType;
}
