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
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;


public class EZShop implements EZShopInterface {
    //users
    private User userLogged = null;
    private HashMap<Integer, User> users_data;
    private JSONArray jArrayUsers;
    //products
    private HashMap<Integer, ProductType> productMap;
    private JSONArray jArrayProduct;
    private FileReader productsFile;
    private AccountBook accountBook;
    //Customers
    private HashMap<Integer, Customer>  customersMap;
    private JSONArray jArrayCustomers;



    //Inner Class
    private class Init{
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


        jArrayProduct=initializeMap(new Init("src/main/persistent_data/productTypes.json", productMap, "product"));
        jArrayUsers=initializeMap(new Init("src/main/persistent_data/users.json", users_data,"user"));
        jArrayCustomers=initializeMap(new Init("src/main/persistent_data/customers.json", customersMap,"customer"));


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
    public Integer assignId(Set<Integer> ids){
        boolean found = false;
        Integer int_random = 0;
        while(!found){
            Random rand = new Random(); //instance of random class
            int_random = rand.nextInt(9);
            if(!ids.contains(int_random) && int_random != 0){
                found = true;
            }
        }
        return  int_random;
    }

    public void parseObjectType(JSONObject obj, String type){
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
                // Get discountRate
                double discountRate = Double.parseDouble((String) obj.get("discountRate"));
                // Get notes
                String notes = (String) obj.get("note");
                // Get availableQty
                Integer availableQty = Integer.parseInt((String) obj.get("availableQty"));
                // Get position
                String position = (String) obj.get("position");

                ProductTypeImplementation newProduct = new ProductTypeImplementation(id, barCode, description, sellPrice, notes);
                newProduct.setQuantity(availableQty);
                newProduct.setDiscountRate(discountRate);
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
        Object dr = p.getDiscountRate();
        pDetails.put("discountRate", (dr==null)?"0":dr.toString());
        pDetails.put("note", p.getNote());
        Object sp = p.getDiscountRate();
        pDetails.put("sellPrice", (sp==null)?"0": sp.toString());
        return pDetails;
    }

    private boolean writejArrayToFile(String filepath, JSONArray jArr){
        System.out.println("writing jarray to file");
        try
        {
            FileWriter fOut = new FileWriter(filepath);
            fOut.write(jArr.toJSONString());
            fOut.flush();
            fOut.close();

        }
        catch(IOException f) {
            f.printStackTrace();
            return false;
        }
        return true;
    }

//---------- Start of Ezshop Interface functions -------------
    @Override
    public void reset() {

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

        //Checking if user exists...
        if(users_data.get(id) != null){
            //Deleting from JSON Array...
            JSONObject user_obj = null;
            for(int i = 0; i< jArrayUsers.size(); i++){
                user_obj  = (JSONObject) jArrayUsers.get(i);
                if(user_obj.get("id").equals(id.toString())){
                    jArrayUsers.remove(i);
                }
            }
            //Deleting from map
            users_data.remove(id);
            //Updating JSON File
            if(!writejArrayToFile("src/main/persistent_data/users.json", jArrayUsers))return false;
        }
        else {
            throw new InvalidUserIdException("User not present!");
        }
        return true;
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

        if(id == null || id==0){
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

        if(role == null ||( !role.equals("Administrator") & !role.equals("Cashier") & !role.equals("ShopManager"))){
            throw new InvalidRoleException("Invalid role");
        }
        if(id == null || id==0){
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
        return userLogged;
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
        if(userLogged!=null && !("ShopManager".equals(userLogged.getRole())) && !"Administrator".equals(userLogged.getRole())) throw new UnauthorizedException();
        Integer productID;

        // check description
        if(description == null || description.isEmpty()) throw new InvalidProductDescriptionException();

        //check if productCode is null or empty and if it is a number
        if((productCode==null || productCode.isEmpty() || !productCode.matches("-?\\d+"))) {
            throw new InvalidProductCodeException();
        }
        Integer id=Integer.parseInt(productCode);
        // check if product code is positive and if it already present in map
        if(id<0 || this.productMap.get(id)!=null) throw new InvalidProductCodeException();


        ProductTypeImplementation p = new ProductTypeImplementation(id,productCode, description,pricePerUnit,note);

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

        //check for invalid product id
        if(id== null || productMap.get(id)==null || id<0) throw new InvalidProductIdException();

        //needs to remove object from memory array and commit to disk
        if(productMap.get(id)==null)return false;
        jArrayProduct.remove(productMap.get(id));

        //update object in map
        ProductTypeImplementation p =(ProductTypeImplementation) productMap.get(id);
        p.setId(id);
        p.setProductDescription(newDescription);
        p.setBarCode(newCode);
        p.setPricePerUnit(newPrice);
        p.setNote(newNote);



        JSONObject pDetails;
        pDetails = initializeJsonProductObject(p);
        jArrayProduct.add(pDetails);
        //(?) I am not doing error handling on this write, if it fails, i should rollback the previous removal
        return writejArrayToFile("src/main/persistent_data/productTypes.json", jArrayProduct);
    }


    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        //check for invalid user
        if(this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))) throw new UnauthorizedException();
        //check for invalid product id
        if(id== null || productMap.get(id)==null || id<0) throw new InvalidProductIdException();

