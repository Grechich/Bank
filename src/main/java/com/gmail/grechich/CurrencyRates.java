package com.gmail.grechich;

import jakarta.persistence.*;

@Entity
@Table(name = "rates")
public class CurrencyRates {
    @Id
    @GeneratedValue
    private Integer rateId;

    private String name;
    private Double rate;

    public CurrencyRates() {
    }

    public CurrencyRates(String name, Double rate) {
        this.name = name;
        this.rate = rate;
    }

    public static void updateRates(EntityManager em) {

        Double rateUAH = 1.0;
        Double rateUSD = 28.1; //need to get from WEB
        Double rateEUR = 32.1; //need to get from WEB

        em.getTransaction().begin();
        try {
            for (int i = 1; i <= 3; i++) {
                CurrencyRates c = em.find(CurrencyRates.class, i);
                if (c.getName().equals("UAH"))
                    c.setRate(rateUAH);
                if (c.getName().equals("USD"))
                    c.setRate(rateUSD);
                if (c.getName().equals("EUR"))
                    c.setRate(rateEUR);
                em.merge(c);
            }
            em.getTransaction().commit();
        } catch (Exception ex) {
            em.getTransaction().rollback();
        }
    }

    public static void createRates(EntityManager em) {
        em.getTransaction().begin();
        try {
            em.persist(new CurrencyRates("UAH", 1.0));
            em.persist(new CurrencyRates("USD", 28.0));
            em.persist(new CurrencyRates("EUR", 32.0));
            em.getTransaction().commit();
        } catch (Exception ex) {
            ex.printStackTrace();
            em.getTransaction().rollback();
        }
    }

    public Integer getRateId() {
        return rateId;
    }

    public void setRateId(Integer rateId) {
        this.rateId = rateId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }
}
