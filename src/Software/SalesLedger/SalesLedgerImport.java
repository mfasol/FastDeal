package Software.SalesLedger;

import Software.Enums.*;
import Software.Utilities.Importer;
import Software.Inventory.DbManagerInventory;
import Software.Inventory.InventoryItem;
import org.apache.commons.csv.CSVRecord;

import java.util.UUID;

/**
 * Created by Michele on 26/11/2015.
 */
public class SalesLedgerImport extends Importer
{
    String referenceChecker = "";

    DbManagerSalesLedger dbManagerSalesLedger = new DbManagerSalesLedger();

    int transactionGroupId = dbManagerSalesLedger.internalTransactionNumberGenerator();
    int transactionLineId = 1;

    SalesLedgerLine salesLedgerLine;
    DbManagerInventory dbManagerInventory = new DbManagerInventory();

    private final int TRANSACTION_DATE = 0;
    private final int EXTERNAL_ORDER_ID = 1;
    private final int PRODUCT_KEY = 2;
    private final int TRANSACTION_CITY = 3;
    private final int TRANSACTION_POSTCODE = 4;
    private final int TRANSACTION_COUNTRY = 5;
    private final int PRODUCT_QUANTITY = 6;
    private final int TRANSACTION_PRICE = 7;
    private final int TRANSACTION_ASSOCIATED_COS = 8;
    private final int TRANSACTION_CHANNEL = 9;
    private final int TRANSACTION_CHANNEL_COUNTRY = 10;
    private final int CURRENCY = 11;
    private final int TRANSACTION_TYPE = 12;
    private final int MERCHANT_CHANNEL = 13;
    private final int MERCHANT_CHANNEL_COUNTRY = 14;
    private final int MERCHANT_CHANNEL_FEES = 15;

    @Override
    protected void completeImportProcess()
    {
        for (CSVRecord csvRecord : super.csvLines)
        {
            System.out.println(csvRecord.toString());
            String currentReference = csvRecord.get(EXTERNAL_ORDER_ID);
            // check if transaction is made of multiple lines (file has to be sorted by transaction ID)
            if(!currentReference.equals(referenceChecker))            {
                transactionGroupId = dbManagerSalesLedger.internalTransactionNumberGenerator();
                transactionLineId  = 1;
            }
            referenceChecker = currentReference;
            //check if transaction is positive (increase in income eg sale)
            if(Double.parseDouble(csvRecord.get(TRANSACTION_PRICE))>0)
            {
                String productKey =  csvRecord.get(PRODUCT_KEY);
                String country = csvRecord.get(TRANSACTION_CHANNEL_COUNTRY);
                String channel = csvRecord.get(TRANSACTION_CHANNEL);
                InventoryItem tempItem = dbManagerInventory.getItemForSale(productKey, country, channel);

                // if saleable item exists create new salesLedgerLine object and get some data from the saleable item
                for (int i = 1; i <= Integer.parseInt(csvRecord.get(PRODUCT_QUANTITY)); i++)
                {
                    if (csvRecord.get(TRANSACTION_TYPE).toLowerCase().equals("order payment") && tempItem != null)
                    {
                        importStandardSale(csvRecord, tempItem, i);
                    }
                    else
                    {
                        importAdjustment(csvRecord, i);
                    }

                    tempItem = dbManagerInventory.getItemForSale(productKey, country, channel);
                }
            }
            // transaction is negative (decrease in income eg refund)
            else
            {
                for (int i = 1; i <= Integer.parseInt(csvRecord.get(PRODUCT_QUANTITY)); i++)
                {
                    {
                        importAdjustment(csvRecord, i);
                    }
                }
            }
        }
    }



