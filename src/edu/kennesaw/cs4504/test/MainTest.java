package edu.kennesaw.cs4504.test;

import java.net.*;
import java.io.*;

public class MainTest {
  public static void main(String[] args) {
    ServerTest server1, server2;
    InetAddress server1IP, server2IP, peerIP;
    int server1Port, server2Port;
    DatagramPacket server1PeerReceive, server2PeerReceive, superPeer1Send, superPeer2Send;
    DatagramSocket server1PeerSocket, server2PeerSocket;
    byte[] s1Buffer, s2Buffer;

    //make super peers
    server1 = new ServerTest();
    server2 = new ServerTest();
    server1.addSuperPeer(server2);
    server2.addSuperPeer(server1);
    try {
      //make UDP sockets
      server1PeerSocket = new DatagramSocket(8900);
      server2PeerSocket = new DatagramSocket(8901);

      //make UDP packets for accepting peers to add and requests
      s1Buffer = new byte[256];
      server1PeerReceive = new DatagramPacket(s1Buffer, s1Buffer.length);
      s2Buffer = new byte[256];
      server2PeerReceive = new DatagramPacket(s2Buffer, s2Buffer.length);

      //test accepting joining peers
      server1PeerSocket.receive(server1PeerReceive);
      peerIP = server1PeerReceive.getAddress();
      String message = new String(server1PeerReceive.getData(), 0, server1PeerReceive.getLength());
      System.out.println(
        "Message received from : " +
        peerIP + "\n message:\n" +
        message
      );
      server1.addPeer(message, peerIP.toString());

      //test finding peers upon request
      boolean running = true;
      System.out.println(InetAddress.getLocalHost().toString());
      server2.addPeer("Sherman", InetAddress.getLocalHost().toString());
      server1.addPeer("Boris", InetAddress.getLocalHost().toString());
      server1.printPeers();
      server2.printPeers();

      while (running) {
        s1Buffer = new byte[256];
        server1PeerReceive = new DatagramPacket(s1Buffer, s1Buffer.length);
        server1PeerSocket.receive(server1PeerReceive);
        peerIP = server1PeerReceive.getAddress();
        message = new String(server1PeerReceive.getData(), 0, server1PeerReceive.getLength());
        System.out.println(
          "Message received from : " +
          peerIP + "\n message:\n" +
          message
        );
        System.out.println(server1.findPeer(message, 0));
        if (message.equalsIgnoreCase("quit")) {
          running = false;
        }
      }

      server1PeerSocket.close();
    } catch (SocketException se) {

    } catch (IOException ioe) {

    }

  }
}
