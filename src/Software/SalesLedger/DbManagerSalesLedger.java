package Software.SalesLedger;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.Enums.Channels;
import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.SaleLedgerTransactionType;
import Software.Utilities.DateConverter;
import Software.Utilities.Importable;


import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

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
                    "TRANSACTION_PRICE, LOGISTIC_ADDITIONAL_COS, LOGISTIC_CHANNEL, LOGISTIC_CHANNEL_COUNTRY," +
                    "CURRENCY, TRANSACTION_ID, TRANSACTION_LINE_ID, TRANSACTION_LINE_UUID, TRANSACTION_STATUS," +
                    " ITEM_ID, TRANSACTION_TIMESTAMP, " +
                    "TRANSACTION_LOCK, ITEM_UUID, MERCHANT_CHANNEL, MERCHANT_CHANNEL_COUNTRY, MERCHANT_CHANNEL_FEES)" +
                    " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

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
                    salesLedgerLine.getProperty("logisticAssociatedCos"))));
            preparedStatement.setString(10,String.valueOf(salesLedgerLine.getProperty("logisticChannel")));
            preparedStatement.setString(11,String.valueOf(salesLedgerLine.getProperty("countryLogisticChannel")));
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
            preparedStatement.setString(21,String.valueOf(salesLedgerLine.getProperty("merchantChannel")));
            preparedStatement.setString(22,String.valueOf(salesLedgerLine.getProperty("merchantChannelCountry")));
            preparedStatement.setDouble(23,Double.parseDouble(String.valueOf(
                    salesLedgerLine.getProperty("merchantChannelCos"))));



            preparedStatement.executeUpdate();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public SalesLedgerLine getTransactionByUUID(String transactionUUID)
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT * FROM SALES_LEDGER WHERE " +
                    "TRANSACTION_LINE_UUID = (?)");

            preparedStatement.setString(1, transactionUUID);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                salesLedgerLine = toSalesLedgerLine(resultSet);
            }

            resultSet.close();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return salesLedgerLine;
    }

    public ArrayList<SalesLedgerLine> getTransactionByExternalId(String externalId, String productId)
    {
        ArrayList<SalesLedgerLine> transactionsArray = new ArrayList<>();
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT * FROM SALES_LEDGER WHERE " +
                    "EXTERNAL_TRANSACTION_ID = (?) AND PRODUCT_KEY = (?)");

            preparedStatement.setString(1, externalId);
            preparedStatement.setString(2, productId);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                salesLedgerLine = toSalesLedgerLine(resultSet);
                transactionsArray.add(salesLedgerLine);
            }

            resultSet.close();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return transactionsArray;
    }

    private SalesLedgerLine toSalesLedgerLine(ResultSet resultSet)
    {
        try
        {
            salesLedgerLine = new SalesLedgerLine(resultSet.getString(1),
                    resultSet.getString(2), resultSet.getString(3), resultSet.getString(4),
                    resultSet.getString(5), Countries.valueOf(resultSet.getString(6)), resultSet.getInt(7),
                    resultSet.getDouble(8), resultSet.getDouble(9), Channels.valueOf(resultSet.getString(10)),
                    Countries.valueOf(resultSet.getString(11)), Currencies.valueOf(resultSet.getString(12)),
                    resultSet.getInt(13),resultSet.getInt(14),
                    SaleLedgerTransactionType.valueOf(resultSet.getString(16)), resultSet.getString(17),
                    java.util.UUID.fromString(String.valueOf(resultSet.getString(20))),
                    Channels.valueOf(resultSet.getString(21)), Countries.valueOf(resultSet.getString(24)),
                    resultSet.getDouble(22));

            salesLedgerLine.setProperty("transactionLineUUID", java.util.UUID.fromString(String.valueOf(
                    resultSet.getString(15))));
        } catch (SQLException e)
        {
            e.printStackTrace();
        }

        return salesLedgerLine;

    }

    public void updateStatus(String status, String transactionUUID)
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("UPDATE SALES_LEDGER SET " +
                    "TRANSACTION_STATUS = (?) WHERE TRANSACTION_LINE_UUID = (?)");

            preparedStatement.setString(1, status);
            preparedStatement.setString(2, transactionUUID);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

    }

    public void flagForRefund(String transactionUUID)
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("UPDATE SALES_LEDGER SET " +
                    "IS_REFUNDED = TRUE WHERE TRANSACTION_LINE_UUID = (?)");

            preparedStatement.setString(1, transactionUUID);
            preparedStatement.executeUpdate();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

    }

    public SalesLedgerLine getItemForRefund(String externalId, String productId)
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT * FROM SALES_LEDGER WHERE " +
                    "TRANSACTION_LINE_UUID = (SELECT(MIN(TRANSACTION_LINE_UUID)) FROM SALES_LEDGER /**/" +
                    "WHERE EXTERNAL_TRANSACTION_ID = (?) AND PRODUCT_KEY = (?) AND TRANSACTION_STATUS = (?)" +
                    "AND IS_REFUNDED = FALSE)");

            preparedStatement.setString(1, externalId);
            preparedStatement.setString(2, productId);
            preparedStatement.setString(3, String.valueOf(SaleLedgerTransactionType.SALE));


            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                salesLedgerLine = toSalesLedgerLine(resultSet);
            }

            resultSet.close();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return salesLedgerLine;
    }


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
