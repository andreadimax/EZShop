package it.polito.ezshop.data;

public class ProductRfid {
    public String RFID;
    public Integer productId;

    public ProductRfid(String RFID, Integer productId){
        this.productId = productId;
        this.RFID = RFID;
    }
}

