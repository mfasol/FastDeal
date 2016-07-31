package Software.PurchaseLedger;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.PurchaseLedgerTransactionType;
import Software.Enums.Channels;
import Software.Utilities.CheckNull;
import Software.Utilities.Importable;
import Software.Inventory.DbManagerInventory;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

import static com.sun.tools.doclint.Entity.ne;

/**
 * Created by Michele on 03/11/2015.
 */
public class DbManagerPurchaseLedger implements DbManagerInterface
{
    ConnectionData connectionData = new ConnectionData();
    PurchaseLedgerLine purchaseLedgerLine;
    PreparedStatement preparedStatement;
    final String TABLE_NAME = "PURCHASE_LEDGER";
    DbManagerInventory dbManagerInventory = new DbManagerInventory();

    private int internalTransactionNumber = 0;

    CheckNull replaceNull = new CheckNull();

    @Override
    public void persistTarget(Importable importable)
    {
        purchaseLedgerLine = (PurchaseLedgerLine) importable;
        System.out.println(purchaseLedgerLine.getProperties().toString());
        persistTransactionLine(purchaseLedgerLine);
    }

    private void persistTransactionLine(PurchaseLedgerLine purchaseLedgerLine)
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_NAME + "(TRANSACTION_DATE, " +
                    "EXTERNAL_TRANSACTION_REFERENCE, SUPPLIER_KEY, DESCRIPTION, PRODUCT_KEY,QUANTITY,PRICE,VAT_CODE," +
                    "VAT_AMOUNT, SHIPPED_FROM_COUNTRY, SHIPPED_TO_COUNTRY,SHIPPED_TO_CHANNEL,TRANSACTION_LINE_UUID," +
                    "INVENTORY_RELEVANT, INTERNAL_TRANSACTION_REFERENCE_KEY, " +
                    "INTERNAL_TRANSACTION_REFERENCE_LINE, INVOICE_UUID, CURRENCY, " +
                    "PURCHASE_LEDGER_TRANSACTION_STORE_TIMESTAMP,TRANSACTION_TYPE, INVENTORY_COS_RELEVANT," +
                    "TRANSACTION_GROUP_ALLOCATION, TRANSACTION_LINE_ALLOCATION) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            // needed for converting a string in java date
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Conversion of a string to a java date
            String dateAsString = purchaseLedgerLine.getProperty("date").toString();
            Date date = sourceFormat.parse(dateAsString);

            // Conversion of a java date to a sql date to store in database
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            preparedStatement.setDate(1, sqlDate);
            preparedStatement.setString(2, purchaseLedgerLine.getProperty("externalAccountingReference").toString());
            preparedStatement.setString(3, purchaseLedgerLine.getProperty("supplierID").toString());
            preparedStatement.setString(4, purchaseLedgerLine.getProperty("description").toString());
            preparedStatement.setString(5, purchaseLedgerLine.getProperty("productKey").toString());
            preparedStatement.setInt(6, Integer.parseInt(purchaseLedgerLine.getProperty("quantity").toString()));
            preparedStatement.setDouble(7, Double.parseDouble(purchaseLedgerLine.getProperty("price").toString()));
            preparedStatement.setString(8, purchaseLedgerLine.getProperty("vatCode").toString());
            preparedStatement.setDouble(9, Double.parseDouble(purchaseLedgerLine.getProperty("vat").toString()));
            preparedStatement.setString(10, String.valueOf(purchaseLedgerLine.getProperty("shippedFromCountry")));
            preparedStatement.setString(11, String.valueOf(purchaseLedgerLine.getProperty("shippedToCountry")));
            preparedStatement.setString(12, String.valueOf(purchaseLedgerLine.getProperty("shippedToChannel")));
            preparedStatement.setString(13, String.valueOf(purchaseLedgerLine.getProperty("transactionLineUUID")));
            preparedStatement.setBoolean(14, Boolean.parseBoolean(purchaseLedgerLine.
                    getProperty("inventoryRelevant").toString()));
            preparedStatement.setInt(15, Integer.parseInt(purchaseLedgerLine.
                    getProperty("internalInvoiceReference").toString()));
            preparedStatement.setInt(16, Integer.parseInt(purchaseLedgerLine.
                    getProperty("internalInvoiceLineReference").toString()));
            preparedStatement.setString(17, String.valueOf(purchaseLedgerLine.getProperty("invoiceUuid")));
            preparedStatement.setString(18, String.valueOf(purchaseLedgerLine.getProperty("currency")));
            preparedStatement.setTimestamp(19, java.sql.Timestamp.from(Instant.now()));
            preparedStatement.setString(20, String.valueOf(purchaseLedgerLine.
                    getProperty("purchaseLedgerTransactionType")));
            preparedStatement.setBoolean(21, Boolean.parseBoolean(
                    purchaseLedgerLine.getProperty("cosRelevant").toString()));
            preparedStatement.setInt(22, Integer.parseInt(purchaseLedgerLine.
                    getProperty("associatedTransactionGroupReference").toString()));
            preparedStatement.setInt(23, Integer.parseInt(purchaseLedgerLine.
                    getProperty("associatedTransactionLineReference").toString()));

