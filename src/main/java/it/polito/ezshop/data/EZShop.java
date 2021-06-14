package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.math.*;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;


public class EZShop implements EZShopInterface {
    //users
    private User userLogged = null;
    private final HashMap<Integer, User> users_data;
    private final JSONArray jArrayUsers;
    //products
    private final HashMap<Integer, ProductType> productMap;
    private final JSONArray jArrayProduct;
    //rfids
    private final HashMap<String,Integer> rfidMap;
    private final JSONArray jArrayRfid;
    //accountbook (balance operations and subclasses)
    private final AccountBook accountBook;
    //Customers
    private final HashMap<Integer, Customer>  customersMap;
    private final JSONArray jArrayCustomers;

    //Opened sale transaction
    private SaleTransactionImplementation ongoingSale;
    //Opened return transaction
    private ReturnTransaction ongoingReturn;



    //Inner Class
    private static class Init{
        String filename;
        HashMap map;
        FileReader file;
        String type;

        public Init(String filename, HashMap map, String type) {
            this.filename = filename;
            this.map = map;
            this.type=type;
        }
    }

    public EZShop(){
        /* ------ Initializing data structures ------ */
        this.productMap = new HashMap<>();                  //Products
        this.accountBook = new AccountBook();                                   //Account book object
        this.users_data = new HashMap<>();                         //Users
        this.customersMap = new HashMap<>();                   //Customers
        this.rfidMap = new HashMap<>();                     //rfid to productId associations


        jArrayProduct=initializeMap(new Init("src/main/persistent_data/productTypes.json", productMap, "product"));
        jArrayUsers=initializeMap(new Init("src/main/persistent_data/users.json", users_data,"user"));
        jArrayCustomers=initializeMap(new Init("src/main/persistent_data/customers.json", customersMap,"customer"));
        jArrayRfid=initializeMap(new Init("src/main/persistent_data/productRfids.json",rfidMap,"rfid"));

    }
//-------------------------Start of our custom FUNCTIONS-------------------
    /*
       This method allows to initialize HashMaps
       with persistent data red from the JSON
       files
       @param i: object of type Init containing
                 all info about map to
                 initialize, file to read, and
                 type of data
     */

    /**
     * checks barcode validity according to the algorithm specified
     * at: https://www.gs1.org/services/how-calculate-check-digit-manually
     * @param barcode the barcode to check, must be 12, 13 or 14 char
     * @return true if barcode is valid, false if it is not
     */
    public static boolean barcodeIsValid(String barcode){
        if(barcode == null || barcode.isEmpty() || !barcode.matches("-?\\d+")){return false;}
        int len = barcode.length();
        if(len<12 || len>14){return false;}

        //mul will switch between x3 and x1 for every digit, res accumulates the result
        //nearestDec will be the nearest multiple of 10 rounded Up
        char[] bcode = barcode.toCharArray();
        int mul, nearestDec;
        Integer res=0;
        if(len==12 || len==14){mul = 3;}
        else{mul = 1;}
        //iterating as described in: https://www.gs1.org/services/how-calculate-check-digit-manually
        for(int i=0; i<len-1; i++){
            res += Character.getNumericValue(bcode[i])*mul;
            if(mul==1){mul=3;}
            else{mul=1;}
        }
        //getting the nearest multiple of 10 >= res and calculating the Check Digit
        nearestDec = (int) Math.ceil(res.doubleValue() / 10)*10;
        int checkDigit = nearestDec - res;
        //if the Check Digit is the same, return true, else return false
        return checkDigit == Character.getNumericValue(bcode[len-1]);
    }
//////---------------------------------------------------------------------------
    public static boolean validateCard(String cardNumber) {
        if (cardNumber == null || cardNumber.equals("") || cardNumber.length()<2)
            return false;
        //convert to array of int
        int[] digits = new int[cardNumber.length()];
        for (int i = 0; i < cardNumber.length(); i++) {
            digits[i] = Character.getNumericValue(cardNumber.charAt(i));
        }

        // double every other digit left to right
        for (int i = 0; i <= digits.length -1; i += 2)	{
            digits[i] += digits[i];

            if (digits[i] >= 10) {
                digits[i] = digits[i] - 9;
            }
        }

        int sum = 0;
        for (int digit : digits) {
            sum += digit;
        }
        return sum % 10 == 0;
    }


