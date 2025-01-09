package com.proof_backend.model.idanalyzer;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentScanRequestDTO {
    private String profile;
    private String document;
    private String documentBack;
    private String face;
    private String faceVideo;
}
