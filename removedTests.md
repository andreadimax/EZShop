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


| Criteria 1            | Valid / Invalid | Description of the test case                  | JUnit test case |
| --------------------- | --------------- | --------------------------------------------- | --------------- |
| Set<Integer> test_set | Valid           | Passing a valid and initialized set of values | testAssignId()  |
| null                  | Invalid         | Passing a null Set                            | ""              |
| B: Set with [1-100]   | Invalid         | Passing a full Set                            | ""              |

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


## 

## AccountBook

### **Class *AccountBook - method *getFilepath()***

**Criteria for method *getFilepath:***

 - Type of the argument

**Predicates for method *getFilepath*:**

| Criteria                     | Predicate                                                    |
| ---------------------------- | ------------------------------------------------------------ |
| correctness of returned path | returned string is src/main/persistent_data/operations.json - valid |
|                              | returned string is not src/main/persistent_data/operations.json - invalid |



**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case       | JUnit test case |
| ---------- | --------------- | ---------------------------------- | --------------- |
| *          | valid           | Object AccountBook is instantiated | testGetFilepath |



### **Class *AccountBook* - method *setBalance()***

**Criteria for method *setBalance***:

**Predicates for method *setBalance*:**

| Criteria | Predicate |
| -------- | --------- |

**Boundaries**:

| Criteria | Boundary values |
| -------- | --------------- |

**Combination of predicates**:


| Criteria 1 | Valid / Invalid | Description of the test case                                 | JUnit test case  |
| ---------- | --------------- | ------------------------------------------------------------ | ---------------- |
| *          | Valid           | Object AccountBookis instantiated<br>accountBook.setBalance(5.0)<br />>value is checked through the getter | testSetBalance() |

### 

### **Class *AccountBook* - method *changeBalance()***

**Criteria for method *setBalance***:

**Predicates for method *setBalance*:**

| Criteria                   | Predicate                               |
| -------------------------- | --------------------------------------- |
| new balance value validity | balance = balance + amount  --> vaild   |
|                            | balance != balance + amount --> invalid |

**Boundaries**:

| Criteria                 | Boundary values      |
| ------------------------ | -------------------- |
| validity of input amount | [-inf, -1] [0, +inf] |
|                          |                      |

**Combination of predicates**:


| Criteria 1                 | Valid / Invalid | Description of the test case                                 | JUnit test case     |
| -------------------------- | --------------- | ------------------------------------------------------------ | ------------------- |
| new balance value validity | Valid           | Object AccountBookis instantiated<br>oldbalance = accountBook.getBalance();<br />accountBook.changeBalance(1);<br />>value is checked through getBalance | testChangeBalance() |
| new balance value validity | Valid           | Object AccountBookis instantiated<br>oldbalance = accountBook.getBalance();<br />accountBook.changeBalance(-1);<br />>value is checked through getBalance | testChangeBalance   |

### 