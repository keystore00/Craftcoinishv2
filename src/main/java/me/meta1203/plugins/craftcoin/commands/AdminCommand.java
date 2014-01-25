package me.meta1203.plugins.monacoin.commands;

import java.math.BigInteger;

import me.meta1203.plugins.monacoin.Monacoinish;
import static me.meta1203.plugins.monacoin.commands.CommandUtil.*;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import com.google.monacoin.core.ScriptException;
import com.google.monacoin.core.Transaction;
import com.google.monacoin.core.Wallet;

public class AdminCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("monacoin.admin")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg3.length != 1) {
			error("Syntax: /monacoin <info>|<reset>", arg0);
			return true;
		}
		if (arg3[0].equalsIgnoreCase("info"))
			printInfo(arg0);
		else if (arg3[0].equalsIgnoreCase("reset"))
			Monacoinish.bapi.reloadWallet();
		else
			error("Syntax: /satoshis <info>|<reset>", arg0);
		
		return true;
	}
	
	private void printInfo(CommandSender arg0) {
		info("INFO:", arg0);
		info("Wallet:", arg0);
		
		Wallet tmp = Monacoinish.bapi.getWallet();
		BigInteger bitcoinBalance = tmp.getBalance();
		double inGameValue = Monacoinish.econ.bitcoinToInGame(bitcoinBalance);
		info("Total balance: " + bitcoinBalance.longValue() + " Satoshi = " + Monacoinish.econ.formatValue(inGameValue, true), arg0);
		info("Recent transactions:", arg0);
		for (Transaction t : tmp.getRecentTransactions(3, false)) {
			try {
				info(t.getHashAsString() + " value: +" + t.getValueSentToMe(tmp) + ", -" + t.getValueSentFromMe(tmp), arg0);
				info("Confirmations: " + t.getConfidence().getDepthInBlocks(), arg0);
			} catch (ScriptException e) {
				error("Transaction " + t.getHashAsString() + " errored out!", arg0);
			} catch (IllegalStateException e) {
				continue;
			}
		}
	}
}
