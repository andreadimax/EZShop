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

|           | shop  | AccountBook | FinancialTransaction | credit | debit | User  | Cashier | ShopManager | Administrator | Order | ProductType | Product | Position | ReturnTransaction | Quantity | Ticket | CreditCardCircuit | SaleTransaction | LoyaltyCard | Customer |
| :-------: | :---: | :---------: | :------------------: | :----: | :---: | :---: | :-----: | :---------: | :-----------: | :---: | :---------: | :-----: | :------: | :---------------: | :------: | :----: | :---------------: | :-------------: | :---------: | :------: |
|    FR1    |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR1.1   |   X   |             |                      |        |       |   X   |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR1.2   |   x   |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR1.3   |   x   |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR1.4   |   x   |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
| FR1.5 (?) |   x   |             |                      |        |       |   x   |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|    FR3    |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR3.1   |   x   |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |        |                   |                 |             |          |
|   FR3.2   |   x   |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR3.3   |   x   |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |        |                   |                 |             |          |
|   FR3.4   |   x   |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |        |                   |                 |             |          |
|    FR4    |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR4.1   |   x   |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |        |                   |                 |             |          |
|   FR4.2   |   x   |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |        |                   |                 |             |          |
|   FR4.3   |  (?)  |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR4.4   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR4.5   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR4.6   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR4.7   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR 5    |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR5.1   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR5.2   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR5.3   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR5.4   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR5.5   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR5.6   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR5.7   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|    FR6    |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.1   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.2   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.3   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.4   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.5   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.6   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.7   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.8   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR6.9   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|  FR6.10   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|  FR6.11   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|  FR6.12   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|  FR6.13   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|  FR6.14   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|  FR6.15   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|    FR7    |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR7.1   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR7.2   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR7.3   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR7.4   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|    FR8    |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR8.1   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR8.2   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR8.3   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |
|   FR8.4   |       |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |        |                   |                 |             |          |









# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

