# Design Document 


Authors: 

Date:

Version:


# Contents

- [Design Document](#design-document)
- [Contents](#contents)
- [Instructions](#instructions)
- [High level design](#high-level-design)
- [Low level design](#low-level-design)
- [Verification traceability matrix](#verification-traceability-matrix)
- [Verification sequence diagrams](#verification-sequence-diagrams)

# Instructions

The design must satisfy the Official Requirements document, notably functional and non functional requirements

# High level design 

<discuss architectural styles used, if any>
<report package diagram>






# Low level design

<for each package, report class diagram>









# Verification traceability matrix

\<for each functional requirement from the requirement document, list which classes concur to implement it>

|           |  | design | coding | unit testing | integration testing | acceptance testing | management | git maven |
|:-----------:|:--------:|:-----------:|:-----------:|:----------:|:------------:|:---------------:|:-------------:|:--------------:|
|  FR1     |   |   |   |   |   |   |   |   |
| FR1.1    | Define a new user, or modify an existing user |
| FR1.2     | Delete a user |
| FR1.3     |    List all users   |
| FR1.4    |    Search a user   |
| FR1.5  |  Manage rights. Authorize access to functions to specific actors according to access rights (?)|
| FR3   | Manage product catalog |
| FR3.1    | Define a new product type, or modify an existing product type|
| FR3.2     | Delete a product type |
| FR3.3     |    List all products types |
| FR3.4   | Search a product type (by bar code, by description) |
| FR4 | Manage inventory |
| FR4.1 | Modify quantity available for a product type |
| FR4.2 | Modify position for a product type |
| FR4.3 | Issue a reorder warning for a product type|
| FR4.4 | Send and pay an order for a product type |
| FR4.5 | Pay an issued reorder warning |
| FR4.6 | Record order arrival |
| FR4.7 | List all orders (issued, payed, completed) |
| FR 5 | Manage customers and cards |
| FR5.1 | Define or modify a customer |
| FR5.2 | Delete a customer  |
| FR5.3 | Search a customer  |
| FR5.4 | List  all customers  |
| FR5.5 | Create a card  |
| FR5.6 | Attach card to a customer  |
| FR5.7 | Modify points on a card   |
| FR6 | Manage a sale transaction |
| FR6.1 | Start a sale  |
| FR6.2 | Add  a product to a sale  |
| FR6.3 | Delete a product from a sale  |
| FR6.4 | Apply discount rate to a sale  |
| FR6.5 | Apply discount rate to a product type  |
| FR6.6 | Compute points for a sale |
| FR6.7 | Read bar code on product |
| FR6.8 | Print sale ticket |
| FR6.9 | Get sale ticket from ticket number |
| FR6.10 | Close  a sale transaction  |
| FR6.11 | Rollback or commit a closed sale transaction  |
| FR6.12 | Start  a return transaction  |
| FR6.13 | Return a product listed in a sale ticket |
| FR6.14 | Close  a return transaction  |
| FR6.15 | Rollback or commit a closed return transaction  |
| FR7 | Manage payment |
| FR7.1 | Receive payment cash|
| FR7.2 | Receive payment credit card|
| FR7.3 | Return payment cash|
| FR7.4 | Return payment credit card|
| FR8 | Accounting |
| FR8.1 | Record debit |
| FR8.2 | Record credit |
| FR8.3 | Show credits and debits over a period of time |
| FR8.4 | Compute balance  |









# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

