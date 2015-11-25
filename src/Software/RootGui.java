package Software;

import Software.Products.ProductImport;
import Software.Products.DbManagerProducts;
import Software.PurchaseLedger.TransactionLineImport;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Michele on 01/11/2015.
 *
 * Initial GUI for the Fast Deal software
 *
 */
public class RootGui extends JFrame
{
    private static final int FRAME_WIDTH = 400;
    private static final int FRAME_HEIGHT = 250;
    private JLabel rateLabel;
    private JButton importProductsButton;
    private JButton importInvoiceLines;



    public RootGui() throws IOException, ParseException
    {
        createTextField();
        createImportProductsButton();
        createRetrieveProductButton();
        createPanel();
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
    }

    private void createTextField()
    {
        rateLabel = new JLabel("Import Products: ");
    }
    private void createImportProductsButton() throws IOException, ParseException
    {
        ProductImport productImport = new ProductImport();
        importProductsButton = new JButton("Select File");

        // lambda function to create actionListener

        importProductsButton.addActionListener((ActionEvent importProducts) -> { productImport.importData() ;});
    }

    private void createRetrieveProductButton()
    {
        TransactionLineImport transactionLineImport = new TransactionLineImport();
        importInvoiceLines = new JButton("Import Invoice Product");

        // lambda function to create actionListener
        DbManagerProducts dbManagerProducts = new DbManagerProducts();
        importInvoiceLines.addActionListener((ActionEvent importInvoiceLines) -> {

            transactionLineImport.importData();
        });
    }

    private void createPanel()
    {
        JPanel panel = new JPanel();
        panel.add(rateLabel);
        panel.add(importProductsButton);
        panel.add(importInvoiceLines);
        add(panel);
    }

}
