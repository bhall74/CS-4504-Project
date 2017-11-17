package edu.kennesaw.cs4504.test;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SThread extends Thread {
    //private Object[][] RTable; // routing table
    private ServerTest clientList;
    private PrintWriter out, outTo; // writers (for writing back to the machine and to destination)
    private BufferedReader in; // reader (for reading from the machine connected to)
    private String inputLine, outputLine, destination, addr; // communication strings
    private Socket outSocket; // socket for communicating with a destination
    private int ind; // indext in the routing table

    // Constructor
    SThread(ServerTest list, Socket toClient) throws IOException {
        out = new PrintWriter(toClient.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
        clientList = list;
        addr = toClient.getInetAddress().getHostAddress();
        outSocket = toClient;
        // RTable[index][0] = addr+":"+Integer.toString(toClient.getPort()); // IP addresses
        // RTable[index][1] = toClient; // sockets for communication
        // ind = index;
        // for (Object[] row : RTable) {
        //     if (row[0] == null) continue;
        //     String a = (String) row[0];
        //     Socket s = (Socket) row[1];
        //     String ip = s.getInetAddress().getHostAddress();
        //     String port = Integer.toString(s.getPort());
        //     System.out.println(String.format("%10s : %10s %10s", a, ip, port));
        // }
    }

    // Run method (will run for each machine that connects to the ServerRouter)
    public void run() {
        try {
            // Initial sends/receives
            inputLine = in.readLine(); //get the name of the client trying to join
            clientList.addPeer(inputLine, addr);
            out.println("connected to router");
            // inputLine = in.readLine(); // initial read (the destination for writing)
            // System.out.println("Forwarding to " + inputLine);
            // out.println("Connected to the router."); // confirmation of connection

            // waits 10 seconds to let the routing table fill with all machines' information
            // try {
            //     System.out.println("WAITING...");
            //     this.sleep(30000);
            //     System.out.println("FINISHED!");
            // } catch (InterruptedException ie) {
            //     System.out.println("Thread interrupted");
            // }

            // loops through the routing table to find the destination

            // for (int i = 0; i < 10; i++) {
            //     if (destination.equals(RTable[i][0])) {
            //         outSocket = (Socket) RTable[i][1]; // gets the socket for communication from the table
            //         System.out.println("Found destination: " + destination);
            //         outTo = new PrintWriter(outSocket.getOutputStream(), true); // assigns a writer
            //     }
            // }

            // Communication loop
            while ((inputLine = in.readLine()) != null) {
              System.out.println("Looking for peer: " + inputLine);
              outputLine = clientList.findPeer(inputLine, 0);
              if (!outputLine.equals("0")) {
                out.println(outputLine);
              } else {
                out.println("Could not find peer: " + inputLine);
              }
              if (inputLine.equalsIgnoreCase("quit")) {
                break;
              }
            //     System.out.println("Client/Server said: \"" + inputLine +"\"");
            //     if (inputLine.equals("Bye.")) { // exit statement
            //         System.out.println("FOUND BYE!!!");
            //         outTo.println("Bye.");
            //         out.println("Bye.");
            //         System.out.println("SENT BYE!!!");
            //         break;
            //     }
            //     outputLine = inputLine; // passes the input from the machine to the output string for the destination
            //
            //     if (outSocket != null) {
            //         outTo.println(outputLine); // writes to the destination
            //     }
            }// end while
        }// end try
        catch (IOException e) {
            System.err.println("Could not listen to socket.");
            System.exit(1);
        } finally {
            System.out.println("FINISHED!!!");
        }
    }
}
