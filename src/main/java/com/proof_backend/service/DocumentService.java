package com.proof_backend.service;

import com.proof_backend.config.IdAnalyzerClient;
import com.proof_backend.enums.DocumentStatus;
import com.proof_backend.enums.DocumentType;
import com.proof_backend.enums.Gender;
import com.proof_backend.enums.UserStatus;
import com.proof_backend.model.idanalyzer.DocumentScanRequestDTO;
import com.proof_backend.model.idanalyzer.DocumentScanDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.proof_backend.model.idanalyzer.DocumentScanResponseDTO;
import static com.proof_backend.utils.Constants.*;
import com.proof_backend.utils.Utils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
public class DocumentService {
    @Autowired
    private IdAnalyzerClient idAnalyzerClient;

    @Autowired
    private UserService userService;

    public DocumentScanResponseDTO verifyDocuments(Long userId, DocumentScanRequestDTO documentScanRequestDTO) {
        log.info("DocumentService:: verifyDocuments() invoked, userId: {}", userId);
        JsonNode rootNode = idAnalyzerClient.verifyDocuments(documentScanRequestDTO);
        DocumentScanDTO documentScanDTO = mapToDocumentScanResponse(rootNode, userId);
        if(documentScanDTO.getStatus().equals(DocumentStatus.VERIFIED)) {
            userService.updateUserVerificationData(userId, documentScanDTO);
        } else {
            log.info("Document does not verified with given document type: {}, user-id: {}", documentScanDTO.getDocumentType(), userId);
            userService.updateUserStatus(userId, UserStatus.VERIFICATION_FAILED);
        }
        return DocumentScanResponseDTO.builder()
                .status(documentScanDTO.getStatus())
                .warningMessages(documentScanDTO.getWarningMessages())
                .build();
    }

    private DocumentScanDTO mapToDocumentScanResponse(JsonNode rootNode, Long userId) {
        log.info("DocumentService:: verifyDocuments() :: mapToDocumentScanResponse() invoked, userId: {}", userId);
        Boolean success = rootNode.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_SUCCESS).asBoolean();
        log.info("Success: {}, user-id: {}", success, userId);
        String decision = rootNode.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION).textValue();
        log.info("Decision: {}, user-id: {}", decision, userId);
        String profileId = rootNode.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_PROFILE_ID).textValue();
        log.info("ProfileId: {}, user-id: {}", profileId, userId);
        JsonNode data = rootNode.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA);
        String documentTypeValue = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("DocumentTypeValue: {}, user-id: {}", documentTypeValue, userId);
        DocumentType documentType = mapToDocumentType(documentTypeValue);
        log.info("DocumentType: {}, user-id: {}", documentType, userId);
        String fullName = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_FULLNAME).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("FullName: {}, user-id: {}", fullName, userId);
        String genderStr = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_SEX).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("GenderStr: {}, user-id: {}", genderStr, userId);
        Gender gender = mapToGender(genderStr);
        log.info("Gender: {}, user-id: {}", gender, userId);
        String birthDate = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOB).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("BirthDate: {}, user-id: {}", birthDate, userId);
        String nationality = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_NATIONALITY_FULL).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("Nationality: {}, user-id: {}", nationality, userId);
        String issuedDate = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_ISSUED).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("IssuedDate: {}, user-id: {}", issuedDate, userId);
        String expiryDate = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_EXPIRY).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("ExpiryDate: {}, user-id: {}", expiryDate, userId);
        String state = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_STATE_FULL).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("State: {}, user-id: {}", state, userId);
        String country = data.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_COUNTRY_FULL).path(0).path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_VALUE).textValue();
        log.info("Country: {}, user-id: {}", country, userId);
        DocumentStatus status = mapScanDecisionToDocumentVerificationStatus(decision);
        log.info("DocumentStatus: {}, user-id: {}", status, userId);

        List<String> warningMessages = new ArrayList<>();
        if (StringUtils.isNotEmpty(decision) && decision.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION_REJECT)) {
            log.info("Fetching the warning, user-id: {}", userId);
            JsonNode warningNode = rootNode.path(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_WARNING);
            List<Map<String, Object>> warning = Utils.arrayNodeToList(warningNode);
            warningMessages = warning.stream()  // Stream the List<Map<String, Object>>
                    .filter(t -> ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION_REJECT.equals(t.get(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION)))  // Filter by "reject" decision
                    .map(t -> (String) t.get(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_WARNING_DESCRIPTION))  // Access the "description" field
                    .collect(Collectors.toList());

            log.info("warningDescription: {}", warningMessages);
        }

        return DocumentScanDTO
                .builder()
                .success(success)
                .decision(decision)
                .profileId(profileId)
                .fullName(fullName)
                .gender(gender)
                .documentType(documentType)
                .birthDate(LocalDate.parse(birthDate))
                .nationality(nationality)
                .issuedDate(StringUtils.isNotEmpty(issuedDate) ? LocalDate.parse(issuedDate) : null)
                .expirationDate(StringUtils.isNotEmpty(expiryDate) ? LocalDate.parse(expiryDate) : null)
                .status(status)
                .state(state)
                .country(country)
                .warningMessages(warningMessages)
                .build();
    }

    private DocumentType mapToDocumentType(String documentTypeValue) {
        log.info("DocumentService:: verifyDocuments() :: mapToDocumentScanResponse() :: mapToDocumentType(), documentType: {}", documentTypeValue);
        if(StringUtils.isNotEmpty(documentTypeValue) && documentTypeValue.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE_D)){
            return DocumentType.DRIVING_LICENSE;
        } else if(StringUtils.isNotEmpty(documentTypeValue) && documentTypeValue.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE_P)){
            return DocumentType.PASSPORT;
        } else if(StringUtils.isNotEmpty(documentTypeValue) && documentTypeValue.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_DOCUMENT_TYPE_I)){
            return DocumentType.IDENTITY_CARD;
        } else{
            return DocumentType.OTHER;
        }
    }

    private DocumentStatus mapScanDecisionToDocumentVerificationStatus(String decision) {
        log.info("DocumentService:: verifyDocuments() :: mapToDocumentScanResponse() :: mapScanDecisionToDocumentVerificationStatus(), decision: {}", decision);
        if(StringUtils.isNotEmpty(decision) && decision.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION_ACCEPT)){
            return  DocumentStatus.VERIFIED;
        } else if(StringUtils.isNotEmpty(decision) && decision.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DECISION_REJECT)){
            return  DocumentStatus.NOT_VERIFIED;
        } else{
            return  DocumentStatus.NOT_VERIFIED;
        }
    }

    private Gender mapToGender(String sex) {
        log.info("DocumentService:: verifyDocuments() :: mapToDocumentScanResponse() :: mapToGender(), sex: {}", sex);
        if(StringUtils.isNotEmpty(sex) && sex.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_SEX_M)){
            return  Gender.MALE;
        } else if(StringUtils.isNotEmpty(sex) && sex.equalsIgnoreCase(ID_ANALYZER_DOCUMENT_ATTRIBUTE_DATA_SEX_F)){
            return  Gender.FEMALE;
        } else{
            return  Gender.OTHER;
        }
    }
}
