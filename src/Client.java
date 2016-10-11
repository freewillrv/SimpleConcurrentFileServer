/*

	Client program.
	
	Steps to run the client.

		i) compile Client.java in commmand prompt
		ii) pass the name of the server ( or the ip address ) and the port no as command line arguements 
			example. 
				javac Client.java
				java Client name_of_server 9999
					or
				java Client 192.168.168.22 9999
			where 192.168.168.22 is the ip address of the server
		
	Supported commands.
	
		i) getavailable - displays the list of al files available on the server
		ii)getfile-
			Copies a file from server to localmachine that the client is running on.
					-getfile "filename_onserver" "filename_to_be_saved_on_loaclmachine"
		iii) quit - Stop the client program
	

*/

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.Socket;
import java.net.SocketException;
import java.io.*;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.io.*;

public class Client 
{

  // Function write a string to a given file with current time stamp
  public static void Log(String filename,String content)
    {
		  OutputStream os=null;
		  DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		  Calendar cal = Calendar.getInstance();    
		  try
		  {
			 os = new FileOutputStream(filename,true);
			 PrintWriter pw = new PrintWriter(os);
			 pw.println("\t Time : "+dateFormat.format(cal.getTime())+content);
			 pw.println();
			 pw.close();
			 os.close();
		  }
		  catch (FileNotFoundException e1)
		  {		
			e1.printStackTrace();
			return;
		  }        	
		  catch(IOException e)
		  {		
			System.out.println(" Log File writing error , check access is not set to read only");
		  }
	}
  

  public static void main(String[] argv) throws Exception
  {
	
	// Checking for correct no of arguements
	if(argv.length!=2){			
		 System.out.println(" Pass name of server and port number where server listens as arguements in the command line");
		 System.exit(-1);
		}
	else
	{
		String server_name = argv[0];
		int port_no = Integer.parseInt(argv[1]);
		Socket server = null;
		
		try{		
			Log("hw6.txt"," Client :Attempting to connect to Server : "+server_name +" , at port : "+port_no);
			server = new Socket(server_name, port_no);			
			Log("hw6.txt"," Connected ");
		}
		catch(UnknownHostException err){
			// Unknown Host
			System.out.println(" Unknown Host");			
			Log("hw6.txt"," Client : Unknown Host error "+err.getMessage());			
			System.exit(-1);
		}
		catch(IOException err){
			//IOException
			System.out.println(" IO Exception");			
			Log("hw6.txt"," Client : IO Exception "+err.getMessage());			
			System.exit(-1);
		}
		BufferedReader br  = new BufferedReader(new InputStreamReader(System.in));
		BufferedReader reader = new BufferedReader(new InputStreamReader(server.getInputStream()));
		OutputStream writer = server.getOutputStream();
		PrintWriter pw = new PrintWriter(writer,true);
		while(true)
		{			
			
			System.out.println(" Client : Waiting for command");
			String command = br.readLine();			
			String arguements[] = command.split("\\ ");
			//String arguements[]={"getfile","abc.txt","c:/a.txt"};
			// Checking correctness of format
			/*
			 *  Choice 1 for getavailable
			 *  	   2 for getfile
			 *  	   3 quit
			 * 
			 * */
			if(arguements.length!=1 && arguements.length!=3)
				System.out.println(" Wrong format of command check read me file"+arguements.length+arguements);
			else{					
					if(arguements[0].equalsIgnoreCase("getavailable")){
						// Get file list from server
						//InputStream reader = sock.getInputStream();	
						Log("hw6.txt"," Client : querying for available files on server ");						
						byte b = 1;
						writer.write(b);
						String s="";					
						while((s = reader.readLine()).length()!=0 )	
							System.out.println(s);					
						Log("hw6.txt"," Client : query sucessfull ");						
					}
					else if (arguements[0].equalsIgnoreCase("getfile"))
						{
							// Copy file from server to user specified file
							if(arguements.length!=3)
							{
								System.out.println(" getfile takes 2 arguements, refer to readme file");
								Log("hw6.txt"," Client : Incorrect arguements to getfile ");	
								continue;						
							}
								
							byte b = 2;
							writer.write(b);
							String serverFile = arguements[1];
							String localFileName = arguements[2];
							System.out.println("Getting File "+serverFile);
							int nob;
							// Setting buffer array with size of 1024 bytes
							final int BUFF_SIZE = 1024;
							byte[] mybytearray = new byte[BUFF_SIZE];
							Log("hw6.txt"," Client : querying for requested file on server ");	
							pw.println(serverFile);// Querying server for file
							String answer = reader.readLine();
							if(answer.equalsIgnoreCase("OK"))
							{
								Log("hw6.txt"," Client : File Found on server ");	
								// File found on server, start receiving file
								System.out.println("File found on server, local file "+localFileName);
								OutputStream osf = new FileOutputStream(localFileName,true);
								BufferedInputStream bis = new BufferedInputStream(server.getInputStream());						
								while(true){
									try{
									   nob = bis.read(mybytearray, 0, mybytearray.length);       
									   if(nob!=-1)// Signifies some bytes read if nob != -1
										   osf.write(mybytearray, 0, nob);
									   osf.flush();
									   if(nob< mybytearray.length)
										   {								    
											osf.close();
											break;	 						 
										   }
									}
									catch(SocketException e)
									{
									 osf.close();
									 Log("hw6.txt","Client : Socket Closed "+e.getMessage());
									}
									catch(IOException e){
									// Server  has finished sending data, close file stream
										osf.close();								
									}
									
								}
								Log("hw6.txt","\n\n Client : File writing completed ");	
								
							}
							else
								{
								 System.out.println("\n\n File Not found on server");
								 Log("hw6.txt","Client : Requested file not found on server");
								}
						}
								
						else if(arguements[0].equalsIgnoreCase("quit"))
									{
										Log("hw6.txt"," Client : Closing connection with server ");	
										// Send bye bye , and close all ports
										System.out.println(" Bye Bye User");
										writer.close();
										reader.close();
										server.close();						
										System.exit(0);
									}
				}
					
			
		}		
		
	}
  }
}
