package com.actioncrafter.plugin;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class ActionCraftPlugin extends JavaPlugin 
{

	@Override
	public void onEnable()
	{
		this.saveDefaultConfig();
		getLogger().info("ActionCrafter logger enabled");
		getLogger().info("Key is " + getConfig().getString("api_key"));
	}
	
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if(cmd.getName().equalsIgnoreCase("ac_event"))
		{
			
			// If the player typed /basic then do the following...
			getLogger().info("Sending ActionCrafter event");
			// doSomething
			return true;
		} //If this has happened the function will return true. 
	        // If this hasn't happened the a value of false will be returned.
		return false; 
	}

}
