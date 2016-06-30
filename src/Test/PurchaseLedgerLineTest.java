package Test;

import Software.Enums.Channels;
import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.PurchaseLedgerTransactionType;
import Software.PurchaseLedger.PurchaseLedgerLine;
import org.junit.Test;

import java.util.Map;

/**
 * Created by Michele on 31/05/2016.
 */
public class PurchaseLedgerLineTest
{
    PurchaseLedgerLine importableTest = new PurchaseLedgerLine("31/10/2015", "supplierID", "externalAccountingReference",
            "description", "productKey", 700, 1.50, 0.3, "vatCode", Countries.IT , Countries.GB,
            Channels.AMAZON, 1, 1,
            java.util.UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"),
            Currencies.GBP, PurchaseLedgerTransactionType.INVOICE,null,null,null,null);
    @Test
    public void getProperties() throws Exception
    {
        Map<java.lang.String, java.lang.Object> result = importableTest.getProperties();
        System.out.println(result.toString());

    }

    @Test
    public void getProperty() throws Exception
    {
        System.out.println(importableTest.getProperty("cosRelevant"));
        assert importableTest.getProperty("supplierID") == ("supplierID");
    }

    @Test
    public void setProperty() throws Exception
    {
        importableTest.setProperty("supplierID", "supplierIDD");
        assert importableTest.getProperty("supplierID") == ("supplierIDD");

    }

}