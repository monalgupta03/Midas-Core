package com.jpmc.midascore.service;

import org.springframework.stereotype.Service;
import com.jpmc.midascore.entity.TransactionRecord;
import com.jpmc.midascore.entity.UserRecord;
import com.jpmc.midascore.foundation.Transaction;
import com.jpmc.midascore.repository.TransactionRecordRepository;
import com.jpmc.midascore.repository.UserRepository;

import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class TransactionService {

    private final UserRepository userRepository;
    private final TransactionRecordRepository transactionRecordRepository;

    public TransactionService(UserRepository userRepository,
                              TransactionRecordRepository transactionRecordRepository) {
        this.userRepository = userRepository;
        this.transactionRecordRepository = transactionRecordRepository;
    }

    @Transactional
    public void process(Transaction tx) {

        UserRecord sender = userRepository.findById(tx.getSenderId());
        UserRecord recipient = userRepository.findById(tx.getRecipientId());

        if (sender == null || recipient == null) return;

        if (sender.getBalance() < (float) tx.getAmount()) return;

        // update balances
        sender.setBalance(sender.getBalance() - tx.getAmount());
        recipient.setBalance(recipient.getBalance() + tx.getAmount());

        // persist users
        userRepository.save(sender);
        userRepository.save(recipient);

        // record transaction
        TransactionRecord record = new TransactionRecord(sender, recipient, tx.getAmount());
        transactionRecordRepository.save(record);
    }
}