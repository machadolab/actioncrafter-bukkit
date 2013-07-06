package com.actioncrafter.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.actioncrafter.core.ACEvent;
import com.actioncrafter.core.ACEventStreamer;

public class ActionCraftPlugin extends JavaPlugin 
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
			ACEvent event = ACEvent.build(args[0]);
			getLogger().fine("Sending ACEvent: " + event);
			
			mEventStreamer.queueEvent(event);
			
			return true;
		}
		
		getLogger().info("Unhandled command: " + cmd.getName());
		
		return false; 
	}

}
