package Software.Products;

import DbServer.ConnectionData;
import DbServer.DbManagerInterface;
import Software.Utilities.Importable;
import com.eaio.uuid.UUID;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Date;

/**
 * Created by Michele on 17/10/2015.
 *
 * Responsibility of this class is to manage the List of Products;
 * This consists in:
 * - persisting products;
 * - Retrieving products;
 * - Deactivating products;
 * - Reactivate Products.
 */
public class DbManagerProducts implements DbManagerInterface
{
    ConnectionData connectionData = new ConnectionData();

    Product product;
    PreparedStatement preparedStatement;
    final String ACTIVE = "Active";
    final String INACTIVE = "Inactive";
    final String TABLE_NAME = "PRODUCT";


    @Override
    public void persistTarget(Importable importable)
    {
        product = (Product) importable;
        this.persistProduct(product);
    }

    private void persistProduct(Product product)
    {


        try
        {
            Class.forName(connectionData.getCLASS_FOR_NAME());
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());

            preparedStatement = connection.prepareStatement("INSERT INTO " + TABLE_NAME + "(ASIN, EAN, " +
                    "SELLER_SKU, ITEM_NAME, MANUFACTURER,OPEN_DATE,STATUS,PRODUCT_KEY,STORE_TIMESTAMP, STORE_UUID) " +
                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

            preparedStatement.setString(1, product.getAsin());
            preparedStatement.setString(2, product.getEan());
            preparedStatement.setString(3, product.getSellerSku());
            preparedStatement.setString(4, product.getItemName());

            // needed for converting a string in java date
            DateFormat sourceFormat = new SimpleDateFormat("dd/MM/yyyy");

            // Conversion of a string to a java date
            String dateAsString = product.getOpenDate();
            Date date = sourceFormat.parse(dateAsString);

            // Conversion of a java date to a sql date to store in database
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());

            preparedStatement.setString(5,product.getManufacturer());
            preparedStatement.setDate(6, sqlDate);
            preparedStatement.setString(7, "Active");
            preparedStatement.setString(8, product.getProductKey());
            preparedStatement.setTimestamp(9,java.sql.Timestamp.from(Instant.now()));

            UUID uuid = new UUID();
            preparedStatement.setString(10, String.valueOf(uuid));

            preparedStatement.executeUpdate();
            preparedStatement.close();
            DbManagerProducts db = new DbManagerProducts();

        }
        catch (ClassNotFoundException | SQLException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    public Product retrieveTarget(String asin)
    {

        return this.retrieveProduct(asin);
    }

    private Product retrieveProduct(String asin)
    {
        // Processing SQL statements with JDBC
        try
        {
            // step 1: establishing a connection
            Class.forName(connectionData.getCLASS_FOR_NAME());

            // step 2: create a preparedStatement
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());
            preparedStatement = connection.prepareStatement("SELECT * FROM " + TABLE_NAME + " WHERE ASIN = " +
                    "(?) AND STATUS = 'Active'");
            preparedStatement.setString(1, asin);

            // step 3: execute the query
            ResultSet resultSet = preparedStatement.executeQuery();
            if(resultSet.next())
            {
                toProduct(resultSet);
            }
            // Step: close connection
            resultSet.close();
            preparedStatement.close();

        }
        catch (ClassNotFoundException | SQLException e) { e.printStackTrace();}

        return product;
    }

    public void deactivateProduct(String asin) {
        this.amendProductStatus(asin,INACTIVE,ACTIVE);
    }

    public void reactivateProduct(String asin) {
        this.amendProductStatus(asin,ACTIVE,INACTIVE);
    }

    public void amendProductStatus(String asin, String newStatus, String oldStatus)
    {
        try
        {
            // step 1: establishing a connection
            Class.forName(connectionData.getCLASS_FOR_NAME());

            // step 2: create a preparedStatement
            Connection connection = DriverManager.getConnection(connectionData.getCONNECTION_PATH());
            preparedStatement = connection.prepareStatement("UPDATE " + TABLE_NAME + " SET STATUS = (?) " +
                    "WHERE ASIN = (?) AND STATUS = (?)");
            preparedStatement.setString(1, newStatus);
            preparedStatement.setString(2, asin);
            preparedStatement.setString(3, oldStatus);

            // step 3: execute query;
            preparedStatement.executeUpdate();

            // step 4: close statement;
            preparedStatement.close();
        }
        catch (ClassNotFoundException | SQLException e)
        {
            e.printStackTrace();
        }
    }

    private Product toProduct(ResultSet resultSet)
    {
        try
        {
            product = new Product(resultSet.getString("ASIN"),resultSet.getString("EAN"),
                    resultSet.getString("SELLER_SKU"),resultSet.getString("ITEM_NAME"),
                    resultSet.getString("OPEN_DATE"),resultSet.getString("MANUFACTURER"));
            product.setStatus(resultSet.getString("STATUS"));

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return product;
    }
}
