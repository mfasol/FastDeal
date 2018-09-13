package Test;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.InventoryItemStatus;
import Software.Enums.Channels;
import Software.Utilities.Importable;
import Software.Inventory.DbManagerInventory;
import Software.Inventory.InventoryItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

import static org.mockito.Mockito.*;

/**
 * Created by Michele on 15/11/2015.
 */
public class DbManagerInventoryTest
{
    Importable importableTest = new InventoryItem(1, 2, 1,
            java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())),
            java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())),
            java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())),
            null ,5.00, Countries.GB, Channels.AMAZON, Currencies.EUR,"31/10/2015");

    DbManagerInventory mockDatabase = mock(DbManagerInventory.class);
    DbManagerInventory dbManagerInventory = new DbManagerInventory();

    @Test
    public void testPersistTarget() throws Exception
    {
        System.out.println(importableTest.toString());
        mockDatabase.persistTarget(importableTest);

        verify(mockDatabase, times(1)).persistTarget(importableTest);
    }

    @Before
    public void setUp() throws Exception
    {

    }

    @After
    public void tearDown() throws Exception

    {

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

    @Test
    public void testUpdateInventoryItemCos() throws Exception
    {
        mockDatabase.updateInventoryItemCos("1-1-1", UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"),
                5.5, "GB", "AMAZON");

        verify(mockDatabase, times(1)).updateInventoryItemCos("1-1-1",
                UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"), 5.5, "GB", "AMAZON");
    }

    @Test
    public void testGetItemForSale() throws Exception
    {
        try
        {
            InventoryItem inventoryItem = dbManagerInventory.getItemForSale(
                    "Proraso Sapone Tubo Rinfrescante", "GB", "AMAZON");

            System.out.println(inventoryItem.getProperties().toString());
        }
        catch (NullPointerException npe)
        {

        }
    }

    @Test
    public void testUpdateInventoryItemStatus() throws Exception
    {
        mockDatabase.updateInventoryItemStatus("1-1-1", UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"),
                InventoryItemStatus.SOLD, "GB", "AMAZON", "01/01/2017");

        verify(mockDatabase, times(1)).updateInventoryItemStatus("1-1-1",
                UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"), InventoryItemStatus.SOLD, "GB",
                "AMAZON", "01/01/2017");

    }
}