package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum Channels
{
    AMAZON,
    EBAY,
    ALIBABA,
    FASTDEAL,
    OVERSTOCK,
    BLANK;

    public static boolean contains(String channel)
    {
        for(Channels aChannel : Channels.values())
        {
            if(aChannel.name().equalsIgnoreCase(channel)) {return true;}
        }
        return false;
    }
}
