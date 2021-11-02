package com.gmail.grechich;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Accounts")
public class Account {

    private String currency;
    @Id
    @GeneratedValue
    private Integer accountId;
    private Double balance;

    @ManyToOne
    @JoinColumn(name = "client")
    private Client client;

    @OneToMany(mappedBy = "accountFrom", cascade = CascadeType.ALL)
    List<Transaction> transactions = new ArrayList<>();

    public Account() {
    }

    public Account(String curr) {
        currencyChecker(curr);
        balance = 0.0;
    }

    private void currencyChecker(String currency) {
        switch (currency) {
            case "UAH" -> this.currency = "UAH";
            case "USD" -> this.currency = "USD";
            case "EUR" -> this.currency = "EUR";
            default -> System.out.println("Wrong currency");
        }
    }

    public Integer getAccountId() {
        return accountId;
    }

    public void setAccountId(Integer id) {
        this.accountId = id;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        currencyChecker(currency);
    }

    public Double getBalance() {
        return balance;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }

    public void refillBalance(Double filling) {
        this.balance += filling;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return "Account{" +
                "id=" + accountId +
                ", clientId=" + client.getClientId() +
                ", currency='" + currency + '\'' +
                ", balance=" + balance +
                '}';
    }
}
