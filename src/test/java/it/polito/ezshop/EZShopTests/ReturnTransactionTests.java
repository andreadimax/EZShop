package it.polito.ezshop.EZShopTests;
import it.polito.ezshop.data.*;

import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.LinkedList;

public class ReturnTransactionTests {
    @Test
    public void testReturnTransaction(){
        BalanceOperationImpl.setBalanceCounter(0);
        TicketEntry t1 = new TicketEntryImpl("00012452","descr",12,2.0,0.5);
        TicketEntry t2 = new TicketEntryImpl("00012453","descr1",13,3.0,0.6);
        TicketEntry t3 = new TicketEntryImpl("00012454","descr2",14,4.0,0.7);
        LinkedList<TicketEntry> tList = new LinkedList<TicketEntry>();
        tList.add(t1);
        tList.add(t2);
        tList.add(t3);

        ReturnTransaction rt = new ReturnTransaction(6, tList);
        ReturnTransaction rt2 = new ReturnTransaction(5, "descr", 2.0, LocalDate.parse("2021-05-22"), 1, "PAYED",tList, 0.0 );
        ReturnTransaction rt3 = new ReturnTransaction(7);

        rt2.setStatus("ISSUED");
        Assert.assertEquals("ISSUED", rt2.getStatus());
        Assert.assertEquals(tList, rt2.getReturnEntries());
        Assert.assertEquals(1, (int) rt2.getSaleId());

        rt3.setReturnEntries(tList);
        Assert.assertEquals(tList, rt3.getReturnEntries());

        rt.setSaleDiscount(0.0);
        Assert.assertEquals(0.0,rt.getSaleDiscount(),0.01);



    }
}
