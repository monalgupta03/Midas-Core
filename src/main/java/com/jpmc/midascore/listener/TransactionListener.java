package com.jpmc.midascore.listener;

import com.jpmc.midascore.foundation.Transaction;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import com.jpmc.midascore.service.TransactionService;
import com.jpmc.midascore.repository.UserRepository;

@Component
public class TransactionListener {

    private final TransactionService transactionService;
    private final UserRepository userRepository;

    public TransactionListener(TransactionService transactionService,
                               UserRepository userRepository) {
        this.transactionService = transactionService;
        this.userRepository = userRepository;
    }

    @KafkaListener(topics = "${general.kafka-topic}", groupId = "midas-core")
    public void listen(Transaction transaction) {

        // process transaction
        transactionService.process(transaction);

        // print waldorf balance EVERY time (we'll use the last one)
        userRepository.findAll().forEach(u -> {
            if (u.getName().equals("waldorf")) {
                System.out.println("WALDORF BALANCE: " + u.getBalance());
            }
        });
    }
}
