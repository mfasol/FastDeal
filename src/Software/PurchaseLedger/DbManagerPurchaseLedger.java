package Software.PurchaseLedger;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.Enums.Countries;
import Software.Enums.Currencies;
import Software.Enums.PurchaseLedgerTransactionType;
import Software.Enums.SaleChannels;
import Software.Importable;
import Software.Inventory.DbManagerInventoryItems;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.UUID;

/**
 * Created by Michele on 03/11/2015.
 */
public class DbManagerPurchaseLedger implements DbManagerInterface
{
    ConnectionData connectionData = new ConnectionData();
    PurchaseLedgerTransactionLine purchaseLedgerTransactionLine;
    PreparedStatement preparedStatement;
    final String TABLE_NAME = "PURCHASE_LEDGER";
    DbManagerInventoryItems dbManagerInventoryItems = new DbManagerInventoryItems();

    private int internalTransactionNumber = 0;


    @Override
    public void persistTarget(Importable importable)
    {
        purchaseLedgerTransactionLine = (PurchaseLedgerTransactionLine) importable;
        persistTransactionLine(purchaseLedgerTransactionLine);
    }

    private void persistTransactionLine(PurchaseLedgerTransactionLine purchaseLedgerTransactionLine)
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
            String dateAsString = purchaseLedgerTransactionLine.getTransactionDate();
            Date date = sourceFormat.parse(dateAsString);

            // Conversion of a java date to a sql date to store in database
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            preparedStatement.setDate(1, sqlDate);
            preparedStatement.setString(2, purchaseLedgerTransactionLine.getExternalAccountingReference());
            preparedStatement.setString(3, purchaseLedgerTransactionLine.getSupplierID());
            preparedStatement.setString(4, purchaseLedgerTransactionLine.getDescription());
            preparedStatement.setString(5, purchaseLedgerTransactionLine.getProductKey());
            preparedStatement.setInt(6, purchaseLedgerTransactionLine.getQuantity());
            preparedStatement.setDouble(7, purchaseLedgerTransactionLine.getPrice());
            preparedStatement.setString(8, purchaseLedgerTransactionLine.getVatCode());
            preparedStatement.setDouble(9, purchaseLedgerTransactionLine.getVat());
            preparedStatement.setString(10, String.valueOf(purchaseLedgerTransactionLine.getShippedFromCountry()));
            preparedStatement.setString(11, String.valueOf(purchaseLedgerTransactionLine.getShippedToCountry()));
            preparedStatement.setString(12, String.valueOf(purchaseLedgerTransactionLine.getShippedToChannel()));
            preparedStatement.setString(13, String.valueOf(purchaseLedgerTransactionLine.getLineUuid()));
            preparedStatement.setBoolean(14, purchaseLedgerTransactionLine.isInventoryRelevant());
            preparedStatement.setInt(15, purchaseLedgerTransactionLine.getInternalTransactionReference());
            preparedStatement.setInt(16, purchaseLedgerTransactionLine.getInternalInvoiceLineReference());
            preparedStatement.setString(17, String.valueOf(purchaseLedgerTransactionLine.getInvoiceUuid()));
            preparedStatement.setString(18, String.valueOf(purchaseLedgerTransactionLine.getCurrency()));
            preparedStatement.setTimestamp(19, java.sql.Timestamp.from(Instant.now()));
            preparedStatement.setString(20, String.valueOf(purchaseLedgerTransactionLine.
                    getPurchaseLedgerTransactionType()));
            preparedStatement.setBoolean(21, purchaseLedgerTransactionLine.isCosRelevant());
            preparedStatement.setInt(22, purchaseLedgerTransactionLine.getAssociatedTransactionGroupReference());
            preparedStatement.setInt(23,purchaseLedgerTransactionLine.getAssociatedTransactionLineReference());

