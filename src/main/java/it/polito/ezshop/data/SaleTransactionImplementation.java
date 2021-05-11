package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleTransactionImplementation extends BalanceOperationImpl {
    String paymentType;
    double discountRate;
    String status;
    List<TicketEntry> entries;
    /* how we waned to mange it instead of using the TicketEntry list:
    ArrayList <Integer> quantity;
    ArrayList <ProductType> ptArrayList;
    */

    /**
     * Constructor with all parameters useful for loading phase from persistent data
     */
    public SaleTransactionImplementation(int balanceId, String description, double money, LocalDate date,
                                         double discountRate, String status, String paymentType, List<TicketEntry> entries){
        super(balanceId, description, money, date);
        this.discountRate = discountRate;
        this.status = status;
        this.paymentType = paymentType;
        this.entries = entries;
    }


    public List<TicketEntry> getEntries() {
        return this.entries;
    }

    public void setEntries(List<TicketEntry> entries) {
        this.entries = entries;
    }

    public double getDiscountRate() {
        return this.discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }

    public double getPrice() {
        return this.money;
    }

    public void setPrice(double price) {
        this.money = price;
    }
}
