package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.*;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountBookTest {

    @Test
    public void testSetBalance(){
        AccountBook accountBook = new AccountBook();
        accountBook.setBalance(5);
        double balance = accountBook.getBalance();
        assertEquals( 5.0, balance, 0.01);
    }

    @Test
    public void testGetFilepath(){
        AccountBook accountBook = new AccountBook();
        assertEquals("src/main/persistent_data/operations.json",accountBook.getFilepath());
    }

    @Test
    public void testChangeBalance(){
        AccountBook accountBook = new AccountBook();
        double balance = accountBook.getBalance();
        accountBook.changeBalance(1);
        assertEquals( balance+1.0, accountBook.getBalance(), 0.01);

        balance = accountBook.getBalance();
        accountBook.changeBalance(-1);
        assertEquals( balance-1.0, accountBook.getBalance(), 0.01);


    }

    @Test
    public void testAddOperation(){
        AccountBook accountBook = new AccountBook();

        BalanceOperationImpl balOp = new BalanceOperationImpl(-1);
        Integer id = balOp.getBalanceId();
        assertTrue(accountBook.addOperation(balOp));
        assertSame(balOp,accountBook.getOperation(id));

        OrderImpl ordOp = new OrderImpl("000000000002",1,2.0);
        id = ordOp.getBalanceId();
        assertTrue(accountBook.addOperation(ordOp));
        assertSame(ordOp,accountBook.getOperation(id));

        SaleTransactionImplementation saleOp = new SaleTransactionImplementation();
        id = saleOp.getBalanceId();
        assertTrue(accountBook.addOperation(saleOp));
        assertSame(saleOp,accountBook.getOperation(id));

        ReturnTransaction retOp = new ReturnTransaction(saleOp.getBalanceId());
        id = retOp.getBalanceId();
        assertTrue(accountBook.addOperation(retOp));
        assertSame(retOp,accountBook.getOperation(id));

        assertFalse(accountBook.addOperation(null));
        assertFalse(accountBook.addOperation(ordOp));
        assertFalse(accountBook.addOperation(saleOp));
        assertFalse(accountBook.addOperation(retOp));


        assertEquals("src/main/persistent_data/operations.json",accountBook.getFilepath());
    }






}
