package it.polito.ezshop.EZShopTests;


import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;


import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

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
    public void testValidateCard(){
        assertFalse(EZShop.validateCard("5333171033425866"));
        assertFalse(EZShop.validateCard(""));
        assertFalse(EZShop.validateCard(null));
    }

    @Test
    public void TestUserAPIs(){

        EZShop ez = new EZShop();

        //invalid role exception
        assertThrows( InvalidRoleException.class, ()->{ez.createUser("john doe", "abc", null);});
        assertThrows( InvalidRoleException.class, ()->{ez.createUser("john doe", "abc", "x");});
        // invalid username exception
        assertThrows( InvalidUsernameException.class, ()->{ez.createUser("", "abc", "Cashier");});
        assertThrows( InvalidUsernameException.class, ()->{ez.createUser(null, "abc", "ShopDirector");});
        // InvalidPasswordException
        assertThrows( InvalidPasswordException.class, ()->{ez.createUser("john doe", null, "Cashier");});
        assertThrows( InvalidPasswordException.class, ()->{ez.createUser("john doe", "", "ShopDirector");});

        // testing scenario: user already present
        Integer id=null;
        try{
            id = ez.createUser("alessio", "abc", "Administrator");
            assertEquals(-1, (int) id);
        }catch(Exception e){
            fail("Exception thrown when not expected");
        }


        // Creation Successful
        try{
            id = ez.createUser("Sandy Brown", "abc", "Cashier");
            assertNotEquals(-1, (int) id);
        }catch(Exception e){
            fail("Exception thrown when not expected");
        }

        // User login failed, username empty or null
        assertThrows(InvalidUsernameException.class, ()->{ ez.login("", "789");});
        assertThrows(InvalidUsernameException.class, ()->{ ez.login(null, "789");});
        // User login failed, username empty or null
        try{
            assertNull(ez.login("dniele", "789"));
        }catch(Exception e){
            fail("should have not thrown any exception, but simply returned null");
        }

        //User Login Failed, password null or empty
        assertThrows(InvalidPasswordException.class, ()->{ ez.login("alessio", "");});
        assertThrows(InvalidPasswordException.class, ()->{ ez.login("alessio", null);});


        //User Login Successful
        try {
            ez.login("daniele", "789");
        }catch(Exception e){
            fail("User Should be able to login");
        }

        // updating User Rights, success
        try{
            assertTrue(ez.updateUserRights(id, "ShopManager"));
        }catch(Exception e){
            System.out.println("Exception catched:"+ e);
            fail("rights should have been updated");
        }

        //User Login Failed, wrong Password
        try{
            assertNull(ez.login(ez.getUser(id).getUsername(), "wrongPassword"));
        }catch(Exception e){

            fail("should have not thrown any exception, but simply returned null");
        }

        //User Login Failed, wrong Password
        try{
            assertNull(ez.login(ez.getUser(id).getUsername(), "wrongPassword"));
        }catch(Exception e){
            fail("should have not thrown any exception, but simply returned null");
        }

        // update user rights failed, invalid Role exception
        Integer finalId = id;
        assertThrows(InvalidRoleException.class, () -> {ez.updateUserRights(finalId, "x");});

        // gettingUserList, successful
        try{
            List<User> list = ez.getAllUsers();
            Integer finalId1 = id;
            // userList should cointain the just added user
            assertTrue(list.stream().anyMatch((x)-> x.getId().equals(finalId1)));
        }catch (Exception e){
            fail("should have been able to retrieve all users");
        }

        //get user, success
        try{
            ez.getUser(id);
        }catch(Exception e){
            fail("Should have been able to retrieve the user");
        }

        // still missing: unauthorized exception both null and "alessio" for getUser


        //User Logout successful
        assertTrue(ez.logout());
        // user logout failed
        assertFalse(ez.logout());

        // getting User list failure because loggeduser==null
        assertThrows(UnauthorizedException.class, ez::getAllUsers);

        // getting user list, failure because logged user's role!=administrator
        try{
            ez.login("alessio", "456");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        // update user rights failed, invalid Role exception
        Integer finalId8 = id;
        assertThrows(UnauthorizedException.class, () -> {ez.updateUserRights(finalId8, "x");});



        // deleting user, failed because of invalid permissions
        Integer finalId2 = id;
        assertThrows(UnauthorizedException.class, ()-> ez.deleteUser(finalId2) );

        // delete user, failed because id is invalid
        try{
            ez.login("daniele", "789");

        }catch(Exception e){
            fail("Should have been able to login, exceptions should have been the expected ones");
        }

        // updating user rights to non existent ones, gives an exception
        Integer finalId3 = id;
        assertThrows(InvalidRoleException.class, () -> ez.updateUserRights(finalId3, "ShopDirector"));

        // delete user, success
        try{
            assertTrue(ez.deleteUser(id));
            assertFalse(ez.deleteUser(id));
        }catch(Exception e){
            System.out.println("Exception Catched: " + e);
            fail("should have been able to delete the selected user");
        }


        try{
            // get user id fails since user doesn't exist anymore
            assertNull(ez.getUser(id));
            // delete user, invalid user id, no user associated to it
            assertFalse(ez.deleteUser(id));
            // updating user Rights failed, invalid id
            assertFalse(ez.updateUserRights(id, "ShopManager"));
        }
        catch(Exception e){
            System.out.println("Catched exception: " + e);
            fail("Should have not thrown exceptions in any of them");
        }
        // trying to delete a user giving id less than or equal to zero or empty gives an exception
        assertThrows(InvalidUserIdException.class, ()->ez.deleteUser(0));
        assertThrows(InvalidUserIdException.class, ()->ez.deleteUser(null));


    }
}
