package com.actioncrafter.core;


import com.pusher.client.Pusher;
import com.pusher.client.PusherOptions;
import com.pusher.client.channel.PrivateChannel;
import com.pusher.client.channel.PrivateChannelEventListener;
import com.pusher.client.channel.SubscriptionEventListener;
import com.pusher.client.connection.ConnectionEventListener;
import com.pusher.client.connection.ConnectionState;
import com.pusher.client.connection.ConnectionStateChange;

import java.util.*;
import java.util.logging.Logger;

public class ACPusherConnector implements ConnectionEventListener, SubscriptionEventListener, PrivateChannelEventListener
{

    private final Logger mLogger;

    private Pusher mPusher;
    private PrivateChannel mChannel;

    private boolean isOpen = false;
    private final List<ACEvent> queuedMessages = new ArrayList<ACEvent>();

    List<ACEventReceiver> receivers = new ArrayList<ACEventReceiver>();


    public ACPusherConnector(String pusherKey, String pusherSecret, Logger logger, String channel)
    {
        mLogger = logger;

        PrivateKeyAuthorizer authorizer = new PrivateKeyAuthorizer(pusherKey, pusherSecret);

        PusherOptions options = new PusherOptions();
        options.setAuthorizer(authorizer);
//        options.setEncrypted(false);

        mPusher = new Pusher(pusherKey, options);

        mChannel = mPusher.subscribePrivate("private-"+channel, this);
    }

    public void bindEvent(String event)
    {
        mChannel.bind("client-"+event, this);
    }

    public void bindEvents(String[] events)
    {
        for (String event : events)
        {
            bindEvent(event);
        }
    }

    public void startConnection()
    {
        mLogger.info("Starting connection to pusher");
        mPusher.connect(this, ConnectionState.ALL);
    }

    public void closeConnection()
    {
        isOpen = false;
        queuedMessages.clear();
        mPusher.disconnect();
    }

    public synchronized void addListener(ACEventReceiver receiver)
    {
        receivers.add(receiver);
    }

    public void sendEvent(ACEvent event)
    {
        synchronized (queuedMessages)
        {
            if (!isOpen)
            {
                queuedMessages.add(event);
                mLogger.warning("Pusher connection is not open yet - queuing event");
            }
        }
        if (isOpen)
        {
            mLogger.info("Sending event " + event);
            mChannel.trigger("client-"+event.getName(), event.paramsAsJson());
        }
    }

    @Override
    public void onConnectionStateChange(ConnectionStateChange change)
    {
        mLogger.info("State changed to " + change.getCurrentState() + " from " + change.getPreviousState());
    }

    @Override
    public void onError(String message, String code, Exception e)
    {
        mLogger.severe("Error ["+code+"]: "+message+": "+e);
    }

    @Override
    public void onEvent(String channel, String eventName, String data)
    {
        ACEvent event = null;
        mLogger.info("Got event " + eventName + " on channel " + channel + " with data " + data);

        // strip client- prefix from event name
        if (eventName.startsWith("client-"))
        {
            eventName = eventName.substring(7);
        }

        if (data != null)
        {
            event = ACEvent.parseJson(data);
            event.setName(eventName);
        }
        else
        {
            event = new ACEvent(eventName);
        }

        for (ACEventReceiver receiver : receivers)
        {
            receiver.handleEvent(event);
        }
    }

    @Override
    public void onAuthenticationFailure(String error, Exception e)
    {
        mLogger.severe("Error authenticating to private channel: " + error + ": " + e);
    }

    @Override
    public void onSubscriptionSucceeded(String channel) {
        mLogger.info("Subscribed to private channel " + channel);
        isOpen = true;
        synchronized (queuedMessages)
        {
            int queueSize = queuedMessages.size();
            for (int i = 0; i < queueSize; i++)
            {
                ACEvent queuedEvent = queuedMessages.remove(0);  // pop off the list, always from the front
                if (queuedEvent != null)
                {
                    sendEvent(queuedEvent);
                }
            }
        }
    }



}
