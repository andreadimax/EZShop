package it.polito.ezshop.data;
import java.util.HashMap;

public class AccountBook {

    private HashMap <Integer,BalanceOperation> operationsMap = new HashMap<>();

    //net balance of all the BalanceOperations performed (payed)
    private double balance;

    public AccountBook(){
        this.balance = 0;
    }

    /**
     *
     * @param NewOp The balanceOperation object to add to the map of operations
     * @return false if operation was already present, true if it's added.
     */
    public boolean addOperation(BalanceOperation NewOp){
        if(this.operationsMap.containsKey(NewOp.getBalanceId())){
            return false;
        }
        this.operationsMap.put(NewOp.getBalanceId(), NewOp);
        return true;
    }

    /**
     * Removes operation with an operationId == index from the operationsMap
     * @return true if finds and removes the operation,
     *         false if can't find the operation to remove.
     */
    public boolean removeOperation(Integer index){
        return this.operationsMap.remove(index) != null;
    }

    /**
     *
     * @param index the balanceId of the operation to get
     * @return the operation or null if it's not present.
     */
    public BalanceOperation getOperation(Integer index){
        return this.operationsMap.get(index);
    }

    public void changeBalance(double amount){
        this.balance += amount;
    }



}
