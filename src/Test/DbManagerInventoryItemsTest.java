package Test;

import Software.Enums.Countries;
import Software.Enums.InventoryItemStatus;
import Software.Enums.Channels;
import Software.Utilities.Importable;
import Software.Inventory.DbManagerInventoryItems;
import Software.Inventory.InventoryItem;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.UUID;

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
            "seller-sku" ,5.00, Countries.GB, Channels.AMAZON, "31/10/2015");

    DbManagerInventoryItems mockDatabase = mock(DbManagerInventoryItems.class);
    DbManagerInventoryItems dbManagerInventoryItems = new DbManagerInventoryItems();

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
        InventoryItem inventoryItem = dbManagerInventoryItems.getItemForSale(
                "Proraso Sapone Tubo Rinfrescante","GB","AMAZON");

        System.out.println(inventoryItem.toString());
    }

    @Test
    public void testUpdateInventoryItemStatus() throws Exception
    {
        mockDatabase.updateInventoryItemStatus("1-1-1", UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"),
                InventoryItemStatus.SOLD, "GB", "AMAZON");

        verify(mockDatabase, times(1)).updateInventoryItemStatus("1-1-1",
                UUID.fromString("c8afe021-8e52-11e5-80d9-1211ca9464ab"), InventoryItemStatus.SOLD, "GB", "AMAZON");

    }
}