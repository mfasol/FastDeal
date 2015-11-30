package Test;

import Software.DateConverter;
import org.junit.Test;

import static org.junit.Assert.*;

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