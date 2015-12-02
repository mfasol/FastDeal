package Software.Utilities;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.*;
import java.util.List;

/**
 * Created by Michele on 03/11/2015.
 */
public abstract class Importer
{
    File file;
    Reader in;
    public CSVParser csvParser;
    public List<CSVRecord> csvLines;

    protected final void importData()
    {
        try
        {
            file = FileImportManager.loadDialog();
            in = new FileReader(file);

            csvParser = new CSVParser(in, CSVFormat.TDF.withIgnoreEmptyLines());

            // Pass csvParser results to an List to remove the headings. Commons withSkipHeadings does not work
            csvLines = csvParser.getRecords();
            csvLines.remove(0);

            // Template Pattern: Final part of the import algorithm left to the subclasses
            completeImportProcess();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    protected abstract void completeImportProcess();
}
