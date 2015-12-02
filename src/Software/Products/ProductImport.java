package Software.Products;

import Software.Utilities.Importer;
import DbServer.DbManagerInterface;
import org.apache.commons.csv.CSVRecord;
import java.io.*;

/**
 * Created by Michele on 17/10/2015.
 *
 * Class responsibility is to import products from a csv excel file
 *
 */
public class ProductImport extends Importer
{

    private final int ASIN = 0;
    private final int EAN = 1;
    private final int SELLER_SKU = 2;
    private final int ITEM_NAME = 3;
    private final int MANUFACTURER = 4;
    private final int OPEN_DATE = 5;

    Product product;
    DbManagerInterface targetDbManager;

    public ProductImport()
    {

    }

    public void completeImportProcess()

    {
        targetDbManager = new DbManagerProducts();

        for (CSVRecord csvRecord : super.csvLines)
        {
            product = new Product(csvRecord.get(ASIN),csvRecord.get(EAN),csvRecord.get(SELLER_SKU),
                    csvRecord.get(ITEM_NAME),csvRecord.get(MANUFACTURER), csvRecord.get(OPEN_DATE));
            targetDbManager.persistTarget(product);
        }
        try
        {
            csvParser.close();
        } catch (IOException e)
        {
            e.printStackTrace();
        }

    }
}
