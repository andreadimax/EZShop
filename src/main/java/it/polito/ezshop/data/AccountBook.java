package it.polito.ezshop.data;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
//@todo remove sub parameter and use description
//@todo implement json/file update for all methods
public class AccountBook {

    private HashMap <Integer,BalanceOperation> operationsMap;
    private JSONArray jArrayOperations;
    private final String filepath = "src/main/persistent_data/operations.json";
    //net balance of all the BalanceOperations performed (payed)
    private double balance;

    public AccountBook(){
        this.balance = 0;
        this.operationsMap = new HashMap<>();

        this.jArrayOperations=initializeMap();

    }

    private JSONArray initializeMap(){

        JSONParser parser = new JSONParser();
        JSONArray jArray = null;
        FileReader file = null;
        try {
            file = new FileReader(this.filepath);
        }
        catch (FileNotFoundException f){
            f.printStackTrace();
        }

        try
        {
            //Read JSON file

            jArray = (JSONArray) parser.parse(file);

            jArray.forEach( x -> parseObjectSubclass( (JSONObject) x ) );

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        //setting the counter generating balanceId's to the greater id present;
        Integer maxBalanceId = operationsMap.values().stream()
                .max((o1,o2) -> o1.getBalanceId() > o2.getBalanceId() ? 1 : -1).get().getBalanceId();
        if(maxBalanceId!=null && maxBalanceId!=0) { BalanceOperationImpl.setBalanceCounter(maxBalanceId); }

        return jArray;
    }

    /**
     * Needed to distinguish between different subclasses of BalanceOperations in the loading phase.
     * @param x the BalanceOperation object in (JSON) state that we need to load into the right subclass.
     */
    private void parseObjectSubclass(JSONObject x) {
        //Get BalanceOperation Id
        int balanceId = Integer.parseInt((String) x.get("balanceId"));
        //Get description
        String description = (String) x.get("description");
        //Get money
        double money = Double.parseDouble((String) x.get("money"));
        //Get date
        LocalDate date = LocalDate.parse((String) x.get("date"));

        /*//based on the "sub" field, present only in JSON version of the object, instantiates different subclasses
        String sub = (String) x.get("sub");*/
        if(description.equals("Order")){

            String productCode = (String) x.get("productCode");
            double pricePerUnit = Double.parseDouble((String) x.get("pricePerUnit"));
            int quantity = Integer.parseInt((String) x.get("quantity"));
            String status = (String) x.get("status");

            //building order with the full constructor
            OrderImpl order = new OrderImpl(balanceId, description, money, date, productCode, quantity, pricePerUnit, status);
            //adding the loaded order back into the operationsMap checking for duplicates
            if(!this.operationsMap.containsKey(balanceId)){
                this.operationsMap.put(balanceId, order);
                if( status.equals("PAYED") || status.equals("COMPLETED")){this.changeBalance(money);}
            }
        }
        else if(description.equals("SaleTransaction")){

            double discountRate = Double.parseDouble((String) x.get("discountRate"));
            String status = (String) x.get("status");
            //JSON array to iterate over TicketEntries "entries"
            JSONArray jEntries = (JSONArray) x.get("entries");
            //loading TicketEntries list of the sale transaction
            List<TicketEntry> entries = new ArrayList<>();
            jEntries.forEach(e -> addEntry(entries, (JSONObject) e));

            //building saleTransaction with the full constructor
            SaleTransactionImplementation Sale = new SaleTransactionImplementation(balanceId,description,money,date,discountRate,status,entries);
            //adding the sale transaction back into the operationsMap checking for duplicates
            if(!this.operationsMap.containsKey(balanceId)){
                this.operationsMap.put(balanceId, Sale);
                if( status.equals("CLOSED") || status.equals("COMPLETED")){this.changeBalance(money);}
            }
        }
        else if(description.equals("ReturnTransaction")){
            //@todo implement ReturnTrans subclass loading
        }
        else if(description.equals("Credit") || description.equals("Debit")){
            //Simple Balance Update Operation (credit or debit)
            BalanceOperation operation = new BalanceOperationImpl(balanceId,description,money,date);
            //adding the operation (credit or debit) back into the operationsMap checking for duplicates
            if(!this.operationsMap.containsKey(balanceId)){
                this.operationsMap.put(balanceId, operation);
                this.changeBalance(money);
            }
        }


    }

    /**
     * used to simplify entries load of sales transactions,
     * parses the Ticket entry fields and adds it to the list of entries
     */
    private void addEntry( List<TicketEntry> entries, JSONObject entry){
        //parsing the entry fields and building it
        String barcode = (String) entry.get("barcode");
        String desc = (String) entry.get("description");
        int amount = Integer.parseInt((String) entry.get("amount"));
        double PPU = Double.parseDouble((String) entry.get("PPU"));
        double discountRate = Double.parseDouble((String) entry.get("discountRate"));
        entries.add(new TicketEntryImpl(barcode,desc,amount,PPU,discountRate));
    }

    /**
     *  this method also updates the Json array but not the file!
     * @param NewOp The balanceOperation object to add to the map of operations
     * @return false if operation was already present, true if it's added.
     */
    public boolean addOperation(BalanceOperation NewOp){
        if(this.operationsMap.containsKey(NewOp.getBalanceId())){
            return false;
        }
        this.operationsMap.put(NewOp.getBalanceId(), NewOp);

        //Updating JSON Object in the JSON Array
        //encoding basic BalanceOperation properties
        JSONObject joperation = new JSONObject();
        joperation.put("balanceId", ((Integer)NewOp.getBalanceId()).toString());
        joperation.put("description", NewOp.getType());
        joperation.put("money", ((Double)NewOp.getMoney()).toString());
        joperation.put("date", NewOp.getDate().toString());

        //encoding subclass specific properties
        if(NewOp instanceof OrderImpl){
            OrderImpl order = (OrderImpl) NewOp;
            //joperation.put("sub","order");
            joperation.put("productCode", order.getProductCode());
            joperation.put("pricePerUnit",((Double) order.getPricePerUnit()).toString());
            joperation.put("quantity", ((Integer) order.getQuantity()).toString());
            joperation.put("status", order.getStatus());
        }
        else if(NewOp instanceof SaleTransactionImplementation){
            SaleTransactionImplementation sale = (SaleTransactionImplementation) NewOp;
            joperation.put("discountRate", ((Double)sale.getDiscountRate()).toString());
            joperation.put("status", sale.getStatus());
            //section to load the JSONArray entries
            JSONArray jEntries = new JSONArray();
            JSONObject jEntry;
            for (int i = 0; i < sale.entries.size(); i++) {
                jEntry = new JSONObject();
                TicketEntry entry = sale.entries.get(i);
                jEntry.put("barcode",entry.getBarCode());
                jEntry.put("description",entry.getProductDescription());
                jEntry.put("amount",((Integer)entry.getAmount()).toString());
                jEntry.put("PPU",((Double)entry.getPricePerUnit()).toString());
                jEntry.put("discountRate",((Double)entry.getDiscountRate()).toString());
                jEntries.add(jEntry);
            }
            joperation.put("entries",jEntries);
        }
        else{
            //case where it's either a simple Debit or Credit operation
            if(NewOp.getMoney() > 0){
                //joperation.put("sub","credit");
            }
            else{
                //joperation.put("sub","debit");
            }
        }
        //@todo implement subclass ReturnTransaction extending BalanceOperationImpl
        //actually adding the encoded operation into the jArray
        jArrayOperations.add(joperation);

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

    public double getBalance(){
        return this.balance;
    }

    public HashMap <Integer,BalanceOperation> getOperationsMap(){
        return this.operationsMap;
    }

    public String getFilepath() {
        return filepath;
    }

    public JSONArray getjArrayOperations() {
        return jArrayOperations;
    }
}
