package Test;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.PurchaseLedgerTransactionType;
import Software.Enums.SaleChannels;
import Software.Importable;
import Software.PurchaseLedger.DbManagerPurchaseLedger;
import Software.PurchaseLedger.PurchaseLedgerTransactionLine;
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
    Importable importableTest = new PurchaseLedgerTransactionLine("31/10/2015", "supplierID", "externalAccountingReference",
            "description", "productKey", 700, 1.50, 0.3, "vatCode", Countries.ITALY , Countries.GB,
            SaleChannels.AMAZON, 1, 1,
            java.util.UUID.fromString(String.valueOf(new UUID())),
            Currencies.GBP, PurchaseLedgerTransactionType.INVOICE);

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
        dbManagerPurchaseLedger.retrieveTransaction(1);
    }

    @Test
    public void testReverseTransaction() throws Exception
    {
        mockDatabase.reverseTransaction(4);
    }

    @Test
    public void testRetrieveTransactionLine() throws Exception
    {
        PurchaseLedgerTransactionLine testLine =  dbManagerPurchaseLedger.retrieveTransactionLine(1,1);
        assert (testLine.getCurrency().equals(Currencies.GBP));
        assert (testLine.getQuantity()==96);
        assert (testLine.getPrice()==212.16);
        assert (testLine.getLineUuid().toString().equals("c8afe020-8e52-11e5-80d9-1211ca9464ab"));
        assert (testLine.getInvoiceUuid().toString().equals("c8afb910-8e52-11e5-80d9-1211ca9464ab"));
        assert (testLine.getPurchaseLedgerTransactionType().equals(PurchaseLedgerTransactionType.ACCRUAL));
        assert (testLine.getShippedFromCountry().equals(Countries.ITALY));
        assert (testLine.getShippedToCountry().equals(Countries.GB));
        assert (testLine.getShippedToChannel().equals(SaleChannels.AMAZON));
        assert (testLine.getTransactionDate().equals("2015-10-28"));
        assert (testLine.getSupplierID().equals("AllBrands"));
        assert (testLine.getDescription().equals("Accrual as invoice has not been received yet"));
        assert (testLine.getProductKey().equals("Proraso Crema Anti - Irritazione"));

    }
}