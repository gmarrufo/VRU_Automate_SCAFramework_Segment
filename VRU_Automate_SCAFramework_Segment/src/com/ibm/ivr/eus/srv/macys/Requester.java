package com.ibm.ivr.eus.srv.macys;

import java.io.*;
import java.net.*;

public class Requester{
	Socket requestSocket;
	ObjectOutputStream out;
 	ObjectInputStream in;
 	public String message;
 	
	public Requester(){}
	
	public int run(String automation_server, String[] automation_port, String automation_string){
		int iResult = 0;
		
		try{
			// Creating a socket to connect to the server
			// requestSocket = new Socket("localhost", 2004);
			// System.out.println("Connected to localhost in port 2004");
			requestSocket = new Socket(automation_server, Integer.parseInt(automation_port[0]));
			System.out.println("Connected to " + automation_server + "in port " + automation_port[0]);
			
			// Get Input and Output streams
			out = new ObjectOutputStream(requestSocket.getOutputStream());
			out.flush();
			in = new ObjectInputStream(requestSocket.getInputStream());
			
			// Communicating with the server
			do{
				try{
					// message = (String)in.readObject();
					// System.out.println("server>" + message);
					// sendMessage("Hi my server");
					// message = "bye";
					sendMessage(automation_string);
					message = (String)in.readObject();
					System.out.println("server>" + message);
				}catch(ClassNotFoundException classNot){
					System.err.println("data received in unknown format");
					iResult = 50;
				}
			}while(!message.equals("bye"));
		}catch(UnknownHostException unknownHost){
			System.err.println("You are trying to connect to an unknown host!");
			iResult = 51;
		}catch(IOException ioException){
			ioException.printStackTrace();
			iResult = 51;
		}finally{
			// Closing connection
			try{
				in.close();
				out.close();
				requestSocket.close();
			}catch(IOException ioException){
				ioException.printStackTrace();
			}
			iResult = 51;
		}
		
		return iResult;
	}
	
	public void sendMessage(String msg){
		try{
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		}catch(IOException ioException){
			ioException.printStackTrace();
		}
	}
	
	/*
	public static void main(String args[])
	{
		Requester client = new Requester();
		client.run();
	}
	*/
}