package Software.Utilities;

import javax.swing.*;
import javax.swing.plaf.FileChooserUI;
import javax.swing.plaf.basic.BasicFileChooserUI;
import java.io.File;
import java.util.NoSuchElementException;

/**
 * Created by Michele on 17/10/2015.
 */
public class FileImportManager
{
    public static File loadDialog() throws NoSuchElementException
    {
        File f = null;
        JFileChooser selectFile = new JFileChooser();
        int returnValue = selectFile.showOpenDialog(null);
        if(returnValue==JFileChooser.APPROVE_OPTION)
        {
            f = selectFile.getSelectedFile();
        }
        return f;
    }

}
