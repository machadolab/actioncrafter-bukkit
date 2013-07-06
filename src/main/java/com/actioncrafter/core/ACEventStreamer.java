package com.actioncrafter.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ACEventStreamer extends Thread 
{
	
	List<ACEvent> eventOutputQueue = new ArrayList<ACEvent>();
	
    private static final String ACTIONCRAFTER_ENDPOINT = "http://localhost:9292/event";
	
    
	private volatile boolean streamerRunning = false;
	
	public void startStreamer()
	{
		this.start();
	}
	
	public void queueEvent(ACEvent event) 
	{
		System.err.println("QUEUE EVENT: " + event);
		synchronized (eventOutputQueue)
		{
			eventOutputQueue.add(event);
			eventOutputQueue.notify();
		}
	}
	
	public void stopStreamer()
	{
		streamerRunning = false;
	}
	
	
	void uploadEvent(ACEvent event)
	{
		System.err.println("SENDING EVENT: " + event);

		try 
		{
		    DefaultHttpClient httpclient = new DefaultHttpClient();

			String url = ACTIONCRAFTER_ENDPOINT + "?" + event.toUrlString();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpclient.execute(httpget);

			HttpEntity entity = response.getEntity();

			System.err.println("Http response status - " + response.getStatusLine());
			if (entity != null)
			{
				System.err.println("Http response body: " + entity.getContent());
			}

			httpclient.getConnectionManager().shutdown();

		}
		catch (Exception e) 
		{
			System.err.println("Exception during http get: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		streamerRunning = true;
		
		while(streamerRunning == true)
		{
			try
			{
				synchronized (eventOutputQueue)
				{
					if (eventOutputQueue.size() > 0)
					{
						uploadEvent(eventOutputQueue.remove(0));
					}
					else
					{
						eventOutputQueue.wait(1000);
					}
				}
			}
			catch (InterruptedException e)
			{
				System.err.println("EventStreamer interrupted");
			}
			catch (Exception e)
			{
				System.err.println("Error in streamer loop - " + e);
				e.printStackTrace();
				streamerRunning = false;
			}
		}


	}

}
