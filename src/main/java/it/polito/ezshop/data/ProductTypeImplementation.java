package it.polito.ezshop.data;

public class ProductTypeImplementation implements ProductType {
    private String barCode;
    private String description;
    private double sellPrice;
    private double discountRate;
    private String notes;
    private Integer availableQty;

    //Constructor
    public ProductTypeImplementation(String barCode, String description, double sellPrice, double discountRate, String notes, Integer availableQty) {
        this.barCode = barCode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.discountRate = discountRate;
        this.notes = notes;
        this.availableQty = availableQty;
    }

    //Methods
    public boolean changeQuantity(Integer amount){
        if(this.availableQty<amount){
            return false;
        }
        this.availableQty-=amount;
        return true;
    }
    // getters and setters
    @Override
    public Integer getQuantity() {
        return null;
    }

    @Override
    public void setQuantity(Integer quantity) {

    }

    @Override
    public String getLocation() {
        return null;
    }

    @Override
    public void setLocation(String location) {

    }

    @Override
    public String getNote() {
        return null;
    }

    @Override
    public void setNote(String note) {

    }

    @Override
    public String getProductDescription() {
        return null;
    }

    @Override
    public void setProductDescription(String productDescription) {

    }

    @Override
    public String getBarCode() {
        return null;
    }

    @Override
    public void setBarCode(String barCode) {

    }

    @Override
    public Double getPricePerUnit() {
        return null;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {

    }

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer id) {

    }
}
