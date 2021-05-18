package it.polito.ezshop.EZShopTests;

import it.polito.ezshop.data.ProductType;
import it.polito.ezshop.data.ProductTypeImplementation;
import org.junit.Test;

import static org.junit.Assert.*;

public class ProductTypeImplementationTest {

    @Test
    public void testProduct2(){

        assertThrows(NullPointerException.class, ()-> {new ProductTypeImplementation(null);});

        ProductType p = new ProductTypeImplementation(8, "000000000002", "banane", 1.0, "note1", 9, "djas-12-djs");
        ProductType product = new ProductTypeImplementation(p);
        int id = product.getId();
        String barCode = product.getBarCode();
        String description = product.getProductDescription();
        Double price = product.getPricePerUnit();
        String note = product.getNote();
        int qty = product.getQuantity();
        String location = product.getLocation();

        assertEquals( 8, id);
        assertEquals( "000000000002", barCode);
        assertEquals( "banane", description);
        assertEquals( (Double) 1.0, price);
        assertEquals("note1", note);
        assertEquals( 9, qty);
        assertEquals( "djas-12-djs", location);
    }
    @Test
    public void testProduct1(){
        ProductType product = new ProductTypeImplementation(10, "000000000001", "pere", 2.0, "note", 8, "afjkdsladfjls");

        int id = product.getId();
        String barCode = product.getBarCode();
        String description = product.getProductDescription();
        Double price = product.getPricePerUnit();
        String note = product.getNote();
        int qty = product.getQuantity();
        String location = product.getLocation();

        assertEquals( 10, id);
        assertEquals( "000000000001", barCode);
        assertEquals( "pere", description);
        assertEquals( (Double) 2.0, price);
        assertEquals("note", note);
        assertEquals( 8, qty);
        assertEquals( "afjkdsladfjls", location);
    }

    @Test
    public void testProduct(){
        ProductType product = new ProductTypeImplementation(5, "000000000000", "mele", 4.0, null);

        int id = product.getId();
        String barCode = product.getBarCode();
        String description = product.getProductDescription();
        Double price = product.getPricePerUnit();
        String note = product.getNote();
        int qty = product.getQuantity();
        String location = product.getLocation();

        assertEquals( 5, id);
        assertEquals( "000000000000", barCode);
        assertEquals( "mele", description);
        assertEquals( (Double) 4.0, price);
        assertNull( note);
        assertEquals( 0, qty);
        assertNull( location);

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

        product.setQuantity(3);
        assertEquals(3, (int) product.getQuantity());

        assertFalse(((ProductTypeImplementation) product).changeQuantity(null));
        assertTrue(((ProductTypeImplementation) product).changeQuantity(7));
        assertTrue(((ProductTypeImplementation) product).changeQuantity(-5));

        /* ---------- WHITE BOX TESTS ---------- */
        /* changeQuantity() */
        //Covering decision amount<0 && availableQty <- amount
        ((ProductTypeImplementation) product).changeQuantity(-5);   //Change quantity to 0
        assertFalse(((ProductTypeImplementation) product).changeQuantity(-5));


    }
}
