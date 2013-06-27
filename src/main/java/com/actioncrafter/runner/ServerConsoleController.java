package com.actioncrafter.runner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

public class ServerConsoleController extends Thread {

	private static final String SERVER_CMD = "/usr/local/bin/bash";
	
	public static BufferedReader inp;
    public static BufferedWriter out;
    
    private volatile boolean serverRunning = false;
    
    List<ServerConsoleListener> consoleListeners = new ArrayList<ServerConsoleListener>();
    
    @Override
    public void run()
    {
    	startServer();
    }
    
    synchronized public void addListener(ServerConsoleListener listener)
    {
    	consoleListeners.add(listener);
    }
    
    synchronized public void removeListener(ServerConsoleListener listener)
    {
    	consoleListeners.remove(listener);
    }
    
    public void stopServer()
    {
    	serverRunning = false;
    	for (ServerConsoleListener l : consoleListeners) 
		{
    		l.serverStopped();
		}
    }
    
	public void startServer()
	{
		
		try
		{
			Process p = Runtime.getRuntime().exec(SERVER_CMD);
			
			inp = new BufferedReader( new InputStreamReader(p.getInputStream()) );
			out = new BufferedWriter( new OutputStreamWriter(p.getOutputStream()) );

			serverRunning = true;

			for (ServerConsoleListener l : consoleListeners) 
			{
				l.serverStarted();
			}
			
			while (serverRunning) 
			{
				readServer();
				Thread.sleep(10);
			}

//			out.write("whoami" + "\n");
//			out.flush();
			
			Thread.sleep(500);
			
			readServer();

			inp.close();
			out.close();
		}
		catch (Exception e)
		{
			System.out.println("Exception: " + e.getMessage());
			e.printStackTrace();
			stopServer();
		}
	}
	
	public void sendCommand(String cmd)
	{
		if (serverRunning)
		{
			try
			{
				out.write(cmd + "\n");
				out.flush();				
			}
			catch (IOException e)
			{
				System.out.println("Exception while sending command: " + e.getMessage());
				stopServer();
			}
		}
	}
	
	protected void readServer()
	throws IOException
	{
		while (inp.ready())
		{
			String line = inp.readLine();
			if (line != null)
			{
				for (ServerConsoleListener l : consoleListeners) 
				{
					l.receivedLine(line);
				}
			}
			else
			{
				throw new IOException("server ended");
			}
		}
	}
}
