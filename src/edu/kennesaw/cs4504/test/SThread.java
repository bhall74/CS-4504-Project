package edu.kennesaw.cs4504.test;

import java.io.*;
import java.net.*;
import java.util.Arrays;

public class SThread extends Thread {
    //private Object[][] RTable; // routing table
    private ServerTest clientList;
    private PrintWriter out; // writers (for writing back to the machine and to destination)
    private BufferedReader in; // reader (for reading from the machine connected to)
    private String inputLine, outputLine, addr, name, port; // communication strings
    private Socket outSocket; // socket for communicating with a destination

    // Constructor
    SThread(ServerTest list, Socket toClient) throws IOException {
        out = new PrintWriter(toClient.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(toClient.getInputStream()));
        clientList = list;
        addr = toClient.getInetAddress().getHostAddress();
        outSocket = toClient;
    }

    // Run method (will run for each machine that connects to the ServerRouter)
    public void run() {
        try {
          // Initial sends/receives
          name = in.readLine(); //get the name of the client trying to join
          port = in.readLine(); //get port number (String)
          addr += ":" + port;
          clientList.addPeer(name, addr);
          clientList.printPeers();
          out.println("connected to router");

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
