package it.polito.ezshop.data;

import java.util.List;

public class SaleTransactionAdapter implements SaleTransaction{
    SaleTransactionImplementation sale;

    public SaleTransactionAdapter(SaleTransactionImplementation sale) {
        this.sale = sale;
    }

    @Override
    public Integer getTicketNumber() {
        return sale.getBalanceId();
    }

    @Override
    public void setTicketNumber(Integer ticketNumber) {
        sale.setBalanceId(ticketNumber);
    }

    @Override
    public List<TicketEntry> getEntries() {
        return sale.getEntries();
    }

    @Override
    public void setEntries(List<TicketEntry> entries) {
        sale.setEntries(entries);
    }

    @Override
    public double getDiscountRate() {
        return sale.getDiscountRate();
    }

    @Override
    public void setDiscountRate(double discountRate) {
        sale.setDiscountRate(discountRate);
    }

    @Override
    public double getPrice() {
        return sale.getPrice();
    }

    @Override
    public void setPrice(double price) {
        sale.setPrice(price);
    }
}
