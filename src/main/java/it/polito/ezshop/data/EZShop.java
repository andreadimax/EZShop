package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;



public class EZShop implements EZShopInterface {

    private UsersData usersData;
    private Integer usersCount;
    private User userLogged;
    private HashMap<Integer, ProductType> productMap = new HashMap<Integer,ProductType>();
    private HashMap <String,Position> positionMap = new HashMap<String,Position>();
    private FileReader productsFile;
    private FileReader positionsFile;

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

            ProductType newProduct = new ProductTypeImplementation(barCode,description,sellPrice,discountRate,notes,availableQty);
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

    public void parsePositionObject(JSONObject pos){

    }

    @Override
    public void reset() {

    }

    @Override
    public Integer createUser(String username, String password, String role) throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        if(password == null | "".equals(password)){
            throw new InvalidPasswordException("Invalid password");
        }

        if(role == null |( role != "Administrator" & role != "Cashier" & role != "ShopManager")){
            throw new InvalidRoleException("Invalid role");
        }


        User user = new UserImplementation(usersCount++, username, password, role);
        if(!usersData.addUser(user)){
            throw new InvalidUsernameException("User already present");
        };
        return user.getId();
    }

    @Override
    public boolean deleteUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null | userLogged.getRole() != "Administrator"){
            throw new UnauthorizedException();
        }

        if(!usersData.removeUser(id)){
            throw new InvalidUserIdException();
        }
        return true;
    }

    @Override
    public List<User> getAllUsers() throws UnauthorizedException {
        if(userLogged == null | userLogged.getRole() != "Administrator"){
            throw new UnauthorizedException();
        }

        return usersData.getUserslist();
    }

    @Override
    public User getUser(Integer id) throws InvalidUserIdException, UnauthorizedException {
        if(userLogged == null | userLogged.getRole() != "Administrator"){
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
        if(userLogged == null | userLogged.getRole() != "Administrator"){
            throw new UnauthorizedException();
        }

        if(role == null |( role != "Administrator" & role != "Cashier" & role != "ShopManager")){
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
        if(username == null | username == ""){
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
        //check user privilegies
        Integer productID;

        return null;
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
        return null;
    }

    @Override
    public ProductType getProductTypeByBarCode(String barCode) throws InvalidProductCodeException, UnauthorizedException {
        return null;
    }

    @Override
    public List<ProductType> getProductTypesByDescription(String description) throws UnauthorizedException {
        return null;
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
        return null;
    }

    @Override
    public Integer payOrderFor(String productCode, int quantity, double pricePerUnit) throws InvalidProductCodeException, InvalidQuantityException, InvalidPricePerUnitException, UnauthorizedException {
        return null;
    }

    @Override
    public boolean payOrder(Integer orderId) throws InvalidOrderIdException, UnauthorizedException {
        return false;
    }

    @Override
    public boolean recordOrderArrival(Integer orderId) throws InvalidOrderIdException, UnauthorizedException, InvalidLocationException {
        return false;
    }

    @Override
    public List<Order> getAllOrders() throws UnauthorizedException {
        return null;
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
        return null;
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
        return null;
    }

    @Override
    public double computeBalance() throws UnauthorizedException {
        return 0;
    }
}
