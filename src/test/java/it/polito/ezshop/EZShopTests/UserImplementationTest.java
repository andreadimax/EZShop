package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.User;
import it.polito.ezshop.data.UserImplementation;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class UserImplementationTest {


    @Test
    public void testUser(){
        User user = new UserImplementation(5, "andrea", "123", "Cashier");

        int id = user.getId();
        String username = user.getUsername();
        String pass = user.getPassword();
        String role = user.getRole();

        assertEquals( 5, id);
        assertEquals( "andrea", username);
        assertEquals( "123", pass);
        assertEquals( "Cashier", role);

        user.setRole("Administrator");
        assertEquals( "Administrator", user.getRole());
        user.setPassword("456");
        assertEquals( "456", user.getPassword());
        user.setUsername("alessio");
        assertEquals( "alessio", user.getUsername());
        user.setId(20);
        assertEquals( 20, (int) user.getId());

    }
}
