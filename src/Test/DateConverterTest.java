package Test;

import Software.Utilities.DateConverter;
import org.junit.Test;

/**
 * Created by Michele on 29/11/2015.
 */
public class DateConverterTest
{

    @Test
    public void testConvert() throws Exception
    {
        DateConverter dateConverter = new DateConverter();
        System.out.println(dateConverter.convert("01/01/2015"));
    }
}