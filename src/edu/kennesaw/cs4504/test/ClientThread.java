package edu.kennesaw.cs4504.test;

import java.io.*;
import java.net.*;

public class ClientThread extends Thread {
  private ServerSocket socket;
  private Socket clientSocket;
  private PrintWriter writer;
  private BufferedReader reader;

  public ClientThread(ServerSocket sock) {
    socket = sock;
  }

  public void run() {
    String input, output;
    try {
      System.out.println("New Thread started...");
      clientSocket = socket.accept();
      writer = new PrintWriter(clientSocket.getOutputStream(), true);
      reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
      input = reader.readLine();
      System.out.println("From client: " + input);
      output = input + " echo echo";
      writer.println(output);
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
