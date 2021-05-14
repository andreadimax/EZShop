package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static org.junit.Assert.*;

public class EZShopTests {
    @Test
    public void testAssignId(){
        HashMap<Integer, Integer> test_set = new HashMap<Integer, Integer>();
        test_set.put(1,1);
        test_set.put(2,2);
        test_set.put(3,3);

        Integer res = EZShop.assignId(test_set.keySet());
        assertTrue(res != 1 && res != 2 && res != 3 && res != 0);
        res = EZShop.assignId(null);
        assertTrue(res == -1);

        for(int i = 4; i<=100; i++){
            test_set.put(i,i);
        }
        res = EZShop.assignId(test_set.keySet());
        assertTrue(res == -1);

    }

    @Test
    public void testBarcodeIsValid(){

        /* ----------- NOT VALID ----------- */
        assertFalse(EZShop.barcodeIsValid("aaaaaaaaaaa"));

        assertFalse(EZShop.barcodeIsValid("aaaaaaaaaaaaaaa"));

        assertFalse(EZShop.barcodeIsValid("aaa"));

        assertFalse(EZShop.barcodeIsValid("thisIsAVeryVeryVeryVeryLongString"));

        assertFalse(EZShop.barcodeIsValid(null));

        assertFalse(EZShop.barcodeIsValid(""));

        /* ----------- VALID ----------- */
        assertTrue(EZShop.barcodeIsValid("000000000000"));

        assertTrue(EZShop.barcodeIsValid("0000000000000"));

        assertTrue(EZShop.barcodeIsValid("00000000000000"));

        assertTrue(EZShop.barcodeIsValid("8004263697047"));

        /* ----------- NOT VALID ----------- */
        assertFalse(EZShop.barcodeIsValid("000000000001"));

        assertFalse(EZShop.barcodeIsValid("5554673697047"));
    }

    @Test
    public void testWriteJarrayToFIle(){
        JSONArray jArray = new JSONArray(), array  = new JSONArray();
        JSONObject obj = new JSONObject();
        JSONParser parser = new JSONParser();
        obj.put("name", "test");
        obj.put("value", "2");
        array.add(obj);

        JSONObject empty_obj = new JSONObject();

        array.add(empty_obj);

        assertFalse(EZShop.writejArrayToFile("", array));

        assertFalse(EZShop.writejArrayToFile(null, array));

        assertFalse(EZShop.writejArrayToFile("src/data/file.json", null));

        /* ----------- Correctness of data ----------- */
        EZShop.writejArrayToFile("./test.json", array);

        //Reading data
        try {
            jArray = (JSONArray) parser.parse(new FileReader("./test.json"));
        }
        catch (FileNotFoundException f){
            f.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        //Checking...
        assertTrue(((JSONObject) jArray.get(0)).get("name").equals("test"));
        assertTrue(((JSONObject) jArray.get(0)).get("value").equals("2"));
        assertTrue(((JSONObject) jArray.get(1)).isEmpty());


    }

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
    
    @Test
    public void testProduct(){
        ProductType product = new ProductTypeImplementation(5, "000000000000", "mele", 4.0, null);

        int id = product.getId();
        String barCode = product.getBarCode();
        String description = product.getProductDescription();
        Double price = product.getPricePerUnit();
        String note = product.getNote();

        assertEquals( 5, id);
        assertEquals( "000000000000", barCode);
        assertEquals( "mele", description);
        assertEquals( (Double) 4.0, price);
        assertEquals(null, note);

        product.setId(10);
        assertEquals( 10, (int) product.getId());
        product.setBarCode("800123739455");
        assertEquals( "800123739455", product.getBarCode());
        product.setProductDescription("pere");
        assertEquals( "pere", product.getProductDescription());
        product.setPricePerUnit(5.0);
        assertEquals( (Double) 5.0, product.getPricePerUnit());
        product.setNote("test");
        assertEquals( "test", product.getNote());
        product.setLocation("location5");
        assertEquals( "location5", product.getLocation());

        assertFalse(((ProductTypeImplementation) product).changeQuantity(null));
        assertTrue(((ProductTypeImplementation) product).changeQuantity(10));
        assertTrue(((ProductTypeImplementation) product).changeQuantity(-5));

        /* ---------- WHITE BOX TESTS ---------- */
        /* changeQuantity() */
        //Covering decision amount<0 && availableQty <- amount
        ((ProductTypeImplementation) product).changeQuantity(-5);   //Change quantity to 0
        assertFalse(((ProductTypeImplementation) product).changeQuantity(-5));

    }
}
