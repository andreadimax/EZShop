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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;


public class EZShop implements EZShopInterface {
    //users
    private Integer usersCount;
    private User userLogged = null;
    private HashMap<Integer, User> users_data;
    private JSONArray jArrayUsers;
    //products
    private HashMap<Integer, ProductType> productMap;
    private JSONArray jArrayProduct;
    private FileReader productsFile;
    //position
    private HashMap <String,Position> positionMap;
    private JSONArray jArrayPosition;
    private FileReader positionsFile;
    private AccountBook accountBook;



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
        this.productMap = new HashMap<Integer, ProductType>();                  //Products
        this.positionMap = new HashMap<String, Position>();                     //Positions
        this.accountBook = new AccountBook();                                   //Account book object
        this.users_data = new HashMap<Integer, User>();                         //Users
        usersCount = 0;                                                         //usersCount is used to assign progressive IDs to new users

        jArrayProduct=initializeMap(new Init("src/main/persistent_data/productTypes.json", productMap, "product"));
        jArrayPosition=initializeMap(new Init("src/main/persistent_data/positions.json", positionMap,"position"));
        jArrayUsers=initializeMap(new Init("src/main/persistent_data/users.json", positionMap,"user"));


    }

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

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return jArray;
    }


    public void parseObjectType(JSONObject obj, String type){
        if(type.equals("product")){
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
            String notes = (String) obj.get("notes");
            // Get availableQty
            Integer availableQty = Integer.parseInt((String) obj.get("availableQty"));

            ProductTypeImplementation newProduct = new ProductTypeImplementation(id,barCode,description,sellPrice,notes);
            newProduct.setQuantity(availableQty);
            newProduct.setDiscountRate(discountRate);
            this.productMap.put(id, newProduct);
        }
        else if(type.equals("position")){
            //Get positionName
            String position = (String) obj.get("position");
            //Get productId associated
            Integer productId = Integer.parseInt( (String) obj.get("productID"));
            //Fetch Product
            ProductType p = productMap.get(productId);
            //instantiate position
            Position newPos = new Position(position, p);
            this.positionMap.put(position, newPos);
        }
        else if(type.equals("user")){

            //Get user id
            Integer id = Integer.parseInt((String) obj.get("id"));

            //Get employee last name
            String username = (String) obj.get("username");

            //Get employee website name
            String password = (String) obj.get("password");

            String role = (String) obj.get("role");

            User new_user = new UserImplementation(id, username, password, role);
            this.users_data.put(id, new_user);
        }


    }


    @Override
    public void reset() {

    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        if(password == null | "".equals(password)){
            throw new InvalidPasswordException("Invalid password");
        }

        if(role == null || ( !role.equals("Administrator") && !role.equals("Cashier") && !role.equals("ShopManager"))){
            throw new InvalidRoleException("Invalid role");
        }

        //Checking if User exists...
        for(User u: this.users_data.values()){
            if(u.getUsername().equals(username)){
                throw new InvalidUsernameException("User already present");
            }
        }

        //Creating new user
        User user = new UserImplementation(usersCount++, username, password, role);
        //Adding to map
        this.users_data.put(user.getId(), user);

        /* Adding to JSON Array (needed to update thr JSON file with new user data) */
        /* ------------------------------------------------------------------------ */

        JSONObject userDetails = new JSONObject();
        userDetails.put("id", user.getId().toString());
        userDetails.put("username", user.getUsername());
        userDetails.put("password", user.getPassword());
        userDetails.put("role", user.getRole());


        /* JSON Array updating...
           NOTE: id is used to insert object so that when there's the need
           to delete it it's easier to find it
         */
        this.jArrayUsers.add(user.getId(), userDetails);

        //Updating file
        try
        {
            FileWriter fout = new FileWriter("src/main/persistent_data/users.json");
            fout.write(jArrayUsers.toJSONString());
            fout.flush();
            fout.close();

        }
        catch(IOException f) {
            f.printStackTrace();
        }

        return user.getId();
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null | !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        //Checking if user exists...
        if(users_data.get(id) != null){
            //Deleting from JSON Array...
            jArrayUsers.remove(users_data.get(id));
            //Deleting from map
            users_data.remove(id);

            //Updating JSON File
            try
            {
                FileWriter fout = new FileWriter("src/main/persistent_data/users.json");
                fout.write(jArrayUsers.toJSONString());
                fout.flush();
                fout.close();

            }
            catch(IOException f) {
                f.printStackTrace();
            }
        }
        else {
            throw new InvalidUserIdException("User not present!");
        }
        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(userLogged == null | !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        return (List<User>) users_data.values();
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null | !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        User user;
        if( (user = this.users_data.get(id)) != null ){
            return user;
        }
        else{
            throw new InvalidUserIdException();
        }
    }

    @Override
    public boolean updateUserRights(Integer id, String role) throws InvalidUserIdException, InvalidRoleException, UnauthorizedException {
        if(userLogged == null | !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        if(role == null |( !role.equals("Administrator") & !role.equals("Cashier") & !role.equals("ShopManager"))){
            throw new InvalidRoleException("Invalid role");
        }
        User user;
        if((user = users_data.get(id)) != null ){
            user.setRole(role);

            //Updating JSON OBject in the JSON Array
            ((JSONObject) jArrayUsers.get(user.getId())).put("role", user.getRole());

            //Updating JSON File
            try
            {
                FileWriter fout = new FileWriter("src/main/persistent_data/users.json");
                fout.write(jArrayUsers.toJSONString());
                fout.flush();
                fout.close();

            }
            catch(IOException f) {
                f.printStackTrace();
            }
            return true;
        }
        else{
            throw new InvalidUserIdException();
        }
    }

    @Override
    public User login(String username, String password) throws InvalidUsernameException, InvalidPasswordException {
        if(username == null || !username.equals("")){
            throw new InvalidUsernameException();
        }

        //Checking credentials
        for(User user: this.users_data.values()){
            if(username.equals(user.getUsername())){
                if( !password.equals(user.getPassword())){
                    throw new InvalidPasswordException("Username or password wrong");
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
        userLogged = null;
        return true;
    }

    @Override
    public Integer createProductType(String description, String productCode, double pricePerUnit, String note) throws InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        //check privilegies
        if(this.userLogged == null || (!this.userLogged.getRole().equals("Administrator") && !this.userLogged.getRole().equals("ShopManager"))) throw new UnauthorizedException();
        Integer productID;

        // check description
        if(description == null || description.isEmpty()) throw new InvalidProductDescriptionException();

        //check productCode
        if((productCode==null || productCode.isEmpty() || !productCode.matches("-?\\d+"))) {
            throw new InvalidProductCodeException();
        }
        Integer id=Integer.parseInt(productCode);
        if(id<0 || this.productMap.get(id)!=null) throw new InvalidProductCodeException();


        ProductTypeImplementation p = new ProductTypeImplementation(id,productCode, description,pricePerUnit,note);
        p.changeQuantity(1);
        this.productMap.put(id,p);


        JSONObject pDetails = new JSONObject();
        pDetails.put("id", p.getId());
        pDetails.put("avaliableQty", p.getQuantity());
        pDetails.put("barCode", p.getBarCode());
        pDetails.put("description", description);
        pDetails.put("discountRate", p.getDiscountRate());
        pDetails.put("Note", p.getNote());
        pDetails.put("sellPrice", p.getPricePerUnit());

        this.jArrayProduct.add(pDetails);
        String filePath= "src/main/persistent_data/productTypes.json";
        if(!writejArraytoFile(filePath, jArrayProduct))System.out.println("Couldn't write to file"+filePath);

        return p.getId();
    }

    private boolean writejArraytoFile(String filepath, JSONArray jArr){
        try
        {
            FileWriter fout = new FileWriter("src/main/persistent_data/productTypes.json");
            fout.write(jArr.toJSONString());
            fout.flush();
            fout.close();

        }
        catch(IOException f) {
            f.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public boolean updateProduct(Integer id, String newDescription, String newCode, double newPrice, String newNote) throws InvalidProductIdException, InvalidProductDescriptionException, InvalidProductCodeException, InvalidPricePerUnitException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteProductType(Integer id) throws InvalidProductIdException, UnauthorizedException {
        return false;
    }

    @Override
    public List<ProductType> getAllProductTypes() throws UnauthorizedException {
        return new ArrayList<>();
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        return null;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        return new ArrayList<>();
    }

    @Override
    public boolean updateQuantity(Integer productId, int toBeAdded) throws InvalidProductIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean updatePosition(Integer productId, String newPos) throws InvalidProductIdException, InvalidLocationException, UnauthorizedException {
        return false;
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
        ProductType product = this.productMap.get(order.getProductCode());
        if(product.getLocation() == null || !this.positionMap.containsValue(product.getLocation())
        ){ throw new InvalidLocationException(); }

        //registering the order arrival and updating the product quantity (unless it was already completed)
        if(order.getStatus().equals("COMPLETED")){
            return true;
        }
        product.setQuantity( product.getQuantity() + order.getQuantity() );
        order.setStatus("COMPLETED");
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
        return null;
    }

    @Override
    public boolean modifyCustomer(Integer id, String newCustomerName, String newCustomerCard) throws InvalidCustomerNameException, InvalidCustomerCardException, InvalidCustomerIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean deleteCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return false;
    }

    @Override
    public Customer getCustomer(Integer id) throws InvalidCustomerIdException, UnauthorizedException {
        return null;
    }

    @Override
    public List<Customer> getAllCustomers() throws UnauthorizedException {
        return new ArrayList<>();
    }

    @Override
    public String createCard() throws UnauthorizedException {
        return null;
    }

    @Override
    public boolean attachCardToCustomer(String customerCard, Integer customerId) throws InvalidCustomerIdException, InvalidCustomerCardException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean modifyPointsOnCard(String customerCard, int pointsToBeAdded) throws InvalidCustomerCardException, UnauthorizedException {
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
        return false;
    }

    @Override
    public List<BalanceOperation> getCreditsAndDebits(LocalDate from, LocalDate to) throws UnauthorizedException {
        return new ArrayList<>();
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        return 0;
    }
}
