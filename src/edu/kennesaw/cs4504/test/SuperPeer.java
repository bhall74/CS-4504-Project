//Zane Womack
//Distributed Computing
//This class is the super Peer for our P2P network
package edu.kennesaw.cs4504.test;
import java.io.InputStreamReader;
import java.io.*;
import java.net.*;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.Scanner;

public class SuperPeer
{
    //The linked lists of the peers and SuperPeers
    private static LinkedList peers = new LinkedList();
    private static ArrayList<peerThread> superPeers = new ArrayList<peerThread>();

    // The Super socket.
    private static ServerSocket superPeerSocket = null;
    // The peer socket.
    private static Socket peerSocket = null;

    public static void main(String args[])
    {
        boolean running = true;
        Scanner reader = new Scanner(System.in);
        //The default port number and host.
        int portNumber = 5555;

        System.out.println("Enter a port number to listen on: ");
        portNumber = reader.nextInt();

        String host = "";
        try
        {
            host = InetAddress.getLocalHost().getHostAddress();
        }
        catch (UnknownHostException e)
        {
            System.err.println("Unknown localhost, How is this Possible?!?)");
        }
        System.out.println("Host Address: "+ host + "  Now using port number = " + portNumber);
        //create a Server Socket
        try
        {
            superPeerSocket = new ServerSocket(portNumber);
        }
        catch (IOException e)
        {
            System.out.println(e);
        }

        //launch a thread for the server console
        new consoleThread(superPeers, peers).start();

        //create a thread for each Peer connecting
        while(running)
        {
            try
            {
                peerSocket = superPeerSocket.accept();
                new peerThread(peerSocket, superPeers, peers).start();
            }
            catch (IOException e)
            {
                System.out.println(e);
            }
        }

        //this is run when the server shuts down
    }

}

//this serves to run the SuperPeers Console
class consoleThread extends Thread
{
    private LinkedList peers = null;
    private ArrayList<peerThread> SuperPeers = null;
    Scanner reader = new Scanner(System.in);

    public consoleThread(ArrayList<peerThread> SP, LinkedList P)
    {
        peers = P;
        SuperPeers = SP;
    }

    public void run()
    {
        boolean running = true;
        String command = "";
        while(running)
        {
            command = "";
            System.out.println("Awating commands: ");
            command = reader.nextLine();
            if(command.equalsIgnoreCase("print peers"))
            {
                peers.printList();
            }
            else if(command.equalsIgnoreCase("print super peers"))
            {
                if(SuperPeers.size() == 0)
                    System.out.println("I am the only Super Peer");
                else
                {
                    for(int i = 0; i < SuperPeers.size(); i++)
                    {
                        System.out.println("Super Peer Name: "+SuperPeers.get(i).getName());
                    }
                }
            }

            //the connect command connects super peers with each other    connect ipaddress port
            else if(command.startsWith("connect"))
            {
                String ipAddress = "";
                int newPort = 1;
                StringTokenizer tokens = new StringTokenizer(command);
                tokens.nextToken();//this gets rid of connect
                if(tokens.hasMoreTokens())
                    ipAddress = tokens.nextToken();
                else
                    System.out.println("Command not recognized: "+ command);
                if(tokens.hasMoreTokens())
                    newPort = Integer.parseInt(tokens.nextToken());
                else
                    System.out.println("Command not recognized: "+ command);

                //we have the IP and port of the new Super peer, time to make a connection

                SuperConnection superConnect = new SuperConnection(ipAddress, newPort, peers);
            }
            else
                System.out.println("Command not recognized: "+ command);
        }
    }
}

//this is for threading the Super peer connecting to other Superpeers
class SuperConnection implements Runnable
{

    private LinkedList peers;
    private static Socket superSocket = null;
    private static PrintStream out = null;
    private static DataInputStream in = null;
    private static BufferedReader inputLine = null;
    private static boolean closed = false;//this variable lets the while loop gracfully close

    SuperConnection(LinkedList p){peers = p;}

    SuperConnection(String host, int portNumber, LinkedList p)
    {
        peers = p;
        try
        {
            superSocket = new Socket(host, portNumber);
            inputLine = new BufferedReader(new InputStreamReader(System.in));
            out = new PrintStream(superSocket.getOutputStream());
            in = new DataInputStream(superSocket.getInputStream());
        }
        catch (UnknownHostException e)
        {
            System.err.println("Unknown host " + host);
        }
        catch (IOException e)
        {
            System.err.println("Couldn't get I/O for the connection to the host "+ host);
        }

        //the socket has been created and we can write info to the SuperPeer
        if (superSocket != null && out != null && in != null)
        {
                String peerInput = "";
                // Create a thread to read from the SuperPeer.
                new Thread(new SuperConnection(p)).start();
        }
    }

