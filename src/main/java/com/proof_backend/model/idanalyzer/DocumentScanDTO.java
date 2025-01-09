package com.proof_backend.model.idanalyzer;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.proof_backend.enums.DocumentStatus;
import com.proof_backend.enums.DocumentType;
import com.proof_backend.enums.Gender;
import lombok.*;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentScanDTO {
    private Boolean success;
    private String decision;
    private String profileId;
    private String fullName;
    private Gender gender;
    private DocumentType documentType;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate birthDate;
    private String nationality;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate issuedDate;
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate expirationDate;
    private DocumentStatus status;
    private String state;
    private String country;
    private List<String> warningMessages;
}
