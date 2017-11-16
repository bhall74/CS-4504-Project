package edu.kennesaw.cs4504.test;

import java.util.Scanner;
public class main
{
    public static void main(String [] args)
    {
        Scanner reader = new Scanner(System.in);

        //Instantiate the SuperPeers
        ServerTest server1 = new ServerTest();
        ServerTest server2 = new ServerTest();
        ServerTest server3 = new ServerTest();

        //give the other super peers to each other
        server1.addSuperPeer(server2);
        server1.addSuperPeer(server3);
        server2.addSuperPeer(server1);
        server2.addSuperPeer(server3);
        server3.addSuperPeer(server1);
        server3.addSuperPeer(server2);

        server1.addPeer("Zane1","123:123");
        server1.addPeer("Zane2","456:456");
        server1.addPeer("Zane3","789:789");
        server2.addPeer("Stella1","abc:abc");
        server2.addPeer("Stella2","zxc:zxc");
        server2.addPeer("Stella3","vbn:vbn");
        server3.addPeer("Robin1","asd:asd");
        server3.addPeer("Robin2","fgh:fgh");
        server3.addPeer("Robin3","jkl:jkl");
        server1.addPeer("Brandt1","qwe:qwe");
        server2.addPeer("Brandt2","rty:rty");
        server3.addPeer("Brandt3","uio:uio");

        server1.printPeers();
        server2.printPeers();
        server3.printPeers();

        String input = "";
        while(!input.equals("quit"))
        {
            System.out.println("Enter a Client Name to search for:");
            input = reader.nextLine();
            System.out.println(server3.findPeer(input, 0));
        }
    }
}
