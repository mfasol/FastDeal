package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum  Countries
{
    GB,
    SP,
    IT,
    GR,
    FR,
    IE,
    BLANK;

    public static boolean contains(String country)
    {
        for(Countries aCountry : Countries.values())
        {
            if(aCountry.name().equalsIgnoreCase(country)) {return true;}
        }
        return false;
    }
}
