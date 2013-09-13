package com.actioncrafter.core;

import static org.junit.Assert.*;

import org.junit.Test;

import java.util.List;

public class ACEventPollerTeset
{

    @Test
    public void testPoller()
    {

        ACEventPoller poller = new ACEventPoller();

        poller.startPoller();
        poller.addListener(new TestEventReceiver());

        try {
            Thread.sleep(25*1000);
        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        poller.stopPoller();


    }

    class TestEventReceiver implements ACEventReceiver
    {

        @Override
        public void handleEvents(List<ACEvent> events)
        {

            if (events.size() > 0)
            {
                for (ACEvent event : events)
                {
                    System.out.println("Got event named " + event.getName() + " which was created at " + event.getDate());
                    for (String k : event.getParamKeys())
                    {
                        System.out.println("  Event had param " + k + " with value " + event.getParam(k));
                    }
                }
            }
            else
            {
                System.out.println("Event list is empty");
            }
        }

    }

}
