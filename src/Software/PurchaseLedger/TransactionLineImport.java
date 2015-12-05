package Software.PurchaseLedger;

import DbServer.DbManagerInterface;
import Software.Enums.*;
import Software.Utilities.Importer;
import Software.Inventory.DbManagerInventoryItems;
import Software.Inventory.InventoryItem;
import com.eaio.uuid.UUID;
import org.apache.commons.csv.CSVRecord;




/**
 * Created by Michele on 08/11/2015.
 *
 *
 */
public class TransactionLineImport extends Importer
{
    DbManagerInterface dbManagerPurchaseLedger = new DbManagerPurchaseLedger();
    DbManagerInterface dbManagerInventoryItems;

    PurchaseLedgerTransactionLine purchaseLedgerTransactionLine;
    InventoryItem inventoryItem;

    String referenceChecker = "";


    int invoiceNumber = new DbManagerPurchaseLedger().internalTransactionNumberGenerator();
    int lineCounter = 1;
    java.util.UUID invoiceUuid =  java.util.UUID.fromString(String.valueOf(new UUID()));

    private final int INVOICE_DATE = 0;
    private final int SUPPLIER_ID = 1;
    private final int EXTERNAL_INVOICE_REFERENCE = 2;
    private final int DESCRIPTION = 3;
    private final int PRODUCT_KEY = 4;
    private final int QUANTITY = 5;
    private final int PRICE = 6;
    private final int VAT = 7;
    private final int VAT_CODE = 8;
    private final int INVENTORY_RELEVANT = 9;
    private final int SHIPPED_FROM_COUNTRY = 10;
    private final int SHIPPED_TO_COUNTRY = 11;
    private final int SHIPPED_TO_CHANNEL = 12;
    private final int CURRENCY = 13;
    private final int COS_RELEVANT = 14;
    private final int ASSOCIATED_TRANSACTION_GROUP_KEY = 15;
    private final int ASSOCIATED_TRANSACTION_LINE_KEY = 16;
    private final int TRANSACTION_TYPE = 17;

    @Override
    public void completeImportProcess()
    {

        for (CSVRecord csvRecord : super.csvLines)
        {
            System.out.println(csvRecord.toString());
            // check that if current line is different from previous line
            String currentReference = csvRecord.get(INVOICE_DATE) +
                    csvRecord.get(SUPPLIER_ID)+csvRecord.get(EXTERNAL_INVOICE_REFERENCE);

            if(!referenceChecker.equals(currentReference))
            {
                // if different, a new invoice number is needed and line counter is reset to 1
                invoiceNumber = new DbManagerPurchaseLedger().internalTransactionNumberGenerator();
                lineCounter = 1;
                invoiceUuid = java.util.UUID.fromString(String.valueOf(new UUID()));
            }
            else
            {
                // if it is the same, only the lineCounter need to be increased
                lineCounter++;
            }

            if (csvRecord.get(INVENTORY_RELEVANT).equals("YES")) 
            {
                importStockRelevantInvoiceLine(csvRecord);
            }
            else if (csvRecord.get(COS_RELEVANT).equals("YES"))
            {
                importCosRelevantTransactionLine(csvRecord);
            }
            else
            {
                importNonStockRelevantInvoiceLine(csvRecord);
            }
            referenceChecker = currentReference;
        }
    }

