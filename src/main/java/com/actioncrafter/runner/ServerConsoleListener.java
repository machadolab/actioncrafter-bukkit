package com.actioncrafter.runner;

public interface ServerConsoleListener 
{
	
	public void receivedLine(String line);
	
	public void serverStopped();
	
	public void serverStarted();

}
