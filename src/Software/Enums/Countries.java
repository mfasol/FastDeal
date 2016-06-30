package Software.Enums;

import static com.sun.tools.doclets.internal.toolkit.util.DocletConstants.NL;
import static java.time.chrono.ThaiBuddhistEra.BE;

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
    IE,
    IM,
    JE,
    IT,
    LU,
    MT,
    NL,
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
