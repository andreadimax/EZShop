package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.*;
import org.json.simple.JSONArray;
import org.junit.Test;

import java.util.HashMap;
import java.util.LinkedList;

import static org.junit.Assert.*;

public class AccountBookTest {

    @Test
    public void testSetters(){
        AccountBook accountBook = new AccountBook();
        accountBook.setBalance(5);
        double balance = accountBook.getBalance();
        assertEquals( 5.0, balance, 0.01);
        HashMap<Integer,BalanceOperation> opMap = new HashMap<>();
        accountBook.setOperationsMap(opMap);
        assertSame(opMap,accountBook.getOperationsMap());
        JSONArray jArrayOp = new JSONArray();
        accountBook.setjArrayOperations(jArrayOp);
        assertSame(jArrayOp,accountBook.getjArrayOperations());
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
        BalanceOperationImpl.setBalanceCounter(0);

        AccountBook accountBook = new AccountBook();

        BalanceOperationImpl balOp = new BalanceOperationImpl(-1);
        Integer id = balOp.getBalanceId();
        assertTrue(accountBook.addOperation(balOp));
        assertSame(balOp,accountBook.getOperation(id));

        OrderImpl ordOp = new OrderImpl("000000000002",1,2.0);
        id = ordOp.getBalanceId();
        assertTrue(accountBook.addOperation(ordOp));
        assertSame(ordOp,accountBook.getOperation(id));

        TicketEntry t1 = new TicketEntryImpl("00012452","descr",12,2.0,0.5);
        TicketEntry t2 = new TicketEntryImpl("00012453","descr1",13,3.0,0.6);
        TicketEntry t3 = new TicketEntryImpl("00012454","descr2",14,4.0,0.7);
        LinkedList<TicketEntry> tList = new LinkedList<TicketEntry>();
        tList.add(t1);
        tList.add(t2);
        tList.add(t3);
        SaleTransactionImplementation saleOp = new SaleTransactionImplementation();
        saleOp.setEntries(tList);
        id = saleOp.getBalanceId();
        assertTrue(accountBook.addOperation(saleOp));
        assertSame(saleOp,accountBook.getOperation(id));

        ReturnTransaction retOp = new ReturnTransaction(saleOp.getBalanceId());
        retOp.setReturnEntries(tList);
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
