package com.actioncrafter.core;

import org.junit.Test;

import java.util.logging.Logger;

public class ACPusherConnectorTest
{

    private final static Logger LOGGER = Logger.getLogger(ACPusherConnectorTest.class.getName());


    @Test
    public void testPusher()
    {

        String acKey = "7f40c7f224f687654e8f";
        String acSecret = "da8fe370577a24473ebb";
        String acChannel = "pushertest";


        ACPusherConnector mACPusherConnector = new ACPusherConnector(acKey, acSecret, LOGGER, acChannel);
        mACPusherConnector.addListener(new TestEventReceiver());

        mACPusherConnector.startConnection();

        try {

//            Thread.sleep(5*1000);

            mACPusherConnector.sendEvent(ACEvent.build("mc_cmd param1=value1|param2=value2"));

            Thread.sleep(300*1000);


        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }

        mACPusherConnector.closeConnection();

    }

    class TestEventReceiver implements ACEventReceiver
    {

        @Override
        public void handleEvent(ACEvent event)
        {
            System.out.println("Got event named " + event.getName() + " which was created at " + event.getDate());
            for (String k : event.getParamKeys())
            {
                System.out.println("  Event had param " + k + " with value " + event.getParam(k));
            }

        }

    }
}
