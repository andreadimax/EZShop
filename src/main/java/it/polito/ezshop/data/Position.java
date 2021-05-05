package it.polito.ezshop.data;

public class Position {
    private String position;
    private ProductType product;


    //CONSTRUCTORS
    public Position(String position, ProductType product){
        this.position=position;
        this.product=product;
    }

    // GETTERS AND SETTERS
    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }
}
