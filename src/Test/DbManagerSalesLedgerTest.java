package Test;

import Software.Enums.*;
import Software.Inventory.DbManagerInventoryItems;
import Software.Inventory.InventoryItem;
import Software.SalesLedger.DbManagerSalesLedger;
import Software.SalesLedger.SalesLedgerLine;
import com.eaio.uuid.UUID;
import org.junit.Test;

import static org.mockito.Mockito.mock;

/**
 * Created by Michele on 29/11/2015.
 */
public class DbManagerSalesLedgerTest
{
    DbManagerSalesLedger dbManagerSalesLedger = new DbManagerSalesLedger();
    DbManagerSalesLedger mockDatabase = mock(DbManagerSalesLedger.class);
    DbManagerInventoryItems dbInventory =  new DbManagerInventoryItems();




    @Test
    public void testPersistTarget() throws Exception
    {
        for (int i = 0; i <= 20 ; i++)
        {
            try
            {
                dbInventory = new DbManagerInventoryItems();
                InventoryItem tempItem = dbInventory.getItemForSale("Proraso Sapone Tubo Rinfrescante", "GB", "AMAZON");
                System.out.println(tempItem.toString());
                String itemId = tempItem.getPrimaryKey();
                java.util.UUID itemUUID = tempItem.getItemUuid();

                SalesLedgerLine salesLedgerLine = new SalesLedgerLine("01/01/2016", "TEST SALE", "Proraso Sapone Tubo Rinfrescante",
                        "Pagani", "SUCA", Countries.GB, 1, 100, 10, Channels.AMAZON, Countries.GB, Currencies.GBP, 100, 0,
                        SaleLedgerTransactionType.SALE, dbInventory.getItemForSale("Proraso Sapone Tubo Rinfrescante",
                        "GB", "AMAZON").getPrimaryKey(),
                        itemUUID, Channels.AMAZON, Countries.GB,0.5);

                salesLedgerLine.setProperty("transactionLineStatus", SaleLedgerTransactionType.SALE);
                dbInventory.updateInventoryItemStatus(itemId, itemUUID, InventoryItemStatus.SOLD, "GB", "AMAZON");

                dbManagerSalesLedger.persistTarget(salesLedgerLine);
            }
            catch (NullPointerException e)
            {
                System.out.println("item does not exist");
            }

        }

    }

    @Test
    public void testInternalTransactionNumberGenerator() throws Exception
    {
        System.out.println(dbManagerSalesLedger.internalTransactionNumberGenerator());
    }

    @Test
    public void testRetrieveSaleLedgerTransaction() throws Exception
    {
        System.out.println(dbManagerSalesLedger.getTransactionByExternalId("205-8088969-6662712","Proraso Sapone Tubo Rinfrescante"));
    }

    @Test
    public void testUpdateStatus() throws Exception
    {
        mockDatabase.updateStatus("REFUND","1dc96860-97bd-11e5-ba4e-be53ae3a8f5c");
    }

    @Test
    public void testGetTransactionByUUID() throws Exception
    {
        System.out.println(dbManagerSalesLedger.getTransactionByUUID("1dc96860-97bd-11e5-ba4e-be53ae3a8f5c").getProperties());
    }

    @Test
    public void testGetItemForRefund() throws Exception
    {
        System.out.println(dbManagerSalesLedger.getItemForRefund("205-8088969-6662712","Proraso Sapone Tubo Rinfrescante").getProperties());
    }
}