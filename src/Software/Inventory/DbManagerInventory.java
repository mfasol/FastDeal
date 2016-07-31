package Software.Inventory;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.Enums.*;
import Software.Utilities.DateConverter;
import Software.Utilities.Importable;


import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

import Software.PurchaseLedger.DbManagerPurchaseLedger;

import java.util.UUID;

import static java.text.NumberFormat.Field.CURRENCY;


/**
 * Created by Michele on 03/11/2015.
 */
public class DbManagerInventory implements DbManagerInterface
{
    private ConnectionData connectionData = new ConnectionData();
    private InventoryItem inventoryItem;
    private PreparedStatement preparedStatement;
    final String TABLE_NAME = "INVENTORY_ITEMS";
    private String country;
    private String channel;
    private String queryTableName;


    private static final int ROUNDING_PRECISION = 12;

    @Override
    public void persistTarget(Importable importable)
    {
        inventoryItem  = (InventoryItem) importable;
        persistInventoryItem(inventoryItem);
    }

    private void persistInventoryItem(InventoryItem inventoryItem)
    {
        String finalTableName = TABLE_NAME + "_" + inventoryItem.getProperty("country") + "_" +
                inventoryItem.getProperty("saleChannel");
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("INSERT INTO " + finalTableName + "(" +
                    "INTERNAL_INVOICE_REFERENCE_KEY, INTERNAL_INVOICE_REFERENCE_LINE, ITEM_NUMBER, " +
                    "CONCATENATED_PRIMARY_KEY, INVOICE_UUID, INVOICE_LINE_UUID, PRODUCT_KEY,ITEM_COS," +
                    "SINGLE_ITEM_UUID, ITEM_DATE, INVENTORY_ITEM_STATUS, INVENTORY_ITEM_CURRENCY," +
                    "INVENTORY_ITEM_STORE_TIMESTAMP, TRANSACTION_TYPE, TRANSACTION_UUID )" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ROUND(?,12), ?, ?, ?, ?, ?, ?, ?)");


            DateConverter dateConverter = new DateConverter();
            java.sql.Date sqlDate = dateConverter.convert(inventoryItem.getProperty("itemDate").toString());

            preparedStatement.setInt(1, Integer.parseInt(inventoryItem.
                    getProperty("internalInvoiceReference").toString()));
            preparedStatement.setInt(2, Integer.parseInt(inventoryItem.
                    getProperty("internalInvoiceReferenceLine").toString()));
            preparedStatement.setInt(3, Integer.parseInt(inventoryItem.getProperty("itemNumber").toString()));
            preparedStatement.setString(4,inventoryItem.getProperty("primaryKey").toString());
            preparedStatement.setString(5, String.valueOf(inventoryItem.getProperty("invoiceUuid")));
            preparedStatement.setString(6, String.valueOf(inventoryItem.getProperty("invoiceLineUuid")));
            preparedStatement.setString(7, inventoryItem.getProperty("productKey").toString());
            preparedStatement.setDouble(8, Double.parseDouble(inventoryItem.getProperty("itemCost").toString()));
            preparedStatement.setString(9, String.valueOf(inventoryItem.getProperty("itemUuid")));
            preparedStatement.setDate(10, sqlDate);
            preparedStatement.setString(11, String.valueOf(inventoryItem.getProperty("itemStatus")));
            preparedStatement.setString(12, String.valueOf(inventoryItem.getProperty("currency")));
            preparedStatement.setTimestamp(13, java.sql.Timestamp.from(Instant.now()));
            preparedStatement.setString(14, String.valueOf(inventoryItem.getProperty("inventoryItemTransactionTypes")));
            preparedStatement.setString(15, String.valueOf(inventoryItem.getProperty("itemUuid")));

            preparedStatement.executeUpdate();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updateTransactionGroupCos(int transactionGroupKey, double cosPerUnit)
    {

        DbManagerPurchaseLedger dbManagerPurchaseLedger = new DbManagerPurchaseLedger();
        String countryString = dbManagerPurchaseLedger.retrieveTransactionLineToCountry(transactionGroupKey,1);
        Countries country = Countries.valueOf(countryString);

        String channelString = dbManagerPurchaseLedger.retrieveTransactionLineToChannel(transactionGroupKey,1);
        Channels saleChannel = Channels.valueOf(channelString);

        String queryTableName = TABLE_NAME+"_"+country+"_"+saleChannel;
        cosPerUnit = round(cosPerUnit);
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("UPDATE " + queryTableName +
                    " SET ITEM_COS = ROUND(ITEM_COS + (?),12) WHERE  INTERNAL_INVOICE_REFERENCE_KEY = (?) " +
                    "AND INVENTORY_TRANSACTION_LOCKED = (?)");

            preparedStatement.setDouble(1, cosPerUnit);
            preparedStatement.setInt(2, transactionGroupKey);
            preparedStatement.setBoolean(3, false);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updateTransactionLineCos(int transactionGroupKey, int transactionLineKey, double cosPerUnit)
    {

        DbManagerPurchaseLedger dbManagerPurchaseLedger = new DbManagerPurchaseLedger();
        String countryString = dbManagerPurchaseLedger.retrieveTransactionLineToCountry(transactionGroupKey,1);
        Countries country = Countries.valueOf(countryString);

        String channelString = dbManagerPurchaseLedger.retrieveTransactionLineToChannel(transactionGroupKey,1);
        Channels saleChannel = Channels.valueOf(channelString);
        cosPerUnit = round(cosPerUnit);
        queryTableName = TABLE_NAME+"_"+country+"_"+saleChannel;

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("UPDATE " + queryTableName +
                    " SET ITEM_COS = ROUND (ITEM_COS + (?), " + ROUNDING_PRECISION + ") WHERE  " +
                    "INTERNAL_INVOICE_REFERENCE_KEY = (?) AND INTERNAL_INVOICE_REFERENCE_LINE = (?)" +
                    " AND INVENTORY_TRANSACTION_LOCKED = (?)");

            preparedStatement.setDouble(1, cosPerUnit);
            preparedStatement.setInt(2, transactionGroupKey);
            preparedStatement.setInt(3, transactionLineKey);
            preparedStatement.setBoolean(4, false);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public void updateInventoryItemCos(String concatenatedKey, UUID itemUUID, double cos, String country,
                                       String saleChannel)
    {
        queryTableName = TABLE_NAME+"_"+country+"_"+saleChannel;
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("UPDATE " + queryTableName +
                    " SET ITEM_COS = ROUND (ITEM_COS + (?), " + ROUNDING_PRECISION + ") WHERE " +
                    "CONCATENATED_PRIMARY_KEY = (?) AND SINGLE_ITEM_UUID = (?) AND " +
                    "INVENTORY_TRANSACTION_LOCKED = (?)");

            preparedStatement.setDouble(1, cos);
            preparedStatement.setString(2, concatenatedKey);
            preparedStatement.setString(3, String.valueOf(itemUUID));
            preparedStatement.setBoolean(4, false);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }


    } //TODO

    public void updateInventoryItemStatus(String concatenatedKey, UUID itemUUID,
                                          InventoryItemStatus inventoryItemStatus, String country, String saleChannel)
    {
        queryTableName = TABLE_NAME+"_"+country+"_"+saleChannel;

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("UPDATE " + queryTableName +
                    " SET INVENTORY_ITEM_STATUS = (?) WHERE " +
                    "CONCATENATED_PRIMARY_KEY = (?) AND SINGLE_ITEM_UUID = (?) AND " +
                    "INVENTORY_TRANSACTION_LOCKED = (?)");

            preparedStatement.setString(1, String.valueOf(inventoryItemStatus));
            preparedStatement.setString(2, concatenatedKey);
            preparedStatement.setString(3, String.valueOf(itemUUID));
            preparedStatement.setBoolean(4, false);

            preparedStatement.executeUpdate();
            preparedStatement.close();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    public InventoryItem getItemForSale(String productKey, String country, String saleChannel)
    {
        inventoryItem = null; // reset inventory item

        queryTableName = TABLE_NAME+"_"+country+"_"+saleChannel;
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT * FROM " + queryTableName +
                    " WHERE SINGLE_ITEM_UUID = (SELECT MIN(SINGLE_ITEM_UUID) " +
                    "FROM " + queryTableName + " WHERE PRODUCT_KEY = (?) AND " +
                    "INVENTORY_ITEM_STATUS = (?))");

            preparedStatement.setString(1, productKey);
            preparedStatement.setString(2, String.valueOf(InventoryItemStatus.AVAILABLE_FOR_SALE));

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                inventoryItem = toInventoryItem(resultSet);
            }

            resultSet.close();
            preparedStatement.close();

        } catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return inventoryItem;
    }

