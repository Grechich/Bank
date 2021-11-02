package com.gmail.grechich;

import jakarta.persistence.*;

@Entity
@Table(name = "Transactions")
public class Transaction {

    @Id
    @GeneratedValue
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "accountFrom")
    private Account accountFrom;

    @ManyToOne
    @JoinColumn(name = "accountTo")
    private Account accountTo;

    private Double amount;

    public Transaction() {
    }

    public Transaction(Account accountFrom, Account accountTo, Double amount) {
        this.accountFrom = accountFrom;
        this.accountTo = accountTo;
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "Transaction{" +
                "id=" + id +
                ", accountFrom=" + accountFrom +
                ", accountTo=" + accountTo +
                ", amount=" + amount +
                '}';
    }
}
