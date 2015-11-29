package Software.SalesLedger;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.SaleChannels;
import Software.Enums.SaleLedgerTransactionType;
import Software.Importer;
import Software.Inventory.DbManagerInventoryItems;
import org.apache.commons.csv.CSVRecord;

/**
 * Created by Michele on 26/11/2015.
 */
public class SalesLineImport extends Importer
{
    String referenceChecker = "";

    int transactionGroupId = new DbManagerSalesLedger().internalTransactionNumberGenerator();
    int transactionLineId = 1;

    SalesLedgerLine salesLedgerLine;
    DbManagerInventoryItems dbManagerInventoryItems;

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

    @Override
    protected void completeImportProcess()
    {
        for (CSVRecord csvRecord : super.csvLines)
        {
            System.out.println(csvRecord.toString());

            String currentReference = csvRecord.get(EXTERNAL_ORDER_ID);

            if(currentReference.equals(referenceChecker))
            {
                transactionGroupId = new DbManagerSalesLedger().internalTransactionNumberGenerator();
                transactionLineId  = 1;
            }
            else
            {
                transactionLineId++;
            }

            importTransactionLines(csvRecord);

            referenceChecker = currentReference;
        }

    }

    private void importTransactionLines(CSVRecord csvRecord)
    {
        String productKey =  csvRecord.get(PRODUCT_KEY);
        String country = csvRecord.get(TRANSACTION_CHANNEL_COUNTRY);
        String channel = csvRecord.get(TRANSACTION_CHANNEL);
        String itemId = dbManagerInventoryItems.getItemForSale(productKey,country,channel).getPrimaryKey();

        salesLedgerLine = new SalesLedgerLine(csvRecord.get(TRANSACTION_DATE),csvRecord.get(EXTERNAL_ORDER_ID),
                productKey,csvRecord.get(TRANSACTION_CITY),csvRecord.get(TRANSACTION_POSTCODE),
                Countries.valueOf(csvRecord.get(TRANSACTION_COUNTRY)),
                Integer.parseInt(csvRecord.get(PRODUCT_QUANTITY)),Double.parseDouble(csvRecord.get(TRANSACTION_PRICE)),
                Double.parseDouble(csvRecord.get(TRANSACTION_ASSOCIATED_COS)), SaleChannels.valueOf(channel),
                Countries.valueOf(country), Currencies.valueOf(csvRecord.get(CURRENCY)), transactionGroupId,
                transactionLineId,SaleLedgerTransactionType.SALE, itemId);
    }
}
