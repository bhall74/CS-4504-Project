package edu.kennesaw.cs4504.test;

import java.util.ArrayList;
public class ServerTest
{
    String ip;
    LinkedList peerList = new LinkedList();
    ArrayList<ServerTest> superPeerList = new ArrayList<ServerTest>();

    //constructor
    ServerTest()
    {

    }

    //prints the peers
    void printPeers()
    {
        peerList.printList();
    }
    //this method adds a super peer to the arraylist of superpeers associated with the System
    void addSuperPeer(ServerTest toAdd)
    {
        superPeerList.add(toAdd);
    }

    //this method adds a peer to the linkedlist of peers associated with this Super Peer
    void addPeer(String name, String IP)
    {
        peerList.addToEnd(name, IP);
    }

    //this method will find and return the IP/Port of a given Client, using the Name
    String findPeer(String Name, int ID)
    {
        String IPtoFind = "";
        //this method searches the peerList, returning the IP if found, else return 0
        IPtoFind = peerList.findIP(Name);
        if(!IPtoFind.equals("0") /*&& !IPtoFind.equals("1")*/)
        {
            return IPtoFind;
        }
        //if the ID = 1, it came from a Super Peer and we dont need to search other Super Peers, so return 0
        if(ID == 1)
        {
            return IPtoFind;
        }

        //if this code runs, IP was not found in this Super Peer, and we must search other Super Peers
        //the for loop goes through the arraylist of each superpeer looking for the client.
        for(int i = 0; i < superPeerList.size(); i++)
        {
            //set the foundIP to the return of the super peer searched
            IPtoFind = superPeerList.get(i).findPeer(Name, 1);
            //if IPtoFind isnt 0, then the IP was found and we need to return
            if(!IPtoFind.equals("0") /*&& !IPtoFind.equals("1")*/)
            {
                return IPtoFind;
            }
        }
        //if the algorithum gets to this line, then the Client was not found in any Super peer, and we must return IPtoFind, which is still "0"
        return IPtoFind;
    }
}
