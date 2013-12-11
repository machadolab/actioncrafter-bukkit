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

    ACPusherConnector mACPusherConnector;

    private static final String AC_EVENT_MC_CMD = "mc_cmd";
	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();

		getLogger().info("Starting ACPusherConnector");
        try
        {
            String acKey = getConfig().getString("pusher_apikey");
            String acSecret = getConfig().getString("pusher_secret");
            String acChannel = getConfig().getString("pusher_channel");

            mACPusherConnector = new ACPusherConnector(acKey, acSecret, getLogger(), acChannel);
            mACPusherConnector.bindEvent(AC_EVENT_MC_CMD);
            mACPusherConnector.addListener(new ActionCrafterInputEventReceiver());

            mACPusherConnector.startConnection();
        }
        catch (Exception e)
        {
            getLogger().warning("Error while initializing actioncrafter pusher connection: " + e.toString());
            e.printStackTrace();
        }

	}
	
	@Override
	public void onDisable()
	{
        mACPusherConnector.closeConnection();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("action"))
		{
			if (args.length <= 0)
			{
				getLogger().info("Invalid usage. Action format is: <action_name> [<argument>=<value>|<argument>=<value>|...]");
				return false;
			}

            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < args.length; i++)
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
                mACPusherConnector.sendEvent(event);
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
            if (event.getName().equals(AC_EVENT_MC_CMD))
            {
                String command = eventToCommand(event);
                if (command != null)
                {
                    getLogger().info("Executing command " + command);
                    getServer().dispatchCommand(getServer().getConsoleSender(), command);
                }
            }
            else
            {
                getLogger().info("Ignoring unknown event " + event.getName());
            }

        }

        public String eventToCommand(ACEvent event)
        {
            String command = event.getParam("cmd");
            String args = event.getParam("args");
            if (args != null)
            {
                command += " " + args;
            }
            return command;
        }
    }

}