    private static ArrayList<String> readCards() {
        FileInputStream stream = null;
        try {
            stream = new FileInputStream("src/main/persistent_data/creditcards.txt");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        String strLine;
        ArrayList<String> lines = new ArrayList<>();
        try {
            while ((strLine = reader.readLine()) != null) {
                lines.add(strLine);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    private JSONArray initializeMap(Init i){
        // Loading Products
        JSONParser parser = new JSONParser();
        JSONArray jArray=null;
        try {
            i.file = new FileReader(i.filename);
        }
        catch (FileNotFoundException f){
            f.printStackTrace();
        }

        try
        {
            //Read JSON file

            jArray = (JSONArray) parser.parse(i.file);

            jArray.forEach( x -> parseObjectType( (JSONObject) x, i.type ) );

        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return jArray;
    }

    /* Generate a random unique ID */
    public static Integer assignId(Set<Integer> ids){
        if(ids == null) return -1;
        if(ids.size() == 100) return -1;    //Full Set
        boolean found = false;
        Integer int_random = 0;
        while(!found){
            Random rand = new Random(); //instance of random class
            int_random = rand.nextInt(100);
            if(!ids.contains(int_random) && int_random != 0){
                found = true;
            }
        }
        return  int_random;
    }

    private void parseObjectType(JSONObject obj, String type){
        switch (type) {
            case "product": {

                //Get ProductID
                Integer id = Integer.parseInt((String) obj.get("id"));
                // Get Barcode
                String barCode = (String) obj.get("barCode");
                //Get ProductDescription
                String description = (String) obj.get("description");
                //Get sellPrice
                double sellPrice = Double.parseDouble((String) obj.get("sellPrice"));
                // Get notes
                String notes = (String) obj.get("note");
                // Get availableQty
                Integer availableQty = Integer.parseInt((String) obj.get("availableQty"));
                // Get position
                String position = (String) obj.get("position");

                ProductTypeImplementation newProduct = new ProductTypeImplementation(id, barCode, description, sellPrice, notes);
                newProduct.setQuantity(availableQty);
                newProduct.setLocation(position);
                this.productMap.put(id, newProduct);
                break;
            }
            case "user": {

                //Get user id
                Integer id = Integer.parseInt((String) obj.get("id"));

                //Get employee last name
                String username = (String) obj.get("username");

                //Get employee website name
                String password = (String) obj.get("password");

                String role = (String) obj.get("role");

                User new_user = new UserImplementation(id, username, password, role);
                this.users_data.put(id, new_user);
                break;
            }
            case "customer": {

                //Get customer name
                Integer id = Integer.parseInt((String) obj.get("id"));

                //Get customer last name
                String card = (String) obj.get("card");

                //Get customer name
                String name = (String) obj.get("name");

                Integer points = Integer.parseInt((String) obj.get("points"));

                Customer new_customer = new CustomerImplementation(name, id, points, card);
                this.customersMap.put(id, new_customer);
                System.out.println(customersMap.size());
                break;
            }
            case "rfid": {

                //Get RFID string of the product instance
                String rfid = (String) obj.get("rfid");

                //Get ProductID
                Integer id = Integer.parseInt((String) obj.get("id"));

                this.rfidMap.put(rfid,id);
                break;
            }
        }


    }

    private JSONObject initializeJsonProductObject(ProductTypeImplementation p){
        //initialize jsonObject
        JSONObject pDetails = new JSONObject();
        pDetails.put("id", p.getId().toString());
        Object qty=p.getQuantity();
        pDetails.put("availableQty", (qty==null)?"0":qty.toString());
        pDetails.put("barCode", p.getBarCode());
        pDetails.put("description", p.getProductDescription());
        pDetails.put("note", p.getNote());
        Object sp = p.getPricePerUnit();
        pDetails.put("sellPrice", (sp==null)?"0": sp.toString());
        pDetails.put("position", p.getLocation());
        return pDetails;
    }

    public static boolean writejArrayToFile(String filepath, JSONArray jArr){

        //System.out.println("filepath: " + filepath);
        if(filepath == null || "".equals(filepath)){
            System.out.println("Error in writing jarray to file: invalid filepath");
            return false;
        }
        else if(jArr == null) {
            System.out.println("Error in writing jarray to file: jarray is null");
            return false;
        }
        try
        {
            FileWriter fOut = new FileWriter(filepath);
            fOut.write(jArr.toJSONString());
            fOut.flush();
            fOut.close();

        }
        catch(IOException f) {
            System.out.println("Error Occurred while writing the jarray to memory");
            System.out.println("filepath: " + filepath);
            System.out.println("jarray: " + jArr);
            return false;
        }
        return true;
    }

//---------- Start of Ezshop Interface functions -------------
    @Override
    public void reset() {
        //setting balance to 0
        accountBook.setBalance(0);
        //clearing all operation history
        BalanceOperationImpl.setBalanceCounter(0);
        accountBook.getOperationsMap().clear();
        accountBook.getjArrayOperations().clear();
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        //clearing all ProductTypes
        this.productMap.clear();
        this.jArrayProduct.clear();
        writejArrayToFile("src/main/persistent_data/productTypes.json",jArrayProduct);
        //clearing all the customers
        this.customersMap.clear();
        this.jArrayCustomers.clear();
        writejArrayToFile("src/main/persistent_data/customers.json",jArrayCustomers);
        //clearing all the customers
        this.users_data.clear();
        this.jArrayUsers.clear();
        writejArrayToFile("src/main/persistent_data/users.json",jArrayUsers);
        //clearing all the rfids
        this.rfidMap.clear();
        this.jArrayRfid.clear();
        writejArrayToFile("src/main/persistent_data/productRfids.json",jArrayRfid);
    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {

        if(password == null | "".equals(password)){
            throw new InvalidPasswordException("Invalid password");
        }
        if(username == null | "".equals(username)){
            throw new InvalidUsernameException("Invalid username");
        }

        if(role == null || ( !role.equals("Administrator") && !role.equals("Cashier") && !role.equals("ShopManager"))){
            throw new InvalidRoleException("Invalid role");
        }

        //Checking if User exists...
        for(User u: this.users_data.values()){
            if(u.getUsername().equals(username)){
                return -1;
            }
        }

        //Creating new user
        User user = new UserImplementation(assignId(this.users_data.keySet()), username, password, role);
        //Adding to map
        this.users_data.put(user.getId(), user);

        /* Adding to JSON Array (needed to update thr JSON file with new user data) */
        /* ------------------------------------------------------------------------ */

        JSONObject userDetails = new JSONObject();
        userDetails.put("id", user.getId().toString());
        userDetails.put("username", user.getUsername());
        userDetails.put("password", user.getPassword());
        userDetails.put("role", user.getRole());

        System.out.println(userDetails.get("id"));
        System.out.println(userDetails.get("username"));
        System.out.println(userDetails.get("password"));
        System.out.println(userDetails.get("role"));
        /* JSON Array updating...
           NOTE: id is used to insert object so that when there's the need
           to delete it it's easier to find it
         */
        this.jArrayUsers.add(userDetails);

        //Updating file
        if(!writejArrayToFile("src/main/persistent_data/users.json", jArrayUsers))return -1;

        return user.getId();
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null || !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }
        if(id==null || id <=0){
            throw new InvalidUserIdException();
        }

        //Checking if user exists...
        if(users_data.get(id) != null){
            //Deleting from JSON Array...
            JSONObject user_obj;
            for(int i = 0; i< jArrayUsers.size(); i++){
                user_obj  = (JSONObject) jArrayUsers.get(i);
                if(user_obj.get("id").equals(id.toString())){
                    jArrayUsers.remove(i);
                }
            }
            //Deleting from map
            users_data.remove(id);
            //Updating JSON File
            return writejArrayToFile("src/main/persistent_data/users.json", jArrayUsers);
        }
        else {
            return false;
        }
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(userLogged == null || !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        return (List<User>) new ArrayList<>(users_data.values());
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null || !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        if(id == null || id<=0){
            throw new InvalidUserIdException();
        }

        User user;
        if( (user = this.users_data.get(id)) != null ){
            return user;
        }
        else{
            return null;
        }
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {

        if(userLogged == null || !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }
        if(role == null ||( !role.equals("Administrator") && !role.equals("Cashier") && !role.equals("ShopManager"))){
            throw new InvalidRoleException("Invalid role");
        }
        if(id == null || id<=0){
            throw new InvalidUserIdException();
        }
        User user;
        if((user = users_data.get(id)) != null ){
            user.setRole(role);

            //Updating JSON Object in the JSON Array
            JSONObject user_obj = null;
            for(int i = 0; i< jArrayUsers.size(); i++){
                user_obj  = (JSONObject) jArrayUsers.get(i);
                if(user_obj.get("id").equals(id.toString())){
                    user_obj.put("role", user.getRole());
                }
            }

            //Updating JSON File
            return writejArrayToFile("src/main/persistent_data/users.json", jArrayUsers);
        }
        else{
            return false;
        }
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if(username == null || username.equals("")){
            throw new InvalidUsernameException();
        }

        if(password == null || password.equals("")){
            throw new InvalidPasswordException("Username or password wrong");
        }

        //Checking credentials
        for(User user: this.users_data.values()){
            if(username.equals(user.getUsername())){
                if( !password.equals(user.getPassword())){
                    return null;
                }
                else{
                    //Credentials ok!
                    this.userLogged = user;
                }
            }
        }
        return null;
    }

    @Override
    public boolean logout() {
        if(userLogged != null) {
            userLogged = null;
        }
        else {
            return false;
        }
        return true;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {

        //check privileges
        if(userLogged==null || (!"ShopManager".equals(userLogged.getRole())) && !"Administrator".equals(userLogged.getRole())) throw new UnauthorizedException();
        Integer productID;
        // check pricePerUnit
        if(pricePerUnit<=0) throw new InvalidPricePerUnitException();
        // check description
        if(description == null || description.isEmpty()) throw new InvalidProductDescriptionException();

        //check if productCode is valid and it is a number
        if(!barcodeIsValid(productCode)) {
            throw new InvalidProductCodeException();
        }
        // return -1 if there already exists a product with the same barcode
        if(productMap.values().stream().anyMatch(x->x.getBarCode().equals(productCode)))return -1;
        Integer id=assignId(this.productMap.keySet());
        // check if product code is positive and if it already present in map
        if(id<0 || this.productMap.get(id)!=null) throw new InvalidProductCodeException();


        ProductTypeImplementation p = new ProductTypeImplementation(id,productCode, description,pricePerUnit,note);

        System.out.println(p.getId() + " " + p.getBarCode() + " " + p.getProductDescription() + " " + p.getPricePerUnit() + " " + p.getNote());

        this.productMap.put(id,p);
        JSONObject pDetails = initializeJsonProductObject(p);

        this.jArrayProduct.add(pDetails);
        String filePath= "src/main/persistent_data/productTypes.json";
        if(!writejArrayToFile(filePath, jArrayProduct))System.out.println("Couldn't write to file"+filePath);

        return p.getId();
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        //check for invalid user
        if(this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))throw new UnauthorizedException();
        if(newDescription==null || "".equals(newDescription))throw new InvalidProductDescriptionException();
        // check productCode
        if(!barcodeIsValid(newCode))throw new InvalidProductCodeException();
        //check for invalid product id
        if(id == null || id<=0) throw new InvalidProductIdException();
        // checkpriceperunit
        if(newPrice<=0) throw new InvalidPricePerUnitException();
        // return false if another product already has the same barcode, but it is not the one we are updating
        if(productMap.values().stream().anyMatch(x->x.getBarCode().equals(newCode) && !x.getId().equals(id)))return false;
        if(productMap.get(id)==null)return false;
        ProductTypeImplementation p;
        // return false if product with given id doesn't exist
        if((p=(ProductTypeImplementation)productMap.get(id))==null)return false;

        // if it exists, update it in RAM
        //update object in map
        p.setProductDescription(newDescription);
        p.setBarCode(newCode);
        p.setPricePerUnit(newPrice);
        p.setNote(newNote);

        // update it on disk
        JSONObject pDetails = null;
        pDetails = initializeJsonProductObject(p);
        for(int i = 0; i< jArrayProduct.size(); i++){
            pDetails  = (JSONObject) jArrayProduct.get(i);
            if(pDetails.get("id").equals(id.toString())){
                jArrayProduct.set(i,initializeJsonProductObject(p));
                /*
                pDetails.put("description", newDescription);
                pDetails.put("barCode", newCode);
                pDetails.put("sellPrice", newPrice);
                pDetails.put("note", newNote);
                */

            }
        }
        //(?) I am not doing error handling on this write, if it fails, i should rollback the previous removal


        return writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);
    }


    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        //check for invalid user
        if(this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))) throw new UnauthorizedException();
        //check for invalid product id
        if(id== null || id<=0) throw new InvalidProductIdException();
        if(productMap.get(id)==null){
            System.out.println("no product with such id exists");
            return false;
        }

        //needs to remove object from memory array and commit to disk
        JSONObject pr = null;
        for(int i = 0; i< jArrayProduct.size(); i++){
            pr  = (JSONObject) jArrayProduct.get(i);
            if(pr.get("id").equals(id.toString())){
                jArrayProduct.remove(i);
            }
        }
        // writing to memory
        if(!writejArrayToFile("src/main/persistent_data/productTypes.json",jArrayProduct)){
            System.out.println("Failure while writing to memory");
            return false;
        }
        //remove object from map
        productMap.remove(id);
        return true;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException{
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }
        //return (List<ProductType>) new ArrayList<>(productMap.values());
        List<ProductType> copyList = productMap.values().stream().map( p -> new ProductTypeImplementation(p)).collect(Collectors.toList());
        return copyList;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }
        if(!barcodeIsValid(barCode)) throw new InvalidProductCodeException();
        try{
            return productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getBarCode().equals(barCode)).findFirst().get();
        }catch(Exception e){
            return null;
        }


    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }
        if(description==null)description="";
        String finalDescription = description;
        return productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getProductDescription().contains(finalDescription)).map(p -> (ProductType)new ProductTypeImplementation(p)).collect(Collectors.toList());
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {

        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }

