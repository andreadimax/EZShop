package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.ProductRfid;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class ProductRfidTest {

    @Test
    public void testProductRfid(){
        ProductRfid pr = new ProductRfid("3874", 4834);

        assertEquals((Integer)4834, pr.productId);
        assertEquals("3874", pr.RFID);
    }
}
