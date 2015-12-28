/**
 * Created by Benjamin on 28/12/15.
 */
public interface RoutingInterface {
    //send message from sender to recipient with specific time to live and max amount of hops
    public void sendMessage(String sender, String reciever, int ttl, int hops);

    //same as sendMsg just vice versa
    public void recieveMessage(String sender, String reciever, int ttl, int hops);
}
