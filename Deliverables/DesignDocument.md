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

|           |  shop  | AccountBook | BalanceOperation     | credit | debit | User  | Cashier | ShopManager | Administrator | Order | ProductType | Product | Position | ReturnTransaction | Quantity | CreditCardCircuit | SaleTransaction | LoyaltyCard | Customer |
| :-------: | :----: | :---------: | :------------------: | :----: | :---: | :---: | :-----: | :---------: | :-----------: | :---: | :---------: | :-----: | :------: | :---------------: | :------: | :---------------: | :-------------: | :---------: | :------: |
|    FR1    |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR1.1   |   X    |             |                      |        |       |   X   |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR1.2   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR1.3   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR1.4   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
| FR1.5     |   x    |             |                      |        |       |   x   |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|    FR3    |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR3.1   |   x    |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |                   |                 |             |          |
|   FR3.2   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR3.3   |   x    |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |                   |                 |             |          |
|   FR3.4   |   x    |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |                   |                 |             |          |
|    FR4    |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR4.1   |   x    |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |                   |                 |             |          |
|   FR4.2   |   x    |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |                   |                 |             |          |
| FR4.3(?)  |   x    |             |          x           |        |       |       |         |             |               |   x   |             |         |          |                   |          |                   |                 |             |          |
|   FR4.4   |   x    |             |          x           |        |   x   |       |         |             |               |   x   |      x      |         |          |                   |          |                   |                 |             |          |
| FR4.5(?)  |   x    |             |                      |        |       |       |         |             |               |   x   |             |         |          |                   |          |                   |                 |             |          |
|   FR4.6   |   x    |             |                      |        |       |       |         |             |               |   x   |             |         |          |                   |          |                   |                 |             |          |
|   FR4.7   |   x    |             |                      |        |       |       |         |             |               |   x   |             |         |          |                   |          |                   |                 |             |          |
|   FR 5    |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR5.1   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |    x     |
|   FR5.2   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |    x     |
|   FR5.3   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |    x     |
|   FR5.4   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |    x     |
|   FR5.5   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |      x      |          |
|   FR5.6   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |      x      |          |
|   FR5.7   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |      x      |          |
|    FR6    |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR6.1   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR6.2   |   x    |             |                      |        |       |       |         |             |               |       |      x      |    x    |          |                   |    x     |                   |        x        |             |          |
|   FR6.3   |   x    |             |                      |        |       |       |         |             |               |       |      x      |    x    |          |                   |    x     |                   |        x        |             |          |
|   FR6.4   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |        x        |             |          |
|   FR6.5   |   x    |             |                      |        |       |       |         |             |               |       |      x      |         |          |                   |          |                   |                 |             |          |
|   FR6.6   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |        x        |             |          |
|   FR6.7   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR6.8   | ticket |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR6.9   | ticket |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|  FR6.10   | ticket |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|  FR6.11   |   x    |      x      |          x           |   x    |       |       |         |             |               |       |             |         |          |                   |          |         x         |        x        |             |          |
|  FR6.12   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |         x         |          |                   |                 |             |          |
|  FR6.13   | ticket |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|  FR6.14   |   x    |             |                      |        |       |       |         |             |               |       |             |         |          |         x         |          |                   |                 |             |          |
|  FR6.15   |   x    |      x      |          x           |        |   x   |       |         |             |               |       |      x      |    x    |          |         x         |          |                   |                 |             |          |
|    FR7    |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR7.1   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR7.2   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR7.3   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR7.4   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|    FR8    |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR8.1   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR8.2   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR8.3   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |
|   FR8.4   |        |             |                      |        |       |       |         |             |               |       |             |         |          |                   |          |                   |                 |             |          |









# Verification sequence diagrams 
\<select key scenarios from the requirement document. For each of them define a sequence diagram showing that the scenario can be implemented by the classes and methods in the design>

