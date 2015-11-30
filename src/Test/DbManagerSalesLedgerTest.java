package Test;

import Software.SalesLedger.DbManagerSalesLedger;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by Michele on 29/11/2015.
 */
public class DbManagerSalesLedgerTest
{
    DbManagerSalesLedger dbManagerSalesLedger = new DbManagerSalesLedger();
    @Test
    public void testPersistTarget() throws Exception
    {

    }

    @Test
    public void testInternalTransactionNumberGenerator() throws Exception
    {
        System.out.println(dbManagerSalesLedger.internalTransactionNumberGenerator());
    }
}