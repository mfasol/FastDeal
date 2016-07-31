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
public class SalesLineImport extends Importer
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
            if(!currentReference.equals(referenceChecker))
            {
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
                    if (csvRecord.get(TRANSACTION_TYPE).equals("Refund"))
                    {
                        importStandardRefund(csvRecord, i);
                    }
                    else
                    {
                        importAdjustment(csvRecord, i);
                    }
                }
            }
        }
    }

    private void importStandardRefund(CSVRecord csvRecord, int itemCounter)
    {
        try
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
                    transactionGroupId, itemCounter, SaleLedgerTransactionType.REFUND, null, null,
                    Channels.valueOf(csvRecord.get(MERCHANT_CHANNEL)),
                    Countries.valueOf(csvRecord.get(MERCHANT_CHANNEL_COUNTRY)),
                    Double.parseDouble(csvRecord.get(MERCHANT_CHANNEL_FEES)));

            SalesLedgerLine tempSalesLedgerLine = dbManagerSalesLedger.getItemForRefund(csvRecord.get(EXTERNAL_ORDER_ID),
                    csvRecord.get(PRODUCT_KEY));
            dbManagerSalesLedger.flagForRefund(
                    String.valueOf(tempSalesLedgerLine.getProperty("transactionLineUUID")));

            salesLedgerLine.setProperty("itemUUID", tempSalesLedgerLine.getProperty("itemUUID"));
            salesLedgerLine.setProperty("itemId", tempSalesLedgerLine.getProperty("itemId"));

            dbManagerInventory.updateInventoryItemStatus(salesLedgerLine.getProperty("itemId").toString(),
                    UUID.fromString(salesLedgerLine.getProperty("itemUUID").toString()),
                    InventoryItemStatus.REFUNDED,
                    salesLedgerLine.getProperty("countryLogisticChannel").toString(),
                    salesLedgerLine.getProperty("logisticChannel").toString());

            dbManagerSalesLedger.persistTarget(salesLedgerLine);
        }
        catch (NullPointerException npExc)
        {
            importAdjustment(csvRecord, itemCounter);
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
                salesLedgerLine.getProperty("logisticChannel").toString());

        dbManagerSalesLedger.persistTarget(salesLedgerLine);
    }

    private void importSalesLedgerTransaction(CSVRecord csvRecord)
    {
        // price and additional cos manipulation for sales involving multiple items of same product;
        int productQuantity = Integer.parseInt(csvRecord.get(PRODUCT_QUANTITY));
        Double itemPrice = Double.parseDouble(csvRecord.get(TRANSACTION_PRICE)) / productQuantity;
        Double itemAdditionalCos = Double.parseDouble(csvRecord.get(TRANSACTION_ASSOCIATED_COS)) / productQuantity;

        // generate a new sale for each unit
        for (int i = 1; i <= productQuantity ; i++)
        {
            boolean importValidatedFlag = false;

            String productKey =  csvRecord.get(PRODUCT_KEY);
            String country = csvRecord.get(TRANSACTION_CHANNEL_COUNTRY);
            String channel = csvRecord.get(TRANSACTION_CHANNEL);
            String externalId = csvRecord.get(EXTERNAL_ORDER_ID);

            // get saleable item from inventory
            InventoryItem tempItem = dbManagerInventory.getItemForSale(productKey, country, channel);

            // if saleable item exists create new salesLedgerLine object and get some data from the saleable item
            if (tempItem!= null)
            {
                String itemId = tempItem.getProperty("primaryKey").toString();
                UUID itemUUID = UUID.fromString(tempItem.getProperty("itemUuid").toString());

                salesLedgerLine = new SalesLedgerLine(csvRecord.get(TRANSACTION_DATE), externalId,
                        productKey, csvRecord.get(TRANSACTION_CITY), csvRecord.get(TRANSACTION_POSTCODE),
                        Countries.valueOf(csvRecord.get(TRANSACTION_COUNTRY)),
                        1, itemPrice, itemAdditionalCos,
                        Channels.valueOf(channel), Countries.valueOf(country), Currencies.valueOf(csvRecord.get(CURRENCY)),
                        transactionGroupId, transactionLineId, SaleLedgerTransactionType.SALE, itemId, itemUUID,
                        Channels.valueOf(csvRecord.get(MERCHANT_CHANNEL)),
                        Countries.valueOf(csvRecord.get(MERCHANT_CHANNEL_COUNTRY)),
                        Double.parseDouble(csvRecord.get(MERCHANT_CHANNEL_FEES)));


                // if the imported record is an "Order Payment" and the saleable item exists, flag the
                // salesLedgerLine as of type SALE and flag the relevant stock item as sold
                if (csvRecord.get(TRANSACTION_TYPE).equals("Order Payment") & (itemId != null))
                {
                    salesLedgerLine.setProperty("transactionLineStatus", SaleLedgerTransactionType.SALE);
                    dbManagerInventory.updateInventoryItemStatus(itemId, itemUUID, InventoryItemStatus.SOLD, country,
                            channel);

                    importValidatedFlag = true;
                }

                // if transaction is a refund
                else if (csvRecord.get(TRANSACTION_TYPE).equals("Refund"))
                {
                    try
                    {
                        SalesLedgerLine tempSalesLedgerLine = dbManagerSalesLedger.getItemForRefund(externalId, productKey);
                        dbManagerSalesLedger.flagForRefund(
                                String.valueOf(tempSalesLedgerLine.getProperty("transactionLineUUID")));

                        salesLedgerLine.setProperty("transactionLineStatus", SaleLedgerTransactionType.REFUND);
                        salesLedgerLine.setProperty("itemUUID", tempSalesLedgerLine.getProperty("itemUUID"));
                        salesLedgerLine.setProperty("itemId", tempSalesLedgerLine.getProperty("itemId"));

                        importValidatedFlag = true;
                    }
                    catch (NullPointerException npExc)
                    {
                        System.out.println("NO TRANSACTION TO BE REFUNDED!!" + csvRecord.toString());
                    }

                } else
                {
                    System.out.println("ERROR " + csvRecord.toString());
                }

                if(importValidatedFlag)
                {
                    dbManagerSalesLedger.persistTarget(salesLedgerLine);
                    transactionLineId++;
                }
            }
            else
            {
                System.out.println("NO ITEM AVAILABLE FOR SALE " + csvRecord.toString());
            }
        }
    }
}
