# SimpleConcurrentFileServer
Small concurrent file server, using TCPListener

Steps to run

1. put all the class in the bin folder

2. Compile them
	javac class_name.java
	
3. From a command window run MainServer.java with arguements of port number and file directory
		example : java MainServer port_no folder_name
				  java MainServer 99901 D:/MyFolder. Here folder_name is name of folder containing the files to be hosted.
		
4. run Client.java from another seperate command window

	java Client server_name port_no 
	exp : java Client 127.0.0.1 99901
	
	generally if testing on a single machine server_name :localhost
	
5. For multiple clients you can create multiple command windows or run from different computers in the network


Client program Supported Commands:

Supported commands.
	
		i) getavailable - displays the list of al files available on the server
		ii)getfile-
			Copies a file from server to localmachine that the client is running on.
					-getfile "filename_onserver" "filename_to_be_saved_on_loaclmachine"
		iii)quit - Stop the client program
	
		
