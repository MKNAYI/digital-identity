package com.proof_backend.controller;

import com.proof_backend.model.idanalyzer.DocumentScanRequestDTO;
import com.proof_backend.model.idanalyzer.DocumentScanResponseDTO;
import com.proof_backend.service.DocumentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/v1")
public class DocumentController {

    @Autowired
    private DocumentService documentService;

    @PostMapping("/users/{user-id}/documents/verify")
    public ResponseEntity<DocumentScanResponseDTO> verifyDocuments(@PathVariable("user-id")Long userId, @RequestBody DocumentScanRequestDTO documentScanRequestDTO) {
        return new ResponseEntity<DocumentScanResponseDTO>(documentService.verifyDocuments(userId, documentScanRequestDTO),HttpStatus.OK);
    }
}
