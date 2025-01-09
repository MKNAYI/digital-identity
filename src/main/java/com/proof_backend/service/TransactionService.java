package com.proof_backend.service;

import com.proof_backend.model.TransactionDTO;
import com.proof_backend.repository.TransactionRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;


@Service
@Slf4j
public class TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    public List<TransactionDTO> getTransactions(Long userId) {
        return null;
    }

    public TransactionDTO getTransactionById() {
        return null;
    }
}