        //needs to remove object from memory array and commit to disk
        if(!jArrayProduct.remove(productMap.get(id)))return false;
        //(?) I am not doing error handling on this write, if it fails, i should rollback the previous removal
        if(!writejArrayToFile("src/main/persistent_data/productTypes.json",jArrayProduct)) return false;
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
        return (List<ProductType>) new ArrayList<>(productMap.values());
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }
        return productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getBarCode().equals(barCode)).findFirst().get();
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }
        return productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getProductDescription().equals(description)).collect(Collectors.toList());
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {

        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }
        System.out.println("updating quantity: " + toBeAdded);
        if(productId==null || productId<=0)throw new InvalidProductIdException();
        ProductTypeImplementation p = (ProductTypeImplementation) productMap.get(productId);
        return p != null && p.changeQuantity(toBeAdded);
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager")))
        {
            throw new UnauthorizedException();
        }

        if( productId==null || productId<=0)throw new InvalidProductIdException();

        //if position is not null, check if it satisfies <aisleNumber>-<rackAlphabeticIdentifier>-<levelNumber> format
        if(newPos==null || !newPos.matches("[0-9]*-[^0-9]*-[0-9]*"))throw new InvalidLocationException();


        //if position is not unique, or productId has no match return false
        ProductTypeImplementation p = (ProductTypeImplementation) productMap.get(productId);
        if(p==null || (newPos!=null && getAllProductTypes().stream().anyMatch(pr -> pr.getLocation() != null && pr.getLocation().equals(newPos))))return false;
        //(?) qui dovrei aggiungere la posizione alla lista di posizioni etc ma sono dell'idea di eliminare la classe posizione
        p.setLocation(newPos);
        return true;
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
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());
        return order.getOrderId();
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        int orderId = issueOrder(productCode, quantity, pricePerUnit);
        if( orderId == -1){ return -1; }

        //check for the order existence
        if( !(this.accountBook.getOperation(orderId) instanceof OrderImpl) ){return -1;}

        OrderImpl order = (OrderImpl) this.accountBook.getOperation(orderId);

        //check if the balance is enough to pay the order, operation is '+' because getMoney() has a negative value for subclass OrderImpl
        if(accountBook.getBalance() + order.getMoney() < 0){ return -1; }

        //if it is enough, change status and change balance
        accountBook.changeBalance(order.getMoney());
        order.setStatus("PAYED");
        //Updating JSON Object in the JSON Array
        ((JSONObject) accountBook.getjArrayOperations().get(orderId)).put("status","PAYED");
        //Updating JSON File
        writejArrayToFile(accountBook.getFilepath(), accountBook.getjArrayOperations());

        return orderId;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        //exceptions
        if(orderId <= 0){throw new InvalidOrderIdException();}
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))
        ){throw new UnauthorizedException();}

        //check for the order existence
        if( !(this.accountBook.getOperation(orderId) instanceof OrderImpl) ){return false;}

        //check if already payed
        OrderImpl order = (OrderImpl) this.accountBook.getOperation(orderId);
        if( order.getStatus().equals("PAYED") ){ return false; }

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
        if(orderId <= 0){throw new InvalidOrderIdException();}
        if( this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))
        ){throw new UnauthorizedException();}

        //making sure the order exists, has an existing location assigned and is in
        if( !(accountBook.getOperation(orderId) instanceof OrderImpl) ){ return false; }
        OrderImpl order = (OrderImpl) accountBook.getOperation(orderId);
        if(order == null){
            System.out.println("RETRIEVED ORDER IS NULL");
        }
        ProductType product = productMap.values().stream().filter(p -> p.getProductDescription()!=null && p.getBarCode().equals(order.getProductCode())).findFirst().get();
        if(product.getLocation() == null){ throw new InvalidLocationException(); }

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

        if(newCustomerName == null || newCustomerName.equals("") ){
            throw new InvalidCustomerNameException();
        }

        if(newCustomerCard == null || newCustomerCard.matches("\\d{11}")){
            throw new InvalidCustomerCardException();
        }

        if(!this.customersMap.containsKey(id)){
            throw new InvalidCustomerIdException();
        }

        //Checking if card code is already assigned to someone
        for(Customer c: customersMap.values()){
            if(c != customersMap.get(id)) {
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
        for(int i = 0; i< jArrayCustomers.size(); i++){
            customer_obj  = (JSONObject) jArrayCustomers.get(i);
            if(customer_obj.get("id").equals(id.toString())){
                customer_obj.put("name", customersMap.get(id).getCustomerName());
                customer_obj.put("card", customersMap.get(id).getCustomerCard());
            }
        }

        //Updating JSON File
        return writejArrayToFile("src/main/persistent_data/customers.json", jArrayCustomers);
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
        if(customerCard == null || customerCard.matches("\\d{11}") || customerCard.equals("")){
            throw new InvalidCustomerCardException();
        }

        for (Customer c: customersMap.values()){
            if(c != customersMap.get(customerId)) {
                if (c.getCustomerCard().equals(customerCard)) {
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
                if(customer_obj.get("id").equals(c.getId())){
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
        if(customerCard == null || customerCard.matches("\\d{11}") || customerCard.equals("")){
            throw new InvalidCustomerCardException();
        }

        //Checking validity of data
        for(Customer c: customersMap.values()){
            if(c.getCustomerCard() != null) {
                if (c.getCustomerCard().equals(customerCard)) {
                    if (c.getPoints() + pointsToBeAdded > 0) {
                        c.setPoints(c.getPoints() + pointsToBeAdded);
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
        return null;
    }

    @Override
    public boolean addProductToSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteProductFromSale(Integer transactionId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToProduct(Integer transactionId, String productCode, double discountRate) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean applyDiscountRateToSale(Integer transactionId, double discountRate) throws InvalidTransactionIdException, InvalidDiscountRateException, UnauthorizedException {
        return false;
    }

    @Override
    public int computePointsForSale(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean endSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteSaleTransaction(Integer saleNumber) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public SaleTransaction getSaleTransaction(Integer transactionId) throws InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public Integer startReturnTransaction(Integer saleNumber) throws /*InvalidTicketNumberException,*/InvalidTransactionIdException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean returnProduct(Integer returnId, String productCode, int amount) throws InvalidTransactionIdException, InvalidProductCodeException, InvalidQuantityException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean endReturnTransaction(Integer returnId, boolean commit) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteReturnTransaction(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return false;
    }

    @Override
    public double receiveCashPayment(Integer ticketNumber, double cash) throws InvalidTransactionIdException, InvalidPaymentException, UnauthorizedException {
        return 0;
    }

    @Override
    public boolean receiveCreditCardPayment(Integer ticketNumber, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return false;
    }

    @Override
    public double returnCashPayment(Integer returnId) throws InvalidTransactionIdException, UnauthorizedException {
        return 0;
    }

    @Override
    public double returnCreditCardPayment(Integer returnId, String creditCard) throws InvalidTransactionIdException, InvalidCreditCardException, UnauthorizedException {
        return 0;
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
