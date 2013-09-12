package com.actioncrafter.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class ACEventStreamer extends Thread 
{
	
	private final List<ACEvent> eventOutputQueue = new ArrayList<ACEvent>();
	
    private static final String ACTIONCRAFTER_ENDPOINT = "http://machadolab.com:3000/event";
	
    
	private volatile boolean streamerRunning = false;
	
	public void startStreamer()
	{
		this.start();
	}
	
	public void queueEvent(ACEvent event) 
	{
		System.out.println("QUEUE EVENT: " + event);
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
		System.out.println("SENDING EVENT: " + event);

		try 
		{
		    DefaultHttpClient httpClient = new DefaultHttpClient();

			String url = ACTIONCRAFTER_ENDPOINT + "?" + event.toUrlString();
			HttpGet httpget = new HttpGet(url);
			HttpResponse response = httpClient.execute(httpget);

			HttpEntity entity = response.getEntity();

			System.out.println("Http response status - " + response.getStatusLine());
			if (entity != null)
			{
				System.out.println("Http response body: " + entity.getContent());
			}

			httpClient.getConnectionManager().shutdown();

		}
		catch (Exception e) 
		{
			System.out.println("Exception during http get: " + e.getMessage());
			e.printStackTrace();
		}
	}

	@Override
	public void run()
	{
		streamerRunning = true;
		
		while(streamerRunning)
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
				System.out.println("EventStreamer interrupted");
			}
			catch (Exception e)
			{
				System.out.println("Error in streamer loop - " + e);
				e.printStackTrace();
				streamerRunning = false;
			}
		}


	}

}
