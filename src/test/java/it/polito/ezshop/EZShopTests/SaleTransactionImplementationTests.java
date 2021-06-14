package it.polito.ezshop.EZShopTests;

import org.junit.*;
import it.polito.ezshop.data.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;

public class SaleTransactionImplementationTests {
    @Test
    public void testSaleTransactionImpl(){
        BalanceOperationImpl.setBalanceCounter(0);
        TicketEntry t1 = new TicketEntryImpl("00012452","descr",12,2.0,0.5);
        TicketEntry t2 = new TicketEntryImpl("00012453","descr1",13,3.0,0.6);
        TicketEntry t3 = new TicketEntryImpl("00012454","descr2",14,4.0,0.7);
        LinkedList<TicketEntry> tList = new LinkedList<TicketEntry>();
        tList.add(t1);
        tList.add(t2);
        tList.add(t3);

        SaleTransactionImplementation st = new SaleTransactionImplementation(-1, "prova", 2.0, LocalDate.parse("2021-05-22"), 0.5, "provaStatus", null, null);
        SaleTransactionImplementation st1 = new SaleTransactionImplementation();

        st.setBalanceId(5);
        Assert.assertEquals(5, st.getBalanceId());
        st.setDiscountRate(0.6);
        Assert.assertEquals((Double) 0.6, (Double) st.getDiscountRate());
        st.setPrice(3.0);
        Assert.assertEquals((Double) 3.0, (Double) st.getPrice());
        st.setStatus("status1");
        Assert.assertEquals("status1", st.getStatus());
        st.setEntries(tList);
        Assert.assertEquals(tList, st.getEntries());

        Assert.assertEquals("OPEN", st1.getStatus());
        Assert.assertEquals((Double) 0.0, (Double) st1.getPrice());
        Assert.assertEquals((Double) 0.0, (Double) st1.getDiscountRate());
        Assert.assertEquals(2, st1.getBalanceId());
        Assert.assertTrue(st1.getEntries() instanceof ArrayList);
    }
}
