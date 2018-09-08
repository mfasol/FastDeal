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
        assert (Double.parseDouble(testLine.getProperty("price").toString())==-184.31);
        assert (testLine.getProperty("transactionLineUUID").toString().equals("5db50130-5673-11e6-821e-2a8d2b544dc2"));
        assert (testLine.getProperty("invoiceUuid").toString().equals("5db48c00-5673-11e6-821e-2a8d2b544dc2"));
        assert (testLine.getProperty("purchaseLedgerTransactionType").equals(PurchaseLedgerTransactionType.INVOICE));
        assert (testLine.getProperty("shippedFromCountry").equals(Countries.IT));
        assert (testLine.getProperty("shippedToCountry").equals(Countries.GB));
        assert (testLine.getProperty("shippedToChannel").equals(Channels.AMAZON));
        assert (testLine.getProperty("date").equals("2015-10-11"));
        assert (testLine.getProperty("supplierID").equals("AllBrands"));
        assert (testLine.getProperty("description").equals("Proraso crema barba vaso anti irrit. 150 ml " +
                "36491 322.00 1.73 20% 557.06"));
        assert (testLine.getProperty("productKey").equals("Proraso Crema Anti - Irritazione"));

    }
}