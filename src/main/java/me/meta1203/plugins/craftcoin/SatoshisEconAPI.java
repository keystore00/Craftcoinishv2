package me.meta1203.plugins.monacoin;

import java.math.BigInteger;

import org.bukkit.event.EventHandler;

public class SatoshisEconAPI  {

	public final double minCurrFee = 0.0005 * Monacoinish.mult;
	public boolean buyerorseller = true;
	
	public void setFunds(String accName, double value) {
		AccountEntry e = Util.loadAccount(accName);
		e.setAmount(value);
		Util.saveAccount(e);
	}
	
	public void addFunds(String accName, double value) {
		AccountEntry e = Util.loadAccount(accName);
		e.setAmount(e.getAmount() + value);
		Util.saveAccount(e);
	}
	
	public void subFunds(String accName, double value) {
		AccountEntry e = Util.loadAccount(accName);
		double fVal = e.getAmount() - value;
		if (fVal < 0) {
			fVal = 0;
		}
		e.setAmount(fVal);
		Util.saveAccount(e);
	}
	
	public double priceOfTax(double traded) {
		return traded * (Monacoinish.tax/100);
	}
	
	public void transact(String playerFrom, String playerTo, double value) {
		if (Monacoinish.salesTax) {
			double tax = priceOfTax(value);
			if (Monacoinish.buyerorseller) {
				subFunds(playerFrom, value+tax);
				addFunds(playerTo, value);
			} else {
				subFunds(playerFrom, value);
				addFunds(playerTo, value-tax);
			}
			addFunds(Monacoinish.owner, tax);
		} else {
			subFunds(playerFrom, value);
			addFunds(playerTo, value);
		}
		
		Monacoinish.log.info("Transaction took place!");
		Monacoinish.log.info(playerFrom + " paid " + playerTo + ": " + formatValue(value, true));
	}
	
	public void transferTax(double value) {
		addFunds(Monacoinish.owner, priceOfTax(value));
	}
	
	public String formatValue(double value, boolean exact) {
		if (exact)
			value = Util.roundTo(value, 2);
		return value + " " + Monacoinish.currencyName;
	}
	
	public String listMoney(String player) {
		if (Util.loadAccount(player) == null) {
			return null;
		}
		return formatValue(Util.loadAccount(player).getAmount(), true);
	}
	
	public boolean addAccount(String player) {
		if (Util.testAccount(player)) {
			return false;
		} else {
			Util.saveAccount(Util.loadAccount(player));
			return true;
		}
	}
	
	public double getMoney(String player) {
		return Util.loadAccount(player).getAmount();
	}
	
	public boolean hasMoney(String player, double amount) {
		double has = Util.loadAccount(player).getAmount();
		return has >= amount;
	}
	
	public BigInteger inGameToBitcoin(double amount) {
		return BigInteger.valueOf((long)(amount * Math.pow(10, 8)/Monacoinish.mult));
	}
	
	public double bitcoinToInGame(BigInteger amount) {
		return (amount.longValue() / Math.pow(10, 8)) * Monacoinish.mult;
	}
}
