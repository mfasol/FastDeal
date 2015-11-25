package DbServer;

import java.sql.Connection;

/**
 * Created by Michele on 11/11/2015.
 */
public class ConnectionData
{
    private final String CLASS_FOR_NAME = "org.h2.Driver";
    private final String CONNECTION_PATH =  "jdbc:h2:file:/Users/Michele/Dropbox/IdeaProjects" +
            "/JavaProjects/FastDeal/FastDeal";


    public String getCLASS_FOR_NAME()
    {
        return CLASS_FOR_NAME;
    }

    public String getCONNECTION_PATH()
    {
        return CONNECTION_PATH;
    }
}
