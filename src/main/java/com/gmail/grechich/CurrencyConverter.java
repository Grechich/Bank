package com.gmail.grechich;

import java.util.List;

public class CurrencyConverter {

    private static final Double HRIVNA = 1.0;
    private static final Double DOLLAR = 1.0 / 28.0;
    private static final Double EURO = 1.0 / 32.0;


    public static boolean convert(Client client, String fromCurrency, String toCurrency) {
        if (!convertCheker(client, fromCurrency, toCurrency))
            return false;
        Account accountFrom = client.getAccountByCurrency(fromCurrency);
        Account accountTo = client.getAccountByCurrency(toCurrency);
        Double balanceFrom = accountFrom.getBalance();
        Double balanceTo = accountTo.getBalance();
        Double koef = 1.0;
        switch (fromCurrency) {
            case "UAH":
                if (toCurrency.equals("EUR")) {
                    koef = EURO;
                } else {
                    koef = DOLLAR;
                }
                break;
            case "USD":
                if (toCurrency.equals("EUR")) {
                    koef = EURO / DOLLAR;
                } else {
                    koef = HRIVNA / DOLLAR;
                }
                break;
            case "EUR":
                if (toCurrency.equals("USD")) {
                    koef = DOLLAR / EURO;
                } else {
                    koef = HRIVNA / EURO;
                }
                break;
        }
        client.getAccountByCurrency(toCurrency).setBalance(balanceTo + balanceFrom * koef);
        client.getAccountByCurrency(fromCurrency).setBalance(0.0);
        return true;
    }

    private static boolean convertCheker(Client client, String fromCurrency, String toCurrency) {
        if (!("UAH".equals(fromCurrency) || "USD".equals(fromCurrency) || "EUR".equals(fromCurrency))
                && ("UAH".equals(toCurrency) || "USD".equals(toCurrency) || "EUR".equals(toCurrency))) {
            System.out.println("Wrong currency");
            return false;
        }

        Account accountFrom = client.getAccountByCurrency(fromCurrency);
        if (accountFrom==null || accountFrom.getBalance()<=0){
            System.out.println("You have no money on you account");
            return false;
        }

        return true;
    }


}
