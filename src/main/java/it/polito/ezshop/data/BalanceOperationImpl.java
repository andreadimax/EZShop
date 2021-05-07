package it.polito.ezshop.data;

import java.time.LocalDate;


public class BalanceOperationImpl implements BalanceOperation {
    //Static method to count the number of existing balance operations
    private static int balanceCounter = 0;

    //class attributes
    protected int balanceId;
    protected String description;
    protected double money;
    protected LocalDate date;

    public BalanceOperationImpl(){
        balanceCounter++;
        this.balanceId = balanceCounter;
        this.description = "";
        this.money = 0;
        this.date = LocalDate.now();
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
        return money;
    }

    @Override
    public void setMoney(double money) {
        this.money = money;
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
