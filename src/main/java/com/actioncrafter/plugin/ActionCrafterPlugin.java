package com.actioncrafter.plugin;

import com.actioncrafter.core.ACEventPoller;
import com.actioncrafter.core.ACEventReceiver;
import com.actioncrafter.core.ACEventUploader;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.actioncrafter.core.ACEvent;
import com.google.common.base.Joiner;

import java.util.List;

public class ActionCrafterPlugin extends JavaPlugin 
{

	ACEventUploader mEventStreamer;
    ACEventPoller mEventPoller;

	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		getLogger().info("Starting EventStreamer");
		
		mEventStreamer = new ACEventUploader();
		mEventStreamer.startStreamer();

        getLogger().info("Starting EventPoller");
        mEventPoller = new ACEventPoller();
        mEventPoller.addListener(new ActionCrafterInputEventReceiver());
        mEventPoller.startPoller();
		
//		getLogger().info("Key is " + getConfig().getString("api_key"));
	}
	
	@Override
	public void onDisable()
	{
		mEventStreamer.stopStreamer();
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("ac_event"))
		{
			if (args.length <= 0)
			{
				getLogger().info("Invalid usage. Event format is: <event_name> [<argument>=<value>|<argument>=<value>|...]");
				return false;
			}
			
			String eventStr = Joiner.on(" ").join(args);
				
			getLogger().info("Event string is " + eventStr);
			try 
			{

                if (eventStr.equals("woot"))
                {
                    getServer().dispatchCommand(sender, "rsc testme");
                }
                else
                {
				ACEvent event = ACEvent.build(eventStr);
				getLogger().info("Sending ACEvent: " + event);
				
				mEventStreamer.queueEvent(event);
                }
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
        public void handleEvents(List<ACEvent> events)
        {
            for (ACEvent event : events)
            {
                String command = eventToCommand(event);
                getLogger().info("Executing command " + command);
                getServer().dispatchCommand(getServer().getConsoleSender(), command);
            }

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
