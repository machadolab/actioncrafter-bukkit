package com.actioncrafter.runner;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class ActionCrafterRunner implements ServerConsoleListener
{

	ConsoleEventListener eventListener;
	
	ServerConsoleController serverConsole;
	
	BufferedReader console;
	
	private volatile boolean consoleRunning = false;
	
	public ActionCrafterRunner()
	{
		
	}
	
	public void run()
	{

		serverConsole = new ServerConsoleController();
		
		eventListener = new ConsoleEventListener(serverConsole);
		
		serverConsole.addListener(eventListener);
		serverConsole.addListener(this);
		serverConsole.start();
		
		console = new BufferedReader (new InputStreamReader(System.in));

		System.out.println("Started console on stdin");
		
		
		consoleRunning = true;
		
		while (consoleRunning) 
		{
			try 
			{
				String consoleLine = console.readLine();
				if (consoleLine != null)
				{
					serverConsole.sendCommand(consoleLine);
				}
				else
				{
					serverConsole.stopServer();
					consoleRunning = false;
				}
			}
			catch (IOException e) 
			{
				System.err.println("Error reading from stdin: " + e.getMessage());
			}
		}
		
		System.out.println("Ending console...");
		
	}
	
	
	public void receivedLine(String line) 
	{
		System.out.println(line);
	}

	public void serverStopped() 
	{
		consoleRunning = false;
	}

	public void serverStarted() 
	{
		System.err.println("[console] Server has started");
	}
	
	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		ActionCrafterRunner runner = new ActionCrafterRunner();
		runner.run();
	}

	
}
