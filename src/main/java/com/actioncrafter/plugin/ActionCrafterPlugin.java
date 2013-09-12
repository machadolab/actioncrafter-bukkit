package com.actioncrafter.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.actioncrafter.core.ACEvent;
import com.actioncrafter.core.ACEventStreamer;
import com.google.common.base.Joiner;

public class ActionCrafterPlugin extends JavaPlugin 
{

	ACEventStreamer mEventStreamer;

	
	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		getLogger().info("Starting EventStreamer");
		
		mEventStreamer = new ACEventStreamer();
		mEventStreamer.startStreamer();
		
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
				ACEvent event = ACEvent.build(eventStr);
				getLogger().info("Sending ACEvent: " + event);
				
				mEventStreamer.queueEvent(event);				
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

}
