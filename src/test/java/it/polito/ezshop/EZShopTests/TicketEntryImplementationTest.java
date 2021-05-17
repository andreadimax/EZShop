package it.polito.ezshop.EZShopTests;

import org.junit.Test;
import it.polito.ezshop.data.*;

import java.time.LocalDate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class TicketEntryImplementationTest {
    @Test
    public void testTicket(){
        TicketEntry t = new TicketEntryImpl("abcdef", "description", 5, 5.0, 0.2);

        Double price = (Double) t.getPricePerUnit();
        String barCode = t.getBarCode();
        String descr = t.getProductDescription();
        int amount = t.getAmount();
        Double discount = (Double) t.getDiscountRate();

        assertEquals( 5, amount);
        assertEquals( "abcdef", barCode);
        assertEquals( (Double) 5.0, price);
        assertEquals( "description", descr);
        assertEquals( (Double) 0.2, discount);

        t.setPricePerUnit(10.0);
        assertEquals( (Double) 10.0,  (Double) t.getPricePerUnit());
        t.setBarCode("00012452");
        assertEquals( "00012452", t.getBarCode());
        t.setProductDescription("pere");
        assertEquals( "pere", t.getProductDescription());
        t.setAmount(-3);
        assertEquals( -3, (int) t.getAmount());
        t.setDiscountRate(0.5);
        assertEquals( (Double) 0.5,  (Double) t.getDiscountRate());

    }
}
