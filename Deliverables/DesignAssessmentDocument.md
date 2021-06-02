# Design assessment


```
<The goal of this document is to analyse the structure of your project, compare it with the design delivered
on April 30, discuss whether the design could be improved>
```

# Levelized structure map
```
<Applying Structure 101 to your project, version to be delivered on june 4, produce the Levelized structure map,
with all elements explosed, all dependencies, NO tangles; and report it here as a picture>
```

# Structural over complexity chart
```
<Applying Structure 101 to your project, version to be delivered on june 4, produce the structural over complexity chart; and report it here as a picture>
```



# Size metrics

```
<Report here the metrics about the size of your project, collected using Structure 101>
```



| Metric                                    | Measure |
| ----------------------------------------- | ------- |
| Packages                                  | 5       |
| Classes (outer)                           | 40      |
| Classes (all)                             | 41      |
| NI (number of bytecode instructions)      | 8,360   |
| LOC (non comment non blank lines of code) | ~3,595  |



# Items with XS

```
<Report here information about code tangles and fat packages>
```

| Item                                                                                                              | Tangled |  Fat |  Size |    XS |
| ----                                                                                                              | ------- | ---- |  ---- |  ---- |
| ezshop.it.polito.ezshop.data.EZShop class                                                                         |         |  266 | 6,406 | 3,516 |
| ezshop.it.polito.ezshop.data.EZShop.deleteReturnTransaction(java.lang.Integer):boolean                            |         |   21 |   285 |    81 |
| ezshop.it.polito.ezshop.data.EZShop.endReturnTransaction(java.lang.Integer, boolean):boolean                      |         |   17 |   280 |    32 |
| ezshop.it.polito.ezshop.data.EZShop.addProductToSale(java.lang.Integer, java.lang.String, int):boolean            |         |   17 |   199 |    23 |
| ezshop.it.polito.ezshop.data.EZShop.recordOrderArrival(java.lang.Integer):boolean                                 |         |   17 |   195 |    22 |
| ezshop.it.polito.ezshop.data.EZShop.deleteProductFromSale(java.lang.Integer, java.lang.String, int):boolean       |         |   17 |   172 |    20 |
| ezshop.it.polito.ezshop.data.EZShop.modifyCustomer(java.lang.Integer, java.lang.String, java.lang.String):boolean |         |   16 |   165 |    10 |
|                                                                                                                   |         |      |       |       |


# Package level tangles

```
<Report screen captures of the package-level tangles by opening the items in the "composition perspective" 
(double click on the tangle from the Views->Complexity page)>
```

# Summary analysis
```
<Discuss here main differences of the current structure of your project vs the design delivered on April 30>
<Discuss if the current structure shows weaknesses that should be fixed>
```
