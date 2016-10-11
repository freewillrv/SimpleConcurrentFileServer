import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;

class MainServer
{
	public static void main(String[] args)
	{
		if(args.length!=2)
		{
		 System.out.println(" Command Line arguement required, Specify port no and file directory as arguement");
		 System.exit(0);
		}
		else
			{
				System.out.println(" "+args[1]);
				File direc = new File(args[1]);				
				int port_no=0;
				try
				{
					port_no = Integer.parseInt(args[0]);
				}
				catch(NumberFormatException e)
				{
					System.out.println(" Port is not a number ");
					System.exit(-1);
				}
				try
				{
					boolean listening=true;
					ServerSocket server = new ServerSocket(Integer.parseInt(args[0]));
					while (listening)
					    new ServerThread(server.accept(),args[1]).start();
			        server.close();					
				}
				catch(IOException e){
					System.out.println("Cannot Listen at port "+port_no);
					System.exit(-1);
				}
				
			}
	}
}