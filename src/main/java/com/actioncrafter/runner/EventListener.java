package com.actioncrafter.runner;

public class EventListener implements ServerConsoleListener 
{
	ServerConsoleController mServerConsole;
	EventStreamer mEventStreamer;
	
	public EventListener(ServerConsoleController serverConsole)
	{
		mServerConsole = serverConsole;
	}
	
	
	public void receivedLine(String line) 
	{
		Event event;
		
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
	
	Event parseEvent(String line)
	{
		Event event = null;
		
		event = new Event("fake_event");
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
		mEventStreamer = new EventStreamer();
		mEventStreamer.startStreamer();
	}

	
}
