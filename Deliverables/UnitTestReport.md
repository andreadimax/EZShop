# Unit Testing Documentation

Authors: Andrea Di Mauro, Alessio Bincoletto, Daniele Cacciabue

Date:	13/05/2021

Version: 1.0

# Contents

- [Black Box Unit Tests](#black-box-unit-tests)




- [White Box Unit Tests](#white-box-unit-tests)


# Black Box Unit Tests

    <Define here criteria, predicates and the combination of predicates for each function of each class.
    Define test cases to cover all equivalence classes and boundary conditions.
    In the table, report the description of the black box test case and (traceability) the correspondence with the JUnit test case writing the 
    class and method name that contains the test case>
    <JUnit test classes must be in src/test/java/it/polito/ezshop   You find here, and you can use,  class TestEzShops.java that is executed  
    to start tests
    >

 ### **Class *EZShop* - method *assignId***

This method returns unique IDs that are not contained in the Set passed. IDs are generated between 1 and 100

**Criteria for method *name*:**

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

**Criteria for method *name*:**

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

**Criteria for method *name*:**

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
|            |             |             |                 |                                                      |                         |
|            |             |             |                 |                                                      |                         |
|            |             |             |                 |                                                      |                         |

 ### 


# 


# White Box Unit Tests

### Test cases definition

    <JUnit test classes must be in src/test/java/it/polito/ezshop>
    <Report here all the created JUnit test cases, and the units/classes under test >
    <For traceability write the class and method name that contains the test case>


| Unit name | JUnit test case |
|--|--|
|||
|||
||||

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



