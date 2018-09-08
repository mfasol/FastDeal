package Software.Utilities;

import Software.Products.ProductImport;
import Software.PurchaseLedger.PurchaseLedgerImport;
import Software.SalesLedger.SalesLedgerImport;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.stream.Stream;
import java.util.stream.Collector;

import static java.util.stream.Collectors.toList;


/**
 * Created by Michele on 01/11/2015.
 *
 * Initial GUI for the Fast Deal software
 *
 */
public class RootGui
{
    private static final int FRAME_WIDTH = 800;
    private static final int FRAME_HEIGHT = 250;
    private JButton importProductsButton;
    private JButton importPurchaseLedger;
    private JButton importSalesLedger;

    public RootGui() throws IOException, ParseException {}

    public void createAndShowGUI() throws IOException, ParseException
    {
        JFrame frame = new JFrame("FastDeal Software");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        createImportProductsButton();
        createPurchaseLedgerImportButton();
        createSalesLedgerImportButton();
        addComponentsToPane(frame.getContentPane());
        frame.pack();
        frame.setVisible(true);
        frame.setSize(FRAME_WIDTH, FRAME_HEIGHT);
        frame.setResizable(false);
    }

    private void createImportProductsButton() throws IOException, ParseException
    {
        ProductImport productImport = new ProductImport();
        importProductsButton = new JButton("Import Products");

        // lambda function to create actionListener
        importProductsButton.addActionListener((ActionEvent importProducts) -> productImport.importData());
    }

    private void createPurchaseLedgerImportButton()
    {
        PurchaseLedgerImport purchaseLedgerImport = new PurchaseLedgerImport();
        importPurchaseLedger = new JButton("Import Purchases");

        // lambda function to create actionListener
        importPurchaseLedger.addActionListener((ActionEvent importInvoiceLines) -> purchaseLedgerImport.importData());
    }

    private void createSalesLedgerImportButton()
    {
        SalesLedgerImport salesLedgerImport = new SalesLedgerImport();
        importSalesLedger = new JButton("Import Sales");

        importSalesLedger.addActionListener((ActionEvent importSales) -> salesLedgerImport.importData());

    }

    private void addComponentsToPane(Container pane)
    {
        pane.setComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(1,1,1,1);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 0;
       
        pane.add(importProductsButton,c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 1;
        pane.add(importPurchaseLedger,c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 0.5;
        c.weighty = 0.5;
        c.gridx = 0;
        c.gridy = 2;
        pane.add(importSalesLedger,c);

        c.fill = GridBagConstraints.BOTH;
        c.weightx = 6;
        c.weighty = 2;
        c.gridx = 1;
        c.gridy = 0;
        c.gridheight = 3;
        c.gridwidth = 1;
//        Object[][] input = {{1},{"fhcufheufwunfr"},{2},{2},{2},{2},{2},{"fhcufheufww"},{2},{2},{2},{"fhcufheufwu"}};
//        Object[][] output = FilteringUniqueElements(input);
//        JTable dataTable = new JTable(output, new String[]{"Import Data"});
//        JScrollPane scrollPane =
//                new JScrollPane(dataTable,JScrollPane.VERTICAL_SCROLLBAR_ALWAYS,JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
//        dataTable.getColumn("Import Data").setPreferredWidth(600);
//
//        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
//        pane.add(scrollPane,c);
    }

    private Object[][] FilteringUniqueElements (Object[][] input){

        Stream objStream =  Arrays.stream(input).flatMap(Arrays::stream);
//        List<Object> output =
//                objStream.
//                map(x -> new Object[] {x})
//                .collect(toList());
//
        objStream.distinct().forEach(System.out::println);
        Object[][] output = input;


        return output;

    }
}