    public InventoryItem toInventoryItem(ResultSet resultSet)
    {
        DbManagerPurchaseLedger dbManagerPurchaseLedger = new DbManagerPurchaseLedger();
        try
        {
            int invoiceNum = resultSet.getInt(1);
            int invoiceLineNum = resultSet.getInt(2);
            inventoryItem = new InventoryItem(invoiceNum, invoiceLineNum, resultSet.getInt(3),
                    UUID.fromString(resultSet.getString(5)), UUID.fromString(resultSet.getString(6)),
                    UUID.fromString(resultSet.getString(9)), resultSet.getString("PRODUCT_KEY"),
                    resultSet.getDouble("ITEM_COS"),
                    Countries.valueOf(dbManagerPurchaseLedger.retrieveTransactionLineToCountry(invoiceNum,
                            invoiceLineNum)),
                    Channels.valueOf(dbManagerPurchaseLedger.retrieveTransactionLineToChannel(invoiceNum,
                            invoiceLineNum)),
                    Currencies.valueOf(resultSet.getNString("INVENTORY_ITEM_CURRENCY")),
                    resultSet.getString("ITEM_DATE"));
            inventoryItem.setProperty("inventoryItemTransactionTypes",
                    InventoryItemTransactionTypes.valueOf(resultSet.getString("TRANSACTION_TYPE")));
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }

        return inventoryItem;
    }

    public static double round(double unrounded)
    {
        BigDecimal bd = new BigDecimal(unrounded);
        BigDecimal rounded = bd.setScale(ROUNDING_PRECISION, BigDecimal.ROUND_HALF_UP);
        return rounded.doubleValue();
    }
}
