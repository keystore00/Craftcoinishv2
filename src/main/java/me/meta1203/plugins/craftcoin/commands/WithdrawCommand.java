package me.meta1203.plugins.monacoin.commands;

import me.meta1203.plugins.monacoin.Monacoinish;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.monacoin.core.Address;
import com.google.monacoin.core.AddressFormatException;
import com.google.monacoin.core.WrongNetworkException;

import static me.meta1203.plugins.monacoin.commands.CommandUtil.*;

public class WithdrawCommand implements CommandExecutor {

	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		if (!arg0.hasPermission("monacoin.withdraw")) {
			error("You do not have permission for this command!", arg0);
			return true;
		}
		
		if (arg0 instanceof Player) {
			Player player = (Player)arg0;
			double value = 0;
			
			// Withdraw exact amount
			if (arg3.length == 2) {
				try {
				
					Address withdrawTo = new Address(Monacoinish.network, arg3[0]);
					double withdraw = Double.parseDouble(arg3[1]);
					if (!Monacoinish.econ.hasMoney(player.getName(), Monacoinish.minWithdraw)) {
						error("Oops! You must have " + Monacoinish.econ.formatValue(Monacoinish.minWithdraw, true) + " to withdraw!", arg0);
						return true;
					}
					
					if (!Monacoinish.econ.hasMoney(arg0.getName(), withdraw - Monacoinish.econ.priceOfTax(withdraw))) {
						error("Oops! You cannot withdraw more money than you have!", arg0);
						return true;
					}
					value = Monacoinish.bapi.localSendCoins(withdrawTo, withdraw);


					
					Monacoinish.econ.subFunds(arg0.getName(), withdraw - Monacoinish.econ.priceOfTax(withdraw));
				} catch (WrongNetworkException e) {
					error("Oops! That address was for the TestNet!", arg0);
				} catch (AddressFormatException e) {
					error("Oops! Is that the correct address?", arg0);
				} catch (NumberFormatException e) {
					error("Usage: /withdraw <address> <amount>", arg0);
					error("Amount must be a number!",arg0);
				}
			} else {
			    error("Usage: /withdraw <address> <amount>", arg0);
			}
			info("Withdrew " + Double.toString(value) + " to " + arg3[0] + " .", arg0);
		}
		
		return true;
	}
}
