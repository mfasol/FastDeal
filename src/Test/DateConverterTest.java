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
        System.out.println(DateConverter.convert("01/01/2015"));
    }
}