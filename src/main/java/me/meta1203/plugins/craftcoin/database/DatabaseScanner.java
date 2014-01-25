package me.meta1203.plugins.monacoin.database;

import me.meta1203.plugins.monacoin.AccountEntry;
import me.meta1203.plugins.monacoin.Monacoinish;
import me.meta1203.plugins.monacoin.Util;

import org.bukkit.ChatColor;

public class DatabaseScanner {
	private Monacoinish plugin;
	
	public DatabaseScanner(Monacoinish plugin) {
		this.plugin = plugin;
	}
	
	private double getTotal() {
		double ret = 0;
		for (AccountEntry curr : plugin.getDatabase().find(AccountEntry.class).findList()) {
			ret += curr.getAmount();
		}
		return ret;
	}
	
	public DatabaseLevel getLevel(double scanned) {
		double valueReal = Util.getBitcoin(Monacoinish.bapi.getWallet().getBalance());
		double valueGame = scanned/Monacoinish.mult;
		
		if (valueReal > valueGame) {
			return DatabaseLevel.UNDER;
		} else if (valueReal == valueGame) {
			return DatabaseLevel.PERFECT;
		} else if (valueGame - valueReal <= 1) {
			return DatabaseLevel.WARNING;
		} else {
			return DatabaseLevel.SEVERE;
		}
	}
	
	public double getOffset(double scanned) {
		double valueReal = Util.getBitcoin(Monacoinish.bapi.getWallet().getBalance());
		double valueGame = scanned/Monacoinish.mult;
		return valueGame - valueReal;
	}
	
	public String getInfo() {
		double scanned = getTotal();
		DatabaseLevel level = getLevel(scanned);
		String info = level.getColor() + "";
		switch (level) {
		case PERFECT:
			info += "System is healthy!";
			break;
		case SEVERE:
			info += "SYSTEM SEVERELY OVERDRAWN!\n" +
					"Over by " + getOffset(scanned) + " BTC!\n" +
					"Total economy reset is recommended!";
			break;
		case UNDER:
			info += "More Bitcoin than " + Monacoinish.currencyName + " exists!\n";
			break;
		case WARNING:
			info += "System overdrawn!\n" +
					"Over by " + getOffset(scanned) + " BTC.\n" +
					"It is recommended to equalize funding by adding BTC directly.";
			break;
		default:
			break;
		}
		
		// Don't forget to turn the color off
		info += ChatColor.RESET;
		
		return info;
	}
}
