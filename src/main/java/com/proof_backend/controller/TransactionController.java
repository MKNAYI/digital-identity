package com.proof_backend.controller;

import com.proof_backend.model.TransactionDTO;
import com.proof_backend.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/users/{user-id}/transactions")
    public ResponseEntity<List<TransactionDTO>> getTransactions(@PathVariable("user-id") Long userId) {
        return new ResponseEntity<List<TransactionDTO>>(transactionService.getTransactions(userId), HttpStatus.OK);
    }

    @GetMapping("/users/{user-id}/transactions/{transaction-id}")
    public ResponseEntity<TransactionDTO> getTransactionById(@PathVariable("user-id") Long userId) {
        return new ResponseEntity<TransactionDTO>(transactionService.getTransactionById(), HttpStatus.CREATED);
    }
}
