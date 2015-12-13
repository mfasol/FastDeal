package Test;

import Software.Products.DbManagerProducts;
import Software.Products.Product;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Michele on 15/11/2015.
 */
public class DbManagerProductsTest //TODO
{
    Product testProduct = new Product("B0082BYLS6", "8004395001019", "Proraso Crema Prebarba Rinfrescante 100", "Proraso Pre and Post Shave Cream (100 ml)","Proraso","13/10/2015" );
    DbManagerProducts  dbManagerProducts = new DbManagerProducts();

    @Before
    public void setUp() throws Exception
    {

    }

    @After
    public void tearDown() throws Exception
    {

    }

    @Test
    public void testPersistTarget() throws Exception
    {
        dbManagerProducts.persistTarget(testProduct);
    }

    @Test
    public void testRetrieveTarget() throws Exception
    {

    }

    @Test
    public void testDeactivateProduct() throws Exception
    {

    }

    @Test
    public void testReactivateProduct() throws Exception
    {

    }

    @Test
    public void testAmendProductStatus() throws Exception
    {

    }
}