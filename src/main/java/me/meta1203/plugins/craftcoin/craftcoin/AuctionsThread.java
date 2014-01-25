package me.meta1203.plugins.monacoin.monacoin;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;

import com.avaje.ebean.Ebean;
import com.google.monacoin.core.*;
import com.google.monacoin.core.TransactionConfidence.ConfidenceType;
import com.google.monacoin.store.BlockStoreException;

import me.meta1203.plugins.monacoin.AuctionEntry;
import me.meta1203.plugins.monacoin.Monacoinish;
import me.meta1203.plugins.monacoin.Util;

public class AuctionsThread extends Thread {
	private List<Transaction> toCheck = new ArrayList<Transaction>();

	private int waitTime = 60;

	public AuctionsThread() {
		
	}
	

	public void run() {
		while (true) {
			checkauctions();
			try {
				synchronized (this) {
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}


	// Loop checks and outputs

	private void checkauctions() {
		synchronized (this) {
			Monacoinish.log.info("Checking 2"); 
			String returns = "";
				Monacoinish p = (Monacoinish)Bukkit.getPluginManager().getPlugin("Monacoinish");

			returns = "Items Found:";
			List<AuctionEntry> ae = p.getDatabase().find(AuctionEntry.class).where().isNotNull("id").findList();
			
			for (AuctionEntry x : ae) {
				int xx = (int) (System.currentTimeMillis()/100 - x.getStarted());
			
				if(xx < 86400*Monacoinish.auction_days)
				{
					try
					{
					if(Bukkit.getPlayer(x.getBidder()) != null)
					{
						
						Bukkit.getPlayer(x.getBidder()).sendMessage(ChatColor.RED + "You have won the auction ");
						ItemStack sx = new ItemStack(Material.matchMaterial(x.getItemname()), x.getStack());
						Bukkit.getPlayer(x.getBidder()).getInventory().addItem(sx);
						Bukkit.getPlayer(x.getBidder()).updateInventory();
						
						p.getDatabase().delete(x);
					}
					}
					catch(Exception e)
					{
					}
					}
				}
			}
		}
	}
