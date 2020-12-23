import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Message {
    String registerMessage(User user) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(user.getEmail());
        System.out.println(user.getName());
        System.out.println(user.getEmail());
        System.out.println(user.getPassword2());
        System.out.println(user.getPasword1());
        System.out.println(mat.matches());
        if (user.getPasword1().equals(user.getPassword2()) && mat.matches()) {


            //REGISTER Nazwa Email Password
            return "REGISTER " + user.getName() + " " + user.getEmail() + " " + user.getPassword2();
        } else
        {
            return "FALSE";
        }
    }
    //łączenie TCP
    String loginMessage(User user)
    {
        String message;
        //LOGIN Name Password
        if(user.getName()!=null&&user.getPasword1()!=null) {
             message = "LOGIN " + user.getName() + " " + user.getPasword1();
        }
        else
        {
            message = "LOGIN" + " d " + "p";
        }
            return message;

    }
    String callAcceptMessage(String name, String myname, int ports[])
    {
        String messageToServer = "CALLACCEPT " +name  + " " + myname;
     for(int i =0 ;i<4; i++)
     {
        messageToServer =messageToServer + " " + ports[i];
      }




        return messageToServer ;
    }
    String requestFriendMessage(String name, String myname)
    {
        //REQUESTFRIEND NAME
        return "REQUESTFRIEND " + name +" "+ myname;
    }
    String acceptFriendMessage(String name, String myname)
    {

        return "ACCEPTFRIEND "+ name + " " + myname;
    }
    String callMessage(String name, String me)
    {
        String messageToServer = "CALL " +name  + " " + me;

        for(int i =5100; i<=5200;i++)
        {
            if(TransmissionManager.isPortAvailable(i))
            {
                messageToServer = messageToServer + " " +i;
            }
        }
        //////JESZCZE PO STRONIE SERWERA CHLOPIE
        return messageToServer;
    }
    String listFriendMessage(String name)
    {
        return "LISTFRIEND " +name;
    }
    String listRequestMessage(String name)
    {
        return "LISTREQUEST " +name;
    }
    String listDeleteFriends(String name)
    {
        return "LISTDELETEFRIENDS "+name;
    }
    String logOutMessage(String name){return "LOGOUT "+name;}
    String deleteFriendMessage(String friend,String me){return "DELETEFRIEND "+friend+ " "+ me;}
    String changeUploadAudio(String friend, String me, int sample)
    {
        return  "UPLOAD "+friend +" "+me+" "+sample;
    }
    //RESPONDCALL + " " + name +[] + me
}
