package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.SaleTransactionAdapter;
import it.polito.ezshop.data.SaleTransactionImplementation;
import it.polito.ezshop.data.TicketEntry;
import it.polito.ezshop.data.TicketEntryImpl;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.LinkedList;


public class SaleTransactionAdapterTests {

        @Test
        public void testSaleTransactionAdapter() {
            TicketEntry t1 = new TicketEntryImpl("00012452", "descr", 12, 2.0, 0.5);
            TicketEntry t2 = new TicketEntryImpl("00012453", "descr1", 13, 3.0, 0.6);
            TicketEntry t3 = new TicketEntryImpl("00012454", "descr2", 14, 4.0, 0.7);
            LinkedList<TicketEntry> tList = new LinkedList<TicketEntry>();
            tList.add(t1);
            tList.add(t2);
            tList.add(t3);

            SaleTransactionImplementation st = new SaleTransactionImplementation(-1, "prova", 2.0, LocalDate.parse("2021-05-22"), 0.5, "provaStatus", null);

            //constructor
            SaleTransactionAdapter sta = new SaleTransactionAdapter((st));
            //getTicketNumber
            Assert.assertEquals( st.getBalanceId(), (int) sta.getTicketNumber());
            //setTicketNumber
            sta.setTicketNumber(7);
            Assert.assertEquals( 7, (int) sta.getTicketNumber());
            //getEntries()
            Assert.assertEquals( st.getEntries(), sta.getEntries());
            //setEntries()
            TicketEntry t4 = new TicketEntryImpl("00012452", "descr", 12, 2.0, 0.5);
            TicketEntry t5 = new TicketEntryImpl("00012453", "descr1", 13, 3.0, 0.6);
            TicketEntry t6 = new TicketEntryImpl("00012454", "descr2", 14, 4.0, 0.7);
            LinkedList<TicketEntry> tList2 = new LinkedList<TicketEntry>();
            tList.add(t4);
            tList.add(t5);
            tList.add(t6);
            sta.setEntries(tList2);
            Assert.assertEquals( tList2, sta.getEntries());

            //getDiscountRate
            Assert.assertEquals( (Double) st.getDiscountRate(), (Double) sta.getDiscountRate());
            //setDiscountRate
            sta.setDiscountRate(0.3);
            Assert.assertEquals( (Double)0.3, (Double) sta.getDiscountRate());

            //getPrice
            Assert.assertEquals( (Double) st.getPrice(), (Double) sta.getPrice());
            //setDiscountRate
            sta.setPrice(0.3);
            Assert.assertEquals( (Double)0.3, (Double) sta.getPrice());



        }
}
