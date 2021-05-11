package it.polito.ezshop.data;

public class TicketEntryImpl implements TicketEntry {
    private String barcode;
    private String description;
    private int amount;
    private double pricePerUnit;
    private double discountRate;

    /**
     * Constructor with all parameters useful for loading phase from persistent data
     */
    public TicketEntryImpl(String barcode, String description, int amount, double pricePerUnit, double discountRate ){
        this.barcode = barcode;
        this.description = description;
        this.amount = amount;
        this.pricePerUnit = pricePerUnit;
        this.discountRate = discountRate;
    }

    @Override
    public String getBarCode() {
        return this.barcode;
    }

    @Override
    public void setBarCode(String barCode) {
        this.barcode = barCode;
    }

    @Override
    public String getProductDescription() {
        return this.description;
    }

    @Override
    public void setProductDescription(String productDescription) {
        this.description=productDescription;
    }

    @Override
    public int getAmount() {
        return this.amount;
    }

    @Override
    public void setAmount(int amount) {
        this.amount = amount;
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
    public double getDiscountRate() {
        return this.discountRate;
    }

    @Override
    public void setDiscountRate(double discountRate) {
        this.discountRate = discountRate;
    }
}
