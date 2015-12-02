package Software.Utilities;

import DbServer.DbManagerInterface;
import Software.Inventory.DbManagerInventoryItems;

/**
 * Created by Michele on 03/11/2015.
 *
 * The responsibility of the class is to add inventory items to the relevant inventory
 */
public class ProductPurchase implements InventoryModifier
{
    DbManagerInterface targetDbManager;
    AbstractInventory targetInventory;

    public ProductPurchase()
    {
    }

    @Override
    public void modifyInventory(AbstractInventory targetInventory)
    {
        targetDbManager = new DbManagerInventoryItems();
        this.targetInventory = targetInventory;
        this.addToInventory();
    }

    private void addToInventory()
    {

    }
}
