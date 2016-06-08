package Software.Utilities;


/**
 * Created by Michele on 06/06/2016.
 */
public class CheckNull
{
    public CheckNull(){};

    public String checkNull(Object checkable, Object returnable) {
        if (checkable!=null && checkable != ""){
            return checkable.toString();
        }
        else
        {
            return returnable.toString();
        }
    }

}
