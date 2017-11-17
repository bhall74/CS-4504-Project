package edu.kennesaw.cs4504.test;

import java.net.*;
import java.io.*;

public class MainClient {
  private String name, strHostIp, strDestIp;
  private int port, superPeerPort;
  // private DatagramSocket superPeerSocket;
  // private DatagramPacket superPeerSend, superPeerReceive;
  private Socket out, superPeerSocket;
  private ServerSocket in;
  private InetAddress hostIp, destIp, superPeerIp;
  private PrintWriter sockWriter, superPeerWriter;
  private BufferedReader sockReader, superPeerReader, userInput;
  // private byte[] buffer;

  public MainClient(String name, String routerIP, int port) {
    String inputLine = "", outputLine = "";
    try {
      superPeerSocket = new Socket(routerIP, port);
      superPeerWriter = new PrintWriter(superPeerSocket.getOutputStream(), true);
      superPeerReader = new BufferedReader(new InputStreamReader(superPeerSocket.getInputStream()));
      userInput = new BufferedReader(new InputStreamReader(System.in));

      //testing connection with super peer
      superPeerWriter.println(name);
      inputLine = superPeerReader.readLine();
      System.out.println(inputLine);

      while (!outputLine.equalsIgnoreCase("quit")) {
        outputLine = userInput.readLine();
        superPeerWriter.println(outputLine);//request peer
        inputLine = superPeerReader.readLine();
        System.out.println(inputLine);
      }
      superPeerSocket.close();
    } catch (UnknownHostException e) {
    } catch (SocketException se) {
    } catch (IOException ioe) {
    }
  }

  public static void main(String[] args) {
    System.out.println("Client Started...");
    MainClient mainClient = new MainClient(args[0], args[1], Integer.parseInt(args[2]));
  }
}