            preparedStatement.executeUpdate();
            preparedStatement.close();


        }
        catch (ClassNotFoundException | SQLException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<PurchaseLedgerLine> retrieveTransaction(int internalInvoiceNumber)
    {
        ArrayList<PurchaseLedgerLine> transactionsList = new ArrayList<>();

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT * FROM PURCHASE_LEDGER WHERE " +
                    "INTERNAL_TRANSACTION_REFERENCE_KEY = (?)");

            preparedStatement.setInt(1, internalInvoiceNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                transactionsList.add(toPurchaseLedgerTransactionLine(resultSet));
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }


        return transactionsList;
    }

    public PurchaseLedgerLine retrieveTransactionLine(int internalTransactionReference,
                                                      int internalTransactionLineReference)
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT * FROM PURCHASE_LEDGER WHERE " +
                    "INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "INTERNAL_TRANSACTION_REFERENCE_LINE = (?) AND " +
                    "TRANSACTION_TYPE <> (?)");

            preparedStatement.setInt(1, internalTransactionReference);
            preparedStatement.setInt(2, internalTransactionLineReference);
            preparedStatement.setString(3, String.valueOf(PurchaseLedgerTransactionType.REVERSAL));

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next())
            {
                purchaseLedgerLine = toPurchaseLedgerTransactionLine(resultSet);
            }

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

        return purchaseLedgerLine;
    }

    public int retrieveTransactionGroupQuantity(int internalInvoiceNumber)
    {
        int result = 0;

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SUM (QUANTITY) AS " + "TOTAL_QUANTITY" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "TRANSACTION_TYPE <> (?) AND INVENTORY_RELEVANT = TRUE");

            preparedStatement.setInt(1, internalInvoiceNumber);
            preparedStatement.setString(2 ,String.valueOf(PurchaseLedgerTransactionType.REVERSAL));

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getInt(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }


        return result;
    }

    public int retrieveTransactionLineQuantity(int internInvoiceNumber, int internalInvoiceLineNumber)
    {
        int result = 0;

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SUM (QUANTITY) AS " + "TOTAL_QUANTITY" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "INTERNAL_TRANSACTION_REFERENCE_LINE = (?) AND " +
                    "TRANSACTION_TYPE <> (?) AND INVENTORY_RELEVANT = TRUE");

            preparedStatement.setInt(1, internInvoiceNumber);
            preparedStatement.setInt(2, internalInvoiceLineNumber);
            preparedStatement.setString(3 ,String.valueOf(PurchaseLedgerTransactionType.REVERSAL));


            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getInt(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public String retrieveTransactionLineFromCountry(int internInvoiceNumber, int internalInvoiceLineNumber)
    {String result = "";

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SHIPPED_FROM_COUNTRY AS " + "COUNTRY" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "INTERNAL_TRANSACTION_REFERENCE_LINE = (?)");

            preparedStatement.setInt(1, internInvoiceNumber);
            preparedStatement.setInt(2, internalInvoiceLineNumber);


            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getString(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;}

    public String retrieveTransactionLineToCountry(int internInvoiceNumber, int internalInvoiceLineNumber)
    {
        String result = "";

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SHIPPED_TO_COUNTRY AS " + "COUNTRY" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "INTERNAL_TRANSACTION_REFERENCE_LINE = (?)");

            preparedStatement.setInt(1, internInvoiceNumber);
            preparedStatement.setInt(2, internalInvoiceLineNumber);


            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getString(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public String retrieveTransactionLineToChannel(int internInvoiceNumber, int internalInvoiceLineNumber)
    {
        String result = "";

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SHIPPED_TO_CHANNEL AS " + "CHANNEL" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "INTERNAL_TRANSACTION_REFERENCE_LINE = (?)");

            preparedStatement.setInt(1, internInvoiceNumber);
            preparedStatement.setInt(2, internalInvoiceLineNumber);


            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getString(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public String retrieveTransactionGroupFromCountry(int internInvoiceNumber){
        String result = "";

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SHIPPED_FROM_COUNTRY AS " + "COUNTRY" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?)");

            preparedStatement.setInt(1, internInvoiceNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getString(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public String retrieveTransactionGroupToCountry(int internInvoiceNumber)
    {
        String result = "";

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SHIPPED_TO_COUNTRY AS " + "COUNTRY" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?)");

            preparedStatement.setInt(1, internInvoiceNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getString(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    public String retrieveTransactionGroupToChannel(int internInvoiceNumber)
    {
        String result = "";

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT SHIPPED_TO_CHANNEL AS " + "CHANNEL" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?)");

            preparedStatement.setInt(1, internInvoiceNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getString(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;

    }

    public int internalTransactionNumberGenerator()
    {
        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            PreparedStatement preparedStatement;

            preparedStatement = connection.prepareStatement("SELECT COALESCE (MAX(INTERNAL_TRANSACTION_REFERENCE_KEY),0)" +
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

    public String retrieveProductKey(int internInvoiceNumber, int internalInvoiceLineNumber)
    {
        String result = "";

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT PRODUCT_KEY AS " + "CHANNEL" +
                    " FROM " + TABLE_NAME + " WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "INTERNAL_TRANSACTION_REFERENCE_LINE = (?)");

            preparedStatement.setInt(1, internInvoiceNumber);
            preparedStatement.setInt(2, internalInvoiceLineNumber);


            ResultSet resultSet = preparedStatement.executeQuery();

            if(resultSet.next())
            {
                result = resultSet.getString(1);
            }

            preparedStatement.close();
            resultSet.close();

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return result;
    }

    // Reverses a group of transaction lines (same internal invoice number). Reverse is done by cloning transactions,
    // reversing relevant numeric values, storing the reference of the reversed transaction
    public void reverseTransaction(int internalTransactionNumber)
    {
        if(!isLockedForUpdate(internalTransactionNumber))
        {
            ArrayList<PurchaseLedgerLine> transactionLines = retrieveTransaction(internalTransactionNumber);

            Iterator transactionLinesIterator = transactionLines.iterator();
            while (transactionLinesIterator.hasNext())
            {
                purchaseLedgerLine = (PurchaseLedgerLine) transactionLinesIterator.next();

                String transactionDate = purchaseLedgerLine.getProperty("date").toString();
                transactionDate = transactionDate.substring(8, 10) + "/" + transactionDate.substring(5, 7) + "/" +
                        transactionDate.substring(0, 4);

                purchaseLedgerLine.setProperty("date",transactionDate);
                purchaseLedgerLine.setProperty("purchaseLedgerTransactionType",PurchaseLedgerTransactionType.REVERSAL);
                purchaseLedgerLine.setProperty("inventoryRelevant",false);
                purchaseLedgerLine.setProperty("price",Integer.parseInt(
                        purchaseLedgerLine.getProperty("price").toString()) * -1);
                purchaseLedgerLine.setProperty("vat",Integer.parseInt(
                        purchaseLedgerLine.getProperty("vat").toString()) * -1);
                purchaseLedgerLine.setProperty("quantity",Integer.parseInt(
                        purchaseLedgerLine.getProperty("quantity").toString()) * -1);

                purchaseLedgerLine.setProperty("transactionLineUUID",
                        UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())));

                persistTransactionLine(purchaseLedgerLine);

                //----Inventory COS modifiers
                // Transactions allocated to transaction group (eg. delivery fees)
                if(Integer.parseInt(purchaseLedgerLine.getProperty(
                        "associatedTransactionGroupReference").toString()) !=0 &&
                        Integer.parseInt(purchaseLedgerLine.getProperty(
                                "associatedTransactionLinReference").toString())==0)
                {
                    double unitCos =
                            Double.valueOf(purchaseLedgerLine.getProperty("price").toString()) /
                            retrieveTransactionGroupQuantity(Integer.valueOf(purchaseLedgerLine.
                                    getProperty("associatedTransactionGroupReference").toString()));

                    dbManagerInventory.updateTransactionGroupCos(
                            Integer.parseInt(purchaseLedgerLine.
                                    getProperty("associatedTransactionGroupReference").toString()),unitCos);
                }
                // Transactions allocated to transaction line (eg. product fees)
                else
                {
                    double unitCos = Double.valueOf(purchaseLedgerLine.getProperty("price").toString()) /
                            retrieveTransactionLineQuantity(Integer.valueOf(purchaseLedgerLine.
                                    getProperty("associatedTransactionGroupReference").toString()),
                                    Integer.parseInt(purchaseLedgerLine.getProperty(
                                            "associatedTransactionLineReference").toString()));

                    dbManagerInventory.updateTransactionLineCos(
                            Integer.valueOf(purchaseLedgerLine.
                                    getProperty("associatedTransactionGroupReference").toString()),
                            Integer.valueOf(purchaseLedgerLine.
                                    getProperty("associatedTransactionLineReference").toString()),unitCos);
                }
            }
            lockForUpdate(internalTransactionNumber);
        }
    }

    private PurchaseLedgerLine toPurchaseLedgerTransactionLine(ResultSet resultSet)
    {
        try
        {
            purchaseLedgerLine = new PurchaseLedgerLine(
                    String.valueOf(resultSet.getDate(1)), resultSet.getString(3) , resultSet.getString(2),
                    resultSet.getString(4), resultSet.getString(5), resultSet.getInt(6), resultSet.getDouble(7),
                    resultSet.getDouble(9), resultSet.getString(8),
                    Countries.valueOf(resultSet.getString(10)), Countries.valueOf((resultSet.getString(11))),
                    Channels.valueOf((resultSet.getString(12))), resultSet.getInt(15),
                    resultSet.getInt(16), null,
                    Currencies.valueOf(resultSet.getString(18)),
                PurchaseLedgerTransactionType.valueOf(resultSet.getString(20)),null,null,null,null);

            // cannot be set up in the constructor as the line UUID gets is not a parameter of the constructor
            purchaseLedgerLine.setProperty("transactionLineUUID",java.util.UUID.fromString(resultSet.getString(13)));

            // Values assigned from import process

            purchaseLedgerLine.setProperty("inventoryRelevant",resultSet.getBoolean(14));
            purchaseLedgerLine.setProperty("internalInvoiceReference",resultSet.getInt(16));
            purchaseLedgerLine.setProperty("invoiceUuid",java.util.UUID.fromString(resultSet.getString(17)));
            purchaseLedgerLine.setProperty("cosRelevant",resultSet.getBoolean(22));
            purchaseLedgerLine.setProperty("associatedTransactionGroupReference",resultSet.getInt(23));
            purchaseLedgerLine.setProperty("associatedTransactionLinReference",resultSet.getInt(24));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return purchaseLedgerLine;
    }

    private boolean isLockedForUpdate(int internalTransactionNumber)
    {
        boolean isLocked = false;

        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE " +
                    "INTERNAL_TRANSACTION_REFERENCE_KEY = (?) AND " +
                    "UPDATE_LOCK = TRUE");

            preparedStatement.setInt(1, internalTransactionNumber);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next())
            {
                isLocked = true;
            }

            preparedStatement.close();
            resultSet.close();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
        return isLocked;
    }

    private void lockForUpdate(int internalTransactionNumber)
    {
        if(!isLockedForUpdate(internalTransactionNumber))
        {
            try
            {
                Class.forName(connectionData.getCLASS_FOR_NAME());
                Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

                preparedStatement = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET UPDATE_LOCK = TRUE " +
                        "WHERE INTERNAL_TRANSACTION_REFERENCE_KEY = (?)");

                preparedStatement.setInt(1, internalTransactionNumber);

                preparedStatement.executeUpdate();
                preparedStatement.close();
            }
            catch (ClassNotFoundException | SQLException e)
            {
                e.printStackTrace();
            }
        }
    }


}

