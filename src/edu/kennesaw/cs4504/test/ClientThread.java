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
  private final String HELP_MENU =
  "\t'list' shows a list of files that you can download\n" +
  "\t'find' to find a file name\n" +
  "\t\tusage: find <file name>\n" +
  "\t'send' to download the requested file\n" +
  "\t\tusage: send <file name>";

  public ClientThread(Socket sock) {
    clientSocket = sock;
    resourceDir = new File ("res/");
    running = true;
  }

  public void run() {
    String input, output;
    try {
      //System.out.println("New Thread started...");
      while (running) {
        writer = new DataOutputStream(clientSocket.getOutputStream());
        reader = new DataInputStream(clientSocket.getInputStream());
        running = true;
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

              //compare the files and see if there is a match
              fileList = resourceDir.listFiles();
              String match = "";
              File fileToSend = null;
              for (File file : fileList) {
                if (fileName.equals(file.getName())) {
                  match += "\t" + file.getName();
                  fileToSend = file;
                }
              }
              fileIS = new FileInputStream(fileToSend);

              if (fileToSend != null) {
                writer.writeUTF(match);//send file name to client


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
                writer.writeUTF("File not found..");
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
      }
    } catch (IOException ioe) {
      ioe.printStackTrace();
    }
  }
}
