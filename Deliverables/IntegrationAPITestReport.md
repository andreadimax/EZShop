# Integration and API Test Documentation

Authors:

Date:

Version:

# Contents

- [Integration and API Test Documentation](#integration-and-api-test-documentation)
- [Contents](#contents)
- [Dependency graph](#dependency-graph)
- [Integration approach](#integration-approach)
- [Tests](#tests)
  - [Step 1](#step-1)
  - [Step 2](#step-2)
  - [Step 3](#step-3)
  - [Step n](#step-n)
- [Scenarios](#scenarios)
  - [Scenario UCx.y](#scenario-ucxy)
- [Coverage of Scenarios and FR](#coverage-of-scenarios-and-fr)
- [Coverage of Non Functional Requirements](#coverage-of-non-functional-requirements)
    - [](#)

- [Tests](#tests)

- [Scenarios](#scenarios)

- [Coverage of scenarios and FR](#scenario-coverage)
- [Coverage of non-functional requirements](#nfr-coverage)



# Dependency graph 

```plantuml
@startuml

title Dependency Graph

class GUI
class "EZShop (API)" as api
class "OrderAdapter" as ordad
class "OrderImpl" as ordimpl
class "AccountBook" as accbook
class "SaleTransactionAdapter" as stadapt
class "SaleTransactionImpl" as stimpl
class "ReturnTransaction" as rettrans
class "ProductTypeImpl" as ptimpl
class "BalanceOperation" as balop
class "TicketEntryImpl" as teimpl
class "UserImpl" as user
class "CustomerImpl" as customer

GUI --> api

api --> ordad
api --> accbook
api --> stadapt
api --> user
api --> customer
api --> ptimpl

ordad --> ordimpl

accbook --> ordimpl
accbook --> rettrans

stadapt --> stimpl

ordimpl --> balop

rettrans --> stimpl

stimpl --> balop
stimpl --> teimpl


@enduml
```

# Integration approach

    <Write here the integration sequence you adopted, in general terms (top down, bottom up, mixed) and as sequence
    (ex: step1: class A, step 2: class A+B, step 3: class A+B+C, etc)> 
    <Some steps may  correspond to unit testing (ex step1 in ex above), presented in other document UnitTestReport.md>
    <One step will  correspond to API testing>

- Integration sequence = Bottom up





#  Tests

   <define below a table for each integration step. For each integration step report the group of classes under test, and the names of
     JUnit test cases applied to them> JUnit test classes should be here src/test/java/it/polito/ezshop

## Step 1
| Classes                                                            | JUnit test cases                                                         |
|--------------------------------------------------------------------|--------------------------------------------------------------------------|
| TicketEntryImpl + BalanceOperation + SaleTransactionImplementation | EZShopTests/SaleTransactionImplementationTests - testSaleTransactionImpl |
| OrderImplementation + BalanceOperation                             | EZShopTests/OrderImplementationTests - testOrderImpl                     |



## Step 2
| Classes                                                                                     | JUnit test cases                                                      |
|---------------------------------------------------------------------------------------------|-----------------------------------------------------------------------|
| TicketEntryImpl + BalanceOperation + ReturnTransaction                                      | EZShopTests/SaleTransactionImplementationTests -testReturnTransaction |
| TicketEntryImpl + BalanceOperation + SaleTransactionImplementation + SaleTransactionAdapter | EZShopTests/SaleTransactionAdapterTests - testOrderAdapter            |


## Step 3
| Classes                                               | JUnit test cases                              |
|-------------------------------------------------------|-----------------------------------------------|
| BalanceOperation + OrderImplementation + OrderAdapter | EZShopTests/OrderAdapterTests - testOrderImpl |
|                                                       |                                               |


## Step 4 (API Testing)

   

| Classes                     | JUnit test cases                       |
|-----------------------------|----------------------------------------|
| UserImplementation + EZShop | EZShopTests/EZShopTests - TestUserAPIs |
|                             |                                        |




# Scenarios


<If needed, define here additional scenarios for the application. Scenarios should be named
 referring the UC in the OfficialRequirements that they detail>

## Scenario UCx.y

| Scenario       | name            |
| -------------  | :-------------: |
| Precondition   |                 |
| Post condition |                 |
| Step#          | Description     |
| 1              | ...             |
| 2              | ...             |



# Coverage of Scenarios and FR


<Report in the following table the coverage of  scenarios (from official requirements and from above) vs FR. 
Report also for each of the scenarios the (one or more) API JUnit tests that cover it. >




| Scenario ID     | Functional Requirements covered | JUnit  Test(s)                            |
| -----------     | ------------------------------- | -----------                               |
| From 2.1 to 2.3 | all FR1 subrequirements         | EZShopTests/EZShopTests - TestUserAPIs    |
| From 1.1 to 1.3 | all FR2 subrequirements         | EZShopTests/EZShopTests - TestProductTypeAPIs |
| ...             |                                 |                                           |
| ...             |                                 |                                           |



# Coverage of Non Functional Requirements


<Report in the following table the coverage of the Non Functional Requirements of the application - only those that can be tested with automated testing frameworks.>


### 

| Non Functional Requirement | Test name |
| -------------------------- | --------- |
|                            |           |

