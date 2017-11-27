package edu.kennesaw.cs4504.test;

import java.net.*;
import java.io.*;
import java.util.StringTokenizer;
import java.util.Scanner;

public class Peer {
  private String logicalName, strHostIp, strDestIp;
  private int clientPort, superPeerPort, serverPort;
  private Socket peerSocket, superPeerSocket, fileSock;
  private ServerSocket serverSocket;
  private InetAddress hostIP, destIp, superPeerIP;
  private PrintWriter superPeerWriter;
  private BufferedReader superPeerReader;
  private Scanner userInput;
  private DataOutputStream sockWriter;
  private DataInputStream sockReader, fileRead;
  private StringTokenizer commandSplit;
  private FileOutputStream fileOS;

  private final String HELP_MENU =
      "Use the following commands:\n" +
      "\t'quit' exits this session and terminates this peer\n" +
      "\t'find' to find a peer\n" +
      "\t\tusage: find <peer name>\n" +
      "\t'help' shows a list of commands\n";

  // private byte[] buffer;

  public Peer() {
    String inputLine = "", outputLine = "";
    serverPort = 0;
    try {
      /*
      *The peer will start a new thread that waits for a connection from other peers
      *Meanwhile this thread will communicate with the super peer
      */

      userInput = new Scanner(System.in);//std in for input from user

      //testing connection with super peer
      System.out.println("Give address for server router");
      superPeerIP = InetAddress.getByName(userInput.next());
      System.out.println("Give port for server router");
      while (!userInput.hasNextInt()) {
        System.out.println("this is not a valid port number");
        String validInput = userInput.next();
      }
      superPeerPort = userInput.nextInt();


      //if connection is successful - caught in try/catch
      superPeerSocket = new Socket(superPeerIP, superPeerPort);
      superPeerWriter = new PrintWriter(superPeerSocket.getOutputStream(), true);
      superPeerReader = new BufferedReader(new InputStreamReader(superPeerSocket.getInputStream()));

      //initial connection correspondence with super peer
      //read response (confirm connection)
      //asks peer type (auto respond)
      inputLine = superPeerReader.readLine();
      superPeerWriter.println("peer");

      //asks peer name
      inputLine = superPeerReader.readLine();
      System.out.println(inputLine);

      //respond with peer name
      logicalName = userInput.next();
      superPeerWriter.println(logicalName);

      //ask peer port for peer/peer connection
      inputLine = superPeerReader.readLine();
      System.out.println(inputLine);

      //send port number
      while (!userInput.hasNextInt()) {
        System.out.println("this is not a valid port number");
        String validInput = userInput.next();
      }

      clientPort = userInput.nextInt();
      superPeerWriter.println(clientPort + "");

      //confirm peer added to network
      inputLine = superPeerReader.readLine();
      System.out.println(inputLine);

      //server socket for other peers that search for this peer and need to use its service
      serverSocket = new ServerSocket(clientPort);
      ClientSocketThread cst = new ClientSocketThread(serverSocket);//thread for awaiting socket connection
      cst.start();
      //clear the scanner for commands
      userInput = new Scanner(System.in);

      //super peer is now waiting for commands
      while (true) {
        //prompt for finding peer (blocking)
        System.out.println("Enter a command: ");
        outputLine = userInput.nextLine();
        commandSplit = new StringTokenizer(outputLine);

        //logic for various commands
        if (outputLine.startsWith("find")) {
          commandSplit.nextToken();//iterate to name of peer
          if (commandSplit.hasMoreTokens()) {//if a name is included
            //request peer
            superPeerWriter.println(outputLine);

            //get super peer response (blocking)
            inputLine = superPeerReader.readLine();
            System.out.println(inputLine);//display return

            //if the peer was found - begin correspondence
            if (!inputLine.equals("0")) {
              //split inputline into address and port number
              String[] addressComponents = inputLine.split(" ");//addr:port
              serverPort = Integer.parseInt(addressComponents[1]);
              strDestIp = addressComponents[0];
              System.out.println(strDestIp + ", " + serverPort);

              //create peer socket
              peerSocket = new Socket(strDestIp, serverPort);
              sockReader = new DataInputStream(peerSocket.getInputStream());
              sockWriter = new DataOutputStream(peerSocket.getOutputStream());

              //socket communication with found peer
              while ((inputLine = sockReader.readUTF()) != null) {
                System.out.println(inputLine);//display intial read from server peer
                outputLine = userInput.nextLine();
                if (outputLine.startsWith("help")) {
                  System.out.println(HELP_MENU);
                  sockWriter.writeUTF(outputLine);
                } else if (outputLine.startsWith("send")) {
                  sockWriter.writeUTF(outputLine);
                  inputLine = sockReader.readUTF();//response to check
                  if (!inputLine.equalsIgnoreCase("file not found")) {
                    //creating directory and file
                    String fileName = inputLine;
                    String path = "dest/";
                    if (!(new File(path).exists())) {
                      new File(path).mkdir();
                    }
                    path += fileName;
                    File file = new File(path);
                    file.createNewFile();
                    fileOS = new FileOutputStream(file);

                    if (fileSock == null) {
                      //specify a port to receive the file
                      System.out.println("Please specify a port to receive the file:");
                      while (!userInput.hasNextInt()) {
                        System.out.println("this is not a valid port number");
                        String validInput = userInput.nextLine();
                      }
                      serverPort = userInput.nextInt();
                      sockWriter.writeInt(serverPort);
                    }

                    fileSock = new Socket(strDestIp, serverPort);
                    fileRead = new DataInputStream(fileSock.getInputStream());
                    byte[] inputByteArr = new byte[2048];
                    int count = 0;
                    while ((count = fileRead.read(inputByteArr)) > 0) {
                      fileOS.write(inputByteArr, 0, count);
                    }
                    fileOS.close();
                  } else {
                    System.out.println(inputLine);
                  }
                } else if (outputLine.startsWith("quit")) {
                  sockWriter.writeUTF(outputLine);
                  inputLine = sockReader.readUTF();
                  System.out.println(inputLine);
                  fileRead.close();
                  peerSocket.close();
                  superPeerSocket.close();
                  fileSock.close();
                  break;
                } else {
                  sockWriter.writeUTF(outputLine);
                }

                inputLine = sockReader.readUTF();
                System.out.println(inputLine);
              }
            } else {
              System.out.println("Peer not found...");
            }
          } else {
            System.out.println(
              "Did you forget to add the name of the peer you're looking for?\n\t" +
              "Try using the 'help' command..."
            );
          }
        } else if (outputLine.startsWith("help")) {
          System.out.println(HELP_MENU);
        } else if (outputLine.startsWith("quit")) {
          superPeerWriter.println(outputLine);
          superPeerSocket.close();
          System.exit(0);
        } else {
          System.out.println("Command: " + outputLine + " not recognized...try using the 'help' command...");
        }
      }
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
