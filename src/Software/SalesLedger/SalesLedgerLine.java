package Software.SalesLedger;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.Channels;
import Software.Enums.SaleLedgerTransactionType;
import Software.Utilities.Importable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Michele on 26/11/2015.
 */
public class SalesLedgerLine extends Importable
{
    private Map<String, Object> properties = new HashMap<String, Object>();

    public SalesLedgerLine(String date, String channelId, String productId, String transactionCity,
                           String transactionPostCode, Countries shipToCountry, int quantity,
                           double transactionPrice, double logisticAssociatedCos, Channels logisticChannel,
                           Countries countryTransactionChannel, Currencies currency, int transactionGroupId,
                           int transactionLineId, SaleLedgerTransactionType transactionLineStatus, String itemId,
                           UUID itemUUID, Channels merchantChannel, Countries merchantChannelCountry,
                           double merchantChannelCos)
    {
        properties.put("date", date);
        properties.put("channelSaleId", channelId);
        properties.put("productId", productId);
        properties.put("city", transactionCity);
        properties.put("postCode", transactionPostCode);
        properties.put("shipToCountry", shipToCountry);
        properties.put("quantity", quantity);
        properties.put("transactionPrice", transactionPrice);
        properties.put("logisticAssociatedCos", logisticAssociatedCos);
        properties.put("logisticChannel", logisticChannel);
        properties.put("countryLogisticChannel",countryTransactionChannel);
        properties.put("currency", currency);
        properties.put("transactionGroupId", transactionGroupId);
        properties.put("transactionLineId", transactionLineId);
        properties.put("transactionLineUUID", java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())));
        properties.put("transactionLineStatus", transactionLineStatus);
        properties.put("itemId", itemId);
        properties.put("itemUUID", itemUUID);
        properties.put("merchantChannel", merchantChannel);
        properties.put("merchantChannelCountry", merchantChannelCountry);
        properties.put("merchantChannelCos", merchantChannelCos);
    }

    public Map<String, Object> getProperties()
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
