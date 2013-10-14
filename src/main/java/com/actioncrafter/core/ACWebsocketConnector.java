package com.actioncrafter.core;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.handshake.ServerHandshake;

public class ACWebsocketConnector extends WebSocketClient
{

    private Logger mLogger;

    List<ACEventReceiver> receivers = new ArrayList<ACEventReceiver>();

    private List<String> queuedMessages = new ArrayList<String>();

    private boolean isOpen = false;


    public ACWebsocketConnector(URI serverURI, Logger logger)
    {
        super(serverURI, new Draft_10());
        mLogger = logger;
    }

    public void startConnection()
    {
        mLogger.info("Connecting to " + getURI());
        connect();
    }

    public void closeConnection()
    {
        close();
    }

    public synchronized void addListener(ACEventReceiver receiver)
    {
        receivers.add(receiver);
    }

    public void subscribeChannel(String channel)
    {
        if (isOpen)
        {
            send("subscribe " + channel);
        }
        else
        {
            queuedMessages.add("subscribe " + channel);
            mLogger.warning("Websocket is not open yet for subscribe to " + channel);
        }
    }

    public void unsubscribeChannel(String channel)
    {
        if (isOpen)
        {
            send("unsubscribe " + channel);
        }
        else
        {
            queuedMessages.add("subscribe " + channel);
            mLogger.warning("Websocket is not open yet for unsubscribe to " + channel);
        }
    }

    public void sendEvent(ACEvent event)
    {
        if (isOpen)
        {
            mLogger.info("Sending event " + event.toUrlString());
            try
            {
                send("action " + event.getChannel() + " " + event.toUrlString());
            }
            catch (Exception e)
            {
                mLogger.warning("Exception while sending event " + event.toUrlString() + ": " + e.toString());
            }
        }
        else
        {
            queuedMessages.add("action " + event.getChannel() + " " + event.toUrlString());
            mLogger.warning("Websocket is not open yet for event " + event.toUrlString());
        }
    }


    @Override
    public void onOpen(ServerHandshake handshakedata)
    {
        mLogger.info("Websocket connection opened");
        int queueSize = queuedMessages.size();
        for (int i = 0; i < queueSize; i++)
        {
            send(queuedMessages.remove(i));
        }
        isOpen = true;
    }

    @Override
    public void onMessage(String message)
    {
        mLogger.info("Received websocket message " + message);

        ACEvent event = ACEvent.parseJson(message);

        try
        {
            for (ACEventReceiver receiver : receivers)
            {
                receiver.handleEvent(event);
            }
        }
        catch (Exception e)
        {
            mLogger.warning("Exception while processing message: " + e.getMessage());
        }

    }

    @Override
    public void onClose(int code, String reason, boolean remote)
    {
        mLogger.warning("Websocket connection closed by " + ( remote ? "remote peer" : "us" ));
        isOpen = false;


    }

    @Override
    public void onError(Exception e)
    {
        mLogger.warning("Error in websocket: " + e.getMessage());
    }

}
