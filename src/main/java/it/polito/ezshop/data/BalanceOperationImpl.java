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

    public BalanceOperationImpl(double money){
        balanceCounter++;
        this.balanceId = balanceCounter;
        this.date = LocalDate.now();
        this.money = money;
        this.description = (money >= 0) ? "Credit" : "Debit";

    }

    public BalanceOperationImpl(String description){
        balanceCounter++;
        this.balanceId = balanceCounter;
        this.description = description;
        this.money = 0;
        this.date = LocalDate.now();
    }

    //used in loading phase by super() call of subclasses constructors
    public BalanceOperationImpl(int balanceId, String description, double money, LocalDate date){
        //balanceCounter++;
        this.balanceId = balanceId;
        this.description = description;
        this.money = money;
        this.date = date;
    }

    public static void setBalanceCounter(int balanceCounter) {
        BalanceOperationImpl.balanceCounter = Integer.valueOf(balanceCounter);
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
