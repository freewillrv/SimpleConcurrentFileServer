import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;


/*
 * 
 * 
 * Accepts directory name and the port no for listening as command line arguements.
 * 
 * Commands accepted
 * 
 * 	quit
 * 
 * 
 * 
 * 
 */

public class ServerThread extends Thread{	


  Socket client;  
  String homeDirec;
  
  public ServerThread(Socket client,String homeDirec)
  {
   this.client = client;  
   this.homeDirec = homeDirec;
   Log("hw6_server_log.txt","Server : Client Accepted "+client.getInetAddress().getHostName());
  }
	

  // Function write a string to a given file
  public static void Log(String filename,String content){
    OutputStream os=null;
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
    Calendar cal = Calendar.getInstance();    
	try {
		 os = new FileOutputStream(filename,true);
		 PrintWriter pw = new PrintWriter(os);
		 pw.println("\t Time : "+dateFormat.format(cal.getTime())+content);
		 pw.println();
		 pw.close();
		 os.close();
		}
	catch (FileNotFoundException e1) {		
		e1.printStackTrace();
		return;
		}        
	
	catch(IOException e){		
		System.out.println(" Log File writing error , check access is not set to read only");
		}
  }
  
  public void run()
  {
	boolean keep_working=true;
    File direc = new File(homeDirec);
    String filesPresent[] = direc.list();
    InputStreamReader reader=null;
    OutputStream writer = null;
    PrintWriter pw =null;
    try
    {
	    reader = new InputStreamReader((client.getInputStream()));	    
		System.out.println(" client connected : "+client.getInetAddress().getHostName());    	 
		writer = client.getOutputStream();
		pw = new PrintWriter(writer,true);
    }
    catch(IOException e){
    	System.out.println(" Error in communicating with client");
    	Log("hw6_server_log.txt","Server :Error communicating with client " + client.getInetAddress().getHostName());
    	return;
    }
    
    while (keep_working)
	{
    	try
		{	      
			while(client.isConnected())
			{    
				int b = reader.read();
				switch(b)
				{
				// Means client sent command "getavailable"
				 case 1:
				 Log("hw6_server_log.txt","Server : Request for available file names from " + client.getInetAddress().getHostName());   		  
					 
					for(int i=0;i<filesPresent.length;i++)
						pw.println(filesPresent[i]);					
					// Tells the client to stop displaying filenames
					pw.println("");
				 break;	    	   
				 // 2 for getfile	    	   
				  case 2:	    	
					  BufferedReader br = new BufferedReader(reader);
					  String clientFile = br.readLine();
					  Log("hw6_server_log.txt","Server : Request copying of file  : "+clientFile+" by " + client.getInetAddress().getHostName());
					  
					  // Checking if file exists on server
					  boolean fileFound=false;
					  for(int i=0;i<filesPresent.length;i++)
						if(filesPresent[i].equals(clientFile))
						{
							pw.println("OK");							
							fileFound= true;
							break;
						}
						if(fileFound)
						{
							// Send the file to the client	    			  	    				
							// Setting buffer array with size of 1024 bytes
							final int BUFF_SIZE = 1024;
							byte[] mybytearray = new byte[BUFF_SIZE];
							BufferedInputStream bis = new BufferedInputStream(new FileInputStream(homeDirec+"/"+clientFile));
							// NOB store no of bytes read
							int nob;
							writer = client.getOutputStream();
							// Writing file to client
							while(true)
							{
							 nob = bis.read(mybytearray, 0, mybytearray.length);       
							if(nob!=-1)// Signifies some bytes read
							   writer.write(mybytearray, 0, nob);
							writer.flush();
							if(nob< mybytearray.length)
							   {						    
								bis.close();						    
								break;	 						 
							   }
							}
							Log("hw6_server_log.txt","Server : File  : "+clientFile+" succesfully copied by " + client.getInetAddress().getHostName());
						}
						else
							pw.println("NO");
				  break;	
					  
				  default:
					  // For quit
					  reader.close();
					  writer.close();
					  client.close();
					  Log("hw6_server_log.txt","Server : Closing session with : " + client.getInetAddress().getHostName());
					  keep_working = false;
				  break;
				}    	
			}      
	     
		}
		catch(Exception e){}
    }
  }
}