        if(productId==null || productId<=0)throw new InvalidProductIdException();

        if(!productMap.containsKey(productId)){return false;}

        ProductTypeImplementation product = (ProductTypeImplementation) productMap.get(productId);
        System.out.println("Updating quantity of product: ("+ productId.toString()+")\n");
        System.out.println("Starting quantity: "+ product.getQuantity().toString() + "\n");
        System.out.println("Adding quantity: " + toBeAdded +"\n");

        if(!product.changeQuantity(toBeAdded))return false;
        //Updating JSON Object in the ProductType JSON Array
        JSONObject tmp;
        if (this.jArrayProduct != null) {
            for (int i=0;i<this.jArrayProduct.size();i++){
                tmp = (JSONObject) this.jArrayProduct.get(i);
                if( ((String)tmp.get("id")).equals(productId.toString()) ){
                    tmp.put("availableQty",product.getQuantity().toString());
                }
            }
        }
        System.out.println("Quantity added, new value: "+ product.getQuantity().toString()+"\n");

        //(?) I am not doing error handling on this write, if it fails, i should rollback the previous removal
        return writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }

        if( productId==null || productId<=0)throw new InvalidProductIdException();

        if("".equals(newPos))newPos=null;
        //if position is not null, check if it satisfies <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber> format
        if(newPos!=null && !newPos.matches("[0-9]*-[^0-9]*-[0-9]*"))throw new InvalidLocationException();


        //if position is not unique, or productId has no match return false
        ProductTypeImplementation p = (ProductTypeImplementation) productMap.get(productId);
        String finalNewPos = newPos;
        if(p==null || (newPos!=null && getAllProductTypes().stream().anyMatch(pr -> pr.getLocation() != null && pr.getLocation().equals(finalNewPos))))return false;

        // updating location
        p.setLocation(newPos);

        JSONObject pDetails;
        pDetails = initializeJsonProductObject(p);

        // removing the old instance from the jarray
        //needs to remove object from memory array
        JSONObject pr = null;
        for(int i = 0; i< jArrayProduct.size(); i++){
            pr  = (JSONObject) jArrayProduct.get(i);
            if(pr.get("id").equals(productId.toString())){
                jArrayProduct.remove(i);
            }
        }
        // adding the product to the jarray
        jArrayProduct.add(pDetails);

        //(?) I am not doing error handling on this write, if it fails, i should rollback the previous removal
        return writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);
    }

    @Override
    public Integer issueOrder(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        //Exception checks
        if( productCode == null || productCode.equals("")){throw new InvalidProductCodeException();}
        if( quantity <= 0 ){throw new InvalidQuantityException();}
        if( pricePerUnit <= 0 ){throw new InvalidPricePerUnitException();}
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))
            ){throw new UnauthorizedException();}

        //return -1 if product doesn't exist
        if(this.getProductTypeByBarCode(productCode) == null){
            return -1;
        }
        //otherwise finally generates the order in "issued" state
        OrderImpl order = new OrderImpl(productCode,quantity,pricePerUnit);
        this.accountBook.addOperation(order);

        if(writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations()))return order.getOrderId();
        else return -1;

    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        int orderId = issueOrder(productCode, quantity, pricePerUnit);

        if( orderId == -1){ return -1; }
        System.out.println("look at operation with balanceId: " + this.accountBook.getOperation(orderId).getBalanceId());
        //check for the order existence

        if (!(this.accountBook.getOperation(orderId) instanceof OrderImpl)) {return -1;}

        OrderImpl order = (OrderImpl) this.accountBook.getOperation(orderId);

        //check if the balance is enough to pay the order, operation is '+' because getMoney() has a negative value for subclass OrderImpl
        if(accountBook.getBalance() + order.getMoney() < 0){ return -1; }

        //if it is enough, change status and change balance
        accountBook.changeBalance(order.getMoney());
        order.setStatus("PAYED");
        //Updating JSON Object in the JSON Array
        JSONObject tmp;
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(((Integer) orderId).toString()) ){
                    tmp.put("status","PAYED");
                }
            }
        }
        //Updating JSON File
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        //Updating JSON File
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());

        return orderId;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        //exceptions
        if(orderId == null || orderId <= 0){throw new InvalidOrderIdException();}
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))
        ){throw new UnauthorizedException();}

        //check for the order existence
        if( !(this.accountBook.getOperation(orderId) instanceof OrderImpl) ){
            System.out.println("order with id "+orderId+"does not exist");
            return false;
        }
        System.out.println("executing payOrder() of orderId: " + orderId);
        //check if already payed
        OrderImpl order = (OrderImpl) this.accountBook.getOperation(orderId);
        if(!order.getStatus().equals("ISSUED") && !order.getStatus().equals("ORDERED")){ return false; }

        //check if the balance is enough to pay the order, operation is '+' because getMoney() has a negative value for subclass OrderImpl
        if(accountBook.getBalance() + order.getMoney() < 0){ return false; }
        //if it is enough, change status and change balance
        accountBook.changeBalance(order.getMoney());
        order.setStatus("PAYED");
        //Updating JSON Object in the JSON Array
        JSONObject tmp;
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(orderId.toString()) ){
                    tmp.put("status","PAYED");
                }
            }
        }
        //Updating JSON File
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        return true;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        //exceptions
        if(orderId == null || orderId <= 0){throw new InvalidOrderIdException();}
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))
        ){throw new UnauthorizedException();}

        //making sure the order exists, has an existing location assigned and is in
        if( !(accountBook.getOperation(orderId) instanceof OrderImpl) ){ return false; }
        OrderImpl order = (OrderImpl) accountBook.getOperation(orderId);
        if(order == null){
            System.out.println("RETRIEVED ORDER IS NULL");
            return false;
        }
        ProductType product = productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getBarCode().equals(order.getProductCode())).findFirst().get();
        if(product.getLocation() == null || product.getLocation().equals("")){ throw new InvalidLocationException(); }

        //returning false if order was not in a ORDERED (ISSUED) / COMPLETED state
        if(!order.getStatus().equals("ORDERED") && !order.getStatus().equals("ISSUED") && !order.getStatus().equals("COMPLETED") && !order.getStatus().equals("PAYED") ){
            return false;
        }
        //registering the order arrival and updating the product quantity (unless it was already completed)
        if(order.getStatus().equals("COMPLETED")){
            return true;
        }
        product.setQuantity( product.getQuantity() + order.getQuantity() );
        order.setStatus("COMPLETED");
        //Updating JSON Object in the ProductType JSON Array
        JSONObject tmp;
        if (this.jArrayProduct != null) {
            for (int i=0;i<this.jArrayProduct.size();i++){
                tmp = (JSONObject) this.jArrayProduct.get(i);
                if( ((String)tmp.get("barCode")).equals(order.getProductCode()) ){
                    tmp.put("availableQty",product.getQuantity().toString());
                }
            }
        }
        //Updating JSON File
        writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);

        //Updating JSON Object in the operations JSON Array
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(orderId.toString()) ){
                    tmp.put("status","COMPLETED");
                }
            }
        }
        //Updating JSON File
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());

        return true;
    }

    @Override
    public boolean recordOrderArrivalRFID(Integer orderId, String RFIDfrom) throws InvalidOrderIdException, UnauthorizedException, 
