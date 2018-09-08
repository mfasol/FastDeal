package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum InventoryItemStatus
{
    AVAILABLE_FOR_SALE,
    SOLD,
    TRANSFERRED_TO,
    TRANSFERRED_FROM,
    DISPOSED,
    REFUNDED,
    ADJUSTMENT_WRITE_OFF, TRANSFER;

    public static boolean contains(String inventoryItemStatus)
    {
        for(InventoryItemStatus aStatus : InventoryItemStatus.values())
        {
            if(aStatus.name().equalsIgnoreCase(inventoryItemStatus)) {return true;}
        }
        return false;
    }
}
