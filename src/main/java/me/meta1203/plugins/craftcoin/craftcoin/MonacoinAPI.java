package me.meta1203.plugins.monacoin.monacoin;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicLong;

import com.google.monacoin.core.*;
import com.google.monacoin.store.UnreadableWalletException;
import com.google.monacoin.store.BlockStoreException;
import com.google.monacoin.store.SPVBlockStore;
import com.google.monacoin.core.AbstractPeerEventListener;
import me.meta1203.plugins.monacoin.Monacoinish;

public class MonacoinAPI {

    private Wallet localWallet;
    private SPVBlockStore localBlock;
    private BlockChain localChain;
    private final File walletFile;
    private PeerGroup localPeerGroup = null;
    public final BigInteger minBitFee = BigInteger.valueOf((long)(0.0005*Math.pow(10, 8)));
    //    private final Handler delayHandler = new Handler();
    private int bestChainHeightEver = 0;;
    private final PeerEventListener blockchainDownloadListener = new AbstractPeerEventListener()
	{
	    private final AtomicLong lastMessageTime = new AtomicLong(0);

	    @Override
	    public void onBlocksDownloaded(final Peer peer, final Block block, final int blocksLeft)
	    {
		// bestChainHeightEver = Math.max(bestChainHeightEver, localChain.getChainHead().getHeight());
		// delayHandler.removeCallbacksAndMessages(null);
		// final long now = System.currentTimeMillis();
		// final long throttle_ms = 1000;
		// if (now -lastMessageTime.get() > throttle_ms) {
		//     delayHandler.post(runnable);
		// } else {
		//     delayHandler.postDelayed(runnable, throtle_ms);
		// }n
		Monacoinish.log.info("Downloaded block " + Integer.toString(localChain.getChainHead().getHeight()) + ", " + Integer.toString(blocksLeft) + "blocks left.");
	}

	    private final Runnable runnable = new Runnable()
		{
		    @Override
		    public void run()
		    {
			// lastMessageTime.set(System.currentTimeMillis());
			// sendBroadcastBlockchainState(
		    }
		};
	};
	
    public MonacoinAPI() {
	walletFile = new File("plugins/Monacoinish/wallet.wallet");
	try {
	    localWallet = Wallet.loadFromFile(walletFile);
	    // Satoshis.log.info(localWallet.toString());
	} catch (UnreadableWalletException e) {
	    Monacoinish.log.info("no local wallet found");
	    localWallet = new Wallet(Monacoinish.network);
	}
	try {
            localBlock = new SPVBlockStore(Monacoinish.network, new File("plugins/Monacoinish/h2.blockchain"));
            localChain = new BlockChain(Monacoinish.network, localWallet, localBlock);
	} catch (BlockStoreException ex) {
	    ex.printStackTrace();
	}
        localWallet.addEventListener(new CoinListener());

        localPeerGroup = new PeerGroup(Monacoinish.network, localChain);
        localPeerGroup.setUserAgent("MonacoinBukkit", "0.2");
        localPeerGroup.addWallet(localWallet);
        try {
	    localPeerGroup.addAddress(new PeerAddress(InetAddress.getByName("133.242.51.93"), 9401));
	    localPeerGroup.addAddress(new PeerAddress(InetAddress.getByName("133.242.19.247"), 9401));
	    localPeerGroup.addAddress(new PeerAddress(InetAddress.getByName("87.98.237.180"), 9401));
	} catch (UnknownHostException e) {
	    Monacoinish.log.info("Unknown host exception");
	    // TODO Auto-generated catch block
	}
        localPeerGroup.start();
	Monacoinish.log.info("localpeerrgroup start");
	try {
	    	    localPeerGroup.startBlockChainDownload(blockchainDownloadListener);
		    //	    localPeerGroup.downloadBlockChain();
	} catch (RuntimeException e) {
	    Monacoinish.log.info("downloadblockchain runtime exception\n" + e.getMessage());
	}
	Monacoinish.log.info("localpeergroup download blockchain");
        try {
	    Monacoinish.log.info("try loadblock.get");
	    StoredBlock b = localBlock.get(new Sha256Hash("64a9141746cbbe06c7e1a4b7f2abb968ccdeba66cd67c1add1091b29db00578e"));
	    Monacoinish.log.info("try done");
			
			
	    for (Transaction tx : localWallet.getTransactions(false)) {
				
	    }
	    System.out.println("Good TX's");
	    for (Transaction tx : localWallet.getTransactionsByTime()) {
			
	    }
	} catch (BlockStoreException e) {
	    Monacoinish.log.info("blockstoreexception " + e.getMessage());
	    e.printStackTrace();
	}
    }

