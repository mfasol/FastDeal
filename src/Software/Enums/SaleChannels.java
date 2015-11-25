package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum SaleChannels
{
    AMAZON,
    EBAY,
    ALIBABA,
    FASTDEAL,
    OVERSTOCK,
    BLANK;

    public static boolean contains(String channel)
    {
        for(SaleChannels aChannel : SaleChannels.values())
        {
            if(aChannel.name().equalsIgnoreCase(channel)) {return true;}
        }
        return false;
    }
}
