package edu.kennesaw.cs4504.test;

import java.net.*;
import java.io.*;

public class MainClient {
  private String name, strHostIp, strDestIp;
  private int port, superPeerPort;
  private DatagramSocket superPeerSocket;
  private DatagramPacket superPeerSend, superPeerReceive;
  private Socket out;
  private ServerSocket in;
  private InetAddress hostIp, destIp, superPeerIp;
  private PrintWriter writer;
  private BufferedReader reader;
  private byte[] buffer;

  public MainClient(String name, int port) {
    try {
      this.name = name;
      this.port = port;
      superPeerIp = InetAddress.getLocalHost();
      superPeerPort = 8900;

      System.out.println("...can be found at: " + name + " : " + port);

      //testing send name to join super peer
      superPeerSocket = new DatagramSocket(port);
      buffer = name.getBytes();
      superPeerSend = new DatagramPacket(buffer, buffer.length, superPeerIp, superPeerPort);
      superPeerSocket.send(superPeerSend);

      //testing request to find peer
      String message = "Sherman";
      buffer = message.getBytes();
      superPeerSend = new DatagramPacket(buffer, buffer.length, superPeerIp, superPeerPort);
      superPeerSocket.send(superPeerSend);

      message = "Boris";
      buffer = message.getBytes();
      superPeerSend = new DatagramPacket(buffer, buffer.length, superPeerIp, superPeerPort);
      superPeerSocket.send(superPeerSend);

      //testing to make socket connection with found peer


      message = "Quit";
      buffer = message.getBytes();
      superPeerSend = new DatagramPacket(buffer, buffer.length, superPeerIp, superPeerPort);
      superPeerSocket.send(superPeerSend);
      superPeerSocket.close();
    } catch (UnknownHostException e) {
    } catch (SocketException se) {
    } catch (IOException ioe) {
    }
  }

  public static void main(String[] args) {
    System.out.println("Client Started...");
    MainClient mainClient = new MainClient(args[0], Integer.parseInt(args[1]));
  }
}
