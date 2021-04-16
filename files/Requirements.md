# Stakeholders
| Stakeholder name | Description                                                                                                                                                      |
| ---------------- | -----------                                                                                                                                                      |
| User             | Any user who can interact with the system:<br>- Cashier<br> - Owner<br> - Shop Director<br> - Product Discarder<br> - Restock Checker<br> - Customer at POS <br> |
| Small shops      | Any type of small-medium sized business (from 50 to 200 square meters) with an inventory ranging from 500 to 2000 different types of products.                   |
| POS System       | The system interacts with a POS type application for managing transactions                                                                                       |
| Developer        | Who develops and maintains the system (software engineer, marketing person, bank's IT specialist)                                                                |
| IT Administrator | Administrator of the IT System                                                                                                                                   |
| Supplier         | A shop's supplier can have te access to the inventory (?) <br> -Replies to issued orders shipping goods to the shop                                              |
| Revenue agency   | gets shop's monthly accounting update notification                                                                                                               |
| Product          | Sold goods                                                                                                                                                       |

# Context Diagram and interfaces

## Context Diagram

<img src="../uml/ContextDiagram.png">

## Interfaces
| Actor          | Logical Interface | Physical Interface                              |
| -------------  | :-------------:   | -----:                                          |
| User           | GUI               | Screen, monitor, keyboard, mouse, cash register |
| POS System     | POS API           | Ethernet cable                                  |
| Supplier       | GUI               | Internet                                        |
| Revenue agency | Web service       | Internet                                        |
| Barcode system | Driver            | Barcode reader                                  |

# Stories and personas


John is the shop owner and director of a small shop of 50-200 square meters and sells about 500-2000 different item types.
John's shop has 4 different checkouts, and 12 employees.

John uses the application EZSHOP in order to manage his shop.

John is able to easily keep the inventory in the application and the actual number of goods inside the shop in sync by compiling every month a MonthlyStolenReport(a list of the missing items in the shop, it is compiled once a month).
In order to do so, John asks his employees to count all the goods present in the shop, which he then compares with the inventory tracked by the application.
After inserting the Monthly stolen report in the application, John can be sure that his inventory will always be coherent with the actual number of items in stock.

John also wants to know actual statistics about his shop earnings and losses, to do so, he accesses the monthly accounting page, in which he has a detailed list of the earnings and expenses he got in previous months.

Everytime he wants to order new goods, he accesses the RestockOrder page, in which he inserts The date and the cost of the order, which will be automatically used by EZSHOP to calculate the monthly accounting.
(?) if we keep it this way, we need to remove the association restockOrder--ProductDescriptor

John can easily manage all his personnel from EZSHOP, he can change their salary, hire them, and fire them. The salary is automatically tracked by the monthly accounting.

In case the inventory needed to be updated, the shop director is able to manage the inventory by adding or removing products but also by updating the properties of the goods present in the EZSHOP inventory, such as price the price at which they are sold or the reorder threshold.

John is automatically warned by the system when has to order a new stock of a product. The system notifies him if a type of item in the inventory is below a specific threshold associated to it.

(?) i think we should remove the "list actions" from Session, it becomes too complicated
______________________________________________________________________________________________
Sara,Tom, Tia and Mike are four of John's employees. As Personnel, they can carry out many duties, from reordering the shelves, to cleaning. The EZSHOP application keeps them flexible in changing their tasks while still being tracked by the system when needed. 

When its Sara's turn to work at the checkout, she borrows a cash container containing cash from the shop, which she uses to give the rest to clients and  logs in using the credentials provided to her by John. 
In order to correctly initiate the checkout session, she has to insert the borrowed amount into her terminal to let the system know her debt.
Once the Checkout session is initiated, Sara is able to start a new transaction for each or her customers, and the system keeps track of the amount of money she has earned during the checkout session.
When sara has to end her checkout session, before she returns the cash container, the system asks her to count the amount of money in it and to log it. 
If the system automatically warns the shopdirector if the money she gathered are too low  compared to what she should have had.

When the goods ordered by the shop director arrive to she shop, Tom can login as a RestockChecker and start counting the incoming goods by scanning the barcodes and inserting the relative quantity. The system automatically updates the inventory without him having to do it manually. Morover, Tom has also the possibility of raise issues to the shopdirector, regarding some of the goods he has received by stating the affected products and quantity.

With the term customers, we mean the people which buy in the shop using a CustomerCard. When Tia logs in in a ManageCustomerSession, she is able to create new customer cards and associate them to customers, morover, she can search through customer data in order to visualize the name, surname, and SSN of the customer which has a specific cardID.

When Mike is reordering the shelves, and finds that a product has expired, he is able start a DiscardExpiredSession, which lets him scan the expired products. Thanks to this procedure, Mike is able to discard the expired products while still keeping the inventory synchronized with the actual number of items present in the shop.









# Functional and non functional requirements
## Functional Requirements
|            ID | Description                                                                    |                                      |
| ------------- | -------------                                                                  | -----                                |
|           FR1 | Manage sales                                                                   |                                      |
|             1 | Start new Transaction                                                          |                                      |
|             2 | Scan products                                                                  |                                      |
|             3 | Calculate total Amount                                                         |                                      |
|             4 | Check Credit Card use                                                          |                                      |
|             5 | Update quantity in ProductDescriptor for each sold product                     |                                      |
|             6 | Rollback ongoing transaction                                                   |                                      |
|             7 | Update fidelity points                                                         |                                      |
|             8 | End Transaction                                                                |                                      |
|               |                                                                                |                                      |
|           FR2 | Manage inventory                                                               |                                      |
|             1 | Add new ProductDescriptor                                                      |                                      |
|             2 | Remove Product Descriptor                                                      |                                      |
|             3 | Update Product Descriptor Properties                                           |                                      |
|             4 | Show inventory                                                                 |                                      |
|               |                                                                                |                                      |
|           FR3 | Manage customers                                                               |                                      |
|             1 | Add loyalty card code                                                          |                                      |
|             2 | Store customer data                                                            |                                      |
|             3 | Remove customer                                                                |                                      |
|             4 | Update fidelity points                                                         |                                      |
|               |                                                                                |                                      |
|           FR4 | Support accounting                                                             |                                      |
|             1 | Perform MonthlyStolenReport                                                    |                                      |
|           1.1 | Get stolen product list                                                        |                                      |
|           1.2 | Calculate total cost of stolen products                                        |                                      |
|           1.3 | Update inventory                                                               |                                      |
|               |                                                                                |                                      |
|             2 | Perform DiscardExpiredSession                                                  |                                      |
|           2.1 | Compute totalDiscardedCost                                                     |                                      |
|           2.2 | Get discardedProductList                                                       |                                      |
|           2.3 | Update Inventory                                                               |                                      |
|               |                                                                                |                                      |
|             3 | Track RestockOrders(date, moneyCost, Suppliers)                                | should it track also products??? (?) |
|               |                                                                                |                                      |
|             4 | Perform RestockCheckingSession                                                 | (?)                                  |
|           4.1 | Scan restock items                                                             |                                      |
|           4.2 | Update inventory                                                               |                                      |
|           4.3 | send RestockIssue to Director                                                  |                                      |
|               |                                                                                |                                      |
|             5 | Perform CheckoutSession                                                        |                                      |
|           5.1 | Get Starting cash amount                                                       |                                      |
|           5.2 | Accumulate Transaction value                                                   |                                      |
|           5.3 | Update CurrentCashAmount                                                       |                                      |
|           5.4 | End CheckoutSession                                                            |                                      |
|         5.4.1 | Get human-counted final cash amount                                            |                                      |
|         5.4.2 | Notify ShopDirector if (CurrentCashAmount - human-counted) > checkoutThreshold |                                      |
|               |                                                                                |                                      |
|               |                                                                                |                                      |
|           FR5 | Manage Personnel                                                               |                                      |
|             1 | Hire Personnel                                                                 |                                      |
|           1.1 | Set PersonnelId                                                                |                                      |
|           1.2 | Set Salary                                                                     |                                      |
|           1.3 | Set Password                                                                   |                                      |
|             2 | Update Salary                                                                  |                                      |
|           2.1 | Get new salary                                                                 |                                      |
|           2.2 | Remove old salary from MonthlyAccounting                                       |                                      |
|           2.3 | Overwrite old salary                                                           |                                      |
|           2.4 | Add new salary to MonthlyAccounting                                            |                                      |
|             3 | Update Password                                                                |                                      |
|           3.1 | Get new password                                                               |                                      |
|           3.2 | Overwrite old password                                                         |                                      |
|             4 | Fire Personnel                                                                 |                                      |
|               |                                                                                |                                      |
|           FR6 | Authenticate User(Login and Logout)                                            |                                      |
|             1 | Get PersonnelId and Password                                                   |                                      |
|             2 | Verify Password is correct                                                     |                                      |
|               |                                                                                |                                      |
|           FR7 | Issue RestockOrder                                                             |                                      |
|               |                                                                                |                                      |

## Non Functional Requirements
| ID            | Type (efficiency, reliability, .. see iso 9126) | Description                                                                                                | Refers to FR |
| ------------- | :-------------:                                 | :-----:                                                                                                    | -----:       |
| NFR1          | Usability                                       | Time to learn how to use  < 1 hour of training + 7 hour of practice                                        |              |
| NFR2          | Functionality                                   | Data stored must be available 24 hrs/ 24 7 days/ 7                                                         | FR2          |
| NFR3          | Efficiency                                      | GUI response time less than 0.5s                                                                           | FR1.2        |
| NFR4          | Efficiency                                      | Support up to 10 Checkouts                                                                                 |              |
| NFR5          | Efficiency                                      | Support up to 2000 different ProductDescriptors                                                            | FR2          |
| NFR6          | Maintainability                                 | Time to restore system < 1 hr                                                                              |              |
| NFR7          | Reliability                                     | number of data backup per day >= 1                                                                         |              |
| NFR8          | Security                                        | For credit card payments only last 4 digits are stored                                                     |              |
| NFR9          | Portability                                     | The application must be compatible with Windows based systems (Windows 7 version or later)                 |              |
| NFR10         | Usability                                       | Gui uses large buttons and large text size in order to be easily usable for people with sight deficiencies |              |
| NFR11         | Efficiency                                      | Support up to 20 employees(personnel)                                                                      |              |

# Use case diagram and use cases
## Use case diagram
<img src="../uml/UsecaseDiagram.png">

## Use Cases
### Use case 1, Create User Profile
| Actors Involved  | Personnel, Shop Director                                        |
| -------------    | -------------                                                   |
| Precondition     | Personnel Account does not exist                                |
| Post condition   | Personnel Account created                                       |
| Nominal Scenario | Only ShopDirector can create a new user account. Id is assigned |
| Variant          | Role (RestockChecker, Cashier...) can be assigned               |

### Use case 2, Delete User Profile
| Actors Involved  | Personnel, Shop Director                                        |
| -------------    | -------------                                                   |
| Precondition     | Personnel Account exist                                |
| Post condition   | Personnel Account does not exist anymore                                       |
| Nominal Scenario | Only ShopDirector can delete a user account. Id is discarded |

### Use case 3, Start RestockCheckingSession
| Actors Involved  | Personnel                                  |
| -------------    | -------------                              |
| Precondition     | Personnel must be logged as RestockChecker |
| Post condition   | Restock Order performed                    |
| Nominal Scenario | Supplier is selected, order is performed   |

### Use case 4, Start Checkout Session
| Actors Involved  | Personnel                                                                      |
| -------------    | -------------                                                                  |
| Precondition     | Personnel must be logged as Cashier                                            |
| Post condition   | A transaction is completed                                                     |
| Nominal Scenario | Cashier opens a new checkout session. Products are scanned with barcode reader |

### Use case 5, Start DiscardExpiredSession
| Actors Involved  | Personnel                                                                       |
| -------------    | -------------                                                                   |
| Precondition     | Personnel must be logged as Discarder                                           |
| Post condition   | Expired products are discarded                                                  |
| Nominal Scenario | Personnel opens a new session. Expired products are scanned with barcode reader |

# Relevant scenarios
## Scenario 1
| Scenario ID:  | Corresponds to UC 4                                                     |
| ------------- | -------------                                                           |
| Description   | Transaction failed                                                      |
| Precondition  | Payment is done with credit card                                        |
| Postcondition | New tentative or transaction blocked                                    |
| Step#         | Step description                                                        |
| 1             | Customer digits a wrong PIN                                             |
| 2             | Checkout is blocked                                                     |
| 3             | if # of attempts < max attempts available new attempt starts            |
| 4             | Transaction is unlocked if POS System return success transaction status |

## Scenario 2
| Scenario ID:  | Corresponds to UC 5     |
| ------------- | -------------           |
| Description   | Wrong product delivered |
| Precondition  |                         |
| Postcondition |                         |
| Step#         |                         |

# Glossary
# System design
<img src='./../uml/ContextDiagram.png'></img>
# Deployment diagram
<img src='./deployment_diagram.png'>
