package Software.SalesLedger;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.Utilities.DateConverter;
import Software.Utilities.Importable;

import java.sql.*;
import java.time.Instant;

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

            preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_NAME + "(TRANSACTION_DATE, " +
                    "EXTERNAL_TRANSACTION_ID, PRODUCT_KEY, SHIP_CITY, SHIP_POSTCODE, SHIP_COUNTRY, QUANTITY, " +
                    "TRANSACTION_PRICE, TRANSACTION_ADDITIONAL_COS, TRANSACTION_CHANNEL, TRANSACTION_CHANNEL_COUNTRY," +
                    "CURRENCY, TRANSACTION_ID, TRANSACTION_LINE_ID, TRANSACTION_LINE_UUID, TRANSACTION_STATUS," +
                    " ITEM_ID, TRANSACTION_TIMESTAMP, " +
                    "TRANSACTION_LOCK, ITEM_UUID) VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

            preparedStatement.setDate(1, DateConverter.convert(String.valueOf(salesLedgerLine.getProperty("date"))));
            preparedStatement.setString(2, String.valueOf(salesLedgerLine.getProperty("channelSaleId")));
            preparedStatement.setString(3,String.valueOf(salesLedgerLine.getProperty("productId")));
            preparedStatement.setString(4,String.valueOf(salesLedgerLine.getProperty("city")));
            preparedStatement.setString(5,String.valueOf(salesLedgerLine.getProperty("postCode")));
            preparedStatement.setString(6,String.valueOf(salesLedgerLine.getProperty("shipToCountry")));
            preparedStatement.setInt(7,Integer.parseInt(String.valueOf(salesLedgerLine.getProperty("quantity"))));
            preparedStatement.setDouble(8,Double.parseDouble(String.valueOf(
                    salesLedgerLine.getProperty("transactionPrice"))));
            preparedStatement.setDouble(9,Double.parseDouble(String.valueOf(
                    salesLedgerLine.getProperty("transactionAssociatedCos"))));
            preparedStatement.setString(10,String.valueOf(salesLedgerLine.getProperty("transactionChannel")));
            preparedStatement.setString(11,String.valueOf(salesLedgerLine.getProperty("countryTransactionChannel")));
            preparedStatement.setString(12,String.valueOf(salesLedgerLine.getProperty("currency")));
            preparedStatement.setInt(13,
                    Integer.parseInt(String.valueOf(salesLedgerLine.getProperty("transactionGroupId"))));
            preparedStatement.setInt(14,
                    Integer.parseInt(String.valueOf(salesLedgerLine.getProperty("transactionLineId"))));
            preparedStatement.setString(15,String.valueOf(salesLedgerLine.getProperty("transactionLineUUID")));
            preparedStatement.setString(16,String.valueOf(salesLedgerLine.getProperty("transactionLineStatus")));
            preparedStatement.setString(17,String.valueOf(salesLedgerLine.getProperty("itemId")));
            preparedStatement.setTimestamp(18, java.sql.Timestamp.from(Instant.now()));
            preparedStatement.setBoolean(19, false);
            preparedStatement.setString(20,String.valueOf(salesLedgerLine.getProperty("itemUUID")));

            preparedStatement.executeUpdate();
            preparedStatement.close();

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
