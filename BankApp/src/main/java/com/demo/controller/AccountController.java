
package com.demo.controller;

import com.demo.model.Account;
import com.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController

public class AccountController {

    @Autowired
    private AccountRepository accountRepository;

    @PostMapping("/insert")
    public Account insert(@RequestBody Account account) {
        // If account number is not provided, generate one
        if (account.getAccountNumber() == null || account.getAccountNumber().isEmpty()) {
            account.setAccountNumber("AC" + (int)(Math.random() * 1_000_000_000));
        }

        // If balance is not provided, set default to 0
        if (account.getBalance() == null) {
            account.setBalance(0.0);
        }

        return accountRepository.save(account);
    }





    @PostMapping("/deposit/{accountNumber}/{amount}")
    public String deposit(@PathVariable String accountNumber, @PathVariable double amount) {
        Optional<Account> optional = accountRepository.findByAccountNumber(accountNumber);
        if (optional.isPresent()) {
            Account acc = optional.get();
            acc.setBalance(acc.getBalance() + amount);
            accountRepository.save(acc);
            return "Deposited ₹" + amount + " to " + accountNumber + ". New balance: ₹" + acc.getBalance();
        } else {
            return "❌ Account not found: " + accountNumber;
        }
    }


    // Withdraw
    @PutMapping("/withdraw/{accNo}/{amount}")
    public ResponseEntity<String> withdraw(@PathVariable String accNo, @PathVariable Double amount) {
        Optional<Account> optionalAcc = accountRepository.findByAccountNumber(accNo);

        if (!optionalAcc.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("❌ Account not found: " + accNo);
        }

        Account acc = optionalAcc.get();

        if (acc.getBalance() < amount) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(" Insufficient balance. Available: ₹" + acc.getBalance() + ", Requested: ₹" + amount);
        }

        acc.setBalance(acc.getBalance() - amount);
        accountRepository.save(acc);

        return ResponseEntity.ok("✅ Withdrawn ₹" + amount + ". New balance: ₹" + acc.getBalance());
    }

      
    
    @DeleteMapping("/delete/{accountNumber}")
    public String deleteAccount(@PathVariable String accountNumber) {
        Optional<Account> accountOptional = accountRepository.findByAccountNumber(accountNumber);

        if (accountOptional.isPresent()) {
            accountRepository.delete(accountOptional.get());
            return "✅ Account deleted successfully: " + accountNumber;
        } else {
            return "❌ Account not found: " + accountNumber;
        }
    }

    

    // Check balance
    @GetMapping("/balance/{accNo}")
    public Double getBalance(@PathVariable String accNo) {
        Account acc = accountRepository.findByAccountNumber(accNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        return acc.getBalance();
    }
}
