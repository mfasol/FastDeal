package Software.PurchaseLedger;

import Software.Enums.Channels;
import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.PurchaseLedgerTransactionType;
import Software.Utilities.Importable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by Michele on 30/05/2016.
 */
public class PurchaseLedgerLine extends Importable
{
    private Map<String, Object> properties = new HashMap<String, Object>();
    // Constructor for inventory relevant lines
    public PurchaseLedgerLine(String transactionDate, String supplierID, String externalAccountingReference,
                                String description, String productKey, int quantity, double price,
                                double vat, String vatCode,
                                Countries shippedFromCountry, Countries shippedToCountry,
                                Channels shippedToChannel, int internalInvoiceReference,
                                int internalInvoiceLineReference, UUID invoiceUuid,
                                Currencies currency,
                                PurchaseLedgerTransactionType purchaseLedgerTransactionType,
                                Boolean cosRelevant,
                                Integer associatedTransactionGroupReference, Integer associatedTransactionLinReference)
    {
        properties.put("date", transactionDate);
        properties.put("supplierID", supplierID);
        properties.put("externalAccountingReference", externalAccountingReference);
        properties.put("description", description);
        properties.put("productKey", productKey);
        properties.put("price", price);
        properties.put("vat", vat);
        properties.put("vatCode", vatCode);
        properties.put("shippedFromCountry", shippedFromCountry);
        properties.put("shippedToCountry", shippedToCountry);
        properties.put("shippedToChannel", shippedToChannel);
        properties.put("internalInvoiceReference", internalInvoiceReference);
        properties.put("internalInvoiceLineReference", internalInvoiceLineReference);
        properties.put("invoiceUuid", invoiceUuid);
        properties.put("currency", currency);
        properties.put("purchaseLedgerTransactionType", purchaseLedgerTransactionType);
        properties.put("transactionLineUUID", java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())));
        properties.put("cosRelevant",cosRelevant);
        properties.put("associatedTransactionGroupReference",associatedTransactionGroupReference);
        properties.put("associatedTransactionLinReference",associatedTransactionLinReference);
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

