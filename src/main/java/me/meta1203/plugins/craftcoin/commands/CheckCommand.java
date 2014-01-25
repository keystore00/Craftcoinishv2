package me.meta1203.plugins.monacoin.commands;

import me.meta1203.plugins.monacoin.Monacoinish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import static me.meta1203.plugins.monacoin.commands.CommandUtil.*;

public class CheckCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (arg0.hasPermission("monacoin.info")) {
			arg0.sendMessage(Monacoinish.scanner.getInfo());
		} else {
			error("You do not have permission for this command!", arg0);
		}
		return true;
	}

}
