package edu.kennesaw.cs4504.test;

import java.io.*;
import java.net.*;
import java.util.StringTokenizer;

public class ClientThread extends Thread {
  private ServerSocket socket;
  private Socket clientSocket;
  private DataOutputStream writer;
  private DataInputStream reader;
  private StringTokenizer commandSplit;
  private File resourceDir;
  private File[] fileList;
  private boolean running;
  private final String HELP_MENU =
  "\t'list' shows a list of files that you can download\n" +
  "\t'find' to find a file name\n" +
  "\t\tusage: find <file name>\n" +
  "\t'send' to download the requested file\n" +
  "\t\tusage: send <file name>";

  public ClientThread(ServerSocket sock) {
    socket = sock;
    resourceDir = new File ("res/");
  }

  public void run() {
    String input, output;
    try {
      //System.out.println("New Thread started...");
      while (true) {
        clientSocket = socket.accept();//blocking
        writer = new DataOutputStream(clientSocket.getOutputStream());
        reader = new DataInputStream(clientSocket.getInputStream());
        running = true;
        while (running) {
          writer.writeUTF("Awaiting commands: ");
          input = reader.readUTF();
          commandSplit = new StringTokenizer(input, " ", true);
          if (input.startsWith("find")) {//find the existance of a file
            commandSplit.nextToken();//iterate past "find"
            commandSplit.nextToken();//iterate past " "
            if (commandSplit.hasMoreTokens()) {
              //get the full file name
              String fileName = "";
              while (commandSplit.hasMoreTokens()) {
                fileName += commandSplit.nextToken();
              }
              System.out.println(fileName);

              //compare the files and see if there is a match
              fileList = resourceDir.listFiles();
              String match = "";
              for (File file : fileList) {
                if (fileName.equals(file.getName())) {
                  match += "\t" + file.getName();
                }
              }
              //send to client the found match - empty string if no match
              writer.writeUTF("Found:\n" + match);
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
          } else if (input.startsWith("send")) {//send the requested file
            commandSplit.nextToken();//iterate past "send"
            commandSplit.nextToken();//iterate past " "
            if (commandSplit.hasMoreTokens()) {
              //get the full file name
              String fileName = "";
              while (commandSplit.hasMoreTokens()) {
                fileName += commandSplit.nextToken();
              }
              System.out.println(fileName);

              //compare the files and see if there is a match
              fileList = resourceDir.listFiles();
              String match = "";
              File fileToSend = null;
              for (File file : fileList) {
                if (fileName.equals(file.getName())) {
                  match += "\t" + file.getName();
                  fileToSend = file;
                  //System.out.println(fileToSend.toString());
                }
              }

              if (fileToSend != null) {
                writer.writeUTF("Found:\n" + match);
                int length = (int)fileToSend.length();
                writer.writeInt(length);
                ServerSocket fileServer = new ServerSocket(4444);
                Socket fileOut = fileServer.accept();
                FileInputStream fileIS = new FileInputStream(fileToSend);
                DataOutputStream fileWriter = new DataOutputStream(fileOut.getOutputStream());
                byte[] byteArr = new byte[2048];
                int count;
                while ((count = fileIS.read(byteArr)) > 0) {
                  System.out.println("count: " + count);
                  fileWriter.write(byteArr, 0, count);
                }
                fileIS.close();
                fileWriter.close();
                fileOut.close();
                fileServer.close();
                writer.writeUTF("file send complete");
              } else {
                writer.writeUTF("No file found");
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
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
