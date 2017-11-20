package edu.kennesaw.cs4504.test;

import java.net.*;
import java.io.*;

public class Peer {
  private String logicalName, strHostIp, strDestIp;
  private int clientPort, superPeerPort, serverPort;
  private Socket peerSocket, superPeerSocket;
  private ServerSocket serverSocket;
  private InetAddress hostIP, destIp, superPeerIP;
  private PrintWriter sockWriter, superPeerWriter;
  private BufferedReader sockReader, superPeerReader, userInput;
  // private byte[] buffer;

  public Peer() {

    String inputLine = "", outputLine = "";

    try {
      /*
      *The peer will start a new thread that waits for a connection from other peers
      *Meanwhile this thread will communicate with the super peer
      */

      userInput = new BufferedReader(new InputStreamReader(System.in));//std in for input from user

      //testing connection with super peer
      System.out.println("Give address for server router");
      superPeerIP = InetAddress.getByName(userInput.readLine());
      System.out.println("Give port for server router");
      superPeerPort = Integer.parseInt(userInput.readLine());

      //if connection is successful - caught in try/catch
      superPeerSocket = new Socket(superPeerIP, superPeerPort);
      superPeerWriter = new PrintWriter(superPeerSocket.getOutputStream(), true);
      superPeerReader = new BufferedReader(new InputStreamReader(superPeerSocket.getInputStream()));

      //initial connection correspondence
      inputLine = superPeerReader.readLine();//read response (confirm connection)
      System.out.println(inputLine);
      outputLine = userInput.readLine();
      superPeerWriter.println(outputLine);//respond with peer type
      inputLine = superPeerReader.readLine();//asks peer name
      System.out.println(inputLine);
      logicalName = userInput.readLine();
      superPeerWriter.println(logicalName);//respond with peer name
      inputLine = superPeerReader.readLine();//ask peer port for peer/peer connection
      System.out.println(inputLine);
      clientPort = Integer.parseInt(userInput.readLine());
      superPeerWriter.println(clientPort);//send port number
      inputLine = superPeerReader.readLine();//confirm peer added to network
      System.out.println(inputLine);

      //server socket for other peers that search for this peer and need to use its service
      serverSocket = new ServerSocket(clientPort);
      ClientThread ct = new ClientThread(serverSocket);//thread for awaiting socket connection
      ct.start();

      //super peer is now waiting for commands
      while (!outputLine.equalsIgnoreCase("quit")) {
        //prompt for finding peer (blocking)
        System.out.println("Enter name of peer to find, or quit to end: ");
        outputLine = userInput.readLine();
        superPeerWriter.println("find " + outputLine);//request peer

        //get super peer response (blocking)
        inputLine = superPeerReader.readLine();
        System.out.println(inputLine);//display return


        if (!inputLine.equals("0")) {
          //split inputline into address and port number
          String[] addressComponents = inputLine.split(" ");//addr:port
          serverPort = Integer.parseInt(addressComponents[1]);
          strDestIp = addressComponents[0];
          System.out.println(strDestIp + ", " + serverPort);

          //create peer socket
          peerSocket = new Socket(strDestIp, serverPort);
          sockReader = new BufferedReader(new InputStreamReader(peerSocket.getInputStream()));
          sockWriter = new PrintWriter(peerSocket.getOutputStream(), true);

          //socket communication with found peer
          System.out.println("Send a message...");
          outputLine = userInput.readLine();
          sockWriter.println(outputLine);
          inputLine = sockReader.readLine();
          System.out.println("From server: " + inputLine);
          outputLine = "quit";
        }
      }
      superPeerSocket.close();
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (SocketException se) {
      se.printStackTrace();
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public static void main(String[] args) {
    System.out.println("Client Started...");
    Peer mainClient = new Peer();
  }
}
