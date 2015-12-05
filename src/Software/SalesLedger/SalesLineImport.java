package Software.SalesLedger;

import Software.Enums.*;
import Software.Utilities.Importer;
import Software.Inventory.DbManagerInventoryItems;
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
    DbManagerInventoryItems dbManagerInventoryItems = new DbManagerInventoryItems();

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

            if(!currentReference.equals(referenceChecker))
            {
                transactionGroupId = dbManagerSalesLedger.internalTransactionNumberGenerator();
                transactionLineId  = 1;
            }

            referenceChecker = currentReference;
        }

    }

    private void importSale(CSVRecord csvRecord)
    {
        // price and additional cos manipulation for sales involving multiple items of same product;
        int productQuantity = Integer.parseInt(csvRecord.get(PRODUCT_QUANTITY));
        Double itemPrice = Double.parseDouble(csvRecord.get(TRANSACTION_PRICE)) / productQuantity;
        Double itemAdditionalCos = Double.parseDouble(csvRecord.get(TRANSACTION_ASSOCIATED_COS)) / productQuantity;

        for (int i = 1; i <= productQuantity ; i++)
        {
            String productKey =  csvRecord.get(PRODUCT_KEY);
            String country = csvRecord.get(TRANSACTION_CHANNEL_COUNTRY);
            String channel = csvRecord.get(TRANSACTION_CHANNEL);
            String externalId = csvRecord.get(EXTERNAL_ORDER_ID);
            InventoryItem tempItem = dbManagerInventoryItems.getItemForSale(productKey,country,channel);
            String itemId = tempItem.getPrimaryKey();
            UUID itemUUID = tempItem.getItemUuid();

            salesLedgerLine = new SalesLedgerLine(csvRecord.get(TRANSACTION_DATE),externalId,
                    productKey,csvRecord.get(TRANSACTION_CITY),csvRecord.get(TRANSACTION_POSTCODE),
                    Countries.valueOf(csvRecord.get(TRANSACTION_COUNTRY)),
                    1,itemPrice, itemAdditionalCos,
                    Channels.valueOf(channel), Countries.valueOf(country), Currencies.valueOf(csvRecord.get(CURRENCY)),
                    transactionGroupId, transactionLineId,SaleLedgerTransactionType.SALE, itemId, itemUUID,
                    Channels.valueOf(csvRecord.get(MERCHANT_CHANNEL)),
                            Countries.valueOf(csvRecord.get(MERCHANT_CHANNEL_COUNTRY)),
                            Double.parseDouble(csvRecord.get(MERCHANT_CHANNEL_FEES)));

            if(csvRecord.get(TRANSACTION_TYPE).equals("Order Payment"))
            {
                salesLedgerLine.setProperty("transactionLineStatus", SaleLedgerTransactionType.SALE);
                dbManagerInventoryItems.updateInventoryItemStatus(itemId, itemUUID, InventoryItemStatus.SOLD, country, channel);
            }
            else if(csvRecord.get(TRANSACTION_TYPE).equals("Refund"))
            {
                SalesLedgerLine tempSalesLedgerLine =  dbManagerSalesLedger.getItemForRefund(externalId, productKey);
                dbManagerSalesLedger.flagForRefund(
                        String.valueOf(tempSalesLedgerLine.getProperty("transactionLineUUID")));

                salesLedgerLine.setProperty("transactionLineStatus", SaleLedgerTransactionType.REFUND);
            }

            dbManagerSalesLedger.persistTarget(salesLedgerLine);

            transactionLineId++;
        }
    }
}
