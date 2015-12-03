package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum InventoryItemTransactionTypes
{
    PURCHASE,
    RETURN,
    BLANK,
    ;

    public static boolean contains(String inventoryItemTransactionType)
    {
        for(InventoryItemTransactionTypes aType : InventoryItemTransactionTypes.values())
        {
            if(aType.name().equalsIgnoreCase(inventoryItemTransactionType)) {return true;}
        }
        return false;
    }
}
