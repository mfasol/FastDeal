package DbClient;

import Software.Utilities.RootGui;
import java.io.IOException;
import java.text.ParseException;

/**
 * Created by Michele on 17/10/2015.
 */
public class DbClient
{
    public static void main(String[] args) throws IOException, ParseException
    {
        RootGui rootGui = new RootGui();
        rootGui.createAndShowGUI();
    }
}
