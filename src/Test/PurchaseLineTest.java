package Test;

import Software.Enums.Countries;
import Software.Enums.SaleChannels;
import Software.SalesLedger.SaleLine;
import org.junit.Test;

/**
 * Created by Michele on 28/11/2015.
 */
public class PurchaseLineTest
{
    SaleLine saleLine = new SaleLine("01/01/2015","Amazon.co.uk","Proraso","London",
            "SE1 7SJ", Countries.GB, 1,
            7.55,-3.55, SaleChannels.AMAZON,
            Countries.GB);

    @Test
    public void testGetProperties() throws Exception
    {
        System.out.println(saleLine.getProperties());
    }

    @Test
    public void testGetProperty() throws Exception
    {
        System.out.println(saleLine.getProperty("shipToCountry"));
    }


    @Test
    public void testSetProperty() throws Exception
    {
        System.out.println(saleLine.getProperty("Date"));
        saleLine.setProperty("Date","01/01/2225");
        System.out.println(saleLine.getProperty("Date"));
    }
}