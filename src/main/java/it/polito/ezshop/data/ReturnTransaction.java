package it.polito.ezshop.data;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ReturnTransaction extends BalanceOperationImpl {
    private Integer saleId;
    private List<TicketEntry> returnEntries;
    private String status = "OPEN";
    private double saleDiscount;
    ArrayList<ProductRfid> rfids;

    /**
     * constructor with all parameters useful for Loading Phase from persistent data
     * @param saleId the saleTransaction referenced by this return
     * @param returnEntries list of product returned and their returned quantity
     */
    public ReturnTransaction(int balanceId, String description, double money, LocalDate date,
                            Integer saleId, String status,List<TicketEntry> returnEntries, double saleDiscount, ArrayList<ProductRfid> rfids){
        super(balanceId, description, money, date);
        this.saleId = saleId;
        this.status = status;
        this.returnEntries = returnEntries;
        this.saleDiscount = saleDiscount;
        this.rfids = rfids;
    }


    public ReturnTransaction(int saleId, List<TicketEntry> returnEntries){
        super("ReturnTransaction");
        this.saleId = saleId;
        this.returnEntries = returnEntries;
        this.rfids = new ArrayList<>();
    }

    /**
     * constructor with only the saleTransaction referenced, used to start a new return transaction, also auto-generates the balanceId as every BalanceOperation does
     */
    public ReturnTransaction(int saleId){
        super("ReturnTransaction");
        //setting the reference to the sale transaction
        this.saleId = saleId;
        this.returnEntries = new ArrayList<>();
        this.rfids = new ArrayList<>();
    }

    public List<TicketEntry> getReturnEntries() {
        return returnEntries;
    }

    public Integer getSaleId() {
        return saleId;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStatus(){
        return this.status;
    }

    public void setReturnEntries(List<TicketEntry> returnEntries) {
        this.returnEntries = returnEntries;
    }

    public double getSaleDiscount() {
        return saleDiscount;
    }

    public void setSaleDiscount(double saleDiscount) {
        this.saleDiscount = saleDiscount;
    }
}
