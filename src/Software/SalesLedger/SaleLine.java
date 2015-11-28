package Software.SalesLedger;

import Software.Enums.Countries;
import Software.Enums.SaleChannels;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michele on 26/11/2015.
 */
public class SaleLine
{
    private Map properties = new HashMap();

    public SaleLine(String date, String channelId, String productId, String saleCity,
                    String salePostCode, Countries shipToCountry, int quantity,
                    double salePrice, double saleAssociatedCos, SaleChannels saleChannel,
                    Countries countrySaleChannel)
    {
        properties.put("Date", date);
        properties.put("ChannelSaleId", channelId);
        properties.put("ProductId", productId);
        properties.put("City", saleCity);
        properties.put("postCode", salePostCode);
        properties.put("shipToCountry", shipToCountry);
        properties.put("quantity", quantity);
        properties.put("salePrice", salePrice);
        properties.put("saleAssociatedCos", saleAssociatedCos);
        properties.put("saleChannel", saleChannel);
        properties.put("countrySaleChannel",countrySaleChannel);

    }

    public Map getProperties()
    {
        return properties;
    }

    public Object getProperty(String property)
    {
        return properties.get(property);
    }

    public void setProperty(String property, Object object)
    {
        properties.replace(property, object);
    }
}
