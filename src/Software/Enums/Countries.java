package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum  Countries
{
    BG,
    DK,
    FI,
    FR,
    GB,
    GR,
    IE,
    IT,
    MT,
    SP,
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
