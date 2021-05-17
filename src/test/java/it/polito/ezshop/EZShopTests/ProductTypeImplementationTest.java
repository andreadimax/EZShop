package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.ProductTypeImplementation;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductTypeImplementationTest {


    @Test
    public void testProduct(){
        ProductType product = new ProductTypeImplementation(5, "000000000000", "mele", 4.0, null);

        int id = product.getId();
        String barCode = product.getBarCode();
        String description = product.getProductDescription();
        Double price = product.getPricePerUnit();
        String note = product.getNote();

        assertEquals( 5, id);
        assertEquals( "000000000000", barCode);
        assertEquals( "mele", description);
        assertEquals( (Double) 4.0, price);
        assertEquals(null, note);

        product.setId(10);
        assertEquals( 10, (int) product.getId());
        product.setBarCode("800123739455");
        assertEquals( "800123739455", product.getBarCode());
        product.setProductDescription("pere");
        assertEquals( "pere", product.getProductDescription());
        product.setPricePerUnit(5.0);
        assertEquals( (Double) 5.0, product.getPricePerUnit());
        product.setNote("test");
        assertEquals( "test", product.getNote());
        product.setLocation("location5");
        assertEquals( "location5", product.getLocation());

        assertFalse(((ProductTypeImplementation) product).changeQuantity(null));
        assertTrue(((ProductTypeImplementation) product).changeQuantity(10));
        assertTrue(((ProductTypeImplementation) product).changeQuantity(-5));

        /* ---------- WHITE BOX TESTS ---------- */
        /* changeQuantity() */
        //Covering decision amount<0 && availableQty <- amount
        ((ProductTypeImplementation) product).changeQuantity(-5);   //Change quantity to 0
        assertFalse(((ProductTypeImplementation) product).changeQuantity(-5));


    }
}
