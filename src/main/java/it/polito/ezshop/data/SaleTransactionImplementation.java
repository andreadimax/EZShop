package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class SaleTransactionImplementation extends BalanceOperationImpl {
    double discountRate;
    String status;
    List<TicketEntry> entries;
    ArrayList<ProductRfid> rfids;
    /* how we waned to mange it instead of using the TicketEntry list:
    ArrayList <Integer> quantity;
    ArrayList <ProductType> ptArrayList;
    */

    /**
     * Constructor with all parameters useful for loading phase from persistent data
     */
    public SaleTransactionImplementation(int balanceId, String description, double money, LocalDate date,
                                         double discountRate, String status,List<TicketEntry> entries, ArrayList<ProductRfid> rfids){
        super(balanceId, description, money, date);
        this.discountRate = discountRate;
        this.status = status;
        this.entries = entries;
        this.rfids = rfids;
    }

    /**
     * constructor with no params, used to start a new sale transaction, only auto-generates the balanceId as every BalanceOperations does
     */
    public SaleTransactionImplementation(){
        super("SaleTransaction");
        //setting proper of this class
        this.discountRate = 0.0;
        this.status = "OPEN";
        this.entries = new ArrayList<>();
        this.rfids = new ArrayList<>();
    }

    @Override
    public int getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
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

    public String getStatus(){
        return  this.status;
    }

    public  void setStatus(String status){
        this.status = status;
    }
}
