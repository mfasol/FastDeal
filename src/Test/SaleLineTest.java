package Test;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.Channels;
import Software.Enums.SaleLedgerTransactionType;
import Software.SalesLedger.SalesLedgerLine;
import org.junit.Test;

import java.util.UUID;

/**
 * Created by Michele on 28/11/2015.
 */
public class SaleLineTest
{
    SalesLedgerLine salesLedgerLine = new SalesLedgerLine("01/01/2015","Amazon.co.uk","Proraso","London",
            "SE1 7SJ", Countries.GB, 1, 7.55,3.55, Channels.AMAZON, Countries.GB, Currencies.GBP,
            1,1, SaleLedgerTransactionType.SALE,"1-1-1", UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"),
            Channels.AMAZON,Countries.GB,0);

    @Test
    public void testGetProperties() throws Exception
    {
        System.out.println(salesLedgerLine.getProperties());
    }

    @Test
    public void testGetProperty() throws Exception
    {
        System.out.println(salesLedgerLine.getProperty("shipToCountry"));
    }


    @Test
    public void testSetProperty() throws Exception
    {
        System.out.println(salesLedgerLine.getProperty("Date"));
        salesLedgerLine.setProperty("Date","01/01/2225");
        System.out.println(salesLedgerLine.getProperty("Date"));
    }
}