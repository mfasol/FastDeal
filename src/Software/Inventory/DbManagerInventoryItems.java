package Software.Inventory;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.Enums.Countries;
import Software.Enums.InventoryItemStatus;
import Software.Enums.SaleChannels;
import Software.Utilities.Importable;


import java.math.BigDecimal;
import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;

import Software.PurchaseLedger.DbManagerPurchaseLedger;

import java.util.UUID;



/**
 * Created by Michele on 03/11/2015.
 */
public class DbManagerInventoryItems implements DbManagerInterface
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
        String finalTableName = TABLE_NAME + "_" + inventoryItem.getCountry() + "_" + inventoryItem.getSaleChannel();
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("INSERT INTO " + finalTableName + "(" +
                    "INTERNAL_INVOICE_REFERENCE_KEY, INTERNAL_INVOICE_REFERENCE_LINE, ITEM_NUMBER, " +
                    "CONCATENATED_PRIMARY_KEY, INVOICE_UUID, INVOICE_LINE_UUID, PRODUCT_KEY,ROUND (ITEM_COS, 12)," +
                    "SINGLE_ITEM_UUID, ITEM_DATE, INVENTORY_ITEM_STATUS, INVENTORY_ITEM_CURRENCY," +
                    "INVENTORY_ITEM_STORE_TIMESTAMP)" +
                    " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            // needed for converting a string in java date
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Conversion of a string to a java date
            String dateAsString = inventoryItem.getItemDate();
            java.util.Date date = sourceFormat.parse(dateAsString);

            // Conversion of a java date to a sql date to store in database
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            preparedStatement.setInt(1, inventoryItem.getInternalInvoiceReference());
            preparedStatement.setInt(2, inventoryItem.getInternalInvoiceReferenceLine());
            preparedStatement.setInt(3, inventoryItem.getItemNumber());
            preparedStatement.setString(4,inventoryItem.getPrimaryKey());
            preparedStatement.setString(5, String.valueOf(inventoryItem.getInvoiceUuid()));
            preparedStatement.setString(6, String.valueOf(inventoryItem.getInvoiceLineUuid()));
            preparedStatement.setString(7, inventoryItem.getProductKey());
            preparedStatement.setDouble(8, round(inventoryItem.getItemCost()));
            preparedStatement.setString(9, String.valueOf(inventoryItem.getItemUuid()));
            preparedStatement.setDate(10, sqlDate);
            preparedStatement.setString(11, String.valueOf(inventoryItem.getInventoryItemStatus()));
            preparedStatement.setString(12, String.valueOf(inventoryItem.getCurrency()));
            preparedStatement.setTimestamp(13, java.sql.Timestamp.from(Instant.now()));

            preparedStatement.executeUpdate();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException | ParseException e)
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
        SaleChannels saleChannel = SaleChannels.valueOf(channelString);

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
        SaleChannels saleChannel = SaleChannels.valueOf(channelString);
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
                    SaleChannels.valueOf(dbManagerPurchaseLedger.retrieveTransactionLineToChannel(invoiceNum,
                            invoiceLineNum)),
                    resultSet.getString("ITEM_DATE"));
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
