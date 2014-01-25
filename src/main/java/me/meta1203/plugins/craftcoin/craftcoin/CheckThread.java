package me.meta1203.plugins.monacoin.monacoin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.google.monacoin.core.*;
import com.google.monacoin.core.TransactionConfidence.ConfidenceType;
import com.google.monacoin.store.BlockStoreException;

import me.meta1203.plugins.monacoin.Monacoinish;
import me.meta1203.plugins.monacoin.Util;

public class CheckThread extends Thread {
	private List<Transaction> toCheck = new ArrayList<Transaction>();

	private int waitTime = 0;
	private int confirmations = 0;

	public CheckThread(int wait, int confirmations) {
		Monacoinish.log.info("Checking for " + Integer.toString(confirmations) + " confirmations every " + Integer.toString(wait) + " seconds.");
		waitTime = wait;
		this.confirmations = confirmations;
		List<Transaction> toAdd = Util.loadChecking();
		Monacoinish.log.info("Adding " + toAdd.size() + " old transactions to the check pool!");
		for (Transaction current : toAdd) {
			Monacoinish.log.info("Added: " + current.getHashAsString());
			toCheck.add(current);
		}
	}

	public void run() {
		while (true) {
			check();
			try {
				synchronized (this) {
					this.wait(waitTime*1000);
				}
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public synchronized void addCheckTransaction(Transaction tx) {
		toCheck.add(tx);
		Monacoinish.log.warning("Added transaction " + tx.getHashAsString() + " to check pool!");
	}

	public synchronized void serialize() {
		Util.serializeChecking(toCheck);
	}

	// Loop checks and outputs

	private void check() {
		synchronized (this) {
			Monacoinish.log.info("Checking 1"); 
			List<Transaction> toRemove = new ArrayList<Transaction>();
			for (Transaction current : toCheck) {
				
				
				Transaction currents = Monacoinish.bapi.getWallet().getTransaction(current.getHash());
			
				if (!currents.getConfidence().getConfidenceType().equals(ConfidenceType.BUILDING)) {
					Monacoinish.log.info("Still building");
					continue;
				}
				int conf = currents.getConfidence().getDepthInBlocks();
				Monacoinish.log.info(conf + " CONFIRMS");
				if (conf >= confirmations) {
						double value = Monacoinish.econ.bitcoinToInGame(currents.getValueSentToMe(Monacoinish.bapi.getWallet()));
						List<Address> receivers = null;
						try {
							Monacoinish.log.info(currents.getOutputs().toString());
							receivers = Util.getContainedAddress(currents.getOutputs());
						
						} catch (ScriptException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
						for (Address x : receivers) {
							String pName = Util.searchAddress(x);
							Monacoinish.econ.addFunds(pName, value);
							Monacoinish.log.warning("Added " + Monacoinish.econ.formatValue(value, true) + " to " + pName + "!");
						}
						
						Monacoinish.bapi.saveWallet();
					toRemove.add(currents);
				}
			}
			toCheck.removeAll(toRemove);
		}
	}

}
