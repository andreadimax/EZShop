package it.polito.ezshop.data;

import java.time.LocalDate;


public class BalanceOperationImpl implements BalanceOperation {
    //Static method to count the number of existing balance operations
    private static int balanceCounter = 0;

    //class attributes
    private int balanceId;
    private String description;
    private double amount;
    private LocalDate date;

    public BalanceOperationImpl(int balanceId, String description, double amount, LocalDate date){
        this.balanceId = balanceId;
        this.description = description;
        this.amount = amount;
        this.date = date;
        balanceCounter++;
    }

    @Override
    public int getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public LocalDate getDate() {
        return this.date;
    }

    @Override
    public void setDate(LocalDate date) {
        this.date = date;
    }

    @Override
    public double getMoney() {
        return amount;
    }

    @Override
    public void setMoney(double money) {
        this.amount = money;
    }

    @Override
    public String getType() {
        return this.description;
    }

    @Override
    public void setType(String type) {
        this.description = type;
    }

}
