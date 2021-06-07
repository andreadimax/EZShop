package it.polito.ezshop.data;

public class ProductRfid {
    String RFID;
    Integer productId;

    public ProductRfid(String RFID, Integer productId){
        this.productId = productId;
        this.RFID = RFID;
    }
}

