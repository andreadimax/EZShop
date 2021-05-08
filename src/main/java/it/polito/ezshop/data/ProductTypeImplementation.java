package it.polito.ezshop.data;

public class ProductTypeImplementation implements ProductType {
    private String barCode;
    private String description;
    private double sellPrice;
    private double discountRate;
    private String note;
    private Integer availableQty;
    private String location;
    private Integer id;

    //Constructor
    public ProductTypeImplementation(Integer id, String barCode, String description, double sellPrice, String note) {
        this.barCode = barCode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.discountRate = discountRate;
        this.note = note;
        this.id=id;
    }

    //Methods
    public boolean changeQuantity(Integer amount){
        //if i am subtracting and the available quantity is not enough
        if(amount<0 && this.availableQty<amount){
            return false;
        }
        this.availableQty+=amount;
        return true;
    }
    // getters and setters
    @Override
    public Integer getQuantity() {
        return availableQty;
    }

    @Override
    public void setQuantity(Integer quantity) {
        this.availableQty=quantity;
    }

    @Override
    public String getLocation() {
        return this.location;
    }

    @Override
    public void setLocation(String location) {
        this.location = location;
    }

    @Override
    public String getNote() {
        return note;
    }

    @Override
    public void setNote(String note) {
        this.note=note;
    }

    @Override
    public String getProductDescription() {
        return description;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.description=productDescription;
    }

    @Override
    public String getBarCode() {
        return barCode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barCode=barCode;
    }

    @Override
    public Double getPricePerUnit() {
        return sellPrice;
    }

    @Override
    public void setPricePerUnit(Double pricePerUnit) {
        this.sellPrice=pricePerUnit;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {

    }

    public double getDiscountRate() {
        return discountRate;
    }

    public void setDiscountRate(double discountRate) {
        this.discountRate=discountRate;
    }
}
