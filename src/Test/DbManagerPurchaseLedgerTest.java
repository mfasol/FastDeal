package Test;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.PurchaseLedgerTransactionType;
import Software.Enums.Channels;
import Software.PurchaseLedger.PurchaseLedgerLine;
import Software.Utilities.Importable;
import Software.PurchaseLedger.DbManagerPurchaseLedger;
import com.eaio.uuid.UUID;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Created by Michele on 15/11/2015.
 */
public class DbManagerPurchaseLedgerTest
{
    Importable importableTest = new PurchaseLedgerLine("31/10/2015", "supplierID", "externalAccountingReference",
            "description", "productKey", 700, 1.50, 0.3, "vatCode", Countries.IT , Countries.GB,
            Channels.AMAZON, 1, 1,
            java.util.UUID.fromString(String.valueOf(new UUID())),
            Currencies.GBP, PurchaseLedgerTransactionType.INVOICE,null,null,null,null);

    DbManagerPurchaseLedger mockDatabase = mock(DbManagerPurchaseLedger.class);
    DbManagerPurchaseLedger dbManagerPurchaseLedger = new DbManagerPurchaseLedger();

    @Before
    public void setUp() throws Exception
    {


    }

    @Test
    public void testPersistTarget() throws Exception
    {
        mockDatabase.persistTarget(importableTest);

        verify(mockDatabase, times(1)).persistTarget(importableTest);

    }

    @Test
    public void testRetrieveTarget() throws Exception
    {

    }

    @Test
    public void testRetrieveInvoiceQuantity() throws Exception
    {
        int result = dbManagerPurchaseLedger.retrieveTransactionGroupQuantity(1);
        System.out.println("The total quantity for invoice 1 is " + result);
        assert (result == 732);
    }

    @Test
    public void testRetrieveInvoiceLineQuantity() throws Exception
    {
        int result = dbManagerPurchaseLedger.retrieveTransactionLineQuantity(1,1);
        System.out.println("The total quantity for invoice 1, line 1 is " + result);
        assert (result == 96);
    }

    @Test
    public void testInternalInvoiceGenerator() throws Exception
    {
        int invoiceNumber = dbManagerPurchaseLedger.internalTransactionNumberGenerator();

        System.out.println("the latest purchase ledger invoice is invoice num. " + (invoiceNumber - 1));

    }

    @Test
    public void testRetrieveInvoiceLineChannel() throws Exception
    {
        assert dbManagerPurchaseLedger.retrieveTransactionLineToChannel(1,1).equals("AMAZON");
    }

    @Test
    public void testRetrieveInvoiceLineShippedToCountry() throws Exception
    {
        assert dbManagerPurchaseLedger.retrieveTransactionLineToCountry(1,1).equals("GB");
    }

    @Test
    public void testRetrieveTransactions() throws Exception
    {
        System.out.println(dbManagerPurchaseLedger.retrieveTransaction(5));
    }

    @Test
    public void testReverseTransaction() throws Exception
    {
        mockDatabase.reverseTransaction(4);
    }

    @Test
    public void testRetrieveTransactionLine() throws Exception
    {
        PurchaseLedgerLine testLine =  dbManagerPurchaseLedger.retrieveTransactionLine(1,1);
        assert (testLine.getProperty("currency").equals(Currencies.GBP));
        assert (Integer.parseInt(testLine.getProperty("quantity").toString())==96);
        assert (Double.parseDouble(testLine.getProperty("price").toString())==212.16);
        assert (testLine.getProperty("transactionLineUUID").toString().equals("c8afe020-8e52-11e5-80d9-1211ca9464ab"));
        assert (testLine.getProperty("invoiceUuid").toString().equals("c8afb910-8e52-11e5-80d9-1211ca9464ab"));
        assert (testLine.getProperty("purchaseLedgerTransactionType").equals(PurchaseLedgerTransactionType.ACCRUAL));
        assert (testLine.getProperty("shippedFromCountry").equals(Countries.IT));
        assert (testLine.getProperty("shippedToCountry").equals(Countries.GB));
        assert (testLine.getProperty("shippedToChannel").equals(Channels.AMAZON));
        assert (testLine.getProperty("date").equals("2015-10-28"));
        assert (testLine.getProperty("supplierID").equals("AllBrands"));
        assert (testLine.getProperty("description").equals("Accrual as invoice has not been received yet"));
        assert (testLine.getProperty("productKey").equals("Proraso Crema Anti - Irritazione"));

    }
}