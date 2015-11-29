package Software.SalesLedger;

import Software.Importer;
import org.apache.commons.csv.CSVRecord;

/**
 * Created by Michele on 26/11/2015.
 */
public class SalesLineImport extends Importer
{

    private final int SALE_DATE = 0;
    private final int EXTERNAL_ORDER_ID = 1;
    private final int PRODUCT_KEY = 2;
    private final int SALE_CITY = 3;
    private final int SALE_POSTCODE = 4;
    private final int SALE_COUNTRY = 5;
    private final int PRODUCT_QUANTITY = 6;
    private final int SALE_PRICE = 7;
    private final int SALE_ADDITIONAL_COS = 8;
    private final int SALE_CHANNEL = 9;
    private final int SALE_CHANNEL_COUNTRY = 10;
    private final int CURRENCY = 11;

    @Override
    protected void completeImportProcess()
    {
        for (CSVRecord csvRecord : super.csvLines)
        {
            System.out.println(csvRecord.toString());
        }

    }
}
