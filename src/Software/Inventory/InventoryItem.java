package Software.Inventory;


import Software.Enums.*;
import Software.Importable;

import java.rmi.server.UID;
import java.util.UUID;


/**
 * Created by Michele on 08/11/2015.
 *
 * Class purpose is to create objects that reflect individual items within an inventory
 */
public class InventoryItem extends Importable
{
    private int internalInvoiceReference;
    private int internalInvoiceReferenceLine;
    private int itemNumber;
    private String primaryKey;
    private UUID invoiceUuid;
    private UUID invoiceLineUuid;
    private UUID itemUuid;
    private String productKey;
    private double itemCost;
    private Countries country;
    private SaleChannels saleChannel;
    private String itemDate;
    private InventoryItemStatus inventoryItemStatus;
    private Currencies currency;
    private int returnCounter;
    private InventoryItemTransactionTypes inventoryItemTransactionTypes;

    public InventoryItem(int internalInvoiceReference, int internalInvoiceReferenceLine, int itemNumber,
                         UUID invoiceUuid, UUID invoiceLineUuid, UUID itemUuid, String sellerSku,
                         double itemCost, Countries country, SaleChannels saleChannel, String itemDate)
    {
        this.internalInvoiceReference = internalInvoiceReference;
        this.internalInvoiceReferenceLine = internalInvoiceReferenceLine;
        this.itemNumber = itemNumber;
        this.invoiceUuid = invoiceUuid;
        this.invoiceLineUuid = invoiceLineUuid;
        this.itemUuid = itemUuid;
        this.productKey = sellerSku;
        this.itemCost = itemCost;
        this.country = country;
        this.saleChannel = saleChannel;
        this.itemDate = itemDate;
        returnCounter = 0;
        inventoryItemTransactionTypes = InventoryItemTransactionTypes.PURCHASE;
        this.primaryKey = internalInvoiceReference+"-"+internalInvoiceReferenceLine+"-"+itemNumber+"-"+returnCounter;
    }

    public int getInternalInvoiceReference()
    {
        return internalInvoiceReference;
    }

    public void setInternalInvoiceReference(int internalInvoiceReference)
    {
        this.internalInvoiceReference = internalInvoiceReference;
    }

    public int getInternalInvoiceReferenceLine()
    {
        return internalInvoiceReferenceLine;
    }

    public void setInternalInvoiceReferenceLine(int internalInvoiceReferenceLine)
    {
        this.internalInvoiceReferenceLine = internalInvoiceReferenceLine;
    }

    public int getItemNumber()
    {
        return itemNumber;
    }

    public void setItemNumber(int itemNumber)
    {
        this.itemNumber = itemNumber;
    }

    public String getPrimaryKey()
    {
        return primaryKey;
    }

    public void setPrimaryKey(String primaryKey)
    {
        this.primaryKey = primaryKey;
    }

    public UUID getInvoiceUuid()
    {
        return invoiceUuid;
    }

    public void setInvoiceUuid(UUID invoiceUuid)
    {
        this.invoiceUuid = invoiceUuid;
    }

    public UUID getInvoiceLineUuid()
    {
        return invoiceLineUuid;
    }

    public void setInvoiceLineUuid(UUID invoiceLineUuid)
    {
        this.invoiceLineUuid = invoiceLineUuid;
    }

    public UUID getItemUuid()
    {
        return itemUuid;
    }

    public void setItemUuid(UUID itemUuid)
    {
        this.itemUuid = itemUuid;
    }

    public String getProductKey()
    {
        return productKey;
    }

    public void setProductKey(String productKey)
    {
        this.productKey = productKey;
    }

    public double getItemCost()
    {
        return itemCost;
    }

    public void setItemCost(double itemCost)
    {
        this.itemCost = itemCost;
    }

    public SaleChannels getSaleChannel()
    {
        return saleChannel;
    }

    public void setSaleChannel(SaleChannels saleChannel)
    {
        this.saleChannel = saleChannel;
    }

    public Countries getCountry()
    {
        return country;
    }

    public void setCountry(Countries country)
    {
        this.country = country;
    }

    public String getItemDate()
    {
        return itemDate;
    }

    public void setItemDate(String itemDate)
    {
        this.itemDate = itemDate;
    }

    public InventoryItemStatus getInventoryItemStatus()
    {
        return inventoryItemStatus;
    }

    public void setInventoryItemStatus(InventoryItemStatus inventoryItemStatus)
    {
        this.inventoryItemStatus = inventoryItemStatus;
    }

    public Currencies getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currencies currency)
    {
        this.currency = currency;
    }

    public int getReturnCounter()
    {
        return returnCounter;
    }

    public void setReturnCounter(int returnCounter)
    {
        this.returnCounter = returnCounter;
    }

    public InventoryItemTransactionTypes getInventoryItemTransactionTypes()
    {
        return inventoryItemTransactionTypes;
    }

    public void setInventoryItemTransactionTypes(InventoryItemTransactionTypes inventoryItemTransactionTypes)
    {
        this.inventoryItemTransactionTypes = inventoryItemTransactionTypes;
    }

    @Override
    public int hashCode()
    {
        return itemUuid.hashCode();
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;

        return itemUuid.equals(that.itemUuid);
    }

    @Override
    public String toString()
    {
        return "InventoryItem{" +
                "itemCost=" + itemCost +
                ", productKey=" + productKey +
                ", internalInvoiceReference=" + internalInvoiceReference +
                ", internalInvoiceReferenceLine=" + internalInvoiceReferenceLine +
                ", itemNumber=" + itemNumber +
                ", primaryKey=" + primaryKey +
                ", itemUuid=" + itemUuid +
                '}';
    }
}