    //this is a thread for the client
    public void run()
    {
        String responseLine;
        String found = "";
        try
        {
            while ((responseLine = in.readLine()) != null)
            {
                //this is the line that takes in a response form the SuperPeer
                if(responseLine.startsWith("Are you a"))
                {
                    out.println("superpeer");
                }

                if(responseLine.startsWith("Super Find"))
                {
                    String toFind = "";
                    StringTokenizer token = new StringTokenizer(responseLine);
                    token.nextToken();//gets rind of "super"
                    if (token.hasMoreTokens())
                    {
                        token.nextToken();//this gets rid of "find"
                        if (token.hasMoreTokens())
                        {
                            toFind = token.nextToken();//toFind is the name of the peer we are looking for
                        }
                        else
                            out.println("0");
                    }
                    else//these else statements prevent a null pointer is the user enters the find command improperly
                        out.println("0");

                    found = peers.findIP(toFind);
                    out.println(found);
                }

                //logic here
            }
            closed = true;//kill the loop in the Main
        }
        catch (IOException e)
        {
            System.err.println("IOException:  " + e);
        }
    }
}






//Thread class runs the IO stream for connected peers
class peerThread extends Thread
{
    private DataInputStream is = null;
    private PrintStream os = null;
    private Socket peerSocket = null;
    private LinkedList peers = null;
    private ArrayList<peerThread> SuperPeers = null;

    PrintStream getOS()
    {
        return os;
    }
    DataInputStream getIS()
    {
        return is;
    }

    public peerThread(Socket peerSocket, ArrayList<peerThread> SP, LinkedList P)
    {
        peers = P;
        SuperPeers = SP;
        this.peerSocket = peerSocket;
    }

    public void run()
    {
        try
        {
         // Create input and output streams for this peer.
            is = new DataInputStream(peerSocket.getInputStream());
            os = new PrintStream(peerSocket.getOutputStream());

            String response;
            boolean identified = false;
            boolean isPeer = true;

            while(!identified)
            {
                os.println("Are you a peer or a Super Peer?");
                response = is.readLine().trim();
                if(response.equalsIgnoreCase("peer"))
                {
                    identified = true;
                }
                else if(response.equalsIgnoreCase("super peer") || response.equalsIgnoreCase("superpeer"))
                {
                    identified = true;
                    isPeer = false;
                }
                else
                {
                    os.println("Unknown response, please try again.");
                }
            }

            String command;
            String name = "";
            if(isPeer)//this contains all the commands and logic avaliable to peers
            {
                //these values are needed for adding peers
                int listeningPort;

                //when the type is found, take in necessary information such as the Name and port
                os.println("Hello new Peer, what is your name?");
                name = is.readLine().trim();
                os.println("What port are you listening for other peers on?");
                listeningPort = Integer.parseInt(is.readLine().trim());

                //adding this peer to the list of Peers
                peers.addToEnd(name, peerSocket.getInetAddress().getHostAddress(), listeningPort, this);
                os.println("Hello " + name + ", you have been added to the Peer Network.");

                //now that the Peer/SuperPeer staus is established, wait for input;
                while (true)
                {
                    command = is.readLine();
                    //"command" is what the peer/super peer is telling this super peer to do

                    //the peer or is trying to find the IP of a peer
                    if (command.startsWith("find "))
                    {
                        String toFind = "";
                        String foundIPandPort = "0";
                        StringTokenizer token = new StringTokenizer(command);
                        if (token.hasMoreTokens())
                        {
                            token.nextToken();//this gets rid of "find"
                            if (token.hasMoreTokens())
                            {
                                toFind = token.nextToken();//toFind is the name of the peer we are looking for
                                System.out.println("looking for: " + toFind);
                            }
                            else
                                os.println("unknown command: " + command);
                        }
                        else//these else staements prevent a null pointer is the user enters the find command improperly
                            os.println("unknown command: " + command);

                        //call the method to search the linked list, implemented in linked list class
                        foundIPandPort = peers.findIP(toFind);

                        if(!foundIPandPort.equals("0"))
                        {
                            os.println(foundIPandPort);
                            //if the found value does not equal 0, we found it, return the value
                        }
                        else //we didnt find the value, we must now go through all other super peers
                        {
                            for (int i = 0; i < SuperPeers.size(); i++)
                            {// go through the super peers until we find the value
                                SuperPeers.get(i).getOS().println("Super Find " + toFind);
                                foundIPandPort = SuperPeers.get(i).getIS().readLine().trim();
                                if (!foundIPandPort.equals("0"))
                                {//if we find the value, break from the for loop
                                    i = SuperPeers.size();
                                }
                            }//return the found value, which will either be 0 or the IP
                            os.println(foundIPandPort);

                        }
                    }

                    //if the peer is turning off
                    else if (command.startsWith("/quit"))
                    {
                        peers.remove(name);
                        break;
                    }
                    else
                        os.println("Command not recognized: " + command);
                }
            }

            else//this is a super peer we are talking to
            {
                SuperPeers.add(this);
                //logic for collecting super peer info
                os.println("Hello " + name + ", you have been added to the Super Peer Network.");

                while(true){}
            }

            os.println("Goodbye " + name);

     //thread is ready to be terminated, begin closing the input and output and socket
            is.close();
            os.close();
            peerSocket.close();
        }
        catch (IOException e)
        {
            System.out.println(e);
        }
    }
}
