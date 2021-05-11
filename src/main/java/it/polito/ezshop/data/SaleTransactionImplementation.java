package it.polito.ezshop.data;

import java.util.ArrayList;
import java.util.List;

public class SaleTransactionImplementation extends BalanceOperationImpl {
    double cost;
    String paymentType;
    double discountRate;
    String status;
    ArrayList <Integer> quantity;
    ArrayList <ProductType> ptArrayList;


    public List<TicketEntry> getEntries() {
        return null;
    }

    public void setEntries(List<TicketEntry> entries) {
        return;
    }

    public double getDiscountRate() {
        return 0;
    }

    public void setDiscountRate(double discountRate) {
    }

    public double getPrice() {
        return 0;
    }

    public void setPrice(double price) {
    }
}
