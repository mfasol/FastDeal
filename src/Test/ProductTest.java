package Test;

import Software.Products.Product;
import org.junit.Test;

/**
 * Created by Michele on 16/10/2015.
 */
public class ProductTest
{

    Product testProduct = new Product("BJK1SSK", "12425151065", "1215151415", "bdydbdbwdwbd","Proraso","13/10/2015" );

    @Test
    public void testToString() throws Exception
    {
        System.out.println(testProduct.toString());
    }

    @Test
    public void testGetAsin() throws Exception
    {
        assert (testProduct.getAsin().equals("BJK1SSK"));
    }

    @Test
    public void testSetAsin() throws Exception
    {
        testProduct.setAsin("NEW-ASIN");
        assert (testProduct.getAsin().equals("NEW-ASIN"));
    }

    @Test
    public void testGetEan() throws Exception
    {
        assert (testProduct.getEan().equals("12425151065"));
    }

    @Test
    public void testSetEan() throws Exception
    {
        testProduct.setAsin("NEW-EAN");
        assert (testProduct.getAsin().equals("NEW-EAN"));
    }

    @Test
    public void testGetIntrastatPurCode() throws Exception
    {

    }

    @Test
    public void testSetIntrastatPurCode() throws Exception
    {

    }

    @Test
    public void testGetIntrastatSalCode() throws Exception
    {

    }

    @Test
    public void testSetIntrastatSalCode() throws Exception
    {

    }

    @Test
    public void testGetStatus() throws Exception
    {

    }

    @Test
    public void testSetStatus() throws Exception
    {

    }

}