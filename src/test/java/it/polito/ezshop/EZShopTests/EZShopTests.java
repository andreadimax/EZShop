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

    //  REQUIRES THE SALE AND RETURN TRANSACTIONS TO BE ALREADY TESTED, IF TESTED NOW WE CANNOT BE SURE WHERE THE ERROR ACTUALLY IS
    @Test
    public void testPaymentRelatedAPIs(){
        EZShop ez = new EZShop();
        /*
            ReceiveCredit Card payment
            records the payment of a sale transaction with cash and returns the change (if present).
            updates balance of the system changes
         */
        try{
            // success=> return the change (cash - sale price) 0 => balance changes
            // return -1   if the cash is not enough and if there are problems with the db, no changes in balance
            // @throws InvalidTransactionIdException if the  number is less than or equal to 0 or if it is null
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            // @throws InvalidPaymentException if the cash is less than or equal to 0
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        // receiveCashPayment
        try{
            // updates balance
            // operation successful => true
            //  if the sale does not exists, => false
            //  if the card has not enough money, => false
            //  if the card is not registered,  => false
            //  if there is some problem with the db connection => false
            // @throws InvalidTransactionIdException if the sale number is less than or equal to 0 or if it is null
            // @throws InvalidCreditCardException if the credit card number is empty, null or if luhn algorithm does not validate the credit card
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        // returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException;
        try{
            // receiveCreditCardPayment(Integer transactionId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException;
            // affects the balance
            // return  the money returned to the customer
            // success => return  the money returned to the customer
            // return -1  if the return transaction is not ended
            // return if it does not exist,
            // if there is a problem with the db
            // @throws InvalidTransactionIdException if the return id is less than or equal to 0
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }



        // public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException;
        try{
            // This method record the payment of a return transaction to a credit card.
            // The credit card number validity should be checked. It should follow the luhn algorithm.
            // The credit card should be registered and its balance will be affected.
            // This method affects the balance of the system.
            // It can be invoked only after a user with role "Administrator", "ShopManager" or "Cashier" is logged in.
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        try{
            // @param returnId the id of the return transaction
            // @param creditCard the credit card number of the customer

            // return  the money returned to the customer
            // return  -1  if the return transaction is not ended,
            // return -1 if it does not exist,
            // return -1 if the card is not registered,
            // return -1 if there is a problem with the db

            // @throws InvalidTransactionIdException if the return id is less than or equal to 0
            // @throws InvalidCreditCardException if the credit card number is empty, null or if luhn algorithm does not validate the credit card
            //throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

    }

    @Test
    public void TestProductTypeAPIs(){

        EZShop ez = new EZShop();
        Integer id=null;
//_______________________________________________________________________
        //Tests CreateProductType
        try{
            ez.login("daniele", "789");
            //     * @throws InvalidProductDescriptionException if the product description is null or empty
            assertThrows(InvalidProductDescriptionException.class, ()->ez.createProductType(null, "8004263697047", 2.99, "simple note"));
            assertThrows(InvalidProductDescriptionException.class, ()->ez.createProductType("", "8004263697047", 2.99, "simple note"));

            //     * @throws InvalidProductCodeException if the product code is null or empty, if it is not a number or if it is not a valid barcode
            assertThrows(InvalidProductCodeException.class, ()->ez.createProductType("description productype", "", 2.99, "simple note"));
            assertThrows(InvalidProductCodeException.class, ()->ez.createProductType("description productype", null, 2.99, "simple note"));
            assertThrows(InvalidProductCodeException.class, ()->ez.createProductType("description productype", "abc", 2.99, "simple note"));

            //     * @throws InvalidPricePerUnitException if the price per unit si less than or equal to 0
            assertThrows(InvalidPricePerUnitException.class, ()->ez.createProductType("description productype", "abc", 0, "simple note"));
            assertThrows(InvalidPricePerUnitException.class, ()->ez.createProductType("description productype", "abc", -1, "simple note"));

            //     * @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->{ez.createProductType("description productype", "8004263697047", 2.99, "simple note");});
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->{ez.createProductType("description productype", "8004263697047", 2.99, "simple note");});
            ez.logout();

            // successful => return The unique identifier of the new product type ( > 0 ).
            ez.login("daniele", "789");
            id=ez.createProductType("description productype", "8004263697047", 2.99, "simple note");
            assertTrue(id!=null && id>0);
            // -1 if there is an error while saving the product type or if it exists a product with the same barcode
            assertEquals(-1, (int)ez.createProductType("description productype", "8004263697047", 2.99, "simple note"));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }


        //___________________________________________________________
        //update Product
        try{
            // false if no products with given product id
            // selecting 1 free id
            int i=0;
            int found=0;
            while(i<9999 && found==0){
                i++;
                int finalI = i;
                if(ez.getAllProductTypes().stream().noneMatch(x-> x.getId()!= finalI))found=1;
            }
            assertFalse(ez.updateProduct(i,"newDescription", "526374859247", 3.99, "newnote2"));
            // false if another product already has the same barcode
            assertFalse(ez.updateProduct(id,"newDescription", "8004263697047", 3.99, "newnote2"));
            // throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
            assertThrows(InvalidProductIdException.class, ()-> ez.updateProduct(0,"newDescription", "4673628643780", 3.99, "newnote2"));
            assertThrows(InvalidProductIdException.class, ()-> ez.updateProduct(-1,"newDescription", "4673628643780", 3.99, "newnote2"));
            // throws InvalidProductDescriptionException if the product description is null or empty
            Integer finalId1 = id;
            assertThrows(InvalidProductDescriptionException.class, ()-> ez.updateProduct(finalId1,"", "4673628643780", 3.99, "newnote2"));
            assertThrows(InvalidProductDescriptionException.class, ()-> ez.updateProduct(finalId1,null, "4673628643780", 3.99, "newnote2"));
            // throws InvalidProductCodeException if the product code is null or empty, if it is not a number or if it is not a valid barcode
            assertThrows(InvalidProductCodeException.class, ()-> ez.updateProduct(finalId1,"description", null, 3.99, "newnote2"));
            assertThrows(InvalidProductCodeException.class, ()-> ez.updateProduct(finalId1,"description", "", 3.99, "newnote2"));
            assertThrows(InvalidProductCodeException.class, ()-> ez.updateProduct(finalId1,"description", "a1", 3.99, "newnote2"));
            assertThrows(InvalidProductCodeException.class, ()-> ez.updateProduct(finalId1,"description", "123453563568", 3.99, "newnote2"));
            // throws InvalidPricePerUnitException if the price per unit si less than or equal to 0
            assertThrows(InvalidPricePerUnitException.class, ()-> ez.updateProduct(finalId1,"description", "4673628643780", 0, "newnote2"));
            assertThrows(InvalidPricePerUnitException.class, ()-> ez.updateProduct(finalId1,"description", "4673628643780", -1, "newnote2"));
            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()-> ez.updateProduct(finalId1,"description", "4673628643780", -1, "newnote2"));
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()-> ez.updateProduct(finalId1,"description", "4673628643780", -1, "newnote2"));
            ez.login("daniele", "789");
            // return  true if the update is successful
            assertTrue(ez.updateProduct(id,"newDescription", "4673628643780", 3.99, "newnote2"));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }




        //________________________________________________________________




        // delete ProductType
        // throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
        assertThrows(InvalidProductIdException.class, ()->ez.deleteProductType(0));
        assertThrows(InvalidProductIdException.class, ()->ez.deleteProductType(-1));
        assertThrows(InvalidProductIdException.class, ()->ez.deleteProductType(null));

        // return true if the product was deleted, false otherwise
        try{
            assertTrue(ez.deleteProductType(id));
            assertFalse(ez.deleteProductType(id));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        // throws UnauthorizedException if there is no logged user
        ez.logout();
        Integer finalId = id;
        assertThrows(UnauthorizedException.class, ()->ez.deleteProductType(finalId));

        // getAllProductTypes
        try{
            ez.login("daniele", "789");
            // return a list containing all saved product types
            Integer finalId2 = id;
            assertTrue(ez.getAllProductTypes().stream().noneMatch(x-> finalId2.equals(x.getId())));
            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ez::getAllProductTypes);
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ez::getAllProductTypes);
            ez.logout();
            ez.login("daniele", "789");
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }


        // getProductTypeByBarCode
        try{
            id=ez.createProductType("description productype", "8004263697047", 2.99, "simple note");
            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.getProductTypeByBarCode("8004263697047"));
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.getProductTypeByBarCode("8004263697047"));
            // throws InvalidProductCodeException if barCode is not a valid bar code, if is it empty or if it is null
            ez.login("daniele", "789");
            assertThrows(InvalidProductCodeException.class, ()->ez.getProductTypeByBarCode("800426369a047"));
            assertThrows(InvalidProductCodeException.class, ()->ez.getProductTypeByBarCode(""));
            assertThrows(InvalidProductCodeException.class, ()->ez.getProductTypeByBarCode(null));
            // return the product type with given barCode if present, null otherwise
            assertNotNull(ez.getProductTypeByBarCode("8004263697047"));
            ez.deleteProductType(id);
            assertNull(ez.getProductTypeByBarCode("8004263697047"));

        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }


        // getProductTypesByDescription
        try{
            id=ez.createProductType("description productype", "8004263697047", 2.99, "simple note");
            // Null should be considered as the empty string
            assertTrue(ez.getProductTypesByDescription("").stream().anyMatch(x->"description productype".equals(x.getProductDescription())));
            assertTrue(ez.getProductTypesByDescription(null).stream().anyMatch(x->"description productype".equals(x.getProductDescription())));
            // return a list of products containing the requested string in their description
            assertTrue(ez.getProductTypesByDescription("description productype").stream().anyMatch(x->"description productype".equals(x.getProductDescription())));
            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.getProductTypesByDescription("description productype"));
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.getProductTypesByDescription("description productype"));
            ez.login("daniele", "789");
            ez.deleteProductType(id);

        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        //updatePosition
        // The position has the following format : <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>
        // The position should be unique null or empty
        try{
            id=ez.createProductType("description productype", "8004263697047", 2.99, "simple note");
            // return true if the update was successful
            assertTrue(ez.updatePosition(id,"232-abs-53"));

            // If is null or empty it should reset the position of given product type.
            assertTrue(ez.updatePosition(id,""));
            assertTrue(ez.updateQuantity(id,+10)); // used to verify the object has a position
            assertTrue(ez.updatePosition(id,null));
            assertTrue(ez.updateQuantity(id,-10)); // used to verify the object has a position



            // false if the product does not exist
            int i=0;
            int found=0;
            while(i<9999 && found==0){
                i++;
                int finalI = i;
                if(ez.getAllProductTypes().stream().noneMatch(x-> x.getId()!= finalI))found=1;
            }
            assertFalse(ez.updatePosition(i,"324-ahfj-421"));
            assertFalse(ez.updatePosition(i,"324-abdu-421"));
            // false if <newPos> is already assigned to another product
            assertTrue(ez.updatePosition(id,"324-abdc-421"));
            int id2=ez.createProductType("description2", "765343443456", 5.99,"note2");
            assertFalse(ez.updatePosition(id2,"324-abdc-421"));
            ez.deleteProductType(id2);


            // throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
            assertThrows(InvalidProductIdException.class, ()->ez.updatePosition(0, "654-add-473"));
            assertThrows(InvalidProductIdException.class, ()->ez.updatePosition(-1, "654-add-473"));
            assertThrows(InvalidProductIdException.class, ()->ez.updatePosition(null, "654-add-473"));
            // throws InvalidLocationException if the product location is in an invalid format (not <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber>)
            Integer finalId4 = id;
            assertThrows(InvalidLocationException.class, ()->ez.updatePosition(finalId4,"a-aa-232"));
            assertThrows(InvalidLocationException.class, ()->ez.updatePosition(finalId4, "654-a2dd-473"));

            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.deleteProductType(id);
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }


        // updateQuantity
        try{
            id=ez.createProductType("description productype", "8004263697047", 2.99, "simple note");
            ez.updatePosition(id, "232-adh-95");
            // throws InvalidProductIdException if the product id is less than or equal to 0 or if it is null
            assertThrows(InvalidProductIdException.class, ()->ez.updateQuantity(-1, 1));
            assertThrows(InvalidProductIdException.class, ()->ez.updateQuantity(0, 1));
            assertThrows(InvalidProductIdException.class, ()->ez.updateQuantity(null, 1));
            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            Integer finalId3 = id;
            assertThrows(UnauthorizedException.class, ()->ez.updateQuantity(finalId3, 1));
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.updateQuantity(finalId3, 1));
            ez.login("daniele", "789");
            // false if the product does not exists, if <toBeAdded> is negative and the resulting amount would be
            int i=0;
            int found=0;
            while(i<9999 && found==0){
                i++;
                int finalI = i;
                if(ez.getAllProductTypes().stream().noneMatch(x-> x.getId()!= finalI))found=1;
            }
            assertFalse(ez.updateQuantity(i, 1));
            // false if <toBeAdded> is negative and the resulting amount would be negative
            assertFalse(ez.updateQuantity(id, -500));
            // return  true if the update was successful
            assertTrue(ez.updateQuantity(id, +500));
            // false if the product type has not an assigned location.
            ez.updatePosition(id, "");
            assertTrue(ez.updateQuantity(id, +500));
            ez.deleteProductType(id);

        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
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
            ez.login("marina blue", "456");
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
