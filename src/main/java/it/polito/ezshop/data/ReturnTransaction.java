package it.polito.ezshop.data;

import java.util.ArrayList;
import java.util.List;

public class ReturnTransaction extends BalanceOperationImpl {
    private SaleTransactionImplementation sale;
    private List<TicketEntry> returnEntries;

    /**
     * constructor with all parameters useful for Loading Phase
     * @param sale the saleTransaction referenced by this return
     * @param returnEntries list of product returned and their returned quantity
     */
    public ReturnTransaction(SaleTransactionImplementation sale, List<TicketEntry> returnEntries){
        super("ReturnTransaction");
        this.sale = sale;
        this.returnEntries = returnEntries;
    }

    /**
     * constructor with only the saleTransaction referenced, used to start a new return transaction, also auto-generates the balanceId as every BalanceOperation does
     */
    public ReturnTransaction(SaleTransactionImplementation sale){
        super("SaleTransaction");
        //setting the reference to the sale transaction
        this.sale = sale;
        this.returnEntries = new ArrayList<>();
    }

    public List<TicketEntry> getReturnEntries() {
        return returnEntries;
    }

    public SaleTransactionImplementation getSale() {
        return sale;
    }
}
