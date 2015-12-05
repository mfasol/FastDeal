package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum SaleLedgerTransactionType
{
    SALE,
    REVERSAL,
    REFUND,
    JOURNAL,
    BLANK
    ;

    public static boolean contains(String purchaseLedgerTransactionType)
    {
        for(SaleLedgerTransactionType aType : SaleLedgerTransactionType.values())
        {
            if(aType.name().equalsIgnoreCase(purchaseLedgerTransactionType)) {return true;}
        }
        return false;
    }
}
