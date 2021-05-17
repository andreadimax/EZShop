package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.Customer;
import it.polito.ezshop.data.CustomerImplementation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class CustomerImplementationTest {


    @Test
    public void testCustomer(){
        Customer customer = new CustomerImplementation("andrea", 1, 5, null);

        int id = customer.getId();
        String name = customer.getCustomerName();
        String card = customer.getCustomerCard();
        int points = customer.getPoints();

        assertEquals( 1, id);
        assertEquals( "andrea", name);
        assertEquals( 5, points);
        assertEquals( null, card);

        customer.setId(10);
        assertEquals( 10, (int) customer.getId());
        customer.setCustomerName("alessio");
        assertEquals( "alessio", customer.getCustomerName());
        customer.setCustomerCard("9000648221");
        assertEquals( "9000648221", customer.getCustomerCard());
        customer.setPoints(-3);
        assertEquals( -3, (int) customer.getPoints());

    }

}
