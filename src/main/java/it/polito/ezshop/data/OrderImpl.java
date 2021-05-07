package it.polito.ezshop.data;

import java.time.LocalDate;

//should extend BalanceOperation but there is a conflict between return type (int) vs (Integer) of method getBalanceId
public class OrderImpl extends BalanceOperationImpl {

    //Attributes properly of this class
    private String productCode;
    private double pricePerUnit;
    private int quantity;
    private String status;


    public OrderImpl(String productCode, int quantity, double pricePerUnit){
        super();
        //setting proper of this class
        this.productCode = productCode;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.status = "issued";
        super.money = -(quantity * pricePerUnit);
    }

    @Override
    public int getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(int balanceId) {
        this.balanceId = balanceId;
    }


    public String getProductCode() {
        return this.productCode;
    }


    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }


    public double getPricePerUnit() {
        return this.pricePerUnit;
    }


    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }


    public int getQuantity() {
        return this.quantity;
    }


    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }


    public String getStatus() {
        return this.status;
    }


    public void setStatus(String status) {
        this.status = status;
    }


    public Integer getOrderId() {
        return this.balanceId;
    }


    public void setOrderId(Integer orderId) {
        this.balanceId = orderId;
    }
}
