package Software.PurchaseLedger;

import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.PurchaseLedgerTransactionType;
import Software.Enums.SaleChannels;
import Software.Utilities.Importable;


/**
 * Created by Michele on 08/11/2015.
 *
 * Class responsibility is to instantiate invoice lines to be imported in the purchase ledger database.
 * There are 2 type of invoice lines. Lines that are relevant also for the inventory and lines that are not.
 *
 */
public class PurchaseLedgerTransactionLine extends Importable
{
    private String transactionDate;
    private String supplierID;
    private java.util.UUID LineUuid;
    private int lineNumber;
    private String externalAccountingReference;
    private String description;
    private String productKey;
    private int quantity;
    private double price;
    private double vat;
    private String vatCode;
    private boolean inventoryRelevant = false;
    private Countries shippedFromCountry = null;
    private Countries shippedToCountry = null;
    private SaleChannels shippedToChannel = null;
    private int internalInvoiceReference = 0;
    private int internalInvoiceLineReference = 0;
    private java.util.UUID invoiceUuid;
    private Currencies currency;
    private boolean cosRelevant = false;
    private int associatedTransactionGroupReference = 0;
    private int associatedTransactionLinReference = 0;

    private PurchaseLedgerTransactionType purchaseLedgerTransactionType;

    // Constructor for inventory relevant lines
    public PurchaseLedgerTransactionLine(String transactionDate, String supplierID, String externalAccountingReference,
                                         String description, String productKey, int quantity, double price,
                                         double vat, String vatCode,
                                         Countries shippedFromCountry, Countries shippedToCountry,
                                         SaleChannels shippedToChannel, int internalInvoiceReference,
                                         int internalInvoiceLineReference, java.util.UUID invoiceUuid,
                                         Currencies currency,
                                         PurchaseLedgerTransactionType purchaseLedgerTransactionType)
    {
        this.transactionDate = transactionDate;
        this.externalAccountingReference = externalAccountingReference;
        this.supplierID = supplierID;
        this.description = description;
        this.productKey = productKey;
        this.quantity = quantity;
        this.price = price;
        this.vat = vat;
        this.vatCode = vatCode;
        this.inventoryRelevant = true;
        this.shippedFromCountry = shippedFromCountry;
        this.shippedToCountry = shippedToCountry;
        this.shippedToChannel = shippedToChannel;
        this.internalInvoiceReference = internalInvoiceReference;
        this.internalInvoiceLineReference = internalInvoiceLineReference;
        LineUuid = java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID()));
        this.invoiceUuid = invoiceUuid;
        this.currency = currency;
        this.purchaseLedgerTransactionType = purchaseLedgerTransactionType;
    }

    // Constructor for non inventory relevant lines
    public PurchaseLedgerTransactionLine(String transactionDate, String supplierID, String externalAccountingReference,
                                         String description, String productKey, int quantity, double price,
                                         double vat, String vatCode,
                                         int internalInvoiceReference, int internalInvoiceLineReference,
                                         java.util.UUID invoiceUuid, Currencies currency,
                                         PurchaseLedgerTransactionType purchaseLedgerTransactionType,
                                         boolean cosRelevant,
                                         int associatedTransactionGroupReference, int associatedTransactionLinReference)
    {
        this.transactionDate = transactionDate;
        this.externalAccountingReference = externalAccountingReference;
        this.supplierID = supplierID;
        this.description = description;
        this.productKey = productKey;
        this.quantity = ((String.valueOf(quantity).isEmpty()) ? 1 : quantity);
        this.price = price;
        this.vat = vat;
        this.vatCode = vatCode;
        this.internalInvoiceReference = internalInvoiceReference;
        this.internalInvoiceLineReference = internalInvoiceLineReference;
        LineUuid = java.util.UUID.fromString(String.valueOf(new com.eaio.uuid.UUID()));
        this.invoiceUuid = invoiceUuid;
        this.currency = currency;
        this.purchaseLedgerTransactionType = purchaseLedgerTransactionType;
        this.cosRelevant = cosRelevant;
        this.associatedTransactionGroupReference = associatedTransactionGroupReference;
        this.associatedTransactionLinReference = associatedTransactionLinReference;

        this.shippedFromCountry = null;
        this.shippedToCountry = null;
        this.shippedToChannel = null;
    }

    public String getTransactionDate()
    {
        return transactionDate;
    }


    public void setTransactionDate(String transactionDate)
    {
        this.transactionDate = transactionDate;
    }

    public String getSupplierID()
    {
        return supplierID;
    }

    public void setSupplierID(String supplierID)
    {
        this.supplierID = supplierID;
    }

    public java.util.UUID getLineUuid()
    {
        return LineUuid;
    }

    public void setLineUuid(java.util.UUID lineUuid)
    {
        this.LineUuid = lineUuid;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber)
    {
        this.lineNumber = lineNumber;
    }

    public String getExternalAccountingReference()
    {
        return externalAccountingReference;
    }

    public void setExternalAccountingReference(String externalAccountingReference)
    {
        this.externalAccountingReference = externalAccountingReference;
    }

    public String getDescription()
    {
        return description;
    }

    public void setDescription(String description)
    {
        this.description = description;
    }

    public String getProductKey()
    {
        return productKey;
    }

    public void setProductKey(String productKey)
    {
        this.productKey = productKey;
    }

    public int getQuantity()
    {
        return quantity;
    }

    public void setQuantity(int quantity)
    {
        this.quantity = quantity;
    }

    public double getPrice()
    {
        return price;
    }

    public void setPrice(double price)
    {
        this.price = price;
    }

    public double getVat()
    {
        return vat;
    }

    public void setVat(double vat)
    {
        this.vat = vat;
    }

    public String getVatCode()
    {
        return vatCode;
    }

    public void setVatCode(String vatCode)
    {
        this.vatCode = vatCode;
    }

    public boolean isInventoryRelevant()
    {
        return inventoryRelevant;
    }

    public void setIsInventoryRelevant(boolean inventoryRelevant)
    {
        this.inventoryRelevant = inventoryRelevant;
    }

    public Countries getShippedFromCountry()
    {
        return shippedFromCountry;
    }

    public void setShippedFromCountry(Countries shippedFromCountry)
    {
        this.shippedFromCountry = shippedFromCountry;
    }

    public Countries getShippedToCountry()
    {
        return shippedToCountry;
    }

    public void setShippedToCountry(Countries shippedToCountry)
    {
        this.shippedToCountry = shippedToCountry;
    }

    public SaleChannels getShippedToChannel()
    {
        return shippedToChannel;
    }

    public void setShippedToChannel(SaleChannels shippedToChannel)
    {
        this.shippedToChannel = shippedToChannel;
    }

    public int getInternalTransactionReference()
    {
        return internalInvoiceReference;
    }

    public void setInternalInvoiceReference(int internalInvoiceReference)
    {
        this.internalInvoiceReference = internalInvoiceReference;
    }

    public int getInternalInvoiceLineReference()
    {
        return internalInvoiceLineReference;
    }

    public void setInternalInvoiceLineReference(int internalInvoiceLineReference)
    {
        this.internalInvoiceLineReference = internalInvoiceLineReference;
    }

    public java.util.UUID getInvoiceUuid()
    {
        return invoiceUuid;
    }

    public void setInvoiceUuid(java.util.UUID invoiceUuid)
    {
        this.invoiceUuid = invoiceUuid;
    }

    public Currencies getCurrency()
    {
        return currency;
    }

    public void setCurrency(Currencies currency)
    {
        this.currency = currency;
    }

    public PurchaseLedgerTransactionType getPurchaseLedgerTransactionType()
    {
        return purchaseLedgerTransactionType;
    }

    public void setPurchaseLedgerTransactionType(PurchaseLedgerTransactionType purchaseLedgerTransactionType)
    {
        this.purchaseLedgerTransactionType = purchaseLedgerTransactionType;
    }

    public int getInternalInvoiceReference()
    {
        return internalInvoiceReference;
    }

    public boolean isCosRelevant()
    {
        return cosRelevant;
    }

    public void setIsCosRelevant(boolean cosRelevant)
    {
        this.cosRelevant = cosRelevant;
    }

    public int getAssociatedTransactionGroupReference()
    {
        return associatedTransactionGroupReference;
    }

    public void setAssociatedTransactionGroupReference(int associatedTransactionGroupReference)
    {
        this.associatedTransactionGroupReference = associatedTransactionGroupReference;
    }

    public int getAssociatedTransactionLineReference()
    {
        return associatedTransactionLinReference;
    }

    public void setAssociatedTransactionLinReference(int associatedTransactionLinReference)
    {
        this.associatedTransactionLinReference = associatedTransactionLinReference;
    }

    @Override
    public String toString()
    {
        return "PurchaseLedgerTransactionLine{" +
                "transactionDate=" + transactionDate +
                ", supplierID='" + supplierID + '\'' +
                ", LineUuid=" + LineUuid +
                ", internalInvoiceReference=" + internalInvoiceReference +
                ", lineNumber=" + lineNumber +
                ", externalAccountingReference='" + externalAccountingReference + '\'' +
                ", description='" + description + '\'' +
                ", productKey='" + productKey + '\'' +
                ", quantity=" + quantity +
                ", price=" + price +
                ", vat=" + vat +
                ", vatCode='" + vatCode + '\'' +
                ", inventoryRelevant=" + inventoryRelevant +
                ", shippedFromCountry=" + shippedFromCountry +
                ", shippedToCountry=" + shippedToCountry +
                ", shippedToChannel=" + shippedToChannel +
                '}';
    }

    @Override
    public boolean equals(Object o)
    {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PurchaseLedgerTransactionLine that = (PurchaseLedgerTransactionLine) o;

        return LineUuid.equals(that.LineUuid);

    }

    @Override
    public int hashCode()
    {
        return LineUuid.hashCode();
    }
}
