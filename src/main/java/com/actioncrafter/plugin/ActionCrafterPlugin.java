package com.actioncrafter.plugin;

import com.actioncrafter.core.*;
import java.net.URI;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.net.URISyntaxException;
import java.util.List;

public class ActionCrafterPlugin extends JavaPlugin 
{

    ACWebsocketConnector mACConnector;
	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();

		getLogger().info("Starting ACWebsocketConnector");
        try
        {

            String acUrl = getConfig().getString("actioncrafter_url");
            String acKey = getConfig().getString("api_key");
            String acChannels = getConfig().getString("subscribe_channels");

            URI acUri = new URI(acUrl+"?key="+acKey);
            mACConnector = new ACWebsocketConnector(acUri, getLogger());

            mACConnector.addListener(new ActionCrafterInputEventReceiver());

            mACConnector.startConnection();

            mACConnector.subscribeChannel(acChannels);

        }
        catch (URISyntaxException e)
        {
            getLogger().warning("Error while initializing actioncrafter websocket: " + e.toString());
            e.printStackTrace();
        }

	}
	
	@Override
	public void onDisable()
	{
        mACConnector.closeConnection();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("action"))
		{
			if (args.length <= 0)
			{
				getLogger().info("Invalid usage. Action format is: <channel> <action_name> [<argument>=<value>|<argument>=<value>|...]");
				return false;
			}

            String channel = args[0];
            StringBuilder sb = new StringBuilder();
            for (int i = 1; i < args.length; i++)
            {
                sb.append(args[i]);
                if (i < args.length)
                {
                    sb.append(" ");
                }
            }

			String eventStr = sb.toString();
				
			getLogger().info("Event string is " + eventStr);
			try 
			{
				ACEvent event = ACEvent.build(eventStr);
                event.setChannel(channel);
                mACConnector.sendEvent(event);
			}
			catch (Exception e)
			{
				getLogger().warning("Exception while queueing event: " + e.getMessage());
			}
			
			return true;
		}
		
		getLogger().info("Unhandled command: " + cmd.getName());
		
		return false; 
	}

    class ActionCrafterInputEventReceiver implements ACEventReceiver
    {

        @Override
        public void handleEvent(ACEvent event)
        {
            String command = eventToCommand(event);
            getLogger().info("Executing command " + command);
            getServer().dispatchCommand(getServer().getConsoleSender(), command);
        }

        public String eventToCommand(ACEvent event)
        {
            String command = event.getName();
            String args = event.getParam("args");
            if (args != null)
            {
                command += " " + args;
            }
            return command;
        }
    }

}
