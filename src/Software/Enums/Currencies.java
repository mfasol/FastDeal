package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum Currencies
{
    GBP,
    EUR,
    USD,
    BLANK;

    public static boolean contains(String currency)
    {
        for(Currencies aCurrency : Currencies.values())
        {
            if(aCurrency.name().equalsIgnoreCase(currency)) {return true;}
        }
        return false;
    }
}
