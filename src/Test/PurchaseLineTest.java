package Test;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.SaleChannels;
import Software.Enums.SaleLedgerTransactionType;
import Software.SalesLedger.SaleLedgerLine;
import org.junit.Test;

/**
 * Created by Michele on 28/11/2015.
 */
public class PurchaseLineTest
{
    SaleLedgerLine saleLedgerLine = new SaleLedgerLine("01/01/2015","Amazon.co.uk","Proraso","London",
            "SE1 7SJ", Countries.GB, 1, 7.55,3.55, SaleChannels.AMAZON, Countries.GB, Currencies.GBP,
            1,1, SaleLedgerTransactionType.SALE,"1-1-1");

    @Test
    public void testGetProperties() throws Exception
    {
        System.out.println(saleLedgerLine.getProperties());
    }

    @Test
    public void testGetProperty() throws Exception
    {
        System.out.println(saleLedgerLine.getProperty("shipToCountry"));
    }


    @Test
    public void testSetProperty() throws Exception
    {
        System.out.println(saleLedgerLine.getProperty("Date"));
        saleLedgerLine.setProperty("Date","01/01/2225");
        System.out.println(saleLedgerLine.getProperty("Date"));
    }
}