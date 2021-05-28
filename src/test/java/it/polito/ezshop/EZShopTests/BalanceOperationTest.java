package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.BalanceOperationImpl;
import org.junit.Test;

import java.time.LocalDate;
import java.time.chrono.ChronoLocalDate;

import static org.junit.Assert.assertEquals;

public class BalanceOperationTest {
    @Test
    public void testCreditDebit(){
        BalanceOperationImpl op1 = new BalanceOperationImpl(10.0);
        BalanceOperationImpl op2 = new BalanceOperationImpl(-10.0);
        BalanceOperationImpl op3 = new BalanceOperationImpl(0);
        BalanceOperationImpl op4 = new BalanceOperationImpl(null);

        assertEquals("Credit",op1.getType());
        assertEquals("Debit",op2.getType());
        assertEquals("Credit",op3.getType());
        //assertEquals("Credit",op4.getType());
        assertEquals(0.0,op4.getMoney(),0.001);

    }

    @Test
    public void testBalanceSetters(){
        BalanceOperationImpl.setBalanceCounter(0);
        BalanceOperationImpl op = new BalanceOperationImpl(10.0);
        op.setBalanceId(1);
        assertEquals(1,op.getBalanceId());

        LocalDate date = LocalDate.parse("2018-12-27");
        op.setDate(date);
        assertEquals(date,op.getDate());

        op.setMoney(5);
        assertEquals(5,op.getMoney(),0.001);
        op.setMoney(-5);
        assertEquals(-5,op.getMoney(),0.001);

        op.setType("TypeExample");
        assertEquals("TypeExample", op.getType());

    }
}
