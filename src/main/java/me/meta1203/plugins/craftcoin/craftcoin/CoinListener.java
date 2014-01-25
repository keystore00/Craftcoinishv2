package me.meta1203.plugins.monacoin.monacoin;

import java.math.BigInteger;
import java.util.List;

import me.meta1203.plugins.monacoin.Monacoinish;
import me.meta1203.plugins.monacoin.Util;

import com.google.monacoin.core.*;
import com.google.monacoin.core.TransactionConfidence.ConfidenceType;
import com.google.monacoin.store.BlockStoreException;
import com.google.monacoin.core.Address;


public class CoinListener extends AbstractWalletEventListener {

	@Override
	public void onCoinsReceived(Wallet wallet, Transaction tx,
			BigInteger prevBalance, BigInteger newBalance) {
		
		
		BigInteger MONAAdded = newBalance.subtract(prevBalance); 
		BigInteger MONAAddedc = BigInteger.ZERO;
		if(MONAAdded.compareTo(MONAAddedc) != -1)
		{
				Monacoinish.checker.addCheckTransaction(tx);
		}
		
		

	
		
	}

}
