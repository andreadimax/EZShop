package it.polito.ezshop.EZShopTests;


import it.polito.ezshop.data.*;
import it.polito.ezshop.exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Before;
import org.junit.Test;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static java.lang.Math.abs;
import static org.junit.Assert.*;

public class EZShopTests {
    static EZShop ez;
    @Before
    public void setup(){
        ez = new EZShop();
        ez.reset();
        try{
            ez.createUser("daniele", "789", "Administrator");
            ez.createUser("alessio", "456", "Administrator");
            ez.createUser("andrea", "123", "Administrator");
            ez.createUser("damiana diamond", "abc", "ShopManager");
            ez.createUser("marina blue", "abc", "Cashier");
        }catch(Exception e){
            e.printStackTrace();
        }

    }
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
        } catch (IOException | ParseException f){
            f.printStackTrace();
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
    public void testResetAPI(){
        try{

            ez.login("daniele", "789");
            // storing 1 product
            int pid = ez.createProductType("description", "789657485759", 10, "note");
            ez.updateQuantity(pid, 1000);
            ez.updatePosition(pid, "673-fhsa-538");

            //storing 1 debit and 1 credit
            ez.recordBalanceUpdate(-300);
            ez.recordBalanceUpdate(300);

            // cannot store a new user since they are never reset
            User u1 = ez.getAllUsers().get(0);

            // create a new customer
            Integer cid = ez.defineCustomer("pippo");
            CustomerImplementation c1 = (CustomerImplementation) ez.getCustomer(cid);

            //store a saleTransaction
            Integer sid = ez.startSaleTransaction();
            ez.addProductToSale(sid, "789657485759", 5);
            ez.endSaleTransaction(sid);
            ez.receiveCashPayment(sid,500);

            // store a returnTransaction
            Integer rid = ez.startReturnTransaction(sid);
            ez.returnCashPayment(rid);
            ez.endReturnTransaction(rid, true);

            // store a new order
            Integer oid = ez.payOrderFor("789657485759",5,10);
            ez.recordOrderArrival(oid);

            // storing balance and number of debits and credits
            double previousBalance = ez.computeBalance();
            int oldSize = ez.getCreditsAndDebits(null, null).size();

            //__________________________________________________________________________
            ez = null; // removing old instance of ezshop => same as a reboot

            ez = new EZShop();
            ez.login("daniele", "789");

            // checking balance
            assertTrue(ez.computeBalance()-previousBalance<0.001);
            // checking product
            ProductType p;
            assertNotNull(p=ez.getProductTypeByBarCode("789657485759"));
            int qty = p.getQuantity();
            assertEquals(1000, qty);
            assertEquals("789657485759", p.getBarCode());
            assertEquals("673-fhsa-538", p.getLocation());
            double price = p.getPricePerUnit();
            assertTrue( abs(10- price) < 0.001);
            assertEquals("note", p.getNote());
            int new_pid = p.getId();
            assertEquals(pid, new_pid);
            assertEquals(p.getProductDescription(), "description" );



            // checking Users
            int userCount = ez.getAllUsers().size();
            User u;
            assertNotNull(u=ez.getUser(u1.getId()));
            assertEquals(u1.getId(),u.getId());
            assertEquals(u1.getUsername(), u.getUsername());
            assertEquals(u1.getPassword(), u.getPassword());
            assertEquals(u1.getRole(), u.getRole());

            // checking Customers
            assertNotNull(ez.getCustomer(cid));

            //checking total of balance operations
            assertEquals(ez.getCreditsAndDebits(null, null).size(), oldSize);

            //________________________________________________________________
            //resetting and checking deletion occurred
            ez.reset();
            ez.createUser("daniele","789","Administrator");
            ez.login("daniele","789");


            // checking balance
            assertTrue(ez.computeBalance()<=0.001);
            // checking product
            assertNull(ez.getProductTypeByBarCode("789657485759"));

            // checking Users
            assertEquals(ez.getAllUsers().size(), 1);

            // checking Customers
            assertNull(ez.getCustomer(cid));

            //checking total of balance operations
            assertEquals(ez.getCreditsAndDebits(null, null).size(), 0);

            // rebooting ezshop, should show no sign of the old values
            ez = new EZShop();
            ez.login("daniele", "789");
            // checking balance
            assertTrue(ez.computeBalance()<=0.001);
            // checking product
            assertNull(ez.getProductTypeByBarCode("789657485759"));

            // checking Users
            assertEquals(ez.getAllUsers().size(), 1);

            // checking Customers
            assertNull(ez.getCustomer(cid));

            //checking total of balance operations
            assertEquals(ez.getCreditsAndDebits(null, null).size(), 0);


        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
    }

    @ Test
    public void testRFIDsAPIs(){

        try {
            ez.login("daniele", "789");
        }catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }
        // storing 1 product
        int pid= 0;
        int pid1=0;
        try{
            pid = ez.createProductType("description", "1234324534531", 10, "note");
            //ez.updateQuantity(pid, 1000);
            pid1 = ez.createProductType("description", "2837948739840", 10, "note");
            ez.updatePosition(pid, "673-fhsa-538");
            ez.updatePosition(pid1, "673-fhba-538");
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }

        /*
         * public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException, InvalidRFIDException)
         */
        Integer oid=0;
        Integer oid1=0;
        ProductType p=null;
        ProductType p1=null;

        try{
            oid = ez.issueOrder("1234324534531",9,10);
            ez.recordBalanceUpdate(100);
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }
        try{
            // @return  false if the order does not exist or if it was not in an ORDERED/COMPLETED state
            //assertFalse(ez.recordOrderArrivalRFID(oid, "0000000000")); // oid is not yet ordered or completed
            assertTrue(ez.payOrder(oid)); // now it is payed
            assertFalse(ez.recordOrderArrivalRFID(50, "0000000000")); // order 50 doesn't exist

        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }
        try{
            // @throws InvalidLocationException if the ordered product type has not an assigned location.
            ez.updatePosition(pid, ""); // set position to null
            Integer finalOid4 = oid;
            assertThrows(InvalidLocationException.class, ()-> ez.recordOrderArrivalRFID(finalOid4, "0000000000"));
            ez.updatePosition(pid, "673-fhsa-538"); // reinsert the correct position
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }
        try{
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            Integer finalOid = oid;
            assertThrows(UnauthorizedException.class, ()->ez.recordOrderArrivalRFID(finalOid, "0000000000"));
            ez.login("marina blue", "abc");
            Integer finalOid1 = oid;
            assertThrows(UnauthorizedException.class, ()->ez.recordOrderArrivalRFID(finalOid1, "0000000000"));
            // later i will test that with shopmanager and director it does not throw anything
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }

            // @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
        assertThrows(InvalidOrderIdException.class, ()-> ez.recordOrderArrivalRFID(0, "0000000000"));
            assertThrows(InvalidOrderIdException.class, ()-> ez.recordOrderArrivalRFID(-1, "0000000000"));
            assertThrows(InvalidOrderIdException.class, ()-> ez.recordOrderArrivalRFID(null, "0000000000"));

        try{
            // @throws InvalidRFIDException if the RFID has invalid format
            ez.login("damiana diamond", "abc");
            Integer finalOid2 = oid;
            assertThrows(InvalidRFIDException.class, ()->ez.recordOrderArrivalRFID(finalOid2, "000000a000"));
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }

        try{
            // @return  true if the operation was successful, verifies also that shopmanager is authorized
            assertTrue(ez.recordOrderArrivalRFID(oid, "0000000000"));

            // verify that quantity of product has changed
            p = ez.getProductTypeByBarCode("1234324534531");
            assertEquals( (Integer) 9, p.getQuantity());
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }
        try{
            // making a second order
            ez.login("daniele", "789");
            ez.recordBalanceUpdate(100);
            oid1 = ez.payOrderFor("2837948739840",9,10);
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }


            // @throws InvalidRFIDException if the RFIDis not unique
        Integer finalOid3 = oid1;
        assertThrows(InvalidRFIDException.class, ()->ez.recordOrderArrivalRFID(finalOid3, "0000000008"));


        try{
            // @return  true if the operation was successful, verifies also that director is authorized
            assertTrue(ez.recordOrderArrivalRFID(oid1, "0000000009"));

            // verify that quantity of product has changed
            p1 = ez.getProductTypeByBarCode("2837948739840");
            assertEquals( (Integer) 9, p1.getQuantity());
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }


        /*
         * public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException)
         */
        // SITUATION:
        // we have 2 products in inventory, p and p1, both have quantity 9
        // p goes from 0 to 8 in RFID, p1 is in range [..009, ...0017]
        Integer sid=null;
        Integer sid1 = null;
        try{
                p = ez.getProductTypeByBarCode("1234324534531");
                p1 = ez.getProductTypeByBarCode("2837948739840");
              sid = ez.startSaleTransaction();
            // @return  true if the operation is successful
              assertTrue(ez.addProductToSaleRFID(sid, "0000000008")); // 8 is the last RFID value for p, the operation is successful

            // checking that the quantity of p1 has decreased
            assertEquals((Integer)8,p.getQuantity());



              // @return  false   if the RFID does not exist,
            assertFalse(ez.addProductToSaleRFID(sid, "0000000018")); // 18 should be the first RFID which is not assigned
              // @return  false   if the transaction id does not identify a started and open transaction.
            assertFalse(ez.addProductToSaleRFID(50, "0000000016"));
            // @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
            assertThrows(InvalidTransactionIdException.class, ()-> ez.addProductToSaleRFID(0, "0000000016"));
            assertThrows(InvalidTransactionIdException.class, ()-> ez.addProductToSaleRFID(-1, "0000000016"));
            assertThrows(InvalidTransactionIdException.class, ()-> ez.addProductToSaleRFID(null, "0000000016"));
            // @throws InvalidRFIDException if the RFID code is empty, null or invalid
            Integer finalSid = sid;
            assertThrows(InvalidRFIDException.class, ()-> ez.addProductToSaleRFID(finalSid, ""));
            assertThrows(InvalidRFIDException.class, ()-> ez.addProductToSaleRFID(finalSid, null));
            assertThrows(InvalidRFIDException.class, ()-> ez.addProductToSaleRFID(finalSid, "00000a0000"));
              // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()-> ez.addProductToSaleRFID(finalSid, "0000000016"));
            ez.login("marina blue", "abc");
            assertTrue(ez.addProductToSaleRFID(finalSid, "0000000017")); // this should be the last product to which we added the rfid

            // checking that the quantity of p1 has decreased
            assertEquals((Integer)8,p1.getQuantity());

            ez.login("damiana diamond", "abc");
            assertTrue(ez.addProductToSaleRFID(finalSid, "0000000016"));

            // checking that the quantity of p1 has decreased
            assertEquals((Integer)7,p1.getQuantity());

            ez.login("daniele", "789");
            assertTrue(ez.addProductToSaleRFID(finalSid, "0000000015"));

            // checking that the quantity of p1 has decreased
            assertEquals((Integer)6,p1.getQuantity());

            // concluding transaction sid while adding products to it, it will be used later for the return
            assertTrue(ez.addProductToSaleRFID(sid, "0000000007"));
            // checking that the quantity of p has decreased
            assertEquals((Integer)7,p.getQuantity());

            assertTrue(ez.addProductToSaleRFID(sid, "0000000006"));
            // checking that the quantity of p1 has decreased
            assertEquals((Integer)6,p.getQuantity());

            ez.endSaleTransaction(sid);
            ez.receiveCashPayment(sid,500);
        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }

        /*
         * public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException;
         */
        // SITUATION:
        // sid is closed with products RFIDs 6,7,8,15,16,17

        // in this test we open a sale transaction sid1 and then
        // we add and remove 3 products (12, 13, 14) from it using the 3 user roles
        try{

            sid1 = ez.startSaleTransaction();
            // here we use sid1, which comes from the previous test

            // @return  false   if the "product code" does not exist(they mean RFID here)
            assertFalse(ez.deleteProductFromSaleRFID(sid1, "0000000018")); // i use one RFID I have never associated to anything before

            // @return false if the transaction id does not identify a started and open transaction.
            assertFalse(ez.deleteProductFromSaleRFID(sid, "0000000016")); //sid is already closed

            // @throws InvalidTransactionIdException if the transaction id less than or equal to 0 or if it is null
            assertThrows(InvalidTransactionIdException.class, ()->ez.deleteProductFromSaleRFID(0, "0000000014"));
            assertThrows(InvalidTransactionIdException.class, ()->ez.deleteProductFromSaleRFID(-1, "0000000014"));
            assertThrows(InvalidTransactionIdException.class, ()->ez.deleteProductFromSaleRFID(null, "0000000014"));

            // @throws InvalidRFIDException if the RFID is empty, null or invalid
            Integer finalSid1 = sid1;
            assertThrows(InvalidRFIDException.class, ()->ez.deleteProductFromSaleRFID(finalSid1, ""));
            assertThrows(InvalidRFIDException.class, ()->ez.deleteProductFromSaleRFID(finalSid1, null));
            assertThrows(InvalidRFIDException.class, ()->ez.deleteProductFromSaleRFID(finalSid1, "0a00000014"));

            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.deleteProductFromSaleRFID(finalSid1, "0000000014")); // no user logged

            // verifying permissions for the other user types
            //CASHIER
            ez.login("marina blue", "abc");
            ez.addProductToSaleRFID(finalSid1,"0000000014");
            ez.addProductToSaleRFID(finalSid1,"0000000013");
            ez.addProductToSaleRFID(finalSid1,"0000000012");
            assertTrue(ez.deleteProductFromSaleRFID(finalSid1, "0000000014"));
            // checking that the quantity of p1 has increased
            assertEquals((Integer)4,p1.getQuantity());

            // SHOPMANAGER
            ez.login("damiana diamond", "abc");
            assertTrue(ez.deleteProductFromSaleRFID(finalSid1, "0000000013"));
            // checking that the quantity of p1 has increased
            assertEquals((Integer)5,p1.getQuantity());

            // ADMINISTRATOR
            // @return  true if the operation is successful, in this case, the shopmanager has the rights to do the operation
            assertTrue(ez.deleteProductFromSaleRFID(sid1, "0000000012")); // i use one of the previous values rfids added to the sale
            // checking that the quantity of p1 has increased
            assertEquals((Integer)6,p1.getQuantity());

            // at this point there are no products in sid1 anymore and I simply close the transaction
            ez.endSaleTransaction(sid1);
            ez.receiveCashPayment(sid1,500);

        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }

        /*
         * public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException)
         */
        // SITUATION:
        // sid(=5) is closed with products RFIDs 6,7,8,15,16,17
        try{
            Integer rid = ez.startReturnTransaction(sid);
            ez.returnCashPayment(rid);
            ez.endReturnTransaction(rid, true);


            // @return  false if the the product to be returned does not exists,
            assertFalse(ez.returnProductRFID(rid,"0000000019")); // rfid never associated to any product
            // @return  false if it was not in the transaction,
            assertFalse(ez.returnProductRFID(rid,"0000000015")); // rfid was in sid1 not in sid
            assertFalse(ez.returnProductRFID(rid,"0000000001")); // rfid is assigned to a product never sold, is on the shelf
            // @return  false if the transaction does not exist
            assertFalse(ez.returnProductRFID(50,"0000000008"));
            // @throws InvalidTransactionIdException if the return id is less ther or equal to 0 or if it is null
            assertThrows(InvalidTransactionIdException.class, ()-> ez.returnProductRFID(-1,"0000000008"));
            assertThrows(InvalidTransactionIdException.class, ()-> ez.returnProductRFID(0,"0000000008"));
            assertThrows(InvalidTransactionIdException.class, ()-> ez.returnProductRFID(null,"0000000008"));
            // @throws InvalidRFIDException if the RFID is empty, null or invalid
            assertThrows(InvalidRFIDException.class, ()-> ez.returnProductRFID(rid,""));
            assertThrows(InvalidRFIDException.class, ()-> ez.returnProductRFID(rid,null));
            assertThrows(InvalidRFIDException.class, ()-> ez.returnProductRFID(rid,"0700000sa0"));
            // @throws UnauthorizedException if there is no logged user, for any other logged user it works
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.returnProductRFID(rid,"0000000008"));

            ez.login("marina blue", "abc");
            // @return  true if the operation is successful
            assertTrue(ez.returnProductRFID(rid,"0000000006")); // product we bought before in sid
            // this method should not update the product quantity
            assertEquals((Integer)6,p.getQuantity());

            ez.login("damiana diamond", "abc");
            // @return  true if the operation is successful
            assertTrue(ez.returnProductRFID(rid,"0000000008")); // product we bought before in sid
            // this method should not update the product quantity
            assertEquals((Integer)6,p.getQuantity());

            ez.login("daniele", "789");
            // @return  true if the operation is successful
            assertTrue(ez.returnProductRFID(rid,"0000000007")); // product we bought before in sid
            // this method should not update the product quantity
            assertEquals((Integer)6,p.getQuantity());


        }
        catch(Exception e){
            e.printStackTrace();
            fail("test failed");
        }
    }
    @Test
    public void testOrderAPIs(){
        //BalanceOperationImpl.setBalanceCounter(0);

        try {
            ez.login("alessio", "456");
        }catch(Exception e){
            fail("unable to login with credentials user: alessio  password: 456");
        }
        try {
            ez.recordBalanceUpdate(1000.0);
        }catch(Exception e){
            fail("unable to update Balance with credit operation");
        }
        int id = -1;
        int id2 = -1;
        double balance = 0;

        //---------------------------------------------------------
        //public Integer issueOrder(String productCode, int quantity, double pricePerUnit)
        try {
            ez.login("daniele", "789");
            // adding some Money to the balance
            assertTrue(ez.recordBalanceUpdate(13));
            System.out.println("BALANCE : " + ez.computeBalance());

            //Optional<ProductType> pOptional = ez.getAllProductTypes().stream().findFirst();
            //assertTrue(pOptional.isPresent());
            //String pCode = pOptional.get().getBarCode();

            Integer tmpId = ez.createProductType("DeathSword","526374859278",666,"first for Order Tests");
            ez.updatePosition(tmpId,"436-atf-3445");
            String pCode = "526374859278";
            // return  the id of the order (> 0)
            assertTrue((id=ez.issueOrder(pCode, 3, 3.99))>0);

            // return -1 if the product does not exists, if there are problems with the db
            String nonExistentPCode = "837249732450"; // may fail if this id is already occupied
            assertEquals(-1, (int)ez.issueOrder(nonExistentPCode, 3, 3.99));
            // @throws InvalidProductCodeException if the productCode is not a valid bar code, if it is null or if it is empty
            assertThrows(InvalidProductCodeException.class, ()->ez.issueOrder("462846283672", 3, 3.99));
            assertThrows(InvalidProductCodeException.class, ()->ez.issueOrder("", 3, 3.99));
            assertThrows(InvalidProductCodeException.class, ()->ez.issueOrder(null, 3, 3.99));
            // @throws InvalidQuantityException if the quantity is less than or equal to 0
            assertThrows(InvalidQuantityException.class, ()->ez.issueOrder("462846283670", 0, 3.99));
            assertThrows(InvalidQuantityException.class, ()->ez.issueOrder("462846283670", -1, 3.99));
            // @throws InvalidPricePerUnitException if the price per unit of product is less than or equal to 0
            assertThrows(InvalidPricePerUnitException.class, ()->ez.issueOrder("462846283670", 5, 0));
            assertThrows(InvalidPricePerUnitException.class, ()->ez.issueOrder("462846283670", 5, 0));
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.issueOrder("462846283670", 5, 1));
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.issueOrder("462846283670", 5, 1));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        //-------------------------------------------------------
        //public Integer payOrderFor(String productCode, int quantity, double pricePerUnit)
        try{
            ez.login("daniele", "789");
            balance= ez.computeBalance();
            // getting a barcode for a product in the inventory
            //Optional<ProductType> pOptional = ez.getAllProductTypes().stream().findFirst();
            //assertTrue(pOptional.isPresent());
            //String pCode = pOptional.get().getBarCode();
            ProductType product = ez.getProductTypeByBarCode("526374859278");
            String pCode = "526374859278";

            // (?) THIS TEST FAILS at statement: if( !(this.accountBook.getOperation(orderId) instanceof OrderImpl)
         // @return  the id of the order (> 0)
            System.out.println("order is actually for product: " + pCode + "with id: "+ product.getId());
            //assertThrows(IndexOutOfBoundsException.class,()->ez.payOrderFor(pCode, 3, 3.99));
            assertTrue((id2=ez.payOrderFor(pCode, 3, 3.99))>0);

            // return   -1 if the product does not exists
            String nonExistentPCode = "837249732450"; // may fail if this id is already occupied
            assertEquals(-1, (int)ez.payOrderFor(nonExistentPCode, 3, 3.99));
         // return -1 if the balance is not enough to satisfy the order
            assertEquals(-1, (int)ez.payOrderFor(nonExistentPCode, 1, ez.computeBalance()+1));
         // return -1 problems with the db
         // not testable
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        try{
         // @throws InvalidProductCodeException if the productCode is not a valid bar code, if it is null or if it is empty
            assertThrows(InvalidProductCodeException.class, ()->ez.payOrderFor("462846283672", 3, 3.99));
            assertThrows(InvalidProductCodeException.class, ()->ez.payOrderFor("", 3, 3.99));
            assertThrows(InvalidProductCodeException.class, ()->ez.payOrderFor(null, 3, 3.99));

            // @throws InvalidQuantityException if the quantity is less than or equal to 0
            assertThrows(InvalidQuantityException.class, ()->ez.payOrderFor("462846283670", 0, 3.99));
            assertThrows(InvalidQuantityException.class, ()->ez.payOrderFor("462846283670", -1, 3.99));

            // @throws InvalidPricePerUnitException if the price per unit of product is less than or equal to 0
            assertThrows(InvalidPricePerUnitException.class, ()->ez.payOrderFor("462846283670", 5, 0));
            assertThrows(InvalidPricePerUnitException.class, ()->ez.payOrderFor("462846283670", 5, 0));

            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.payOrderFor("462846283670", 5, 1));
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.payOrderFor("462846283670", 5, 1));
            // need to check that it actually subtracts from the balance
            ez.login("daniele", "789");
            /*
            assertTrue(ez.computeBalance()<balance);
            */
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        // ------------------------------------------------------------------------------------
        //public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException;
        try {
            ez.login("daniele", "789");
            balance = ez.computeBalance();
        // return  true if the order has been successfully ordered
            assertTrue(ez.payOrder(id));
        // return  false if the order does not exist or if it was not in an ISSUED/ORDERED state
            int nonExistentOrder = 62148234; //if it fails, it may be occupied
            assertFalse(ez.payOrder(id2));
            assertFalse(ez.payOrder(nonExistentOrder));

            // throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
            assertThrows(InvalidOrderIdException.class, ()->ez.payOrder(0));
            assertThrows(InvalidOrderIdException.class, ()->ez.payOrder(null));
            assertThrows(InvalidOrderIdException.class, ()->ez.payOrder(-1));
            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            int finalId = id;
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.payOrder(finalId));
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.payOrder(finalId));
            // check the balance
            ez.login("daniele", "789");
            assertTrue(ez.computeBalance()<balance);
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        // --------------------------------------------------------------------------------
        //public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException;
        try {
            ez.login("daniele", "789");
            Integer tmpId = ez.createProductType("necronomicon","526374859261",6.66,"for Order Tests");
            ez.updatePosition(tmpId,"435-atf-3444");
            //Optional<ProductType> pOptional = ez.getAllProductTypes().stream().findFirst();
            //assertTrue(pOptional.isPresent());
            //String pCode = pOptional.get().getBarCode();
            String pCode = "526374859261";
            // return  true if the operation was successful
            System.out.println("id just before fail is: "+id);
            assertTrue(ez.recordOrderArrival(id));
            assertTrue(ez.recordOrderArrival(id)); // even if already completed should be true
            // return  false if the order does not exist or if it was not in an ORDERED/ISSUED/PAYED/COMPLETED state
            Integer idBadState = ez.issueOrder(pCode, 3, 3.00);
            ez.getAllOrders().stream().filter(o->o.getBalanceId() == idBadState).findFirst()
                    .get().setStatus("INVALIDSTATUS");
            assertFalse(ez.recordOrderArrival(idBadState));
            // @throws InvalidOrderIdException if the order id is less than or equal to 0 or if it is null.
            assertThrows(InvalidOrderIdException.class, ()-> ez.recordOrderArrival(0));
            assertThrows(InvalidOrderIdException.class, ()-> ez.recordOrderArrival(-1));
            assertThrows(InvalidOrderIdException.class, ()-> ez.recordOrderArrival(null));
            // @throws InvalidLocationException if the ordered product type has not an assigned location.
            Integer noLocProdId = ez.createProductType("LocationLess","526374859285",1.00,"for Order Tests with no location");

            Integer noLocOrder = ez.payOrderFor("526374859285",2,0.50);
            int finalId1 = noLocOrder;
            System.out.println("noLocOrder before failing is: "+noLocOrder);
            assertThrows(InvalidLocationException.class, ()-> ez.recordOrderArrival(finalId1));
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation

        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        //------------------------------------------------------------
        //public List<Order> getAllOrders() throws UnauthorizedException;
        try {
            // return a list containing all orders
            ez.login("daniele", "789");
            assertNotNull(ez.getAllOrders());
            ez.login("sandy brown", "abc");
            assertNotNull(ez.getAllOrders());
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ez::getAllOrders);
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ez::getAllOrders);
            // maybe there is the need to also check the actual list
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

    }

    @Test
    public void testCustomerAPIs(){
        EZShop ez = new EZShop();
        int id=-1;

        // --------------------------------------------------------
        // public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException
        try{
            ez.login("daniele", "789");
            // return the id (>0) of the new customer if successful
            id = ez.defineCustomer("customername");
            assertTrue(id>0);

            // -1 otherwise
            assertEquals(-1, (int) ez.defineCustomer("customername"));

            // @throws InvalidCustomerNameException if the customer name is empty or null
            assertThrows(InvalidCustomerNameException.class, () -> ez.defineCustomer(""));

            assertThrows(InvalidCustomerNameException.class, () -> ez.defineCustomer(null));
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();

            assertThrows(UnauthorizedException.class, () -> ez.defineCustomer("another name"));

            ez.login("marina blue", "abc");
            int id3=-1;
            assertTrue(0<(id3=ez.defineCustomer("another name")));
            assertTrue(ez.deleteCustomer(id3));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        //-----------------------------------------------------------
        // public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException;
        try{
            // @return true if the update is successful
            assertTrue(ez.modifyCustomer(id, "newName", "1234567890"));

            // @return false if the update fails ( cardCode assigned to another user, db unreacheable)
            int id2 = ez.defineCustomer("pippo");
            assertFalse(ez.modifyCustomer(id2, "anotherName", "1234567890"));
            assertTrue(ez.deleteCustomer(id2));
            assertNull(ez.getCustomer(id2));

            // @throws InvalidCustomerNameException if the customer name is empty or null
            int finalId = id;
            assertThrows(InvalidCustomerNameException.class, ()-> ez.modifyCustomer(finalId, "", "4758365837"));
            assertThrows(InvalidCustomerNameException.class, ()-> ez.modifyCustomer(finalId, null, "4758365837"));

            // @throws InvalidCustomerCardException if the customer card is empty, null or if it is not in a valid format (string with 10 digits)
            assertThrows(InvalidCustomerCardException.class, ()-> ez.modifyCustomer(finalId, "anothername", "475836583"));
            assertThrows(InvalidCustomerCardException.class, ()-> ez.modifyCustomer(finalId, "anothername", "47583658377"));
            assertThrows(InvalidCustomerCardException.class, ()-> ez.modifyCustomer(finalId, "anothername", "475836583a"));

            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()-> ez.modifyCustomer(finalId, "anothername", "4758365838"));
            ez.login("marina blue", "abc");
            assertTrue(ez.modifyCustomer(finalId, "anothername", "4758365839"));

            // @throws InvalidCustomerIdException if the id is less than or equal to zero
            assertThrows(InvalidCustomerIdException.class, ()-> ez.modifyCustomer(0, "anothername", "4758365837"));
            assertThrows(InvalidCustomerIdException.class, ()-> ez.modifyCustomer(-1, "anothername", "4758365837"));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        //---------------------------------------------------------------
        // public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException;
        try{
            ez.login("daniele", "789");

            // return false if the user does not exists or if we have problems to reach the db
            int i;
            for(i=1; ez.getCustomer(i)!=null; i++);
            assertFalse(ez.deleteCustomer(i));
            // @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
            assertThrows(InvalidCustomerIdException.class, ()-> ez.deleteCustomer(0));
            assertThrows(InvalidCustomerIdException.class, ()-> ez.deleteCustomer(-1));
            assertThrows(InvalidCustomerIdException.class, ()-> ez.deleteCustomer(null));
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()-> ez.deleteCustomer(null));
            ez.login("daniele", "789");
            // return true if the customer was successfully deleted
            assertTrue(ez.deleteCustomer(id));
            assertNull(ez.getCustomer(id));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        // -----------------------------------------------
        // public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException;
        try{

            ez.login("daniele", "789");
            id = ez.defineCustomer("newCustomerTest");
            // return the customer with given id
            assertEquals("newCustomerTest", ez.getCustomer(id).getCustomerName());
            ez.logout();
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            assertThrows(UnauthorizedException.class, ez::getAllCustomers);
            ez.login("marina blue", "abc");
            assertEquals("newCustomerTest", ez.getCustomer(id).getCustomerName());
            ez.deleteCustomer(id);

            // return null if that user does not exists
            assertNull(ez.getCustomer(id));
            // @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
            assertThrows(InvalidCustomerIdException.class, ()->ez.getCustomer(null));
            assertThrows(InvalidCustomerIdException.class, ()->ez.getCustomer(0));
            assertThrows(InvalidCustomerIdException.class, ()->ez.getCustomer(-1));
        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

        // ------------------------------------------------
        // public List<Customer> getAllCustomers() throws UnauthorizedException;
        try{
            //@return the list of all the customers registered
            ez.login("daniele", "789");
            id = ez.defineCustomer("newCustomerTest");
            assertTrue(ez.getAllCustomers().stream().anyMatch(x -> x.getCustomerName().equals("newCustomerTest")));
            ez.logout();
            // throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            assertThrows(UnauthorizedException.class, ez::getAllCustomers);
            ez.login("marina blue", "abc");
            assertTrue(ez.getAllCustomers().stream().anyMatch(x -> x.getCustomerName().equals("newCustomerTest")));
            ez.deleteCustomer(id);
        }
        catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        // -------------------------------------------------
        // public String createCard() throws UnauthorizedException;
        try{
            ez.login("daniele", "789");
            // @return the code of a new available card. An empty string if the db is unreachable
            assertTrue(ez.createCard().matches("\\d{10}"));
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()-> ez.createCard());
            ez.login("marina blue", "abc");
            assertTrue(ez.createCard().matches("\\d{10}"));
        }
        catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        // ---------------------------------------------------------
        // public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException;
        try{

            ez.login("daniele", "789");
            id = ez.defineCustomer("newCustomerTest");
            String card = ez.createCard();

            // @return true if the operation was successful
            assertTrue(ez.attachCardToCustomer(card, id));

            // return false if the card is already assigned to another user, if there is no customer with given id, if the db is unreachable
            int id2 = -1;
            assertTrue(0<(id2=ez.defineCustomer("customerTesting")));
            assertFalse(ez.attachCardToCustomer(card, id2));

            // @throws InvalidCustomerIdException if the id is null, less than or equal to 0.
            assertThrows(InvalidCustomerIdException.class, ()->ez.attachCardToCustomer(ez.createCard(), 0));
            assertThrows(InvalidCustomerIdException.class, ()->ez.attachCardToCustomer(ez.createCard(), -1));

            // @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
            int finalId1 = id;
            assertThrows(InvalidCustomerCardException.class, ()->ez.attachCardToCustomer(null, finalId1));
            assertThrows(InvalidCustomerCardException.class, ()->ez.attachCardToCustomer("", finalId1));
            assertThrows(InvalidCustomerCardException.class, ()->ez.attachCardToCustomer("22421a", finalId1));
            assertThrows(InvalidCustomerCardException.class, ()->ez.attachCardToCustomer("22421664", finalId1));

            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            int finalId = id2;
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.attachCardToCustomer(ez.createCard(), finalId));
            ez.login("daniele", "789");
            ez.deleteCustomer(id2);
            ez.deleteCustomer(id);

        }
        catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        // ------------------------------------------------------------
        // public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException;
        try{
            ez.login("daniele", "789");
            String card = ez.createCard();
            id=ez.defineCustomer("pippo");
            assertTrue(ez.attachCardToCustomer(card,id));
            // @return true if the operation is successful
            assertTrue(ez.modifyPointsOnCard(card, 50));
            assertTrue(ez.modifyPointsOnCard(card, -50));
            // return false   if there is no card with given code,
            assertFalse(ez.modifyPointsOnCard(ez.createCard(), 50));
            // return false if pointsToBeAdded is negative and there were not enough points on that card before this operation
            assertFalse(ez.modifyPointsOnCard(card, -100));
            // return false   if we cannot reach the db
            //cannot test it

            // @throws InvalidCustomerCardException if the card is null, empty or in an invalid format
            assertThrows(InvalidCustomerCardException.class, ()->ez.modifyPointsOnCard(null,+100));
            assertThrows(InvalidCustomerCardException.class, ()->ez.modifyPointsOnCard("",+100));
            assertThrows(InvalidCustomerCardException.class, ()->ez.modifyPointsOnCard("a43262",+100));
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            assertThrows(UnauthorizedException.class, ()->ez.modifyPointsOnCard(card,+100));
            ez.login("marina blue", "abc");
            assertTrue(ez.modifyPointsOnCard(card,+100));
            ez.deleteCustomer(id);
        }
        catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
    }

    @Test
    public void testBalanceRelatedAPIs(){
        EZShop ez = new EZShop();
        //__________________________________________________________
        // public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException;
        /*
         * This method record a balance update. <toBeAdded> can be both positive and nevative. If positive the balance entry
         * should be recorded as CREDIT, if negative as DEBIT. The final balance after this operation should always be
         * positive.
         */
        try{
            ez.login("daniele","789");
            // @return  true if the balance has been successfully updated
            assertTrue(ez.recordBalanceUpdate(500));
            assertTrue(ez.recordBalanceUpdate(-500));
            // return   false if toBeAdded + currentBalance < 0.
            assertFalse(ez.recordBalanceUpdate(-1));
            //@throws UnauthorizedException if there is no logged user or if it is not administrator or shopmanager
            ez.logout();
            //no user
            assertThrows(UnauthorizedException.class, ()->ez.recordBalanceUpdate(500));
            //cashier
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.recordBalanceUpdate(500));
            //shopmanager(administrator has already been tested)
            ez.login("damiana diamond", "abc");
            assertTrue(ez.recordBalanceUpdate(500));
            // checking the final balance
            assertTrue(abs(500.0-ez.computeBalance()) < 0.001 );

        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        //______________________________________________________________
        //public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException;
        /**
         * This method returns a list of all the balance operations (CREDIT,DEBIT,ORDER,SALE,RETURN) performed between two
         * given dates.
         * This method should understand if a user exchanges the order of the dates and act consequently to correct
         * them.
         * Both <from> and <to> are included in the range of dates and might be null. This means the absence of one (or
         * both) temporal constraints.
         **/
        try{
            ez.login("daniele", "789");
             // @return All the operations on the balance whose date is <= to and >= from
            List <BalanceOperation> lb = ez.getCreditsAndDebits(null,null);
            assertNotNull(lb);
            assertEquals(lb.size(),3);
            LocalDate dt = lb.get(0).getDate();
            assertTrue(ez.getCreditsAndDebits(dt.plusDays(1),null).isEmpty());
            assertTrue(ez.getCreditsAndDebits(null,dt.minusDays(1)).isEmpty());
            assertTrue(ez.getCreditsAndDebits(dt.minusDays(1),dt.plusDays(1)).stream().anyMatch(x->x.getBalanceId()==lb.get(0).getBalanceId()));
             // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            //no user
            assertThrows(UnauthorizedException.class, ()->ez.getCreditsAndDebits(null,null));
            //cashier
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ()->ez.getCreditsAndDebits(null,null));
            //shopmanager(administrator has already been tested)
            ez.login("damiana diamond", "abc");
            ez.getCreditsAndDebits(null,null);

        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }
        //_____________________________________________________________
        // public double computeBalance() throws UnauthorizedException
        // This method returns the actual balance of the system.
        try{
            ez.login("daniele", "789");

            // @return the value of the current balance
            assertTrue(abs(500- ez.computeBalance())<0.001);
            Integer sid = ez.startSaleTransaction();
            assertTrue(sid>=0);
            Integer pid = ez.createProductType("description", "632857389245", 10, "item");
            assertTrue(0<pid);
            ez.updateQuantity(pid, 5);
            assertTrue(ez.addProductToSale(sid,"632857389245", 5));
            assertTrue(ez.endSaleTransaction(sid));
            assertNotNull(ez.getSaleTransaction(sid));
            assertTrue(ez.receiveCashPayment(sid, 60)!=-1);
            assertTrue(abs((500+50)- ez.computeBalance())<0.001);
            // @throws UnauthorizedException if there is no logged user or if it has not the rights to perform the operation
            ez.logout();
            //no user
            assertThrows(UnauthorizedException.class, ez::computeBalance);
            //cashier
            ez.login("marina blue", "abc");
            assertThrows(UnauthorizedException.class, ez::computeBalance);
            //shopmanager(administrator has already been tested)
            ez.login("damiana diamond", "abc");
            ez.computeBalance();

        }catch(Exception e){
            System.out.println("catched Exception: " + e);
            fail("should have not thrown any exception");
        }

    }

    @Test
    public void TestProductTypeAPIs(){

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
                if(ez.getAllProductTypes().stream().noneMatch(x-> x.getId()== finalI))found=1;
            }
            assertFalse(ez.updateProduct(i,"newDescription", "526374859247", 3.99, "newnote2"));
            // false if another product already has the same barcode and it is not the one we are updating
            assertFalse(ez.updateProduct(id+1,"newDescription", "8004263697047", 3.99, "newnote2"));
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
            assertNotNull(ez.getAllProductTypes());
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
                if(ez.getAllProductTypes().stream().noneMatch(x-> x.getId()== finalI))found=1;
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
                if(ez.getAllProductTypes().stream().noneMatch(x-> x.getId()== finalI))found=1;
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

    @Test
    public void testSaleTransactionAPIs(){
        EZShop ez = new EZShop();
        try {
            ez.login("alessio", "456");
        }catch(Exception e){
            System.out.println("unable to login.");
        }
        Integer id = 0;
        ez.logout();

        /* -------------- startSaleTransaction() -------------- */
        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.startSaleTransaction());

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.startSaleTransaction() >= 0);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.startSaleTransaction() >= 0);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            id = ez.startSaleTransaction();
            assertTrue(id >= 0);
            System.out.println(id);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        ez.logout();

        /* ---------- END startSaleTransaction() ---------- */

        /* ------------- addProductToSale() ------------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.addProductToSale(2, "000000000000", 5));

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
            //ShopManager will add 2 of product to sale
            //Cashier will add 1
            //Administrator will add 1
            //firstly i create the productType for the test
            Integer tmpId = ez.createProductType("chair","526374859254",19.99,null);
            ez.updatePosition(tmpId,"434-atf-3443");
            //so i put 4 items of the same product into the system before starting
            ProductType tmpProd = ez.getProductTypeByBarCode("526374859254");
            ez.updateQuantity(tmpProd.getId(),4);
        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        System.out.println(id);
        try {
            assertTrue(ez.addProductToSale(id, "526374859254", 2));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.addProductToSale(id, "526374859254", 1) );
        }
        catch (Exception e){
            fail("Should have been able add The product To sale " + e);
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.addProductToSale(id, "526374859254", 1));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.addProductToSale(-1, "8004263697047", 5));
        assertThrows(InvalidTransactionIdException.class, ()->ez.addProductToSale(null, "8004263697047", 5));
        Integer finalId = id;
        assertThrows(InvalidQuantityException.class, ()->ez.addProductToSale(finalId, "8004263697047", -1));
        assertThrows(InvalidProductCodeException.class, ()->ez.addProductToSale(finalId, "", 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.addProductToSale(finalId, null, 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.addProductToSale(finalId, "123456789", 5));

        ez.logout();
        /* --------- END addProductToSale() --------- */

        /* ----- deleteProductFromSale ----- */
        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.deleteProductFromSale(2, "000000000000", 5));

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }
        System.out.println(id);
        try {
            assertTrue(ez.deleteProductFromSale(id, "526374859254", 1));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.deleteProductFromSale(id, "526374859254", 1) );
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.deleteProductFromSale(id, "526374859254", 1));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.deleteProductFromSale(-1, "526374859254", 5));
        assertThrows(InvalidTransactionIdException.class, ()->ez.deleteProductFromSale(null, "526374859254", 5));
        assertThrows(InvalidQuantityException.class, ()->ez.deleteProductFromSale(finalId, "526374859254", -1));
        assertThrows(InvalidProductCodeException.class, ()->ez.deleteProductFromSale(finalId, "", 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.deleteProductFromSale(finalId, null, 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.deleteProductFromSale(finalId, "123456789", 5));

        ez.logout();

        /* --------- END deleteProductFromSale() --------- */

        /* --------- applyDiscountRateToProduct ---------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.applyDiscountRateToProduct(2, "000000000000", 0.2));

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }
        System.out.println(id);
        try {
            assertTrue(ez.applyDiscountRateToProduct(id, "526374859254", 0.2));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.applyDiscountRateToProduct(id, "526374859254", 0.2) );
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.applyDiscountRateToProduct(id, "526374859254", 0.2));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.applyDiscountRateToProduct(-1, "526374859254", 0.2));
        assertThrows(InvalidTransactionIdException.class, ()->ez.applyDiscountRateToProduct(null, "526374859254", 0.2));
        assertThrows(InvalidDiscountRateException.class, ()->ez.applyDiscountRateToProduct(finalId, "526374859254", -1));
        assertThrows(InvalidDiscountRateException.class, ()->ez.applyDiscountRateToProduct(finalId, "526374859254", 2));
        assertThrows(InvalidProductCodeException.class, ()->ez.applyDiscountRateToProduct(finalId, "", 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.applyDiscountRateToProduct(finalId, null, 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.applyDiscountRateToProduct(finalId, "123456789", 5));

        ez.logout();

        /* --------- END  applyDiscountRateToProduct ---------- */

        /* --------- applyDiscountRateToSale --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.applyDiscountRateToSale(2, 0.2));

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }
        try {
            assertTrue(ez.applyDiscountRateToSale(id,  0.2));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.applyDiscountRateToSale(id,  0.2) );
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.applyDiscountRateToSale(id,  0.2));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.applyDiscountRateToSale(-1,  0.2));
        assertThrows(InvalidTransactionIdException.class, ()->ez.applyDiscountRateToSale(null,  0.2));
        assertThrows(InvalidDiscountRateException.class, ()->ez.applyDiscountRateToSale(finalId,  -1));
        assertThrows(InvalidDiscountRateException.class, ()->ez.applyDiscountRateToSale(finalId,  2));

        ez.logout();

        /* --------- END applyDiscountRateToSale --------- */

        /* --------- computePointsForSale --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.computePointsForSale(2));

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }
        try {
            assertTrue(ez.computePointsForSale(id) != -1);
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.computePointsForSale(id) != -1);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(ez.computePointsForSale(id) != -1);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.computePointsForSale(-1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.computePointsForSale(null));

        ez.logout();

        /* --------- END computePointsForSale --------- */

        /* --------- endSaleTransaction --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.endSaleTransaction(2));

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }
        try {
            assertTrue(ez.endSaleTransaction(id) );
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse(ez.endSaleTransaction(id) );
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse(ez.endSaleTransaction(id));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.endSaleTransaction(-1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.endSaleTransaction(null));

        ez.logout();

        /* --------- END endSaleTransaction --------- */

        /* --------- getSaleTransaction --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.getSaleTransaction(2));

        //User logged as Administrator, Cashier or ShopManager

        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }
        try {
            assertTrue((ez.getSaleTransaction(id) != null));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.getSaleTransaction(id) != null));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.getSaleTransaction(id) != null));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.getSaleTransaction(-1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.getSaleTransaction(null));

        //Testing a saleTransaction not closed
        try {
            id = ez.startSaleTransaction();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }
        try {
            assertTrue((ez.getSaleTransaction(id) == null));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }


        ez.logout();

        /* --------- END getSaleTransaction --------- */

        /* --------- deleteSaleTransaction --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.deleteSaleTransaction(2));

        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

            //ending saleTransaction
            ez.endSaleTransaction(id);
        }catch(Exception e){
            fail("Should have been able to login");
        }
        System.out.println(id);
        try {
            assertTrue((ez.deleteSaleTransaction(id)));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse((ez.deleteSaleTransaction(id)));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse((ez.deleteSaleTransaction(id)));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.deleteSaleTransaction(-1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.deleteSaleTransaction(null));

        //Testing a saleTransaction payed
        try {
            id = ez.startSaleTransaction();
            ez.addProductToSale(id, "526374859254", 1);
            ez.endSaleTransaction(id);
        } catch (UnauthorizedException | InvalidTransactionIdException | InvalidProductCodeException | InvalidQuantityException e) {
            e.printStackTrace();
        }
        //paying saleTransaction
        try {
            ez.receiveCashPayment(id, ez.getSaleTransaction(id).getPrice());
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (InvalidPaymentException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }

        try {
            assertFalse(ez.deleteSaleTransaction(id));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }


        ez.logout();

        /* --------- END deleteSaleTransaction --------- */
    }

    @Test
    public void testReturnTransactionAPIs(){

        /* --------- startReturnTransaction --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.startReturnTransaction(2));

        Integer finalId = 0;
        Integer productId = 0;
        Integer finalReturnId = 0;


        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

            productId = ez.createProductType("chair", "526374859254", 5.0, "nota" );
            ez.updateQuantity(productId, 500);

            //Completing a sale transaction
            finalId = ez.startSaleTransaction();
            ez.addProductToSale(finalId, "526374859254", 15);
            ez.endSaleTransaction(finalId);
            ez.receiveCashPayment(finalId, ez.getSaleTransaction(finalId).getPrice() );

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue(( ez.startReturnTransaction(finalId)) != -1);
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.startReturnTransaction(finalId)) != -1);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((finalReturnId = ez.startReturnTransaction(finalId)) != -1);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.startReturnTransaction(-1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.startReturnTransaction(null));

        ez.logout();

        /* --------- END startReturnTransaction --------- */

        /* --------- returnProduct --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.returnProduct(2, "526374859254", 2));
        Integer finalReturnId1 = finalReturnId;

        //User logged as Administrator, Cashier or ShopManager
        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue((ez.returnProduct(finalReturnId, "526374859254", 1 ) ) );
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue( ez.returnProduct(finalReturnId1, "526374859254", 1 )  );
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.returnProduct(finalReturnId, "526374859254", 1 ) ) );
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse(ez.returnProduct(finalReturnId, "526374859254", 20000 ));
            assertFalse(ez.returnProduct(10, "526374859254", 1 ));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (InvalidProductCodeException e) {
            e.printStackTrace();
        } catch (InvalidQuantityException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.returnProduct(-1, "526374859254", 1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.returnProduct(null, "526374859254", 1));
        assertThrows(InvalidProductCodeException.class, ()->ez.returnProduct(finalReturnId1, "", 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.returnProduct(finalReturnId1, null, 5));
        assertThrows(InvalidProductCodeException.class, ()->ez.returnProduct(finalReturnId1, "123456789", 5));
        assertThrows(InvalidQuantityException.class, ()->ez.returnProduct(finalReturnId1, "526374859254", -2));
        assertThrows(InvalidQuantityException.class, ()->ez.returnProduct(finalReturnId1, "526374859254", 0));

        ez.logout();

        /* --------- END returnProduct --------- */

        /* --------- endReturnTransaction --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.endReturnTransaction(finalReturnId1, false));


        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue(( ez.endReturnTransaction(finalReturnId1, true)));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse(( ez.endReturnTransaction(finalReturnId1, false)));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse(( ez.endReturnTransaction(finalReturnId1, false)));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        assertThrows(InvalidTransactionIdException.class, ()->ez.endReturnTransaction(-1, false));
        assertThrows(InvalidTransactionIdException.class, ()->ez.endReturnTransaction(0, false));
        assertThrows(InvalidTransactionIdException.class, ()->ez.endReturnTransaction(null, false));

        ez.logout();

        /* --------- END endReturnTransaction --------- */

        /* --------- deleteReturnTransaction --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.deleteReturnTransaction(finalReturnId1));
        Integer finalReturnId2 = 0;


        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

            finalReturnId2 = ez.startReturnTransaction(finalId);
            ez.returnProduct(finalReturnId2, "526374859254", 1 );
            ez.endReturnTransaction(finalReturnId2, true);

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue(( ez.deleteReturnTransaction(finalReturnId2)));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(( ez.deleteReturnTransaction(finalReturnId2)));
        }
        catch (Exception e){
            fail("Should have been able to delete"+e);
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue(( ez.deleteReturnTransaction(finalReturnId2)));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        try {
            assertFalse(ez.deleteReturnTransaction(22));
        } catch (InvalidTransactionIdException e) {
            e.printStackTrace();
        } catch (UnauthorizedException e) {
            e.printStackTrace();
        }
        assertThrows(InvalidTransactionIdException.class, ()->ez.deleteReturnTransaction(-1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.deleteReturnTransaction(0));
        assertThrows(InvalidTransactionIdException.class, ()->ez.deleteReturnTransaction(null));

        ez.logout();

        /* --------- END deleteReturnTransaction --------- */

    }

    @Test
    public void testPaymentAPIs(){


        /*SAVING STATE OF CREDITCARDS.TXT BEFORE TESTS TO ROLLBACK FILE AT THE END*/
        FileInputStream stream = null;
        try {
            stream = new FileInputStream("src/main/persistent_data/creditcards.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String strLine;
        ArrayList<String> lines = new ArrayList<String>();
        try {
            while ((strLine = reader.readLine()) != null) {
                String lastWord = strLine;
                lines.add(lastWord);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }


        /* --------- receiveCashPayment --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.receiveCashPayment(2, 3));

        Integer finalId = 0;
        Integer productId = 0;


        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

            productId = ez.createProductType("chair", "526374859254", 5.0, "nota" );
            ez.updateQuantity(productId, 500);

            //Completing a return transaction
            finalId = ez.startSaleTransaction();
            ez.addProductToSale(finalId, "526374859254", 15);
            ez.endSaleTransaction(finalId);

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue((ez.receiveCashPayment(finalId, ez.getSaleTransaction(finalId).getPrice() ) == 0));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.receiveCashPayment(finalId, ez.getSaleTransaction(finalId).getPrice() ) == 0));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.receiveCashPayment(finalId, ez.getSaleTransaction(finalId).getPrice() ) == 0));
            assertEquals(-1, (int) ez.receiveCashPayment(22, ez.getSaleTransaction(finalId).getPrice()));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }


        Integer finalId1 = finalId;
        assertThrows(InvalidPaymentException.class, ()->ez.receiveCashPayment(finalId1, -2.0));
        assertThrows(InvalidPaymentException.class, ()->ez.receiveCashPayment(finalId1, 0));
        assertThrows(InvalidTransactionIdException.class, ()->ez.receiveCashPayment(-1, 10));
        assertThrows(InvalidTransactionIdException.class, ()->ez.receiveCashPayment(0, 10));
        assertThrows(InvalidTransactionIdException.class, ()->ez.receiveCashPayment(null, 10));

        ez.logout();

        /* --------- END receiveCashPayment --------- */

        /* --------- receiveCreditCardPayment --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.receiveCreditCardPayment(2, "4485370086510891"));



        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue((ez.receiveCreditCardPayment(finalId, "4485370086510891" ) ));
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.receiveCreditCardPayment(finalId, "4485370086510891" ) ));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse(ez.receiveCreditCardPayment(finalId, "4716258050958645"));
            assertFalse(ez.receiveCreditCardPayment(22, "4716258050958645"));
        }
        catch (Exception e){
            fail("receiveCreditCardPayment throws: "+e);
        }


        Integer finalId2 = finalId;
        assertThrows(InvalidCreditCardException.class, ()->ez.receiveCreditCardPayment(finalId2, ""));
        assertThrows(InvalidCreditCardException.class, ()->ez.receiveCreditCardPayment(finalId2, null));
        assertThrows(InvalidCreditCardException.class, ()->ez.receiveCreditCardPayment(finalId2, "11"));
        assertThrows(InvalidTransactionIdException.class, ()->ez.receiveCreditCardPayment(-1, "4485370086510891" ));
        assertThrows(InvalidTransactionIdException.class, ()->ez.receiveCreditCardPayment(0, "4485370086510891" ));
        assertThrows(InvalidTransactionIdException.class, ()->ez.receiveCreditCardPayment(null, "4485370086510891" ));

        ez.logout();

        /* --------- END receiveCreditCardPayment --------- */

        /* --------- returnCashPayment --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.returnCashPayment(1));

        Integer returnId = 0;


        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");

            //Completing a return transaction
            returnId = ez.startReturnTransaction(finalId);
            ez.returnProduct(returnId, "526374859254" , 2);

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue((ez.returnCashPayment(returnId)) == -1);
            ez.endReturnTransaction(returnId, true);
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.returnCashPayment(returnId)) != -1);
        }
        catch (Exception e){
            fail("Should have been able to login");
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.returnCashPayment(returnId)) != -1);
            assertEquals(-1, (int) ez.returnCashPayment(22));
        }
        catch (Exception e){
            fail("Should have been able to login");
        }



        assertThrows(InvalidTransactionIdException.class, ()->ez.returnCashPayment(-1));
        assertThrows(InvalidTransactionIdException.class, ()->ez.returnCashPayment(0));

        ez.logout();

        /* --------- END returnCashPayment --------- */

        /* --------- returnCreditCardPayment --------- */

        //No user logged
        assertThrows(UnauthorizedException.class, ()->ez.returnCreditCardPayment(2, "4485370086510891"));



        //User logged as Administrator, Cashier or ShopManager


        //ShopManager
        try{
            ez.login("damiana diamond", "abc");
            returnId = ez.startReturnTransaction(finalId);
            ez.returnProduct(returnId, "526374859254" , 2);

        }catch(Exception e){
            fail("Should have been able to login"+e);
        }
        try {
            assertTrue((ez.returnCreditCardPayment(returnId, "4485370086510891" ) )== -1);
            ez.endReturnTransaction(returnId, true);
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Cashier
        try{
            ez.login("marina blue", "abc");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertTrue((ez.returnCreditCardPayment(returnId, "4485370086510891" ) )!= -1);
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }

        //Administator
        try{
            ez.login("andrea", "123");
        }catch(Exception e){
            fail("Should have been able to login");
        }

        try {
            assertFalse(ez.returnCreditCardPayment(finalId, "4716258050958645") != -1);
            assertFalse(ez.returnCreditCardPayment(22, "4716258050958645") != -1);
        }
        catch (Exception e){
            fail("Should have been able to login"+e);
        }


        Integer finalReturnId2 = returnId;
        assertThrows(InvalidCreditCardException.class, ()->ez.returnCreditCardPayment(finalReturnId2, ""));
        assertThrows(InvalidCreditCardException.class, ()->ez.returnCreditCardPayment(finalReturnId2 , null));
        assertThrows(InvalidCreditCardException.class, ()->ez.returnCreditCardPayment(finalReturnId2 , "11"));
        assertThrows(InvalidTransactionIdException.class, ()->ez.returnCreditCardPayment(-1, "4485370086510891" ));
        assertThrows(InvalidTransactionIdException.class, ()->ez.returnCreditCardPayment(0, "4485370086510891" ));

        ez.logout();

        /* --------- END returnCreditCardPayment --------- */



        /*ROLLING BACK FILE CREDITCARDS.TXT TO ORIGINAL STATE*/
        try{
            Files.write(Paths.get("src/main/persistent_data/creditcards.txt"),
                    (Iterable<String>)lines.stream()::iterator); //(Iterable<String>)cards.stream().filter(x->!x.contains(line.get()))::iterator);
        }
        //HandLing Exception
        catch (Exception e) {
            System.out.println("something went wrong when writing to file\n");
            e.printStackTrace();
        }
    }

}