package it.polito.ezshop.EZShopTests;
import it.polito.ezshop.data.*;
import org.junit.*;
import it.polito.ezshop.*;

import java.time.LocalDate;
import java.util.ArrayList;

public class OrderImplementationTests {
    @Test
    public void testOrderImpl(){
        BalanceOperationImpl.setBalanceCounter(0);
        OrderImpl order = new OrderImpl("0110345", 2, 3.0);
        OrderImpl order1 = new OrderImpl(5, "descr", 2.0, LocalDate.parse("2021-05-22"), "0110346", 6,3.0,"PAYED");

        order1.setBalanceId(6);
        Assert.assertEquals(6, order1.getBalanceId());
        order1.setProductCode("0110346");
        Assert.assertEquals("0110346", order1.getProductCode());
        order1.setPricePerUnit(4.0);
        Assert.assertEquals((Double) 4.0, (Double) order1.getPricePerUnit());
        order1.setQuantity(3);
        Assert.assertEquals(3, order1.getQuantity());
        order1.setStatus("ISSUED");
        Assert.assertEquals("ISSUED", order1.getStatus());
        order1.setOrderId(2);
        Assert.assertEquals(2, (int) order1.getOrderId());

        Assert.assertEquals("ISSUED", order.getStatus());
        Assert.assertEquals((Double) 3.0, (Double) order.getPricePerUnit());
        Assert.assertEquals(2,  order.getQuantity());
        Assert.assertEquals("0110345", order.getProductCode());
        Assert.assertEquals(1, order.getBalanceId());
        Assert.assertEquals(1, (int) order.getOrderId());
    }
}
