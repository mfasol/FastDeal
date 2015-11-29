package Software.SalesLedger;

import DbServer.DbManagerInterface;
import Software.Importable;

/**
 * Created by Michele on 26/11/2015.
 */
public class DbManagerSalesLedger implements DbManagerInterface
{

    @Override
    public void persistTarget(Importable importable)
    {

    }

    public int internalTransactionNumberGenerator()
    {
        return 0;
    }
}