    private void importAdjustment(CSVRecord csvRecord, int itemCounter)
    {
        int productQuantity = Integer.parseInt(csvRecord.get(PRODUCT_QUANTITY));
        Double itemPrice = Double.parseDouble(csvRecord.get(TRANSACTION_PRICE)) / productQuantity;
        Double itemAdditionalCos = Double.parseDouble(csvRecord.get(TRANSACTION_ASSOCIATED_COS)) / productQuantity;

        salesLedgerLine = new SalesLedgerLine(csvRecord.get(TRANSACTION_DATE), csvRecord.get(EXTERNAL_ORDER_ID),
                csvRecord.get(PRODUCT_KEY), csvRecord.get(TRANSACTION_CITY), csvRecord.get(TRANSACTION_POSTCODE),
                Countries.valueOf(csvRecord.get(TRANSACTION_COUNTRY)),
                1, itemPrice, itemAdditionalCos,
                Channels.valueOf(csvRecord.get(TRANSACTION_CHANNEL)),
                Countries.valueOf(csvRecord.get(TRANSACTION_CHANNEL_COUNTRY)),
                Currencies.valueOf(csvRecord.get(CURRENCY)),
                transactionGroupId, itemCounter, SaleLedgerTransactionType.ADJUSTMENT, null, null,
                Channels.valueOf(csvRecord.get(MERCHANT_CHANNEL)),
                Countries.valueOf(csvRecord.get(MERCHANT_CHANNEL_COUNTRY)),
                Double.parseDouble(csvRecord.get(MERCHANT_CHANNEL_FEES)));

        salesLedgerLine.setProperty("itemUUID",
                UUID.fromString(salesLedgerLine.getProperty("transactionLineUUID").toString()));

        dbManagerSalesLedger.persistTarget(salesLedgerLine);
    }

    private void importStandardSale(CSVRecord csvRecord, InventoryItem inventoryItem, int itemCounter)
    {
        // price and additional cos manipulation for sales involving multiple items of same product;
        int productQuantity = Integer.parseInt(csvRecord.get(PRODUCT_QUANTITY));
        Double itemPrice = Double.parseDouble(csvRecord.get(TRANSACTION_PRICE)) / productQuantity;
        Double itemAdditionalCos = Double.parseDouble(csvRecord.get(TRANSACTION_ASSOCIATED_COS)) / productQuantity;

        String itemId = inventoryItem.getProperty("primaryKey").toString();
        UUID itemUUID = UUID.fromString(inventoryItem.getProperty("itemUuid").toString());

        salesLedgerLine = new SalesLedgerLine(csvRecord.get(TRANSACTION_DATE), csvRecord.get(EXTERNAL_ORDER_ID),
                csvRecord.get(PRODUCT_KEY), csvRecord.get(TRANSACTION_CITY), csvRecord.get(TRANSACTION_POSTCODE),
                Countries.valueOf(csvRecord.get(TRANSACTION_COUNTRY)),
                1, itemPrice, itemAdditionalCos,
                Channels.valueOf(csvRecord.get(TRANSACTION_CHANNEL)),
                Countries.valueOf(csvRecord.get(TRANSACTION_CHANNEL_COUNTRY)),
                Currencies.valueOf(csvRecord.get(CURRENCY)),
                transactionGroupId, itemCounter, SaleLedgerTransactionType.SALE, itemId, itemUUID,
                Channels.valueOf(csvRecord.get(MERCHANT_CHANNEL)),
                Countries.valueOf(csvRecord.get(MERCHANT_CHANNEL_COUNTRY)),
                Double.parseDouble(csvRecord.get(MERCHANT_CHANNEL_FEES)));

        // Flag inventory item as sold
        dbManagerInventory.updateInventoryItemStatus(itemId, itemUUID, InventoryItemStatus.SOLD,
                salesLedgerLine.getProperty("countryLogisticChannel").toString(),
                salesLedgerLine.getProperty("logisticChannel").toString(), csvRecord.get(TRANSACTION_DATE));

        dbManagerSalesLedger.persistTarget(salesLedgerLine);
    }
}