InvalidLocationException, InvalidRFIDException {
        //exceptions and false returns
        if(orderId == null || orderId <= 0){ throw new InvalidOrderIdException();}
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))
        ){throw new UnauthorizedException();}

        if(RFIDfrom.length() != 12 || !RFIDfrom.matches("[0-9]{12}")){ throw new InvalidRFIDException();}

        //making sure the order exists, has an existing location assigned and is in
        if( !(accountBook.getOperation(orderId) instanceof OrderImpl) ){ return false; }
        OrderImpl order = (OrderImpl) accountBook.getOperation(orderId);
        if(order == null){
            System.out.println("RETRIEVED ORDER IS NULL");
            return false;
        }

        //verify rfids are not already present in the database.
        String rfidString;
        Long rfid = Long.parseLong(RFIDfrom);
        for(int i=0; i<order.getQuantity(); i++){
            rfidString = String.format("%012d",rfid + i);
            if(rfidMap.containsKey(rfidString)){
                System.out.println("RFID "+rfidString+" already present in Database!!");
                throw new InvalidRFIDException();
            }
        }

        ProductType product = productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getBarCode().equals(order.getProductCode())).findFirst().get();
        if(product.getLocation() == null || product.getLocation().equals("")){ throw new InvalidLocationException(); }

        //returning false if order was not in a ORDERED (ISSUED) / COMPLETED state
        if(!order.getStatus().equals("ORDERED") && !order.getStatus().equals("ISSUED") && !order.getStatus().equals("COMPLETED") && !order.getStatus().equals("PAYED") ){
            return false;
        }

        //CALL TO recordOrderArrival API method
        if(!recordOrderArrival(orderId)){ return false; }

        //Adding the RFIDS to the Database (json array and map)
        JSONObject tmp;
        Integer pID = product.getId();
        for(int i=0; i<order.getQuantity(); i++){
            rfidString = String.format("%012d",rfid + i);
            rfidMap.put(rfidString,pID);
            tmp = new JSONObject();
            tmp.put("rfid",rfidString);
            tmp.put("id",pID.toString());
            jArrayRfid.add(tmp);
        }
        //Updating JSON File
        writejArrayToFile("src/main/persistent_data/productRfids.json", jArrayRfid);

        return true;
    }
    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        //exception
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))
        ){throw new UnauthorizedException();}

        return accountBook.getOperationsMap().values().stream()
                .filter( balOp -> balOp instanceof OrderImpl )
                .map( balOp -> new OrderAdapter((OrderImpl) balOp))
                .collect(Collectors.toList());
    }

    @Override
    public Integer defineCustomer(String customerName) throws InvalidCustomerNameException, UnauthorizedException {
        // verifying user privileges
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }
        // verifying  customer name is valid
        if(customerName == null || customerName.equals("")){
            throw new InvalidCustomerNameException();
        }
        // the customer name should be unique
        for(Customer c: this.customersMap.values()){
            if( customerName.equals(c.getCustomerName())){
                return -1;
            }
        }

        //Create customer
        System.out.println(customersMap.size());
        Customer c = new CustomerImplementation(customerName, assignId(this.customersMap.keySet()), 0, null);
        //Add customer to map
        customersMap.put(c.getId(), c);

        /* Adding to JSON Array (needed to update thr JSON file with new user data) */
        /* ------------------------------------------------------------------------ */

        JSONObject userDetails = new JSONObject();
        userDetails.put("id", c.getId().toString());
        userDetails.put("card", c.getCustomerCard());
        userDetails.put("name", c.getCustomerName());
        userDetails.put("points",c.getPoints().toString());


        /* JSON Array updating...
           NOTE: id is used to insert object so that when there's the need
           to delete it it's easier to find it
         */
        this.jArrayCustomers.add(userDetails);

        //Updating file
        if(!writejArrayToFile("src/main/persistent_data/customers.json", jArrayCustomers))return -1;

        return c.getId();
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }

        if(id == null || id <= 0){
            throw new InvalidCustomerIdException();
        }

        if(newCustomerName == null || newCustomerName.equals("") ){
            throw new InvalidCustomerNameException();
        }

        if(!newCustomerCard.matches("\\d{10}")){
            throw new InvalidCustomerCardException();
        }

        if(id == null || id<=0){
            throw new InvalidCustomerIdException();
        }
        if(!this.customersMap.containsKey(id)){
            return false;
        }

        if(newCustomerCard != null) {
            //Checking if card code is already assigned to someone
            for (Customer c : customersMap.values()) {
                if (c != customersMap.get(id)) {
                    if (c.getCustomerCard() != null) {
                        if (newCustomerCard.equals(c.getCustomerCard())) {
                            return false;
                        }
                    }
                }
            }

            //Updating values
            customersMap.get(id).setCustomerCard(newCustomerCard);
            customersMap.get(id).setCustomerName(newCustomerName);

            JSONObject customer_obj = null;
            //Updating JSON Object in the JSON Array
            for (int i = 0; i < jArrayCustomers.size(); i++) {
                customer_obj = (JSONObject) jArrayCustomers.get(i);
                if (customer_obj.get("id").equals(id.toString())) {
                    customer_obj.put("name", customersMap.get(id).getCustomerName());
                    customer_obj.put("card", customersMap.get(id).getCustomerCard());
                }
            }

            //Updating JSON File
            return writejArrayToFile("src/main/persistent_data/customers.json", jArrayCustomers);
        }
        else{
            return false;
        }
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }

        if(id == null || id<=0){
            throw new InvalidCustomerIdException();
        }

        //Checking if customer exists...
        if(customersMap.get(id) != null){
            //Deleting from JSON Array...
            JSONObject customer_obj = null;
            for(int i = 0; i< jArrayCustomers.size(); i++){
                customer_obj  = (JSONObject) jArrayCustomers.get(i);
                if(customer_obj.get("id").equals(id.toString())){
                    jArrayCustomers.remove(i);
                }
            }
            //Deleting from map
            customersMap.remove(id);

            //Updating JSON File
            return writejArrayToFile("src/main/persistent_data/customers.json", jArrayCustomers);

        }
        else {
            return false;
        }
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }
        if(id == null || id<=0){
            throw new InvalidCustomerIdException();
        }
        if(!this.customersMap.containsKey(id)){
            return null;
        }

        return this.customersMap.get(id);
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }

        return (List<Customer>) new ArrayList<>(customersMap.values());
    }

    @Override
    public String createCard() throws UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }
        String serialNumber = new String();
        //Generating card...
        for(int i=0; i<10; i++){
            Random rand = new Random(); //instance of random class
            Integer int_random = rand.nextInt(9);
            serialNumber += int_random.toString();
        }
        //Checking if it's already assigned
        for(Customer c: customersMap.values()){
            if(c.getCustomerCard() != null) {
                if (c.getCustomerCard().equals(serialNumber.toString())) {
                    System.out.println("Error");
                }
            }
        }

        return serialNumber;
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }
        if(customerId == null || customerId<=0 ){
            throw new InvalidCustomerIdException();
        }
        if(customerCard == null || !customerCard.matches("\\d{10}") || customerCard.equals("")){
            throw new InvalidCustomerCardException();
        }

        for (Customer c: customersMap.values()){
            if(c != customersMap.get(customerId)) {
                if (customerCard.equals(c.getCustomerCard())) {
                    return false;
                }
            }
        }

        //Checking if customer exists...
        if(customersMap.containsKey(customerId)){
            Customer c= customersMap.get(customerId);
            JSONObject customer_obj = null;
            //Updating JSON Object in the JSON Array
            for(int i = 0; i< jArrayCustomers.size(); i++){
                customer_obj  = (JSONObject) jArrayCustomers.get(i);
                if(customer_obj.get("id").equals(c.getId().toString())){
                    c.setCustomerCard(customerCard);
                    customer_obj.put("card", c.getCustomerCard());
                }
            }
            return writejArrayToFile("src/main/persistent_data/customers.json", jArrayCustomers);
        }


        return false;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager") && !this.userLogged.getRole().equals("Cashier")))
        {
            throw new UnauthorizedException();
        }
        if(customerCard == null || !customerCard.matches("\\d{10}") || customerCard.equals("")){
            throw new InvalidCustomerCardException();
        }

        //Checking validity of data
        for(Customer c: customersMap.values()){
            if(c.getCustomerCard() != null) {
                if (c.getCustomerCard().equals(customerCard)) {
                    if (c.getPoints() + pointsToBeAdded >= 0) {                  //In case points are negative -> enough points on card?
                        c.setPoints(c.getPoints() + pointsToBeAdded);           //Updating points
                        JSONObject customer_obj = null;
                        //Updating JSON Object in the JSON Array
                        for(int i = 0; i< jArrayCustomers.size(); i++){
                            customer_obj  = (JSONObject) jArrayCustomers.get(i);
                            if(customer_obj.get("card") != null) {
                                if (customer_obj.get("card").equals(c.getCustomerCard())) {
                                    customer_obj.put("points", new Integer(c.getPoints()).toString());
                                }
                            }
                        }
                        return writejArrayToFile("src/main/persistent_data/customers.json", jArrayCustomers);
                    }
                }
            }
        }

        return false;
    }

    @Override
    public Integer startSaleTransaction() throws UnauthorizedException {
        //exceptions
        if(userLogged == null){
            throw new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){
            throw new UnauthorizedException();
        }
        //initialize the ongoing sale with a new instance
        this.ongoingSale = new SaleTransactionImplementation();
        return ongoingSale.getBalanceId();
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        //exceptions
        if(transactionId == null || transactionId <= 0){throw  new InvalidTransactionIdException();}
        if(productCode==null || productCode.equals("") || !barcodeIsValid(productCode)){throw new InvalidProductCodeException();}
        if(amount < 0){throw  new InvalidQuantityException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //false returns
        if(this.ongoingSale == null || transactionId != this.ongoingSale.getBalanceId()){return false;}
        if(productMap.values().stream().noneMatch(p -> p.getBarCode().equals(productCode))){return false;}
        ProductType product = productMap.values().stream().filter(p -> p.getBarCode().equals(productCode)).findFirst().get();;
        if(product.getQuantity() < amount){return false;}

        //if product already in sale, update quantity, otherwise, create the new Ticket Entry
        TicketEntry entry;
        if(ongoingSale.getEntries().stream().anyMatch( e -> e.getBarCode().equals(productCode))){
            entry = ongoingSale.getEntries().stream()
                    .filter( e-> e.getBarCode().equals(productCode))
                    .findFirst().get();
            entry.setAmount( entry.getAmount()+amount);
            System.out.println("Adding "+amount+" to already existing entry");
        }
        else{
            entry = new TicketEntryImpl(product.getBarCode(),product.getProductDescription(),amount,product.getPricePerUnit(),0.0);
            ongoingSale.entries.add(entry);
            System.out.println("Generated a new entry");
        }

        //updating the quantity available of the product on the shelves
        product.setQuantity(product.getQuantity() - amount);
        //Updating JSON Object in the ProductType JSON Array
        JSONObject tmp;
        if (this.jArrayProduct != null) {
            for (int i=0;i<this.jArrayProduct.size();i++){
                tmp = (JSONObject) this.jArrayProduct.get(i);
                if( ((String)tmp.get("barCode")).equals(productCode) ){
                    tmp.put("availableQty",product.getQuantity().toString());
                }
            }
        }
        //Updating JSON File
        writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);
        return true;
    }

    @Override
    public boolean addProductToSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException{
        if(transactionId==null || transactionId<=0)throw new InvalidTransactionIdException();
        if(RFID == null || RFID.equals("") || !RFID.matches("[0-9]{12}")) throw new InvalidRFIDException();
        if(userLogged == null )throw new UnauthorizedException();

        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        // return false if the rfid does not exist
        if(!this.rfidMap.containsKey(RFID)){
            System.out.println("RFID: " + RFID + " non present in the system");
            return false;
        }

        // return false if the transaction id does not identify an open transaction and started transaction
        if(this.ongoingSale == null || transactionId != this.ongoingSale.getBalanceId()){return false;}

        //if product already in sale, update quantity, otherwise, create the new Ticket Entry
        ProductType product = (productMap.get(rfidMap.get(RFID)));
        String productCode = product.getBarCode();
        try {
            if(!addProductToSale(transactionId,productCode,1)){
                return false;
            }
            else{
                //if product has been added, add rfid into sale transaction
                ProductRfid pRfid = new ProductRfid(RFID,rfidMap.get(RFID));
                ongoingSale.rfids.add(pRfid);
                //and remove it from database (map and file json)
                rfidMap.remove(RFID);
                JSONObject tmp;
                for(int i = 0; i< jArrayRfid.size(); i++){
                    tmp = (JSONObject) jArrayRfid.get(i);
                    if(tmp.get("rfid").equals(RFID)){
                        jArrayRfid.remove(i);
                    }
                }
                // writing to memory
                writejArrayToFile("src/main/persistent_data/productRfids.json",jArrayRfid);
            }
        } catch (InvalidProductCodeException e){
            System.out.println("This product has an invalid product code!");
            return false;
        }
        /*TicketEntry entry;
        if(ongoingSale.getEntries().stream().anyMatch( e -> e.getBarCode().equals(productCode))){
            entry = ongoingSale.getEntries().stream()
                    .filter( e-> e.getBarCode().equals(productCode))
                    .findFirst().get();
            entry.setAmount( entry.getAmount()+1);
            System.out.println("Adding 1 item of product: " + productCode +" to already existing entry");
        }
        else{
            entry = new TicketEntryImpl(product.getBarCode(),product.getProductDescription(),1,product.getPricePerUnit(),0.0);
            ongoingSale.entries.add(entry);
            System.out.println("Generated a new entry");
        }

        //updating the quantity available of the product on the shelves
        product.setQuantity(product.getQuantity() - 1);
        //Updating JSON Object in the ProductType JSON Array
        JSONObject tmp;
        if (this.jArrayProduct != null) {
            for (int i=0;i<this.jArrayProduct.size();i++){
                tmp = (JSONObject) this.jArrayProduct.get(i);
                if( ((String)tmp.get("barCode")).equals(productCode) ){
                    tmp.put("availableQty",product.getQuantity().toString());
                }
            }
        }
        //Updating JSON File
        writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);*/

        return true;

    }
    
    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        //exceptions
        if(transactionId == null || transactionId <= 0){throw  new InvalidTransactionIdException();}
        if(productCode==null || productCode.equals("") || !barcodeIsValid(productCode)){throw new InvalidProductCodeException();}
        if(amount < 0){throw  new InvalidQuantityException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //false returns
        if(transactionId != this.ongoingSale.getBalanceId()){return false;}
        //false for non existent product code
        if(productMap.values().stream().noneMatch(p -> p.getBarCode().equals(productCode))){return false;}
        ProductType product = productMap.values().stream().filter(p -> p.getBarCode().equals(productCode)).findFirst().get();
        //false for insufficient quantity
        if(ongoingSale.getEntries().stream().noneMatch(e -> e.getBarCode().equals(productCode))){return false;}
        TicketEntry entry = ongoingSale.getEntries().stream().filter(e -> e.getBarCode().equals(productCode)).findFirst().get();
        if(entry.getAmount() < amount){return false;}

        //updating the product quantity of the relative entry
        entry.setAmount(entry.getAmount() - amount);
        //eventually deleting the entry if product quantity reached 0
        if(entry.getAmount() == 0){ongoingSale.entries.remove(entry);}

        //updating the quantity available of the product on the shelves
        product.setQuantity(product.getQuantity() + amount);
        //Updating JSON Object in the ProductType JSON Array
        JSONObject tmp;
        if (this.jArrayProduct != null) {
            for (int i=0;i<this.jArrayProduct.size();i++){
                tmp = (JSONObject) this.jArrayProduct.get(i);
                if( ((String)tmp.get("barCode")).equals(productCode) ){
                    tmp.put("availableQty",product.getQuantity().toString());
                }
            }
        }
        //Updating JSON File
        writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);
        return true;
    }

    @Override
    public boolean deleteProductFromSaleRFID(Integer transactionId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, InvalidQuantityException, UnauthorizedException{
        //exceptions
        if(userLogged == null){throw new UnauthorizedException();}
        if(transactionId == null || transactionId <= 0){throw  new InvalidTransactionIdException();}
        if(RFID == null || RFID.equals("") || !RFID.matches("[0-9]{12}")) throw new InvalidRFIDException();
        /*NB: in effetti non dovrebbe essere presente nel sistema essendo stato tolto e aggiunto alla ongoingSale
              quindi non va fatto questo check
        if(!this.rfidMap.containsKey(RFID)){
            System.out.println("RFID: " + RFID + " non present in the system");
            return false;
        }*/

        //check if the rfid is present in the open saleTransaction (ongoingSale)
        if(ongoingSale.rfids.stream().noneMatch( r -> r.RFID.equals(RFID))){return false;}
        Integer productId = ongoingSale.rfids.stream()
                            .filter(r -> r.RFID.equals(RFID))
                            .map(r->r.productId).findFirst().get();
        ProductType product = (productMap.get(productId));
        String productCode = product.getBarCode();

        //false returns
        if(transactionId != this.ongoingSale.getBalanceId()){return false;}
        //false for non existent product code
        if(productMap.values().stream().noneMatch(p -> p.getBarCode().equals(productCode))){return false;}

        //call to deleteProductFromSale method
        try{
            if(!deleteProductFromSale(transactionId,productCode,1)){
                return false;
            }
            else{
                //if product has been deleted, delete also it's ProductRfid object
                int i;
                for(i=0; i<ongoingSale.rfids.size(); i++){
                    if(ongoingSale.rfids.get(i).RFID.equals(RFID)){
                        ongoingSale.rfids.remove(i);
                    }
                }
                //and add the rfid back to the Database (json array and map)
                JSONObject tmp;
                rfidMap.put(RFID,productId);
                tmp = new JSONObject();
                tmp.put("rfid",RFID);
                tmp.put("id",productId.toString());
                jArrayRfid.add(tmp);

                //Updating JSON File
                writejArrayToFile("src/main/persistent_data/productRfids.json", jArrayRfid);

            }
        }catch(InvalidProductCodeException e){
            System.out.println("no productType with such product code (barcode) in the system!\n");
            return false;
        }

        /*TicketEntry entry = ongoingSale.getEntries().stream().filter(e -> e.getBarCode().equals(productCode)).findFirst().get();
        //updating the product quantity of the relative entry
        entry.setAmount(entry.getAmount() + 1);

        //updating the quantity available of the product on the shelves
        product.setQuantity(product.getQuantity() + 1);
        //Updating JSON Object in the ProductType JSON Array
        JSONObject tmp;
        if (this.jArrayProduct != null) {
            for (int i=0;i<this.jArrayProduct.size();i++){
                tmp = (JSONObject) this.jArrayProduct.get(i);
                if( ((String)tmp.get("barCode")).equals(productCode) ){
                    tmp.put("availableQty",product.getQuantity().toString());
                }
            }
        }
        //Updating JSON File
        writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);
        return true;*/
        return true;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        //exceptions
        if(transactionId == null || transactionId <= 0){throw new InvalidTransactionIdException();}
        if(productCode==null || productCode.equals("") || !barcodeIsValid(productCode)){throw new InvalidProductCodeException();}
        if(discountRate < 0 || discountRate >=1){throw new InvalidDiscountRateException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //false returns
        if(transactionId != this.ongoingSale.getBalanceId()){return false;}
        //false for non existent product code
        if(productMap.values().stream().noneMatch(p -> p.getBarCode().equals(productCode))){return false;}
        //false for non existent product code in the ongoing sale transaction
        if(ongoingSale.getEntries().stream().noneMatch(e -> e.getBarCode().equals(productCode))){return false;}

        //get the entry relative to the product code and set the discount rate
        TicketEntry entry = ongoingSale.getEntries().stream().filter(e -> e.getBarCode().equals(productCode))
                            .findFirst().get();
        entry.setDiscountRate(discountRate);

        return true;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        //exceptions
        if(transactionId == null || transactionId <= 0){throw new InvalidTransactionIdException();}
        if(discountRate < 0 || discountRate >=1){throw new InvalidDiscountRateException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //if the transaction is the ongoing sale transaction,
        // set it's discount rate, otherwise look for a matching closed sale transaction
        if(transactionId == this.ongoingSale.getBalanceId()){
            ongoingSale.setDiscountRate(discountRate);
        }
        else{
            //if it doesn't match any close (but not payed) sale, return false,
            // else update the sale disocuntRate of the matched sale saved in the persistent json file
            if(accountBook.getOperationsMap().values().stream()
                    .filter( o -> o instanceof SaleTransactionImplementation)
                    .filter( o -> o.getBalanceId() == transactionId)
                    .map( o -> (SaleTransactionImplementation) o)
                    .filter( o -> o.getStatus().equals("CLOSED"))
                    .noneMatch( o -> o.getBalanceId() == transactionId))
            {
                return false;
            }
            else {
                SaleTransactionImplementation sale = accountBook.getOperationsMap().values().stream()
                        .filter( o -> o instanceof SaleTransactionImplementation)
                        .filter( o -> o.getBalanceId() == transactionId)
                        .map( o -> (SaleTransactionImplementation) o)
                        .filter( o -> o.getStatus().equals("CLOSED"))
                        .findFirst().get();
                sale.setDiscountRate(discountRate);

                //Updating JSON Object in the JSON Array
                JSONObject tmp;
                if (accountBook.getjArrayOperations() != null) {
                    for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                        tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                        if( ((String)tmp.get("balanceId")).equals(transactionId.toString()) ){
                            tmp.put("discountRate",Double.toString(discountRate));
                        }
                    }
                }
                //Updating JSON File
                writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
            }
        }
        return true;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        //exceptions
        if(transactionId == null || transactionId <= 0){throw new InvalidTransactionIdException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        SaleTransactionImplementation sale;
        //if it's a reference to the (open) ongoing sale, reference it;
        //else search for it between the closed and payed sales
        if(ongoingSale.getBalanceId() == transactionId){
            sale = ongoingSale;
        }
        else{
            //if it doesn't match any CLOSED or PAYED sale, return false,
            // else update the sale disocuntRate of the matched sale saved in the persistent json file
            if(accountBook.getOperationsMap().values().stream()
                    .filter( o -> o instanceof SaleTransactionImplementation)
                    .filter( o -> o.getBalanceId() == transactionId)
                    .map( o -> (SaleTransactionImplementation) o)
                    .noneMatch( o -> o.getBalanceId() == transactionId))
            {
                return -1;
            }
            else {
                sale = accountBook.getOperationsMap().values().stream()
                        .filter( o -> o instanceof SaleTransactionImplementation)
                        .filter( o -> o.getBalanceId() == transactionId)
                        .map( o -> (SaleTransactionImplementation) o)
                        .findFirst().get();
            }
        }

        //compute, truncate by typecasting and return points
        return (int) sale.getMoney()/10;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        //exceptions
        if(transactionId == null || transactionId <= 0){throw new InvalidTransactionIdException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //false return in case of nonexisting open sale with matching transactionId
        if(ongoingSale==null || ongoingSale.getBalanceId() != transactionId){return  false;}
        //false return if transaction was already closed
        if(ongoingSale.getStatus().equals("CLOSED")){return false;}

        //computing the money of the transaction
        Double totalMoney = ongoingSale.getEntries().stream()
                .map( e -> (e.getPricePerUnit() * e.getAmount() * (1.0 - e.getDiscountRate())) )
                .reduce(0.0,(e1,e2) -> e1 + e2);
        totalMoney = totalMoney * (1.0 - ongoingSale.getDiscountRate());
        //rounding it to 2 decimal places
        totalMoney = new BigDecimal(Double.toString(totalMoney)).setScale(2,RoundingMode.HALF_UP).doubleValue();
        ongoingSale.setMoney(totalMoney);

        //closing the transaction and adding it to the persistent data
        ongoingSale.setStatus("CLOSED");
        this.accountBook.addOperation(ongoingSale);
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        ongoingSale = null;
        return true;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        //exceptions
        if(saleNumber == null || saleNumber <= 0){throw new InvalidTransactionIdException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}


        //if it doesn't match any close (but not payed) sale, return false,
        // else update the sale disocuntRate of the matched sale saved in the persistent json file
        if(accountBook.getOperationsMap().values().stream()
                .filter( o -> o instanceof SaleTransactionImplementation)
                .filter( o -> o.getBalanceId() == saleNumber)
                .map( o -> (SaleTransactionImplementation) o)
                .filter( o -> o.getStatus().equals("CLOSED"))
                .noneMatch( o -> o.getBalanceId() == saleNumber))
        {
            return false;
        }
        else {
            //SaleTransactionImplementation sale = ((SaleTransactionImplementation) accountBook.getOperationsMap().get(saleNumber));
            //removing the operation from the map
            accountBook.getOperationsMap().remove(saleNumber);

            //Removing JSON Object in the JSON Array
            JSONObject tmp;
            if (accountBook.getjArrayOperations() != null) {
                for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                    tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                    if( ((String)tmp.get("balanceId")).equals(saleNumber.toString()) ){
                        accountBook.getjArrayOperations().remove(tmp);
                    }
                }
            }
            //Updating JSON File
            writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        }

        return true;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        //exceptions
        if(transactionId == null || transactionId <= 0){throw new InvalidTransactionIdException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        if(!accountBook.getOperationsMap().containsKey(transactionId)
            || !(accountBook.getOperation(transactionId) instanceof SaleTransactionImplementation) ){return null;}

        SaleTransaction sale = new SaleTransactionAdapter( (SaleTransactionImplementation) accountBook.getOperation(transactionId));
        return sale;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        //exceptions
        if(userLogged == null){
            throw new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){
            throw new UnauthorizedException();
        }
        if(saleNumber == null || saleNumber <= 0){throw new InvalidTransactionIdException();}
        System.out.println("startReturnTransaction with saleNumber "+saleNumber.toString());

        //get the operation and verify it's a sale transaction and it has been already payed otherwise return -1
        BalanceOperation operation = accountBook.getOperation(saleNumber);
        if(operation == null || !(operation instanceof SaleTransactionImplementation)){
            System.out.println("operation was not a sale transaction");return -1;}
        SaleTransactionImplementation sale = (SaleTransactionImplementation) operation;
        if (!sale.getStatus().equals("PAYED") && !sale.getStatus().equals("COMPLETED")){
            System.out.println("sale was not PAYED!, status:"+sale.getStatus()+"\n");return -1;}

        //initialize the ongoing return with a new instance
        this.ongoingReturn = new ReturnTransaction(saleNumber);
        this.ongoingReturn.setSaleDiscount(sale.getDiscountRate());
        return ongoingReturn.getBalanceId();
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        //exceptions
        if(returnId == null || returnId <= 0){throw  new InvalidTransactionIdException();}
        if(productCode==null || productCode.equals("") || !barcodeIsValid(productCode)){throw new InvalidProductCodeException();}
        if(amount <= 0){throw  new InvalidQuantityException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //false returns
        //return transaction does not exist
        if(ongoingReturn == null || returnId != this.ongoingReturn.getBalanceId()){return false;}
        //case were the product does not exist
        ProductType product = productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getBarCode().equals(productCode)).findFirst().map( p -> (ProductType)new ProductTypeImplementation(p)).get();
        if(product == null ){return false;}
        SaleTransactionImplementation sale = (SaleTransactionImplementation) accountBook.getOperation(ongoingReturn.getSaleId());
        //case where the product returned was not part of the referenced sale entries
        if(sale.getEntries().stream().noneMatch(e -> e.getBarCode().equals(productCode))){return false;}
        //the amount is higher than the one sold in the referenced sale
        if(amount > sale.getEntries().stream().filter(e ->e.getBarCode().equals(productCode)).findFirst().get().getAmount()){return false;}


        //if product already in returnTransaction, update quantity, otherwise, create the new Ticket Entry
        TicketEntry entry;
        if(ongoingReturn.getReturnEntries().stream().anyMatch( e -> e.getBarCode().equals(productCode))){
            entry = ongoingReturn.getReturnEntries().stream()
                    .filter( e-> e.getBarCode().equals(productCode))
                    .findFirst().get();
            entry.setAmount( entry.getAmount()+amount);
            System.out.println("Adding "+amount+" to already existing RETURN entry");
        }
        else{
            TicketEntry saleEntry = sale.getEntries().stream()
                    .filter( e-> e.getBarCode().equals(productCode))
                    .findFirst().get();
            entry = new TicketEntryImpl(saleEntry.getBarCode(),saleEntry.getProductDescription(),amount,saleEntry.getPricePerUnit(),saleEntry.getDiscountRate());
            ongoingReturn.getReturnEntries().add(entry);
            System.out.println("Generated a new RETURN entry");
        }

        return true;
    }

    @Override
    public boolean returnProductRFID(Integer returnId, String RFID) throws InvalidTransactionIdException, InvalidRFIDException, UnauthorizedException 
    {
        // Throwing all exceptions
        if(returnId==null || returnId<=0 ) throw new InvalidTransactionIdException();
        if(RFID == null || RFID.equals("") || !RFID.matches("[0-9]{12}")) throw new InvalidRFIDException();
        if(userLogged == null )throw new UnauthorizedException();
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}


        //check for the referenced saleTransaction (into the returnTransaction) existence
        if(accountBook.getOperation(ongoingReturn.getSaleId()) == null){return false;}
        SaleTransactionImplementation sale = ((SaleTransactionImplementation) accountBook.getOperation(ongoingReturn.getSaleId()));
        //check if the rfid is present in the saleTransaction referenced from the returnSaleTransaction
        if(sale.rfids.stream().noneMatch(r -> r.RFID.equals(RFID))){return false;}
        ProductRfid pRfid = sale.rfids.stream().filter(r -> r.RFID.equals(RFID)).findFirst().get();

        //return false if RFID is not associated to any product id
        Integer pid = pRfid.productId;
        if(pid== null)return false;
        // return false if the transaction id does not identify an open transaction and started transaction
        if(this.ongoingReturn == null || returnId != this.ongoingReturn.getBalanceId()){return false;}
        ProductType product = this.productMap.get(pid);
        if(product == null){return false;} //false is productType does not exist in the system
        String productCode = product.getBarCode();

        try{
            if(!returnProduct(returnId,productCode,1)){
                return false;
            }
            else{
                //if product has been added to sale, add the reference of the rfid in the sale to the return transaction
                ongoingReturn.rfids.add(pRfid);
            }
        } catch(InvalidProductCodeException e){
            System.out.println("Product Code of identified product is invalid!");
        } catch(InvalidQuantityException e){
            System.out.println("specified quantity is an invalid value <= 0!");
        }

        //return false if the product is not present in the returntransaction
        /*
        if(ongoingReturn.rfids.stream().noneMatch( r -> r.RFID.equals(RFID))){return false;}
        Integer productId = ongoingReturn.rfids.stream()
                .filter(r -> r.RFID.equals(RFID))
                .map(r->r.productId).findFirst().get();

        ProductType product = (productMap.get(productId));
        String productCode = product.getBarCode();

        try{
            returnProduct(returnId,productCode,1);
            updateQuantity(productId,-1); // we remove the returned item from the inventory
        }
        catch(Exception e){
            return false; // not really sure if this is the right way to handle it
        }
        */
        return true;
    }


    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        //exceptions
        if(returnId == null || returnId <= 0){throw new InvalidTransactionIdException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //return false for returnId not being the id of the active return transaction
        if(ongoingReturn != null){ if(ongoingReturn.getBalanceId() != returnId){return false;}}else{return false;}

        SaleTransactionImplementation sale = (SaleTransactionImplementation) accountBook.getOperation(ongoingReturn.getSaleId());
        //return false for non available sale Transaction (db problems)
        if(sale == null){return false;}

        //undoing (by de-referencing) the return transaction if commit == false
        if(commit==false){
            ongoingReturn = null;
            return true;
        }

        //if commit==true
        List<TicketEntry> saleEntries = sale.getEntries();
        List<TicketEntry> returnEntries = ongoingReturn.getReturnEntries();
        ProductType product;

        for(TicketEntry returnE : returnEntries){
            System.out.println("Return Entry product to remove: "+returnE.getBarCode()+" QT: "+returnE.getAmount());
            for(TicketEntry saleE : saleEntries){
                if(saleE.getBarCode().equals(returnE.getBarCode())){

                    System.out.println("Amount in Sale of : "+saleE.getBarCode()+" is QT: "+saleE.getAmount());
                    //decrease quantity in sale and add back product to shelves
                    saleE.setAmount(saleE.getAmount()-returnE.getAmount());
                    System.out.println("New Amount is: "+saleE.getAmount());
                    try {
                        product = getProductTypeByBarCode(returnE.getBarCode());
                        product.setQuantity(product.getQuantity()+returnE.getAmount());
                    }catch (InvalidProductCodeException e){
                        System.out.println("productException nella endReturnTransaction");
                        return false;
                    }
                }
            }
        }

        ArrayList<ProductRfid> saleRfids = sale.rfids;
        ArrayList<ProductRfid> returnRfids = ongoingReturn.rfids;
        if(saleRfids != null && returnRfids != null){
            JSONObject tmp;
            for(ProductRfid retRFID : returnRfids){
                for(ProductRfid saleRFID : saleRfids){
                    if(retRFID.RFID.equals(saleRFID.RFID)){
                        saleRfids.remove(saleRFID);
                        //Adding the RFIDS back to the Database (json array and map)
                        rfidMap.put(retRFID.RFID,retRFID.productId);
                        tmp = new JSONObject();
                        tmp.put("rfid",retRFID.RFID);
                        tmp.put("id",retRFID.productId);
                        jArrayRfid.add(tmp);
                    }
                }
            }
            //Updating JSON File
            writejArrayToFile("src/main/persistent_data/productRfids.json", jArrayRfid);

        }


        //computing the money of the transaction
        Double totalMoney = saleEntries.stream()
                .map( e -> (e.getPricePerUnit() * e.getAmount() * (1.0 - e.getDiscountRate())) )
                .reduce(0.0,(e1,e2) -> e1 + e2);
        totalMoney = totalMoney * (1.0 - sale.getDiscountRate());
        //rounding it to 2 decimal places
        totalMoney = new BigDecimal(Double.toString(totalMoney)).setScale(2,RoundingMode.HALF_UP).doubleValue();
        sale.setMoney(totalMoney);

        //removing old instance in jArray of operations and operationsMap
        JSONObject tmp;
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(ongoingReturn.getSaleId().toString()) ){
                    accountBook.getjArrayOperations().remove(tmp);
                }
            }
        }
        accountBook.getOperationsMap().remove(ongoingReturn.getSaleId());
        //replacing it with the updated sale
        accountBook.addOperation(sale);

        //closing the transaction and adding it to the persistent data
        ongoingReturn.setStatus("CLOSED");
        accountBook.addOperation(ongoingReturn);
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        ongoingReturn = null;

        return true;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        //exceptions
        if(returnId == null || returnId <= 0){throw new InvalidTransactionIdException();}
        if(userLogged == null){throw new UnauthorizedException();}
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){throw new UnauthorizedException();}

        //return false if not existing return transaction with given returnId
        BalanceOperationImpl operation = ((BalanceOperationImpl) accountBook.getOperation(returnId));
        if(operation==null || !(operation instanceof ReturnTransaction)){return false;}
        ReturnTransaction retTran = ((ReturnTransaction) operation);

        operation = ((BalanceOperationImpl) accountBook.getOperation(retTran.getSaleId()));
        if(operation==null || !(operation instanceof SaleTransactionImplementation)){return false;}
        SaleTransactionImplementation sale = (SaleTransactionImplementation) accountBook.getOperation(retTran.getSaleId());
        //return false for non available sale Transaction (db problems)
        if(sale == null){return false;}

        List<TicketEntry> saleEntries = sale.getEntries();
        List<TicketEntry> returnEntries = retTran.getReturnEntries();
        ProductType product;

        for(TicketEntry returnE : returnEntries){
            for(TicketEntry saleE : saleEntries){
                if(saleE.getBarCode().equals(returnE.getBarCode())){
                    try {
                        String barCode = returnE.getBarCode();
                        product = productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getBarCode().equals(barCode)).findFirst().map(p -> (ProductType)new ProductTypeImplementation(p)).get();
                    }catch (Exception e){
                        System.out.println("exception thrown in deleteReturnTransaction: "+e);
                        return false;
                    }
                    //Add back product to sale and decrease quantity in shelves
                    saleE.setAmount(saleE.getAmount()+returnE.getAmount());
                    product.setQuantity(product.getQuantity()-returnE.getAmount());
                }
            }
        }

        //computing the money of the transaction
        Double totalMoney = saleEntries.stream()
                .map( e -> (e.getPricePerUnit() * e.getAmount() * (1.0 - e.getDiscountRate())) )
                .reduce(0.0,(e1,e2) -> e1 + e2);
        totalMoney = totalMoney * (1.0 - sale.getDiscountRate());
        //rounding it to 2 decimal places
        totalMoney = new BigDecimal(Double.toString(totalMoney)).setScale(2,RoundingMode.HALF_UP).doubleValue();
        sale.setMoney(totalMoney);

        //removing old instance in jArray of operations
        JSONObject tmp;
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(retTran.getSaleId().toString()) ){
                    accountBook.getjArrayOperations().remove(tmp);
                }
            }
        }
        //replacing it with the updated sale
        accountBook.addOperation(sale);

        //deleting the transaction and removing it from the persistent data
        accountBook.getOperationsMap().remove(retTran);
        //removing old instance in jArray of operations
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(returnId.toString()) ){
                    accountBook.getjArrayOperations().remove(tmp);
                }
            }
        }

        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        ongoingReturn = null;
        return true;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        if(userLogged == null){
            throw  new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){
            throw new UnauthorizedException();
        }
        if(ticketNumber==null || ticketNumber<=0)throw new InvalidTransactionIdException();
        if(cash<=0) throw new InvalidPaymentException();
        ArrayList <String> cards = readCards();

        SaleTransactionAdapter s = (SaleTransactionAdapter) getSaleTransaction(ticketNumber);
        if(s==null)return -1;
        double difference = cash-s.getPrice();
        if(difference<0)return -1;
        accountBook.changeBalance(s.getPrice());

        //Updating SaleTransaction Status
        s.sale.setStatus("PAYED");
        //Updating JSON Object in the JSON Array
        JSONObject tmp;
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(ticketNumber.toString()) ){
                    tmp.put("status","PAYED");
                }
            }
        }
        //Updating JSON File
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());

        return difference;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        if(userLogged == null){
            throw  new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){
            throw new UnauthorizedException();
        }
        if(ticketNumber==null || ticketNumber<=0)throw new InvalidTransactionIdException();
        if(creditCard== null || creditCard.isEmpty() || !validateCard(creditCard)) throw new InvalidCreditCardException();
        ArrayList <String> cards = readCards();

        //checking if card is inside the list
        if(cards.stream().filter(x->x.charAt(0)!='#').noneMatch(x->x.contains(creditCard))){return false;}

        String line = cards.stream().filter(x->x.charAt(0)!='#').filter(x->x.contains(creditCard)).findFirst().get();
        double money = Double.parseDouble(line.split(";")[1]);
        double costTransaction = 0.0;
        if(getSaleTransaction(ticketNumber) != null) {
            costTransaction = getSaleTransaction(ticketNumber).getPrice();
        }
        else{
            return false;
        }
        if (money< costTransaction)return false;
        money=money-costTransaction;
        accountBook.changeBalance(+costTransaction);
        // proceed to recording the payment
        String updatedEntry = creditCard.concat(";").concat(""+money);
        int updateIndex = cards.indexOf(line);
        cards.remove(updateIndex);
        cards.add(updateIndex,updatedEntry);
        try{
            Files.write(Paths.get("src/main/persistent_data/creditcards.txt"),
                    (Iterable<String>)cards.stream()::iterator); //(Iterable<String>)cards.stream().filter(x->!x.contains(line.get()))::iterator);
        }
