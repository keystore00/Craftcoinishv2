package me.meta1203.plugins.monacoin;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class Auctions {
	public static Monacoinish plugin;
	
	public String search(String name) {
		String returns = "";
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Monacoinish");
			plugin = (Monacoinish) p;
		}
		returns = "Items Found:";
		List<AuctionEntry> ae = plugin.getDatabase().find(AuctionEntry.class).where().ieq("itemname", name).findList();
			
		for (AuctionEntry x : ae) {
			returns = returns + "\n ID:" + x.getId() + " Description: " + x.getStack() + " " + x.getItemname() + " for " + x.getBuyout() + " or bid more than " + x.getPrice() ;
		}
		return returns;
	}
	public AuctionEntry searchid(String name) {
		String returns = "";
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Monacoinish");
			plugin = (Monacoinish) p;
		}
		returns = "Items Found:";
		List<AuctionEntry> ae = plugin.getDatabase().find(AuctionEntry.class).where().ieq("id", name).findList();
			
		for (AuctionEntry x : ae) {
			return x;
		}
		return null;
	}
	public void saveAuction(AuctionEntry ae)
	{		
		if (plugin == null) {
			Plugin p = Bukkit.getPluginManager().getPlugin("Monacoinish");
			plugin = (Monacoinish) p;
		}
		plugin.getDatabase().save(ae);
	}
}
