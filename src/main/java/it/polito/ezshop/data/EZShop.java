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

    private UsersData usersData;
    private Integer usersCount;
    private User userLogged = null;
    private HashMap<Integer, ProductType> productMap = new HashMap<>();
    private HashMap <String,Position> positionMap = new HashMap<>();
    private FileReader productsFile;
    private FileReader positionsFile;
    private AccountBook accountBook = new AccountBook();

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
        usersData = new UsersData();
        usersCount = 0;

        initializeMap(new Init("src/main/persistent_data/productTypes.json", productMap, "product"));
        initializeMap(new Init("src/main/persistent_data/positions.json", positionMap,"position"));


    }

    //  INITIALIZATION FOR PRODUCT TYPES
    private void initializeMap(Init i){
        // Loading Products
        JSONParser parser = new JSONParser();
        try {
            i.file = new FileReader(i.filename);
        }
        catch (FileNotFoundException f){
            f.printStackTrace();
        }

        try
        {
            //Read JSON file

            JSONArray jArray = (JSONArray) parser.parse(i.file);

            jArray.forEach( x -> parseObjectType( (JSONObject) x, i.type ) );

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
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


        User user = new UserImplementation(usersCount++, username, password, role);
        if(!usersData.addUser(user)){
            throw new InvalidUsernameException("User already present");
        }
        return user.getId();
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null | !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        if(!usersData.removeUser(id)){
            throw new InvalidUserIdException();
        }
        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(userLogged == null | !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        return usersData.getUserslist();
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null | !userLogged.getRole().equals("Administrator")){
            throw new UnauthorizedException();
        }

        User user;
        if((user = usersData.getUser(id)) != null ){
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
        if((user = usersData.getUser(id)) != null ){
            user.setRole(role);
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
        if(!usersData.searchForLogin(username, password)){
            throw new InvalidPasswordException("Username or password wrong");
        }

        this.userLogged = usersData.getUser(username);

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

        //MI FERMO PERCHè non mi è ancora chiaro il perchè della mappa e della lista
        // quando si memorizza su json
        return p.getId();
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