//Handing Exception
        catch (Exception e) {
            System.out.println("something went wrong when writing to file\n");
            e.printStackTrace();
        }

        SaleTransactionAdapter s = (SaleTransactionAdapter) getSaleTransaction(ticketNumber);
        //Updating SaleTransaction Status
        s.sale.setStatus("PAYED");
        //Updating JSON Object in the JSON Array
        JSONObject tmp;
        if (accountBook.getjArrayOperations() != null) {
            for (int i=0;i<accountBook.getjArrayOperations().size();i++){
                tmp = (JSONObject) accountBook.getjArrayOperations().get(i);
                if( ((String)tmp.get("balanceId")).equals(ticketNumber.toString()) ){
                    tmp.put("status","PAYED");
                }
            }
        }
        //Updating JSON File
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());

        return true;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        if(userLogged == null){
            throw  new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){
            throw new UnauthorizedException();
        }
        if (returnId== null || returnId <= 0) {
            throw new InvalidTransactionIdException();
        }
        BalanceOperationImpl op = (BalanceOperationImpl) accountBook.getOperation(returnId);
        if(! (op instanceof ReturnTransaction))return -1;
        ReturnTransaction ret = (ReturnTransaction) op;
        if(!ret.getStatus().equals("CLOSED")) return -1;

        accountBook.changeBalance(-ret.getMoney());

        return ret.getMoney();
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        // checking privilegies
        if(userLogged == null){
            throw  new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager") && !role.equals("Cashier"))){
            throw new UnauthorizedException();
        }
        // checking returnId validity
        if(returnId == null || returnId<=0)throw new InvalidTransactionIdException();
        BalanceOperationImpl op = (BalanceOperationImpl) accountBook.getOperation(returnId);
        // checking credit card validity
        if(creditCard== null || creditCard.isEmpty() || !validateCard(creditCard)) throw new InvalidCreditCardException();
        if(! (op instanceof ReturnTransaction))return -1;
        ReturnTransaction ret = (ReturnTransaction) op;
        if(!ret.getStatus().equals("CLOSED")) return -1;

        // checking if credit card is stored
        ArrayList <String> cards = readCards();
        Optional<String> line = cards.stream().filter(x->x.charAt(0)!='#').filter(x->x.contains(creditCard)).findFirst();
        if(!line.isPresent())return -1;


        double money = Double.parseDouble(line.get().split(";")[1]);
        double refund = ret.getMoney();
        money=money+refund;
        accountBook.changeBalance(-refund);
        // proceed to recording the payment
        String updatedEntry = creditCard.concat(";").concat(""+money);
        cards.add(updatedEntry);
        try{
            Files.write(Paths.get("src/main/persistent_data/creditcards.txt"),
                    (Iterable<String>)cards.stream().filter(x->!x.contains(line.get()))::iterator);
        }
