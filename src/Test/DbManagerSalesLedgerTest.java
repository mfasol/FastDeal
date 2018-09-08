package Test;

import Software.Enums.*;
import Software.Inventory.DbManagerInventory;
import Software.Inventory.InventoryItem;
import Software.SalesLedger.DbManagerSalesLedger;
import Software.SalesLedger.SalesLedgerLine;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Michele on 29/11/2015.
 */
public class DbManagerSalesLedgerTest
{
    DbManagerSalesLedger dbManagerSalesLedger = new DbManagerSalesLedger();
    DbManagerSalesLedger mockDatabase = mock(DbManagerSalesLedger.class);
    //DbManagerInventory dbInventory =  new DbManagerInventory();




    @Test
    public void testPersistTarget() throws Exception
    {

    }

    @Test
    public void testInternalTransactionNumberGenerator() throws Exception
    {
        System.out.println(dbManagerSalesLedger.internalTransactionNumberGenerator());
    }

    @Test
    public void testRetrieveSaleLedgerTransaction() throws Exception
    {
        System.out.println(dbManagerSalesLedger.getTransactionByExternalId("205-8088969-6662712",
                "Proraso Sapone Tubo Rinfrescante"));
    }

    @Test
    public void testUpdateStatus() throws Exception
    {
        mockDatabase.updateStatus("REFUND","1dc96860-97bd-11e5-ba4e-be53ae3a8f5c");
    }

    @Test
    public void testGetTransactionByUUID() throws Exception
    {
        try
        {
            System.out.println(dbManagerSalesLedger.getTransactionByUUID(
                    "1dc96860-97bd-11e5-ba4e-be53ae3a8f5c").getProperties());
        }
        catch (NullPointerException npe){}
    }

}