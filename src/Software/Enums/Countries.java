package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum  Countries
{
    BE,
    BG,
    DE,
    DK,
    ES,
    FI,
    FR,
    GB,
    GR,
    IE,
    IT,
    LU,
    MT,
    RO,
    SE,
    SP,
    SK,
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
