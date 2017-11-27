package edu.kennesaw.cs4504.test;

import java.io.*;
import java.net.*;

public class ClientSocketThread extends Thread {
  private ServerSocket serverSock;
  private Socket clientSock;
  private ClientThread ct;
  public ClientSocketThread(ServerSocket ss) {
    serverSock = ss;
  }

  public void run() {
    try {
      while (true) {
        clientSock = serverSock.accept();
        ct = new ClientThread(clientSock);
        ct.start();
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
