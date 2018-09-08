package Software.Enums;


/**
 * Created by Michele on 08/11/2015.
 */
public enum  Countries
{
    AT,
    BE,
    BG,
    CY,
    DE,
    DK,
    EE,
    ES,
    FI,
    FR,
    GB,
    GG,
    GR,
    HR,
    HU,
    IE,
    IM,
    JE,
    IT,
    LU,
    MT,
    NL,
    PL,
    PT,
    RO,
    SE,
    SI,
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
