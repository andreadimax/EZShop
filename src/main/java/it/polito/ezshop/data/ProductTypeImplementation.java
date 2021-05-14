package it.polito.ezshop.data;

public class ProductTypeImplementation implements ProductType {
    private String barCode;
    private String description;
    private double sellPrice;
    private String note;
    private Integer availableQty;
    private String location;
    private Integer id;

    //Constructor
    public ProductTypeImplementation(Integer id, String barCode, String description, double sellPrice, String note) {
        this.barCode = barCode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.note = note;
        this.id=id;
        this.availableQty=0;
    }

    //serviceConstructor
    public ProductTypeImplementation(Integer id, String barCode, String description, double sellPrice, String note, Integer availableQty, String location) {
        this.barCode = barCode;
        this.description = description;
        this.sellPrice = sellPrice;
        this.note = note;
        this.id=id;
        this.availableQty=availableQty;
        this.location=location;
    }

    //CopyConstructor
    public ProductTypeImplementation(ProductType p){
        this(Integer.valueOf(p.getId()),p.getBarCode(),p.getProductDescription(),
                Double.valueOf(p.getPricePerUnit()),p.getNote(),Integer.valueOf(p.getQuantity()),p.getLocation());

    }

    //Methods
    public boolean changeQuantity(Integer amount){
        if(amount == null){ return false;}
        System.out.println("updating quantity in implementation: " + amount);
        //if i am subtracting and the available quantity is not enough
        if(amount<0 && this.availableQty<-amount){
            return false;
        }
        this.availableQty = this.availableQty + amount;
        return true;
    }
    // getters and setters
    @Override
    public Integer getQuantity() {
        System.out.println("getQuantity of product: "+ this.barCode +"\n");
        System.out.println("availableQty: " + this.availableQty.toString() +"\n");
        return availableQty;
    }

    @Override
    public void setQuantity(Integer quantity) {
        System.out.println("setQuantity of product: "+ this.barCode +"\n");
        System.out.println("availableQty BEFORE SET: " + this.availableQty.toString() +"\n");
        this.availableQty=quantity;
        System.out.println("availableQty AFTER SET: "+ this.availableQty.toString() + "\n");
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
        this.id = id;
    }
}
