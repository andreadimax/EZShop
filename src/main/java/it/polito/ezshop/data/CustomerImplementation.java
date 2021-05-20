package it.polito.ezshop.data;

public class CustomerImplementation implements Customer {
    private String name;
    private String customerCard;
    private Integer id;
    private Integer points;

    public CustomerImplementation(String name, Integer id, Integer points, String customerCard){
        this.id = id;
        this.name = name;
        this.points = points;
        this.customerCard=null;
        if(customerCard!=null && customerCard.matches("\\d{10}")) {
            this.customerCard = customerCard;
        }
    }

    @Override
    public String getCustomerName() {
        return name;
    }

    @Override
    public void setCustomerName(String customerName) {
        this.name = customerName;
    }

    @Override
    public String getCustomerCard() {
        return customerCard;
    }

    @Override
    public void setCustomerCard(String customerCard) {
        this.customerCard = customerCard;
    }

    @Override
    public Integer getId() {
        return id;
    }

    @Override
    public void setId(Integer id) {
        this.id = id;
    }

    @Override
    public Integer getPoints() {
        return points;
    }

    @Override
    public void setPoints(Integer points) {
        this.points = points;
    }

}
