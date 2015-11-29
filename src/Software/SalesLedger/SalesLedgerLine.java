package Software.SalesLedger;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.SaleChannels;
import Software.Enums.SaleLedgerTransactionType;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Michele on 26/11/2015.
 */
public class SalesLedgerLine
{
    private Map properties = new HashMap();

    public SalesLedgerLine(String date, String channelId, String productId, String transactionCity,
                           String transactionPostCode, Countries shipToCountry, int quantity,
                           double transactionPrice, double transactionAssociatedCos, SaleChannels transactionChannel,
                           Countries countryTransactionChannel, Currencies currency, int transactionGroupId,
                           int transactionLineId, SaleLedgerTransactionType transactionLineStatus, String itemId)
    {
        properties.put("date", date);
        properties.put("channelSaleId", channelId);
        properties.put("productId", productId);
        properties.put("city", transactionCity);
        properties.put("postCode", transactionPostCode);
        properties.put("shipToCountry", shipToCountry);
        properties.put("quantity", quantity);
        properties.put("transactionPrice", transactionPrice);
        properties.put("transactionAssociatedCos", transactionAssociatedCos);
        properties.put("transactionChannel", transactionChannel);
        properties.put("countryTransactionChannel",countryTransactionChannel);
        properties.put("currency", currency);
        properties.put("transactionGroupId", transactionGroupId);
        properties.put("transactionLineId", transactionLineId);
        properties.put("transactionLineUUID", java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())));
        properties.put("transactionLineStatus", transactionLineStatus);
        properties.put("itemId", itemId);
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
