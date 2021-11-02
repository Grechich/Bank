package com.gmail.grechich;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "Clients")
public class Client {
    @Id
    @GeneratedValue
    private Integer clientId;
    private String name;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL)
    private List<Account> accounts = new ArrayList<>();

    public void addAccount(Account account) {
        if (!accounts.contains(account)) {
            accounts.add(account);
            account.setClient(this);
        }
    }

    public Account getAccountByCurrency(String currency) {
        for (Account a : accounts) {
            if (a.getCurrency().equals(currency)) {
                return a;
            }
        }
        Account account = new Account(currency);
        this.addAccount(account);
        return account;
    }

    public Client() {
    }

    public Client(String name) {
        this.name = name;
    }

    public Integer getClientId() {
        return clientId;
    }

    public void setClientId(Integer id) {
        this.clientId = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public void setAccounts(List<Account> accounts) {
        this.accounts = accounts;
    }

    @Override
    public String toString() {
        return "Client{" +
                "id=" + clientId +
                ", name='" + name + '\'' +
                ", accounts=" + accounts +
                '}';
    }
}
