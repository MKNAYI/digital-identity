package com.proof_backend.model.idanalyzer;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.proof_backend.enums.DocumentStatus;
import lombok.*;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentScanResponseDTO {
    private DocumentStatus status;
    private List<String> warningMessages;
}
