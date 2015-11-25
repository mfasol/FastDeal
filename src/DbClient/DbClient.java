package DbClient;




import Software.Enums.Countries;
import Software.RootGui;
import com.eaio.uuid.UUID;

import javax.swing.*;
import java.io.IOException;
import java.text.ParseException;


/**
 * Created by Michele on 17/10/2015.
 */
public class DbClient
{

    public static void main(String[] args) throws IOException, ParseException
    {
        JFrame rootGui = new RootGui();
        rootGui.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        rootGui.setVisible(true);

    }
}
