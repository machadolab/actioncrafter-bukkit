package com.actioncrafter.core;

import org.junit.Test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.logging.Logger;

public class ACWebsocketConnectorTest
{

    private final static Logger LOGGER = Logger.getLogger(ACWebsocketConnectorTest.class.getName());

    @Test
    public void testWebsocket()
    {

        URI uri = null;
        try {
            uri = new URI("ws://localhost:8081?key=121212");
        } catch (URISyntaxException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        ACWebsocketConnector connector = new ACWebsocketConnector(uri, LOGGER);

        connector.addListener(new TestEventReceiver());
        connector.startConnection();


        try {

            Thread.sleep(5*1000);

            connector.sendEvent(ACEvent.build("cmd1 param1=value1|param2=value2"));

            Thread.sleep(300*1000);


        } catch (InterruptedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        connector.closeConnection();


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