    private void importCosRelevantTransactionLine(CSVRecord csvRecord) //TODO
    {
        int transactionGroupKey = Integer.valueOf(csvRecord.get(ASSOCIATED_TRANSACTION_GROUP_KEY));
        int transactionLineKey;
        Countries countryFrom;
        Countries countryTo;
        Channels saleChannel;
        String productKey = "";
        try
        {
            // Check if there is an associated transaction line (on top of the transaction group key)
            transactionLineKey = Integer.valueOf(csvRecord.get(ASSOCIATED_TRANSACTION_LINE_KEY));
            countryFrom = Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionLineFromCountry(
                    transactionGroupKey, transactionLineKey));
            countryTo= Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionLineToCountry(
                            transactionGroupKey, transactionLineKey));
            saleChannel = Channels.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionLineToChannel(
                            transactionGroupKey, transactionLineKey));
            productKey = new DbManagerPurchaseLedger().retrieveProductKey(
                    transactionGroupKey, transactionLineKey);


        }
        catch (Exception e) // if error is thrown, associated transaction line is null and the transaction is to split
                            // on the overall group of transactions (rather than the specific line)
        {
            transactionLineKey = 0;
            countryFrom = Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionGroupFromCountry(
                            transactionGroupKey));
            countryTo= Countries.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionGroupToCountry(
                            transactionGroupKey));
            saleChannel = Channels.valueOf(
                    new DbManagerPurchaseLedger().retrieveTransactionGroupToChannel(
                            transactionGroupKey));
        }



        purchaseLedgerTransactionLine = new PurchaseLedgerTransactionLine(csvRecord.get(INVOICE_DATE),
                csvRecord.get(SUPPLIER_ID), csvRecord.get(EXTERNAL_INVOICE_REFERENCE),
                csvRecord.get(DESCRIPTION), productKey,
                Integer.valueOf(csvRecord.get(QUANTITY)),
                Double.valueOf(csvRecord.get(PRICE)), Double.valueOf(csvRecord.get(VAT)),
                csvRecord.get(VAT_CODE),invoiceNumber,lineCounter, invoiceUuid,
                Currencies.valueOf(csvRecord.get(CURRENCY)),
                PurchaseLedgerTransactionType.valueOf(csvRecord.get(TRANSACTION_TYPE)),
                true, transactionGroupKey, transactionLineKey);

        purchaseLedgerTransactionLine.setShippedFromCountry(countryFrom);
        purchaseLedgerTransactionLine.setShippedToCountry(countryTo);
        purchaseLedgerTransactionLine.setShippedToChannel(saleChannel);


        dbManagerPurchaseLedger.persistTarget(purchaseLedgerTransactionLine);

        double cos = Double.parseDouble(csvRecord.get(PRICE));
        double cosPerUnit;
        int quantity;

        if(transactionLineKey == 0)
        {
            quantity = new DbManagerPurchaseLedger().retrieveTransactionGroupQuantity(transactionGroupKey);
            cosPerUnit = cos / quantity;
            new DbManagerInventoryItems().updateTransactionGroupCos(transactionGroupKey, cosPerUnit);
        }

        else
        {
            quantity = new DbManagerPurchaseLedger().retrieveTransactionLineQuantity(transactionGroupKey,
                    transactionLineKey);
            cosPerUnit = cos / quantity;
            new DbManagerInventoryItems().updateTransactionLineCos(transactionGroupKey, transactionLineKey,
                    cosPerUnit);
        }
    }

    private void importStockRelevantInvoiceLine(CSVRecord csvRecord)
    {
        purchaseLedgerTransactionLine = new PurchaseLedgerTransactionLine(csvRecord.get(INVOICE_DATE),
                csvRecord.get(SUPPLIER_ID), csvRecord.get(EXTERNAL_INVOICE_REFERENCE),
                csvRecord.get(DESCRIPTION), csvRecord.get(PRODUCT_KEY),
                Integer.valueOf(csvRecord.get(QUANTITY)), Double.valueOf(csvRecord.get(PRICE)),
                Double.valueOf(csvRecord.get(VAT)), csvRecord.get(VAT_CODE),
                Countries.valueOf(csvRecord.get(SHIPPED_FROM_COUNTRY)),
                Countries.valueOf(csvRecord.get(SHIPPED_TO_COUNTRY)),
                Channels.valueOf(csvRecord.get(SHIPPED_TO_CHANNEL)),
                invoiceNumber,lineCounter, invoiceUuid, Currencies.valueOf(csvRecord.get(CURRENCY)),
                PurchaseLedgerTransactionType.valueOf(csvRecord.get(TRANSACTION_TYPE)));

        Double itemCost =  Double.valueOf(csvRecord.get(PRICE)) / Integer.valueOf(csvRecord.get(QUANTITY));

        // persist inventory items
        for (int i = 1; i <= Integer.valueOf(csvRecord.get(QUANTITY)); i++)
        {
            inventoryItem = new InventoryItem(invoiceNumber, lineCounter, i,
                    purchaseLedgerTransactionLine.getInvoiceUuid(), purchaseLedgerTransactionLine.getLineUuid(),
                    java.util.UUID.fromString(String.valueOf(new UUID())),
                    purchaseLedgerTransactionLine.getProductKey(), itemCost,
                    purchaseLedgerTransactionLine.getShippedToCountry(),
                    purchaseLedgerTransactionLine.getShippedToChannel(),
                    purchaseLedgerTransactionLine.getTransactionDate());

            inventoryItem.setInventoryItemStatus(InventoryItemStatus.AVAILABLE_FOR_SALE);
            inventoryItem.setCurrency(purchaseLedgerTransactionLine.getCurrency());

            dbManagerInventoryItems.persistTarget(inventoryItem);
        }
        dbManagerPurchaseLedger.persistTarget(purchaseLedgerTransactionLine);
    }

    private void importNonStockRelevantInvoiceLine(CSVRecord csvRecord)
    {
        if(csvRecord.get(COS_RELEVANT).equals("YES"))
        {

            int associatedTransactionGroupKey = Integer.valueOf(csvRecord.get(ASSOCIATED_TRANSACTION_GROUP_KEY));
            int associatedTransactionLineKey = Integer.valueOf(csvRecord.get(ASSOCIATED_TRANSACTION_LINE_KEY));
        }

        int quantity = (csvRecord.get(QUANTITY).isEmpty() ? 1 : Integer.valueOf(csvRecord.get(QUANTITY)));
        purchaseLedgerTransactionLine = new PurchaseLedgerTransactionLine(csvRecord.get(INVOICE_DATE),
                csvRecord.get(SUPPLIER_ID), csvRecord.get(EXTERNAL_INVOICE_REFERENCE),
                csvRecord.get(DESCRIPTION), csvRecord.get(PRODUCT_KEY),
                quantity,
                Double.valueOf(csvRecord.get(PRICE)), Double.valueOf(csvRecord.get(VAT)),
                csvRecord.get(VAT_CODE),invoiceNumber,lineCounter, invoiceUuid,
                Currencies.valueOf(csvRecord.get(CURRENCY)),
                PurchaseLedgerTransactionType.valueOf(csvRecord.get(TRANSACTION_TYPE)),false,0,0);

        dbManagerPurchaseLedger.persistTarget(purchaseLedgerTransactionLine);
    }

}
