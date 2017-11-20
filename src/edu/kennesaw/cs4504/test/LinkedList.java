//Zane Womack
//Linked list class(also includes Node for use in the list)
//Methods: printList, addToList, searchList, GetIP
package edu.kennesaw.cs4504.test;

class LinkedList
{
	//values to store: Number of nodes, head node, tail node.
	Node head, tail;

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
				return runner.ipAddress + " " + runner.port;
			}
			runner = runner.nextNode;
		}
		return "0";
	}

	//AddToEnd Method, takes in a string, creates a new node, connects the tail to the node, set the new node as the tail
	void addToEnd(String newName, String ip, int newPort, peerThread newThread)
	{
		Node toAdd = new Node(newName, ip, newPort, newThread);
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

	void remove(String nameToRemove)
	{
		Node runner = head;
		Node previous = head;
		while(runner != null)
		{
			if(runner.name.equals(nameToRemove))
			{//remove this node
				if(runner == head && runner != tail)
				{
					head = runner.nextNode;
					return;
				}
				if(runner == tail && runner != head)
				{
					previous.nextNode = null;
					tail = previous;
					return;
				}
				if(runner == head && runner == tail)
				{
					head = null;
					tail = null;
					return;
				}
				previous.nextNode = runner.nextNode;

			}
			previous = runner;
			runner = runner.nextNode;
		}
	}

	//create a runner node, start at head, move to tail, while printing the string value
	void printList() {
		Node runner = head;
		if (head == null)
		{
			System.out.println("No peers connected.");
			return;
		}
		while(runner != null)
		{
			System.out.println("Name: " + runner.name + " IP: " + runner.ipAddress + " Port: " + runner.port);
			runner = runner.nextNode;
		}
	}
}

//The Node class is the object encapsulated in the list
class Node
{
	String name;
	Node nextNode;
	String ipAddress;
	int port;
	peerThread CThread;

	Node()
	{
		nextNode = null;
	}

	Node(String newName, String ip, int newPort,peerThread newThread)
	{

		name = newName;
		ipAddress = ip;
		port = newPort;
		CThread = newThread;
		nextNode = null;
	}
}
