import javax.swing.*;
import java.io.File;
import java.io.FileWriter;

//Zane Womack
//Linked list class(also includes Node for use in the list)
//Methods: printList, addToList, searchList, GetIP
class LinkedList
{
	//values to store: Number of nodes, head node, tail node.
	Node head, tail;

	//Constructor: Take in the first instruction, set it to the head node and tail node
	LinkedList(String newName, String ip/*add port number arg*/)
	{
		// TODO add accessor methods and add parameter to node class as well
		head = new Node(newName, ip);
		tail = head;
	}

	//default constructor
	LinkedList()
	{
		head = null;
		tail = null;
	}
	
	Node getHead()
	{
		return head;
	}

	String findIP(String toFind)
	{
		Node runner = head;
		while(runner!=null)
		{
			if(runner.name.equals(toFind))
			{
				return runner.ipAddressPort;
			}
			runner = runner.nextNode;
		}
		return "0";
	}

	//AddToEnd Method, takes in a string, creates a new node, connects the tail to the node, set the new node as the tail
	void addToEnd(String newName, String ip)
	{
		Node toAdd = new Node(newName, ip);
		//if there is no head(list is empty) make the new node the head
		if(head == null)
		{
			head = toAdd;
			tail = toAdd;
		}
		else //there is a Head, so add to end(after tail)
		{
			tail.nextNode = toAdd;
			tail = toAdd;
		}
	}
	//create a runner node, start at head, move to tail, while printing the string value
	void printList()
	{
		Node runner = head;
		while(runner != null)
		{
			System.out.println(runner.name + " " + runner.ipAddressPort);
			runner = runner.nextNode;
		}
	}
}

//The Node class is the object encapsulated in the list
class Node
{
	String name;
	Node nextNode;
	String ipAddressPort;
	
	Node()
	{
		name = null;
		nextNode = null;
	}

	Node(String newName, String ip)
	{
		name = newName;
		ipAddressPort = ip;
		nextNode = null;
	}
}