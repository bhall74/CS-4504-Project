package edu.kennesaw.cs4504.test;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ClientThread extends Thread {
  private ServerSocket fileServer;
  private Socket clientSocket, fileOut;
  private DataOutputStream writer, fileWriter;
  private DataInputStream reader;
  private StringTokenizer commandSplit;
  private File resourceDir;
  private File[] fileList;
  private FileInputStream fileIS;
  private boolean running;
  private String path = "res/";
  private final String HELP_MENU =
  "\t'list' shows a list of files that you can download\n" +
  "\t'find' to find a file name\n" +
  "\t\tusage: find <file name>\n" +
  "\t'send' to download the requested file\n" +
  "\t\tusage: send <file name>";

  public ClientThread(Socket sock) {
    clientSocket = sock;
    resourceDir = new File(path);
    running = true;
  }

  public void run() {
    String input, output;
    try {
      //System.out.println("New Thread started...");
      writer = new DataOutputStream(clientSocket.getOutputStream());
      reader = new DataInputStream(clientSocket.getInputStream());
      while (running) {
        writer.writeUTF("Awaiting commands: ");
        input = reader.readUTF();
        commandSplit = new StringTokenizer(input, " ", true);

        //find the existance of a file
        if (input.startsWith("find")) {
          commandSplit.nextToken();//iterate past "find"
          commandSplit.nextToken();//iterate past " "
          if (commandSplit.hasMoreTokens()) {
            //get the full file name
            String fileName = "";
            while (commandSplit.hasMoreTokens()) {
              fileName += commandSplit.nextToken();
            }
            System.out.println(fileName);

            //send to client the found match - empty string if no match
            if (hasFile(fileName)) {
              writer.writeUTF("Found:\n" + fileName);
            } else {
              writer.writeUTF("file not found");
            }

          } else {
            writer.writeUTF(
              "Did you forget to add the file name?\n" +
              "Use the 'help' command to find commands and usage"
            );
          }
        } else if (input.startsWith("list")) {//list the avaliable files
          fileList = resourceDir.listFiles();
          String list = "";
          for (File file : fileList) {
            list += "\t" + file.getName() + "\n";
          }
          writer.writeUTF("List of files available:\n" + list);

          //send the requested file
        } else if (input.startsWith("send")) {
          commandSplit.nextToken();//iterate past "send"
          commandSplit.nextToken();//iterate past " "
          if (commandSplit.hasMoreTokens()) {
            //get the full file name
            String fileName = "";
            while (commandSplit.hasMoreTokens()) {
              fileName += commandSplit.nextToken();
            }
            System.out.println(fileName);

            if (hasFile(fileName)) {
              System.out.println("found: " + fileName);
              File fileToSend = new File(path + fileName);
              fileIS = new FileInputStream(fileToSend);
              writer.writeUTF(fileName);//send file name to client

              /*
              * Create a new socket to send the file.  This will close after
              * file is sent so that the client does not write any output
              * from the server to the file.
              */
              if (fileServer == null) {
                int port = reader.readInt();//receive port for file send socket
                fileServer = new ServerSocket(port);
              }

              fileOut = fileServer.accept();
              fileWriter = new DataOutputStream(fileOut.getOutputStream());
              byte[] byteArr = new byte[2048];
              int count;
              long t0, t1 = 0L, t = 0L;//time variables for performance analysis
              t0 = System.currentTimeMillis();

              while ((count = fileIS.read(byteArr)) > 0) {
                fileWriter.write(byteArr, 0, count);
                t1 = System.currentTimeMillis();
                t = t1 - t0;//current time elapsed
              }
              fileIS.close();
              fileWriter.close();
              fileOut.close();

              writer.writeUTF("file send complete in " + t + " ms");
            } else {
              writer.writeUTF("file not found");
            }
          } else {
            writer.writeUTF(
              "Did you forget to add the file name?\n" +
              "Use the 'help' command to find commands and usage"
            );
          }
        } else if (input.startsWith("quit")) {
          writer.writeUTF("Quitting session");
          running = false;
        } else if (input.startsWith("help")) {
          writer.writeUTF(HELP_MENU);
        } else {
          writer.writeUTF("Command not recognized...use the help command...");
        }
      }
      fileServer.close();
      clientSocket.close();

    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }

  public boolean hasFile(String name) {
    //compare the files and see if there is a match
    fileList = resourceDir.listFiles();
    for (File file : fileList) {
      if (name.equals(file.getName())) {
        return true;
      }
    }
    return false;
  }
}
