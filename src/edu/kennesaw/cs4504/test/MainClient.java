package edu.kennesaw.cs4504.test;

import java.net.*;
import java.io.*;

public class MainClient {
  private String logicalName, strHostIp, strDestIp;
  private int clientPort, superPeerPort, serverPort;
  // private DatagramSocket superPeerSocket;
  // private DatagramPacket superPeerSend, superPeerReceive;
  private Socket peerSocket, superPeerSocket;
  private ServerSocket serverSocket;
  private InetAddress hostIP, destIp, superPeerIP;
  private PrintWriter sockWriter, superPeerWriter;
  private BufferedReader sockReader, superPeerReader, userInput;
  // private byte[] buffer;

  public MainClient(String name, int port) {
    logicalName = name;
    clientPort = port;
    String inputLine = "", outputLine = "";

    try {
      userInput = new BufferedReader(new InputStreamReader(System.in));
      serverSocket = new ServerSocket(port);
      ClientThread ct = new ClientThread(serverSocket);//thread for awaiting socket connection
      ct.start();
      //testing connection with super peer
      System.out.println("Give address for server router");
      superPeerIP = InetAddress.getByName(userInput.readLine());
      System.out.println("Give port for server router");
      superPeerPort = Integer.parseInt(userInput.readLine());

      superPeerSocket = new Socket(superPeerIP, superPeerPort);
      superPeerWriter = new PrintWriter(superPeerSocket.getOutputStream(), true);
      superPeerReader = new BufferedReader(new InputStreamReader(superPeerSocket.getInputStream()));

      superPeerWriter.println(name);//send logical name
      superPeerWriter.println(clientPort);//send port number
      inputLine = superPeerReader.readLine();
      System.out.println(inputLine);//read response


      while (!outputLine.equalsIgnoreCase("quit")) {
        System.out.println("Enter name of peer to find, or quit to end: ");
        outputLine = userInput.readLine();//prompt for finding peer
        superPeerWriter.println(outputLine);//request peer

        //find peer (blocking)
        inputLine = superPeerReader.readLine();
        System.out.println(inputLine);//display return


        if (!inputLine.equals("Could not find peer")) {
          //split inputline into address and port number
          String[] addressComponents = inputLine.split(":");//addr:port
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
    MainClient mainClient = new MainClient(args[0], Integer.parseInt(args[1]));
  }
}
