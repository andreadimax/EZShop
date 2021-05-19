# Unit Testing Documentation

Authors: Andrea Di Mauro, Alessio Bincoletto, Daniele Cacciabue

Date:	13/05/2021

Version: 1.0


___________________________
# Contents

- [Black Box Unit Tests](#black-box-unit-tests)
  - [CustomerImplementation](#customerimplementation)
  - [ProductTypeImplementation](#producttypeimplementation)
  - [UserImplementation](#user)
  - [TicketEntry](#ticketentry)
  - [EZShop](#ezshop)
  - [AccountBook](#accountbook)
- [White box unit tests](#white-box-unit-tests)


# Black Box Unit Tests

    <Define here criteria, predicates and the combination of predicates for each function of each class.
    Define test cases to cover all equivalence classes and boundary conditions.
    In the table, report the description of the black box test case and (traceability) the correspondence with the JUnit test case writing the 
    class and method name that contains the test case>
    <JUnit test classes must be in src/test/java/it/polito/ezshop   You find here, and you can use,  class TestEzShops.java that is executed  
    to start tests
    >
## CustomerImplementation
### **Class *CustomerImplementation* - method *public CustomerImplementation(String name, Integer id, Integer points, String customerCard)***

**Criteria for Constructor *CustomerImplementation(String name, Integer id, Integer points, String customerCard)*:**

**Predicates for method **:**

| Criteria             | Predicate                        |
| -------------------- | --------------------------       |
| type of name         | Any String                       |
|                      |                                  |
| type of id           | Any Integer                      |
|                      |                                  |
| type of points       | Any Integer                      |
|                      |                                  |
| CustomerCard value   | customerCard.matches("\\d{10}")  |
|                      | customerCard is  null            |
|                      | !customerCard.matches("\\d{10}") |


**Boundaries**:
no boundaries for the first three criteria, since they only consist of 1 single equivalence class
no boundaries for the fourth criteria, since boundaries cannot be stated for string values


**Combination of predicates**:

| Criteria 4                      | Criteria 1 to 3 | Valid / Invalid | Description of the test case                                            | JUnit test case      |
|---------------------------------|-----------------|-----------------|-------------------------------------------------------------------------|----------------------|
| customercard is null            | *               | Valid           | T("andrea", 1, 5, null)  => customer with given attributes              | testCustomer()       |
| customercard matches "\\d{10}"  | *               | "               | T("marina blue", 3, 40, "1038475839") => customer with given attributes | testCustomerConstr() |
| customercard !matches "\\d{10}" | **              | "               | T("Rob Robinson", 4, 2, "2383") => customer with creditCard=null        | testCustomerConstr() |

### **Class *CustomerImplementationTest* - method *setCustomerName()***

**Criteria for method *setCustomerName***:

**Predicates for method *setCustomerName*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                                                                   | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------                                                   | --------------- |
| *          | Valid           | Object Customer is instantiated<br>customer.setCustomerName("alessio");<br>value is checked through the getter | testCustomer()  |

### **Class *CustomerImplementationTest* - method *setCustomerCard()***

**Criteria for method *setCustomerCard***:

**Predicates for method *setCustomerCard*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Customer is instantiated<br>customer.setCustomerCard("9000648221");<br>value is checked through the getter | testCustomer()  |

### **Class *CustomerImplementationTest* - method *setId()***

**Criteria for method *setCustomerId***:

**Predicates for method *setCustomerId*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Customer is instantiated<br>customer.setId(10);<br>value is checked through the getter | testCustomer()  |

### **Class *CustomerImplementationTest* - method *setPoints()***

**Criteria for method *setPoints***:

**Predicates for method *setPoints*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Customer is instantiated<br>customer.setPoints(-3);<br>value is checked through the getter | testCustomer()  |

## UserImplementation
### **Class *UserImplementationTest* - method *setId()***

**Criteria for method *setId***:

**Predicates for method *setId*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object User is instantiated<br>user.setId(20);<br>value is checked through the getter | testUser()      |

### **Class *UserImplementationTest* - method *setUsername()***

**Criteria for method *setUsername***:

**Predicates for method *setUsername*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object User is instantiated<br>user.setUsername("alessio");<br>value is checked through the getter | testUser()      |



### **Class *UserImplementationTest* - method *setPassword()***

**Criteria for method *setPassword***:

**Predicates for method *setPassword*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object User is instantiated<br>user.setPassword("456");<br>value is checked through the getter | testUser()      |

### **Class *UserImplementationTest* - method *setRole()***

**Criteria for method *setRole***:

**Predicates for method *setRole*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object User is instantiated<br>user.setRole("Administrator");<br>value is checked through the getter | testUser()      |


## ProductTypeImplementation
### **Class *ProductTypeImplementation* - method *public ProductTypeImplementation(Integer id, String barCode, String description, double sellPrice, String note)***

**Criteria for method *ProductTypeImplementation(ProductType p)*:**

**Predicates for method *ProductTypeImplementation*:**

| Criteria             | Predicate                  |
| -------------------- | -------------------------- |
| p validity           | p is not null              |


**Boundaries**:

No boundaries, we consider one single equivalent class for any of the input arguments.

**Combination of predicates**:

| Criteria 1 | Valid / Invalid | Description of the test case                                                              | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------                              | --------------- |
| p validity | invalid         | T(null)  expect NullPointerException                                                      | testProduct2()  |
| p validity | valid           | T(ProductTypeImplementation(8, "000000000002", "banane", 1.0, "note1", 9, "djas-12-djs")) | testProduct2()  |


### **Class *ProductTypeImplementation* - method *changeQuantity()***

**Criteria for method *name*:**

 - Type of the argument

**Predicates for method *name*:**

| Criteria             | Predicate                  |
| -------------------- | -------------------------- |
| Sign of the argument | Integer >= 0 \| <0 *valid* |
|                      | null *not valid*           |



**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |



**Combination of predicates**:


| Criteria 1         | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ------------------ | --------------- | ------------------------------------------------------------ | --------------- |
| null               | Invalid         | changeQuantity(null)                                         | testProduct()   |
| Integer >= 0 \| <0 | Valid           | changeQuantity(7)<br>changeQuantity(0)<br>changeQuantity(-5)  | ""              |
### **Class *ProductTypeImplementationTest* - method *setQuantity()***

**Criteria for method *setAmount***:

**Predicates for method *setAmount*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                                                       | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------                                       | --------------- |
| *          | Valid           | Object TicketEntry is instantiated<br>ticket.setQuantity(3);<br>value is checked through the getter  | testProduct()   |
### **Class *ProductTypeImplementationTest* - method *setLocation()***

**Criteria for method *setLocation***:

**Predicates for method *setLocation*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Product is instantiated<br>product.setLocation("location5");<br>value is checked through the getter | testProduct()   |

### **Class *ProductTypeImplementationTest* - method *setNote()***

**Criteria for method *setNote***:

**Predicates for method *setNote*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Product is instantiated<br>product.setNote("test");<br>value is checked through the getter | testProduct()   |

### **Class *ProductTypeImplementationTest* - method *setProductDescription()***

**Criteria for method *setProductDescription***:

**Predicates for method *setProductDescription*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Product is instantiated<br>product.setProductDescription("pere");<br>value is checked through the getter | testProduct()   |

### **Class *ProductTypeImplementationTest* - method *setBarCode()***

**Criteria for method *setBarCode***:

**Predicates for method *setBarCode*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Product is instantiated<br>product.setBarCode("800123739455");<br>value is checked through the getter | testProduct()   |

### **Class *ProductTypeImplementationTest* - method *setPricePerUnit()***

**Criteria for method *setPricePerUnit***:

**Predicates for method *setPricePerUnit*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Product is instantiated<br>product.setPricePerUnit(5.0);<br>value is checked through the getter | testProduct()   |

### **Class *ProductTypeImplementationTest* - method *setId()***

**Criteria for method *setId***:

**Predicates for method *setId*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object Product is instantiated<br>product.setId(10);<br>value is checked through the getter | testProduct()   |


## TicketEntry
### **Class *TicketEntryImplementationTest* - method *setBarCode()***

**Criteria for method *setBarCode***:

**Predicates for method *setBarCode*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object TicketEntry is instantiated<br>ticket.setBarCode("00012452");<br>value is checked through the getter | testTicket()    |

### **Class *TicketEntryImplementationTest* - method *setProductDescription()***

**Criteria for method *setProductDescription***:

**Predicates for method *setProductDescription*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object TicketEntry is instantiated<br>ticket.setProductDescription("pere");<br>value is checked through the getter | testTicket()    |


### **Class *ProductTypeImplementationTest* - method *setAmount()***

**Criteria for method *setAmount***:

**Predicates for method *setAmount*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object TicketEntry is instantiated<br>ticket.setAmount(-3);<br>value is checked through the getter | testTicket()    |

### **Class *TicketEntryImplementationTest* - method *setPricePerUnit()***

**Criteria for method *setPricePerUnit***:

**Predicates for method *setPricePerUnit*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object TicketEntry is instantiated<br>ticket.setPricePerUnit(10.0);<br>value is checked through the getter | testTicket()    |

### **Class *TicketEntryImplementationTest* - method *setDiscountRate()***

**Criteria for method *setDiscountRate***:

**Predicates for method *setDiscountRate*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case |
| ---------- | --------------- | ------------------------------------------------------------ | --------------- |
| *          | Valid           | Object TicketEntry is instantiated<br>ticket.setDiscountRate(0.5);<br>value is checked through the getter | testTicket()    |

## EZShop

### **Class *EZShop* - method *validateCard***

**Criteria for method *validateCard:***

 - Type of the argument

**Predicates for method *name*:**

| Criteria             | Predicate               |
| -------------------- | ----------------------- |
| Type of the argument | String *valid*          |
|                      | null String *not valid* |



**Boundaries**:

| Criteria             | Boundary values |
| -------------------- | --------------- |
| Type of the argument | ""              |



**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case    |
| ---------- | --------------- | ------------------------------------------------------------ | ------------------ |
| String     | Valid           | B: validateCard("") -> false<br>validateCard("5333171033425866") -> false | testValidateCard() |
| null       | Invalid         | validateCard(null) -> false                                  | ""                 |

### 

### **Class *EZShop* - method *assignId***

This method returns unique IDs that are not contained in the Set passed. IDs are generated between 1 and 100

**Criteria for method *assignId:***

 - Type of the argument

**Predicates for method *name*:**

| Criteria             | Predicate            |
| -------------------- | -------------------- |
| Type of the argument | Set<Integer> *valid* |
|                      | null Set *not valid* |



**Boundaries**:

| Criteria             | Boundary values    |
| -------------------- | ------------------ |
| Type of the argument | Passing a full Set |



**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case | JUnit test case |
|-------|-------|-------|-------|
|Set<Integer> test_set|Valid|Passing a valid and initialized set of values|testAssignId()|
|null|Invalid|Passing a null Set|""|
|B: Set with [1-100]|Invalid|Passing a full Set|""|

### **Class *EZShop* - method *barcodeIsValid()***

**Criteria for method *barcodeIsValid*:**

 - Validity of string
 - GS1 Standard

**Predicates for method *name*:**

| Criteria           | Predicate                                                    |
| ------------------ | ------------------------------------------------------------ |
| Validity of string | length = 12 \| 13 \| 14 *valid*                              |
|                    | length < 12 \| length > 14 *invalid*                         |
| GS1 Standard       | string matches the GS1 validation algorithm ( described at: https://www.gs1.org/services/how-calculate-check-digit-manually) - (*valid*) |
|                    | string not match GS1 validation algorithm -  (*not valid*)   |



**Boundaries**:

| Criteria           | Boundary values                          |
| ------------------ | ---------------------------------------- |
| Validity of string | null, string with length 11 or 15, ""    |
| GS1 Standard       | all-zeros strings of length 12 13 and 14 |



**Combination of predicates**:


| Criteria 1                 | Criteria 2 | Valid / Invalid | Description of the test case                                 | JUnit test case      |
| -------------------------- | ---------- | --------------- | ------------------------------------------------------------ | -------------------- |
| length < 12 \| length > 14 | *          | Invalid         | B: barcodeIsValid("aaaaaaaaaaa") <br>B: barcodeIsValid("aaaaaaaaaaaaaaa")<br>barcodeIsValid("aaa")<br>barcodeIsValid("thisIsAVeryVeryVeryVeryLongString")<br>B: barcodeIsValid(null)<br>B: barcodeIsValid("") | testBarcodeIsValid() |
| length == 12 \| 13 \| 14   | match      | Valid           | B: barcodeIsValid("000000000000")<br>B: barcodeIsValid("0000000000000")<br>B: barcodeIsValid("00000000000000")<br> barcodeIsValid("8004263697047") | ""                   |
|                            | not match  | Invalid         | barcodeIsValid("000000000001")<br>barcodeIsValid("5554673697047") | ""                   |

### **Class *EZShop* - method *writejArrayToFile()***

**Criteria for method *writejArrayToFile*:**

 - Validity of string
 - Validity of JSON Array
 - Correctness of written data

**Predicates for method *name*:**

| Criteria                    | Predicate                                                    |
| --------------------------- | ------------------------------------------------------------ |
| Validity of string          | string is a valid path - *valid*                             |
|                             | string is not a valid path (see boundary values) - *not valid* |
| Validity of JSON Array      | array is not null - *valid*                                  |
|                             | array is null - *not valid*                                  |
| Correctness of written data | data read from JSON after the write are equal to the data written - *valid* |
|                             | data read from JSON after the write are not equal to the data written - *not valid* |



**Boundaries**:

| Criteria                    | Boundary values |
| --------------------------- | --------------- |
| Validity of string          | null or ""      |
| Validity of JSON Array      | null            |
| Correctness of written data | empty data      |



**Combination of predicates**:


| Criteria 1 | Criteria 2  | Criteria 3  | Valid / Invalid | Description of the test case                         | JUnit test case         |
| ---------- | ----------- | ----------- | --------------- | ---------------------------------------------------- | ----------------------- |
| null       | *           | *           | Invalid         | writejArrayToFile("", array)                         | testWriteJarrayToFIle() |
| ""         | *           | *           | Invalid         | writejArrayToFile(null, array)                       | ""                      |
| valid path | null        | *           | Invalid         | writejArrayToFile("src/data/file.txt", null)         | ""                      |
| valid path | valid array | JSON Object | Valid           | writing {"name":"test","value":"2"}<br>B: writing {} | ""                      |


## AccountBook
### **Class *AccountBook - method *addOperation***

**Criteria for method *addOperation:***

 - Type of the argument

**Predicates for method *name*:**

| Criteria             | Predicate                       |
| -------------------- | ------------------------------- |
| Type of the argument | BalanceOperation object *valid* |
|                      | null *not valid*                |



**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1       | Valid / Invalid | Description of the test case                  | JUnit test case |
| ---------------- | --------------- | --------------------------------------------- | --------------- |
| BalanceOperation | Valid           | Passing a valid and initialized set of values | testAssignId()  |
| null             | Invalid         | Passing a null Set                            | "**              |



# White Box Unit Tests
### Test cases definition

    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name | JUnit test case |
|--|--|
|ProductTypeImplementation - changeQuantity|testProduct()|
|||
|||

### Code coverage report

    <Add here the screenshot report of the statement and branch coverage obtained using
    the Eclemma tool. >


### Loop coverage analysis
    <Identify significant loops in the units and reports the test cases
    developed to cover zero, one or multiple iterations >

|Unit name | Loop rows | Number of iterations | JUnit test case |
|---|---|---|---|
|||||
|||||
||||||
