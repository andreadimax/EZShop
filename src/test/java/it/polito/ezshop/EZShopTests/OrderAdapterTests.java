package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.BalanceOperationImpl;
import it.polito.ezshop.data.OrderAdapter;
import it.polito.ezshop.data.OrderImpl;
import org.junit.Assert;
import org.junit.Test;

import java.time.LocalDate;

public class OrderAdapterTests {

    @Test
    public void testOrderAdapter() {
        BalanceOperationImpl.setBalanceCounter(0);
        OrderImpl order = new OrderImpl("0110345", 2, 3.0);

        order.setBalanceId(6);
        order.setProductCode("0110346");
        order.setPricePerUnit(4.0);
        order.setQuantity(3);
        order.setStatus("ISSUED");
        order.setOrderId(2);

        // constructor
        OrderAdapter oa = new OrderAdapter(order);
        //getBalanceId
        Assert.assertEquals( order.getBalanceId(), (int) oa.getBalanceId());
        //setBalanceId
        oa.setBalanceId(7);
        Assert.assertEquals( 7, (int) oa.getBalanceId());
        //getProductCode()
        Assert.assertEquals( order.getProductCode(), oa.getProductCode());
        //setProductCode()
        oa.setProductCode("7");
        Assert.assertEquals( "7", oa.getProductCode());

        //getPricePerUnit
        Assert.assertEquals( (Double) order.getPricePerUnit(), (Double) oa.getPricePerUnit());
        //setPricePerUnit
        oa.setPricePerUnit(7.9);
        Assert.assertEquals( (Double)7.9, (Double)oa.getPricePerUnit());

        //getQuantity
        Assert.assertEquals( (int) order.getQuantity(), (int) oa.getQuantity());
        //setPricePerUnit
        oa.setQuantity(9);
        Assert.assertEquals(9, oa.getQuantity());

        //getStatus
        Assert.assertEquals( order.getStatus(), oa.getStatus());
        //setPricePerUnit
        oa.setStatus("PAYED");
        Assert.assertEquals("PAYED", oa.getStatus());

        //getOrderId
        Assert.assertEquals( order.getOrderId(),  oa.getOrderId());
        //setPricePerUnit
        oa.setOrderId(9);
        Assert.assertEquals(9, (int) oa.getOrderId());
    }
}
