Routing Project

This project should demonstrate, how routing can be realized by using the Shark Framework. For this purpose, we created a 
simple routing interface with two methods:

public void sendMessage(PeerSemanticTag sender, PeerSemanticTag receiver, String message, int ttl, int hops) throws SharkException, IOException;
public void sendMessage(PeerSemanticTag sender, PeerSemanticTag receiver, byte[] message, int ttl, int hops) throws SharkException, IOException;

The user can choose which one he wants to use. If you just want to send a simple message, you can use the first one. If
you want to encrypt the message before sending, you should use the second one. 

Note: In case you need end2end encryption, you have to do it on your own! The current version only supports session encryption 
using the ShawkFW encryption. For an end2end encryption test, a simple XOR method was implemented.

How does this routing work?

Every RoutingPeer has a contactlist and a Routing KnowledgePort. The contactlist contains others Peers we can connect to. 
The KnowledgePort is responsible for handling our incoming messages and forwarding these to all known peers. On top of that,
the KnowledgePort has a "TTL-Checker". This one will check all messages in a specified time intervall. If the time to live
value is exceeded, the message will be removed. It's just for keeping our KnowledgeBase clean.

Example Usage:

At first we create our Routing Peers:

RoutingPeer alice = new RoutingPeer("alice", "http://sharksystems.net/alice", "tcp://localhost:7000", 7000);
RoutingPeer bob = new RoutingPeer("bob", "http://sharksystems.net/bob", "tcp://localhost:7001", 7001);
RoutingPeer chris = new RoutingPeer("chris", "http://sharksystems.net/chris", "tcp://localhost:7002", 7002);

Then we add some peers to the contactlist of each peer:

alice.routingKP.addPeerContact(bob);
bob.routingKP.addPeerContact(chris);

Finally we send the message (to chris in this case):

alice.routingKP.sendMessage(alice.getOwnPeerTag(), chris.getOwnPeerTag(), "hallo", RoutingKP.TTL_HOUR, RoutingKP.MAX_HOPS);

So what is happening now?

Alice's KnowledgePort creates the message, we use a knowledge for this purpose, containing some informations like timestamp,
message content, message id etc.. For further information please look it up in "RoutingKP.java". On top of that we use a SemanticTag
as flag, so the KnowledgePort knows, wether this message needs to be routed or not.

If Alice doesn't know Chris, the message gets forwarded to every known peer in our contact list, hopefully anyone of them knows Chris.
If Alice knows about Chris, the message will be send directly to Chris and no one else. The message will be send to Bob now.

Before the message gets forwarded by the receiver (Bob), the KnowledgePort checks if the time to live value is exceeded.
This is simply done by calculating the time difference between "now" and the message creation time. Furthermore the hop count
will be checked. One hop is equivalent to one peer, who forwarded this message. This value is decremented each time, the message
gets forwarded by a peer. This TTL and hop check should prevent an endless message sending cycle. If both tests are passed, 
the KnowledgePort checks if we already processed / forwarded this message. This is achieved by extracting all messages from our
local KnowledgeBase. If the message id was found in one of them and no new contact was added in the meantime, the forwarding process
will be cancelled. If it's a new message or a new contact was added, it will get stored in our KnowledgeBase and forwarded... 
The KnowledgePort will notify our peer, if there is a new incoming message. (See RoutingKPListener.java)

In our example, Alice doesn't know chris. So Alice has just one option, contacting Bob. Luckily for Alice, Bob knows Chris.
All Bob has to do now, is sending this message to Chris. So Alice can communicate with Chris, without even knowing him.



