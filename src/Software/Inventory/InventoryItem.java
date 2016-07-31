package Software.Inventory;


import Software.Enums.*;
import Software.PurchaseLedger.PurchaseLedgerLine;
import Software.Utilities.Importable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


/**
 * Created by Michele on 08/11/2015.
 *
 * Class purpose is to create objects that reflect individual items within an inventory
 */
public class InventoryItem extends Importable
{
    private Map<String, Object> properties = new HashMap<String, Object>();

    public InventoryItem(int internalInvoiceReference, int internalInvoiceReferenceLine, int itemNumber,
                         UUID invoiceUuid, UUID invoiceLineUuid, UUID itemUuid, String sellerSku,
                         double itemCost, Countries country, Channels saleChannel,Currencies currency, String itemDate)
    {
        properties.put("internalInvoiceReference" ,internalInvoiceReference);
        properties.put("internalInvoiceReferenceLine", internalInvoiceReferenceLine);
        properties.put("itemNumber", itemNumber);
        properties.put("invoiceUuid", invoiceUuid);
        properties.put("invoiceLineUuid",invoiceLineUuid);
        properties.put("itemUuid", itemUuid);
        properties.put("productKey", sellerSku);
        properties.put("itemCost", itemCost);
        properties.put("country", country);
        properties.put("saleChannel", saleChannel);
        properties.put("itemDate", itemDate);
        properties.put("inventoryItemTransactionTypes", InventoryItemTransactionTypes.PURCHASE);
        properties.put("primaryKey", internalInvoiceReference+"-"+internalInvoiceReferenceLine+"-"+itemNumber);
        properties.put("currency", currency);
        properties.put("itemDate", itemDate);
        properties.put("itemStatus", InventoryItemStatus.AVAILABLE_FOR_SALE);
    }

    public Map<String, Object> getProperties()
    {
        return properties;
    }

    public Object getProperty(String property)
    {
        return properties.get(property);
    }

    public void setProperty(String property, Object object)
    {
        properties.replace(property, object);
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        InventoryItem that = (InventoryItem) o;

        return properties.get("itemUuid").toString().equals(that.getProperty("itemUuid").toString());

    }

    @Override
    public int hashCode()
    {
        return properties.get("itemUuid").toString().hashCode();
    }

}
