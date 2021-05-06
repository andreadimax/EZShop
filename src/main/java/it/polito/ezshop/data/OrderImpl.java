package it.polito.ezshop.data;

import java.time.LocalDate;

//should extend BalanceOperation but there is a conflict between return type (int) vs (Integer) of method getBalanceId
public class OrderImpl implements Order {

    //Attributes that should inherit from BalanceOperation:
    private static int balanceCounter = 0;
    private int balanceId;
    private String description;
    private double money;
    private LocalDate date;

    //Attributes properly of this class
    private String productCode;
    private double pricePerUnit;
    private int quantity;
    private String status;


    public OrderImpl(String productCode, int quantity, double pricePerUnit){
        //should be set from super constructor of BalanceOperation...
        balanceCounter++;
        this.balanceId = balanceCounter;
        this.description = "";
        this.money = 0;
        this.date = LocalDate.now();

        //setting proper of this class
        this.productCode = productCode;
        this.quantity = quantity;
        this.pricePerUnit = pricePerUnit;
        this.status = "issued";

    }

    @Override
    public Integer getBalanceId() {
        return this.balanceId;
    }

    @Override
    public void setBalanceId(Integer balanceId) {
        this.balanceId = balanceId;
    }

    @Override
    public String getProductCode() {
        return this.productCode;
    }

    @Override
    public void setProductCode(String productCode) {
        this.productCode = productCode;
    }

    @Override
    public double getPricePerUnit() {
        return this.pricePerUnit;
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.pricePerUnit = pricePerUnit;
    }

    @Override
    public int getQuantity() {
        return this.quantity;
    }

    @Override
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String getStatus() {
        return this.status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public Integer getOrderId() {
        return this.balanceId;
    }

    @Override
    public void setOrderId(Integer orderId) {
        this.balanceId = orderId;
    }
}