//Handing Exception
        catch (Exception e) {
            System.out.println("something went wrong when writing to file\n");
            e.printStackTrace();
        }


        accountBook.changeBalance(-ret.getMoney());
        return ret.getMoney();
    }

    @Override
    public boolean recordBalanceUpdate(double toBeAdded) throws UnauthorizedException {
        //exceptions
        if(userLogged == null){
            throw new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager"))){
            throw new UnauthorizedException();
        }
        //checking if balance is enough in cas of DEBIT operation
        if(accountBook.getBalance() + toBeAdded < 0){return false;}

        //if i have enough money i do the operation.
        BalanceOperation operation = new BalanceOperationImpl(toBeAdded);
        this.accountBook.addOperation(operation);
        accountBook.changeBalance(toBeAdded);
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        return true;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        //exceptions
        if(userLogged == null){
            throw new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager"))){
            throw new UnauthorizedException();
        }
        //control if dates are exchanged and eventually correct them
        final LocalDate myTo;
        final LocalDate myFrom;
        if((from != null && to != null) && from.compareTo(to) > 0){
             myTo = from;
             myFrom = to;
        }
        else {
             myTo = to;
             myFrom = from;
        }

        //looking for missing temporal constraints
        if(from == null && to == null){
            //if dates are not defined, return all operations
            return new ArrayList<>(accountBook.getOperationsMap().values());
        }
        else if(from == null){
            //if only from is missing
            return accountBook.getOperationsMap().values()
                    .stream().filter( op -> (myTo.compareTo(op.getDate())>=0) )
                    .collect(Collectors.toList());
        }
        else if(to == null){
            return accountBook.getOperationsMap().values()
                    .stream().filter( op -> (myFrom.compareTo(op.getDate()) <= 0) )
                    .collect(Collectors.toList());
        }
        else{
            //if both dates are defined
            return accountBook.getOperationsMap().values()
                    .stream().filter( op -> ((myFrom.compareTo(op.getDate()) <= 0 && myTo.compareTo(op.getDate())>=0)) )
                    .collect(Collectors.toList());
        }
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        //exceptions
        if(userLogged == null){
            throw new UnauthorizedException();
        }
        String role = userLogged.getRole();
        if(role == null || (!role.equals("Administrator") && !role.equals("ShopManager"))){
            throw new UnauthorizedException();
        }
        return accountBook.getBalance();
    }


}