package com.actioncrafter.runner;

import com.actioncrafter.core.ACEvent;
import com.actioncrafter.core.ACEventStreamer;

public class ConsoleEventListener implements ServerConsoleListener 
{
	ServerConsoleController mServerConsole;
	ACEventStreamer mEventStreamer;
	
	public ConsoleEventListener(ServerConsoleController serverConsole)
	{
		mServerConsole = serverConsole;
	}
	
	
	public void receivedLine(String line) 
	{
		ACEvent event;
		
		if ((event = parseEvent(line)) != null)
		{
			System.err.println("Event line: " + line);
			mEventStreamer.queueEvent(event);
		}
		else
		{
			System.err.println("Non-event line: " + line);
		}
	}
	
	ACEvent parseEvent(String line)
	{
		ACEvent event = null;
		
		event = new ACEvent("fake_event");
		event.setParam("fake_param1", "blah");
		
		return event;
	}

	public void serverStopped()
	{
		mEventStreamer.stopStreamer();
	}

	public void serverStarted()
	{
		System.err.println("[eventListener] Server has started");
		mEventStreamer = new ACEventStreamer();
		mEventStreamer.startStreamer();
	}

	
}