            preparedStatement.executeUpdate();
            preparedStatement.close();


        }
        catch (ClassNotFoundException | SQLException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<PurchaseLedgerTransactionLine> retrieveTransaction(int internalInvoiceNumber)
    {
        ArrayList<PurchaseLedgerTransactionLine> transactionsList = new ArrayList<PurchaseLedgerTransactionLine>();

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

    public PurchaseLedgerTransactionLine retrieveTransactionLine(int internalTransactionReference,
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
                purchaseLedgerTransactionLine = toPurchaseLedgerTransactionLine(resultSet);
            }

        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }

        return purchaseLedgerTransactionLine;
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
                    "TRANSACTION_TYPE <> (?)");

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
                    "TRANSACTION_TYPE <> (?)");

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
            ArrayList<PurchaseLedgerTransactionLine> transactionLines = retrieveTransaction(internalTransactionNumber);

            Iterator transactionLinesIterator = transactionLines.iterator();
            while (transactionLinesIterator.hasNext())
            {
                purchaseLedgerTransactionLine = (PurchaseLedgerTransactionLine) transactionLinesIterator.next();

                String transactionDate = purchaseLedgerTransactionLine.getTransactionDate();
                transactionDate = transactionDate.substring(8, 10) + "/" + transactionDate.substring(5, 7) + "/" +
                        transactionDate.substring(0, 4);

                purchaseLedgerTransactionLine.setTransactionDate(transactionDate);
                purchaseLedgerTransactionLine.setPurchaseLedgerTransactionType(PurchaseLedgerTransactionType.REVERSAL);
                purchaseLedgerTransactionLine.setPrice(purchaseLedgerTransactionLine.getPrice() * -1);
                purchaseLedgerTransactionLine.setVat(purchaseLedgerTransactionLine.getVat() * -1);
                purchaseLedgerTransactionLine.setQuantity(purchaseLedgerTransactionLine.getQuantity() * -1);

                purchaseLedgerTransactionLine.setLineUuid(UUID.fromString(String.valueOf(new com.eaio.uuid.UUID())));



                persistTransactionLine(purchaseLedgerTransactionLine);


                if(purchaseLedgerTransactionLine.getAssociatedTransactionLineReference()==0)
                {
                    double unitCos = ((purchaseLedgerTransactionLine.getPrice()) /
                            (retrieveTransactionGroupQuantity(purchaseLedgerTransactionLine.
                                    getAssociatedTransactionGroupReference())));

                    dbManagerInventoryItems.updateTransactionGroupCos(
                            purchaseLedgerTransactionLine.getAssociatedTransactionGroupReference(),unitCos);
                }
                else
                {
                    double unitCos = ((purchaseLedgerTransactionLine.getPrice()) /
                            (retrieveTransactionLineQuantity(purchaseLedgerTransactionLine.
                                    getAssociatedTransactionGroupReference(),
                                    purchaseLedgerTransactionLine.
                                            getAssociatedTransactionLineReference())));

                    dbManagerInventoryItems.updateTransactionLineCos(
                            purchaseLedgerTransactionLine.getAssociatedTransactionGroupReference(),
                            purchaseLedgerTransactionLine.getAssociatedTransactionLineReference(),
                            unitCos);
                }
            }
            lockForUpdate(internalTransactionNumber);
        }
    }

    private PurchaseLedgerTransactionLine toPurchaseLedgerTransactionLine(ResultSet resultSet)
    {
        try
        {
            purchaseLedgerTransactionLine = new PurchaseLedgerTransactionLine(
                    String.valueOf(resultSet.getDate(1)), resultSet.getString(3) , resultSet.getString(2),
                    resultSet.getString(4), resultSet.getString(5), resultSet.getInt(6), resultSet.getDouble(7),
                    resultSet.getDouble(9), resultSet.getString(8),
                    Countries.valueOf(resultSet.getString(10)), Countries.valueOf((resultSet.getString(11))),
                    SaleChannels.valueOf((resultSet.getString(12))), resultSet.getInt(15),
                    resultSet.getInt(16), null,
                    Currencies.valueOf(resultSet.getString(18)),
                PurchaseLedgerTransactionType.valueOf(resultSet.getString(20)));

            // cannot be set up in the constructor as the line UUID gets is not a parameter of the constructor
            purchaseLedgerTransactionLine.setLineUuid(java.util.UUID.fromString(resultSet.getString(13)));

            // Values assigned from import process

            purchaseLedgerTransactionLine.setIsInventoryRelevant(resultSet.getBoolean(14));
            purchaseLedgerTransactionLine.setLineNumber(resultSet.getInt(16));
            purchaseLedgerTransactionLine.setInvoiceUuid(java.util.UUID.fromString(resultSet.getString(17)));
            purchaseLedgerTransactionLine.setIsCosRelevant(resultSet.getBoolean(22));
            purchaseLedgerTransactionLine.setAssociatedTransactionGroupReference(resultSet.getInt(23));
            purchaseLedgerTransactionLine.setAssociatedTransactionLinReference(resultSet.getInt(24));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return purchaseLedgerTransactionLine;
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

