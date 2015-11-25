package Software.Enums;

/**
 * Created by Michele on 08/11/2015.
 */
public enum PurchaseLedgerTransactionType
{
    INVOICE,
    ACCRUAL,
    QUOTE,
    CREDIT_NOTE,
    REVERSAL,
    JOURNAL,
    ;

    public static boolean contains(String purchaseLedgerTransactionType)
    {
        for(PurchaseLedgerTransactionType aType : PurchaseLedgerTransactionType.values())
        {
            if(aType.name().equalsIgnoreCase(purchaseLedgerTransactionType)) {return true;}
        }
        return false;
    }
}
