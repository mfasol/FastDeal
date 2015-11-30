package Software.SalesLedger;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.DateConverter;
import Software.Importable;

import java.sql.*;

/**
 * Created by Michele on 26/11/2015.
 */
public class DbManagerSalesLedger implements DbManagerInterface
{
    ConnectionData connectionData = new ConnectionData();
    SalesLedgerLine salesLedgerLine;
    PreparedStatement preparedStatement;
    final String TABLE_NAME = "SALES_LEDGER";

    private int internalTransactionNumber = 0;

    @Override
    public void persistTarget(Importable importable)
    {
        salesLedgerLine = (SalesLedgerLine) importable;
        persistSaleLedgerLine(salesLedgerLine);

    }

    private void persistSaleLedgerLine(SalesLedgerLine salesLedgerLine)
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("INSERT INTO SALES_LEDGER(TRANSACTION_DATE, " +
                    "EXTERNAL_TRANSACTION_ID, PRODUCT_KEY, SHIP_CITY, SHIP_POSTCODE, SHIP_COUNTRY, QUANTITY, " +
                    "TRANSACTION_PRICE, TRANSACTION_ADDITIONAL_COS, TRANSACTION_CHANNEL, CURRENCY, TRANSACTION_ID, " +
                    "TRANSACTION_LINE_ID, TRANSACTION_LINE_UUID, TRANSACTION_STATUS, ITEM_ID, TRANSACTION_TIMESTAMP, " +
                    "TRANSACTION_LOCK, ITEM_UUID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            preparedStatement.setDate(1, DateConverter.convert(String.valueOf(salesLedgerLine.getProperty("date"))));

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }    //TODO

    public int internalTransactionNumberGenerator()
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            PreparedStatement preparedStatement;

            preparedStatement = connection.prepareStatement("SELECT COALESCE (MAX(TRANSACTION_ID),0)" +
                    " AS RESULT FROM " + TABLE_NAME);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                internalTransactionNumber = resultSet.getInt("RESULT") + 1;
            }

            resultSet.close();
            preparedStatement.close();

        } catch (SQLException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
        return internalTransactionNumber;
    }
}
