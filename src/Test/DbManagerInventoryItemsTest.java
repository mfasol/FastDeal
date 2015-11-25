package Test;

import Software.Enums.Countries;
import Software.Enums.SaleChannels;
import Software.Importable;
import Software.Inventory.DbManagerInventoryItems;
import Software.Inventory.InventoryItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.*;

/**
 * Created by Michele on 15/11/2015.
 */
public class DbManagerInventoryItemsTest
{
    Importable importableTest = new InventoryItem(1, 2, 1,
            java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())),
            java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())),
            java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())),
            "seller-sku" ,5.00, Countries.UK, SaleChannels.AMAZON, "31/10/2015");

    DbManagerInventoryItems mockDatabase = mock(DbManagerInventoryItems.class);
    DbManagerInventoryItems dbManagerPurchaseLedger = new DbManagerInventoryItems();

    @Before
    public void setUp() throws Exception
    {

    }

    @After
    public void tearDown() throws Exception

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
    public void testUpdateCos() throws Exception
    {
        mockDatabase.updateTransactionGroupCos(1 , 5.65);

        verify(mockDatabase, times(1)).updateTransactionGroupCos(1 , 5.65);



    }

    @Test
    public void testToInventoryItem() throws Exception
    {


    }
}