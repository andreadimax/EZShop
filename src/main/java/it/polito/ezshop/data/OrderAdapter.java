package it.polito.ezshop.data;

public class OrderAdapter implements Order {
    //the wrapped order
    private OrderImpl order;

    public OrderAdapter(OrderImpl order){
        this.order = order;
    }

    @Override
    public Integer getBalanceId() {
        return this.order.getBalanceId();
    }

    @Override
    public void setBalanceId(Integer balanceId) {
        this.order.setBalanceId(balanceId);
    }

    @Override
    public String getProductCode() {
        return this.order.getProductCode();
    }

    @Override
    public void setProductCode(String productCode) {
        this.order.setProductCode(productCode);
    }

    @Override
    public double getPricePerUnit() {
        return this.order.getPricePerUnit();
    }

    @Override
    public void setPricePerUnit(double pricePerUnit) {
        this.order.setPricePerUnit(pricePerUnit);
    }

    @Override
    public int getQuantity() {
        return this.order.getQuantity();
    }

    @Override
    public void setQuantity(int quantity) {
        this.order.setQuantity(quantity);
    }

    @Override
    public String getStatus() {
        return this.order.getStatus();
    }

    @Override
    public void setStatus(String status) {
        this.order.setStatus(status);
    }

    @Override
    public Integer getOrderId() {
        return this.order.getOrderId();
    }

    @Override
    public void setOrderId(Integer orderId) {
        this.order.setOrderId(orderId);
    }
}
