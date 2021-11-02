package com.gmail.grechich;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;

import java.util.List;
import java.util.Scanner;

public class Main {
    static EntityManagerFactory emf;
    static EntityManager em;
    static Scanner sc = new Scanner(System.in);

    public static void main(String[] args) {

        try {
            // create connection
            emf = Persistence.createEntityManagerFactory("JPATest");
            em = emf.createEntityManager();
            CurrencyRates.createRates(em);
            try {
                while (true) {
                    System.out.println("1: add client");
                    System.out.println("2: add account");
                    System.out.println("3: refill account");
                    System.out.println("4: convertor");
                    System.out.println("5: view rates");
                    System.out.println("6: view transactions");
                    System.out.println("7: view clients");
                    System.out.println("8: view accounts");

                    System.out.print("-> ");

                    String s = sc.nextLine();
                    switch (s) {
                        case "1":
                            addClient();
                            break;
                        case "2":
                            addAccount();
                            break;
                        case "3":
                            refillAccount();
                            break;
                        case "4":
                            convert();
                            break;
                        case "5":
                            viewRates();
                            break;
                        case "6":
                            viewTransactionsCriteria();
                            break;
                        case "7":
                            viewClientsCriteria();
                            break;
                        case "8":
                            viewAccountsCriteria();
                            break;
                        case "9":
                            sendMoney();
                            break;

                        default:
                            continue;
                    }
                }
            } finally {
                sc.close();
                em.close();
                emf.close();
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return;
        }
    }

    private static void viewRates() {
        CurrencyRates.updateRates(em);
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<CurrencyRates> criteria = builder.createQuery(CurrencyRates.class);
        Root<CurrencyRates> root = criteria.from(CurrencyRates.class);
        criteria.select(root);
        List<CurrencyRates> res1 = em.createQuery(criteria).getResultList();
        for (CurrencyRates c : res1)
            System.out.println(c.getName() + "=" + c.getRate());
    }

    private static void convert() {
        Client client = getClientById();
        if (client == null)
            return;

        System.out.print("Enter currency to convert: ");
        String currencyFrom = sc.nextLine();
        System.out.print("Enter currency to convert to: ");
        String currencyTo = sc.nextLine();
        if (CurrencyConverter.convert(client, currencyFrom, currencyTo)) {
            Transaction transaction = new Transaction(
                    client.getAccountByCurrency(currencyFrom), client.getAccountByCurrency(currencyTo), client.getAccountByCurrency(currencyFrom).getBalance());
            em.getTransaction().begin();
            try {
                em.persist(client);
                em.getTransaction().commit();
                addTransaction(transaction);
            } catch (Exception ex) {
                em.getTransaction().rollback();
            }
        } else {
            System.out.println("Conversion failed");
        }
    }

    private static void refillAccount() {
        Client client = getClientById();
        if (client == null)
            return;

        System.out.print("Enter currency to refill: ");
        String currencyToRefill = sc.nextLine();
        System.out.print("Enter amount: ");
        String sAmount = sc.nextLine();
        Double amount = Double.parseDouble(sAmount);
        Account account = client.getAccountByCurrency(currencyToRefill);
        account.refillBalance(amount);
        Transaction transaction = new Transaction(account, account, amount);

        em.getTransaction().begin();
        try {
            em.persist(client);
            em.getTransaction().commit();
            addTransaction(transaction);

        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }

    private static void addAccount() {
        Client client = getClientById();
        if (client == null)
            return;
        System.out.print("Enter account currency: ");
        String currency = sc.nextLine();

        Account account = new Account(currency);
        if (client != null) {
            client.addAccount(account);
        } else {
            return;
        }
        em.getTransaction().begin();
        try {
            em.persist(client);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void addClient() {
        System.out.print("Enter client name: ");
        String name = sc.nextLine();
        Client client = new Client(name);

        em.getTransaction().begin();
        try {
            em.persist(client);
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    private static void sendMoney() {

        System.out.print("Enter client which send money id: ");
        Client clientFrom = getClientById();
        if (clientFrom == null)
            return;
        System.out.print("Enter client which receive money id: ");
        Client clientTo = getClientById();
        if (clientTo == null)
            return;

        System.out.print("Enter currency: ");
        String currency = sc.nextLine();
        Account accountFrom = clientFrom.getAccountByCurrency(currency);
        Account accountTo = clientTo.getAccountByCurrency(currency);
        accountTo.setBalance(accountTo.getBalance() + accountFrom.getBalance());
        accountFrom.setBalance(0.0);
        em.getTransaction().begin();
        try {
            em.merge(accountFrom);
            em.merge(accountTo);
        } catch (Exception e) {
            em.getTransaction().rollback();
        }

    }


    public static void viewClientsCriteria() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Client> criteria = builder.createQuery(Client.class);
        Root<Client> root = criteria.from(Client.class);
        criteria.select(root);
        List<Client> res1 = em.createQuery(criteria).getResultList();
        for (Client client : res1)
            System.out.println(client);
    }

    public static void viewAccountsCriteria() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Account> criteria = builder.createQuery(Account.class);
        Root<Account> root = criteria.from(Account.class);
        criteria.select(root);
        List<Account> res1 = em.createQuery(criteria).getResultList();
        for (Account account : res1)
            System.out.println(account);
    }

    public static void viewTransactionsCriteria() {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<Transaction> criteria = builder.createQuery(Transaction.class);
        Root<Transaction> root = criteria.from(Transaction.class);
        criteria.select(root);
        List<Transaction> res1 = em.createQuery(criteria).getResultList();
        for (Transaction transaction : res1)
            System.out.println(transaction);
    }

    private static Client getClientById() {
        System.out.print("Enter client id: ");
        String sId = sc.nextLine();
        Integer id = Integer.parseInt(sId);
        Client client = em.find(Client.class, id);
        if (client == null) {
            System.out.println("Client with id=" + id + " not found");
            return null;
        }
        return client;
    }

    private static void addTransaction(Transaction transaction) {
        em.getTransaction().begin();
        try {
            em.persist(transaction);
            em.getTransaction().commit();
        } catch (Exception e) {
            em.getTransaction().rollback();
        }
    }


}

//          Создать базу данных «Банк» с таблицами «Пользователи», «Транзакции», «Счета» и «Курсы валют».
//          Счет бывает 3-х видов: USD, EUR, UAH.
//          Написать запросы для
//            пополнения счета в нужной валюте,
//            перевода средств с одного счета на другой,
//            конвертации валюты по курсу в рамках счетов одного пользователя.
//          Написать запрос для получения суммарных средств на счету одного пользователя в UAH (расчет по курсу).