    public PeerGroup getLocalPeerGroup() {
	return localPeerGroup;
    }

    public void setLocalPeerGroup(PeerGroup localPeerGroup) {
	this.localPeerGroup = localPeerGroup;
    }

    public Address genAddress() {
	ECKey key = new ECKey();
	localWallet.addKey(key);
	return key.toAddress(Monacoinish.network);
    }

    @Override
    protected void finalize() throws Throwable {
        localWallet.saveToFile(new File("plugins/Monacoinish/wallet.wallet"));
    }
	
    public boolean localSendCoins(Address a, double value) {
        BigInteger sendAmount = Monacoinish.econ.inGameToBitcoin(value);
        
        Wallet.SendRequest request = Wallet.SendRequest.to(a, sendAmount);
        request.fee = minBitFee;
        
	try {
	    localWallet.completeTx(request);
	} catch (InsufficientMoneyException e) {
	    Monacoinish.log.warning("Insufficient money exception: " + request.tx.getHash());
	    return false;
	}
        localPeerGroup.broadcastTransaction(request.tx);
        try {
	    try {
		localWallet.completeTx(request);
	    } catch (InsufficientMoneyException e) {
		Monacoinish.log.warning("Insufficient money exception: " + request.tx.getHash());
		return false;
	    }
	    localPeerGroup.broadcastTransaction(request.tx);
	    try {
		localWallet.commitTx(request.tx);
	    } catch (VerificationException e) {
		Monacoinish.log.warning("Verification failed: " + request.tx.getHash());
	    }
        } catch (IllegalArgumentException x){
	    Monacoinish.log.warning("Illegal argument: " + request.tx.getHash());
	    return false;
	}
	Monacoinish.log.warning("Sent transaction: " + request.tx.getHash());
	saveWallet();
	return true;

    }
	
    public boolean sendCoinsMulti(Map<Address, Double> toSend) {
	Transaction tx = new Transaction(Monacoinish.network);
	double totalSend = 0.0;
		
	for (Entry<Address, Double> current : toSend.entrySet()) {
	    totalSend += current.getValue() / Monacoinish.mult;
	    tx.addOutput(Monacoinish.econ.inGameToBitcoin(current.getValue()), current.getKey());
	}
		
	if (totalSend < 0.01) {
	    return false;
	}
		
	Wallet.SendRequest request = Wallet.SendRequest.forTx(tx);
		
	try {
	    localWallet.completeTx(request);
	} catch (InsufficientMoneyException e) {
	    Monacoinish.log.warning("Insufficient money exception: " + request.tx.getHash());
	    return false;
	}
	localPeerGroup.broadcastTransaction(request.tx);
	try {
	    localWallet.commitTx(request.tx);
	} catch (VerificationException e) {
	    e.printStackTrace();
	}
	return true;
    }


    public void saveWallet() {
	try {
            localWallet.saveToFile(walletFile);
	    // localPeerGroup.stop();
	} catch (IOException e1) {
	    e1.printStackTrace();
	}
    }

    public Wallet getWallet() {
        return localWallet;
    }

    public BlockChain getChain() {
        return localChain;
    }
    
    public void reloadWallet() {
    	localPeerGroup.stop();
    	localWallet.clearTransactions(0);
    	new File("plugins/Monacoinish/spv.blockchain").delete();
    	localPeerGroup.start();
    	localPeerGroup.downloadBlockChain();
    }

    public SPVBlockStore getLocalBlock() {
	return localBlock;
    }
}
