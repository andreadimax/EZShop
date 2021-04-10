# Stakeholders
| Stakeholder name | Description                                                                                                                                                      |   |
| ---------------- | -----------                                                                                                                                                      |   |
| User             | Any user who can interact with the system:<br>- Cashier<br> - Owner<br> - Shop Director<br> - Product Discarder<br> - Restock Checker<br> - Customer at POS <br> |   |
| Small shops      | Any type of small-medium sized business (from 50 to 200 square meters) with an inventory ranging from 500 to 2000 different types of products.                   |   |
| POS System       | The system interacts with a POS type application for managing transactions                                                                                       |   |
| Developer        | Who develops and maintains the system (software engineer, marketing person, bank's IT specialist)                                                                |   |
| IT Administrator | Administrator of the IT System                                                                                                                                   |   |
| Supplier         | A shop's supplier can have te access to the inventory (?) <br> -Replies to issued orders shipping goods to the shop                                              |   |
| Revenue agency   | gets shop's monthly accounting update notification                                                                                                               |   |
| Product          | Sold goods                                                                                                                                                       |   |

# Context Diagram and interfaces

## Context Diagram

<img src="ContextDiagram.png"></img>
## Interfaces
| Actor | Logical Interface | Physical Interface |
| ------------- |:-------------:| -----:|
| User | GUI | Screen, monitor, keyboard, mouse, cash register |
| POS System | POS API | Ethernet cable |
| Supplier | GUI | Internet |
| Revenue agency | Web service | Internet |
| Barcode system | Driver | Barcode reader |


# Functional and non functional requirements
## Functional Requirements
| ID| Description |
| ------------- | ------------- |
| FR1 | Manage sales |
| 1 | Start new Transaction |
| 2 | Scan products |
| 3 | Calculate total Amount|
| 4 | Check Credit Card use |
| 5 | Update quantity in ProductDescriptor for each sold product|
| 6 | Rollback ongoing transaction|
| 7 | Update fidelity points|
| 8 | End Transaction |
| FR2 | Manage inventory |
| 1 | Add new ProductDescriptor |
| 2 | Remove Product Descriptor |
| 3 | Update Product Descriptor Properties |
| 4 | Show inventory |
| FR3 | Manage customers |
| 1 | Add loyalty card code |
| 2 | Store customer data |
| 3 | Remove customer |
| 4 | Update fidelity points |
| FR4 | Support accounting |
| 1 | Perform MonthlyStolenReport |
| 1.1| Get stolen product list |
|1.2| Calculate total cost of stolen products|
|1.3| Update inventory |
| 2 | Perform DiscardExpiredSession |
| 2.1 | Compute totalDiscardedCost |
| 2.2 | Get discardedProductList |
| 2.3 | Update Inventory |
| 3 | Tracks RestockOrders | (?)
| 3.1 | Compute moneyCost of the order|
| 4 | Perform RestockCheckingSession | (?)
| 5 | Perform CheckoutSession |
| 5.1 | get Starting cash amount |
| 5.2 | accumulate Transaction value |
| 5.3 | update CurrentCashAmount |
| 5.4 | end CheckoutSession |
| 5.4.1| get human-counted final cash amount|
| 5.4.2| Compare difference between CurrentCashAmount and human-counted final cash amount to checkoutThreshold |
| 5.4.3 | if above threshold notify ShopDirector (?) |
| 6 | Account Salaries |
| FR5 | Manage Personnel |
| 1 | Hire Personnel |
| 1.1 | Set PersonnelId|
| 1.2 | Set Salary |
| 1.3 | Set Password |
| 2 | Update Salary |
| 2.1 | get new salary
| 2.2 | remove old salary from MonthlyAccounting
| 2.3 | overwrite old salary
| 2.4 | add new salary to MonthlyAccounting
| 3 | Update Password |
| 3.1 | get new password |
| 3.2 |  overwrite old password |
| 4 | Fire Personnel | 
| FR6 | Permits System Login and Logout |
| 1 | Get PersonnelId and Password |
| 2 | Verify Password is correct |

## Non Functional Requirements
| ID | Type (efficiency, reliability, .. see iso 9126) |Description | Refers to FR |
| ------------- |:-------------:| :-----:| -----:|
| NFR1 | Usability | time to learn how to use  < 1 hour of traininf + 7 hour of practice  | |
| NFR2 | Functionality | Data stored must be available 24 hrs/ 24 7 days/ 7 | FR2 |
| NFR3 | Efficiency | GUI response time less than 0.5s | FR1.2|
| NFR4 | Efficiency | Support up to 10 Checkouts||
| NFR5 | Efficiency |Support up to 2000 different ProductDescriptors||
| NFR6 | Maintainability | time to restore system < 1 hr | |
| NFR7 | Reliability | # of data backup per day >= 1 | |
| NFR8 | Security | for credit card payments only last 4 digits are stored |
| NFR9 | Portability | The application must be compatible with Windows based systems (Windows 7 version or later) | |

# Use case diagram and use cases
## Use case diagram
## Use Cases
### Use case 1, Create User Profile
| Actors Involved | Personnel, Shop Director |
| ------------- | ------------- |
| Precondition| Personnel Account does not exist |
| Post condition| Personnel Account created |
| Nominal Scenario |  Only ShopDirector can create a new user account. Id is assigned |
| Variant | Role (RestockChecker, Cashier...) can be assigned |

### Use case 2, Delete User Profile
| Actors Involved | Personnel, Shop Director |
| ------------- | ------------- |
| Precondition| Personnel Account does not exist |
| Post condition| Personnel Account created |
| Nominal Scenario |  Only ShopDirector can create a new user account. Id is assigned |

### Use case 3, Start RestockCheckingSession
| Actors Involved| Personnel  |
| ------------- | ------------- |
| Precondition| Personnel must be logged as RestockChecker |
| Post condition| Restock Order performed |
| Nominal Scenario |  Supplier is selected, order is performed |

### Use case 4, Start Checkout Session
| Actors Involved| Personnel  |
| ------------- | ------------- |
| Precondition| Personnel must be logged as Cashier |
| Post condition| A transaction is completed |
| Nominal Scenario | Cashier opens a new checkout session. Products are scanned with barcode reader  |

### Use case 5, Start DiscardExpiredSession
| Actors Involved| Personnel  |
| ------------- | ------------- |
| Precondition| |
| Post condition| Expired products are discarded  |
| Nominal Scenario | Personnel opens a new session. Expired products are scanned with barcode reader |

# Relevant scenarios
## Scenario 1
| Scenario ID:  | Corresponds to UC 4 |
| ------------- | -------------|
| Description  | Transaction failed |
| Precondition | Payment is done with credit card |
| Postcondition | New tentative or transaction blocked |
|Step# | Step description|
|1| Customer digits a wrong PIN |
|2| Checkout is blocked |
|3| if # of attempts < max attempts available new attempt starts |
|4| Transaction is unlocked if POS System return success transaction status |

## Scenario 2
| Scenario ID:  | Corresponds to UC 5 |
| ------------- | -------------|
| Description  | Wrong product delivered |
| Precondition | |
| Postcondition |  |
|Step# | |

# Glossary
# System design
<img src='./uml.png'>
# Deployment diagram
<img src='./deployment_diagram.png'>
