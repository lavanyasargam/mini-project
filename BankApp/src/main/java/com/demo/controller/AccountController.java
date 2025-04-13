package com.demo.controller;

import com.demo.model.Account;
import com.demo.repository.AccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
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
    public Account withdraw(@PathVariable String accNo, @PathVariable Double amount) {
        Account acc = accountRepository.findByAccountNumber(accNo)
                .orElseThrow(() -> new RuntimeException("Account not found"));
        
        if (acc.getBalance() < amount) {
            throw new RuntimeException("Insufficient balance");
        }

        acc.setBalance(acc.getBalance() - amount);
        return accountRepository.save(acc);
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
