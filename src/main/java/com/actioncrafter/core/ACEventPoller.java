package com.actioncrafter.core;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class ACEventPoller
{

    private static int EVENT_POLL_INTERVAL = 10 * 1000;

    List<ACEventReceiver> receivers = new ArrayList<ACEventReceiver>();

    Timer pollTimer;

    public synchronized void addListener(ACEventReceiver receiver)
    {
        receivers.add(receiver);
    }


    public void startPoller()
    {
        pollTimer = new Timer();
        pollTimer.schedule(new EventPollerTask(), 0, EVENT_POLL_INTERVAL);
    }

    public void stopPoller()
    {
        if (pollTimer != null)
        {
            pollTimer.cancel();
        }
    }

    class EventPollerTask extends TimerTask
    {
        public void run()
        {
            List<ACEvent> events = getEvents();
            for (ACEventReceiver receiver : receivers)
            {
                receiver.handleEvents(events);
            }

        }

        public List<ACEvent> getEvents()
        {

            DefaultHttpClient httpClient = new DefaultHttpClient();

            try
            {
                String url = ACConfig.ACTIONCRAFTER_ENDPOINT + "/queue/mc_in/all?key="+ACConfig.ACTIONCRAFTER_KEY+"&save=0";

                System.out.println("Polling actioncrafter server at " + url);

                HttpGet httpget = new HttpGet(url);
                HttpResponse response = httpClient.execute(httpget);

                HttpEntity entity = response.getEntity();

                System.out.println("Http response status - " + response.getStatusLine());
                if (response.getStatusLine().getStatusCode() == 200)
                {
                    if (entity != null)
                    {
                        return ACEvent.parseJson(new BufferedReader(new InputStreamReader(entity.getContent())));
                    }
                    else
                    {
                        System.out.println("Problem with content from server");
                    }
                }
                else
                {
                    System.out.println("Error from server: " +response.getStatusLine().getStatusCode());
                }

            }
            catch (Exception e)
            {
                System.out.println("Exception during http get: " + e.getMessage());
                e.printStackTrace();
            }
            finally
            {
                httpClient.getConnectionManager().shutdown();
            }


            return null;
        }
    }




}
