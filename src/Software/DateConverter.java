package Software;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;

/**
 * Created by Michele on 29/11/2015.
 */
public class DateConverter
{
    // needed for converting a string in java date
    static DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

    public DateConverter()
    {
    }
    public static java.sql.Date convert(String dateString)
    {
        java.sql.Date sqlDate = null;
        try
        {
            // Conversion of a string to a java date
            String dateAsString = dateString;
            java.util.Date date = sourceFormat.parse(dateAsString);

            // Conversion of a java date to a sql date to store in database
            sqlDate = new java.sql.Date(date.getTime());
        }
        catch (ParseException e)
        {
            e.printStackTrace();
        }
        return sqlDate;
    }
}
