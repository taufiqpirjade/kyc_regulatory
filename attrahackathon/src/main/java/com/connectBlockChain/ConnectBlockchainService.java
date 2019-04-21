package com.connectBlockChain;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.stereotype.Service;
import org.web3j.crypto.CipherException;
import org.web3j.crypto.Credentials;
import org.web3j.crypto.WalletUtils;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

@Service
public class ConnectBlockchainService {
	
	private Web3j web3j;
	
	private String ip = "http://127.0.0.1:8545";
	
	private String walletFolderPath = "E:\\Blockchain\\Hackathon\\blockchain\\";
	
	private String accountAddress = "0x3c537e01317d9dd6792be803659d60aee2bfdb24";
	
	private void setInfo(String username, String password, String data) throws Exception {
		Register_sol_Register contract  = getContract(username,password);
		byte[] array = new byte[32];
		array = Arrays.copyOfRange(new String(data).getBytes(), 0, 32);
		contract.setInfo(array).send();
	}
	
	private byte[] getInfo(String username, String password) throws Exception {
		Register_sol_Register contract  = getContract(username,password);
		return contract.getInfo().send();
	}
	
	public String createWallet(String username) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException {
		String password = "ZsdW#2134";
		String fileName = generateWallet(password, username);
		return fileName;
	}
	
	public Web3j getBlockchainConnection(String password, String username) throws Exception {
		if (web3j == null) {
			web3j = Web3j.build(new HttpService(ip));  // defaults to http://localhost:8545/
		}
		return web3j;
	}
	
	public String generateWallet(String password, String username) throws InvalidAlgorithmParameterException, NoSuchAlgorithmException, NoSuchProviderException, CipherException, IOException {
		File file  = new File(walletFolderPath+username+"\\");
		if (!file.exists()) {
			String fileName = WalletUtils.generateNewWalletFile(password,new File(walletFolderPath+username+"\\"));
			return fileName;
		} else {
			File[] files = file.listFiles();
			if (files != null && files.length > 0) {
				return files[0].getName();
			} else {
				String fileName = WalletUtils.generateNewWalletFile(password,new File(walletFolderPath+username+"\\"));
				return fileName;
			}
		}
	}
	
	private Register_sol_Register getContract(String username, String password) throws IOException, CipherException {
		Credentials credentials = WalletUtils
				.loadCredentials(password,walletFolderPath+"\\"+username+"\\");
		Register_sol_Register contract = 
				Register_sol_Register.load(accountAddress,web3j, credentials, new BigInteger("0"), new BigInteger("1000000"));
		return contract;
	}

}
