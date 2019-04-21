package com.attrahackathon.controller;


import io.ipfs.multihash.Multihash;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.attrahackathon.ipfs.IPFSImplimentation;
import com.attrahackathon.repository.KycHexMappingRepository;
import com.attrahackathon.repository.KycRepository;
import com.attrahackathon.request.IdType;
import com.attrahackathon.request.KycMapping;
import com.attrahackathon.request.KycRequest;
import com.connectBlockChain.ConnectBlockchainService;


@CrossOrigin("*")
@RestController
@RequestMapping("/hackathon/ekyc")
public class FrontController {
	
	@Autowired
	private IPFSImplimentation ipfsImplimentation;
	
	@Autowired
	private KycRepository kycRepository;
	
	@Autowired
	private KycHexMappingRepository kycHexMappingRepository;
	
	@Autowired
	private ConnectBlockchainService connectBlockchainService;
	
	private final Logger LOGGER = LoggerFactory.getLogger(this.getClass());
	
	/**
	 * It will be called after USER/REG-BODY/BANK submits KYC requests
	 * @param kycRequest
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/submiteKyc")
	public Object executeSubmiteKyc(@RequestBody KycRequest kycRequest) throws Exception {
		LOGGER.info("Inside Submit KYC");
		for (IdType idType : kycRequest.getIdType()) {
			if (idType.getType().equalsIgnoreCase("AADHAR")) {
				kycRequest.setPrimaryIdNumber(idType.getIdNumber());
				kycRequest.setPrimaryIdType(idType.getType());
			} 
		}
		Optional<KycRequest> storedKyc = kycRepository.findById(kycRequest.getPrimaryIdNumber());
		if (!storedKyc.isPresent()) {
			kycRequest.setState("PENDING");
			kycRepository.save(kycRequest);
			return "Your e-Kyc request  is send for verification!";
		}
		return storedKyc.get();
	}
	
	/**
	 * 
	 * @param kycRequest
	 * @param status
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/verifyKyc/{status}")
	public String executeVerifyKyc(@RequestBody KycRequest kycRequest, @PathVariable String status) throws Exception {
		LOGGER.info("Inside VERIFY KYC");
		// For getting kyc object of the user please refer the primaryIdNumber and primaryIdType
		Optional<KycRequest> storedKyc = kycRepository.findById(kycRequest.getPrimaryIdNumber());
		if (status.equalsIgnoreCase("APPROVED")) {
			Multihash multiHash = ipfsImplimentation.saveFileUsingByteArray(storedKyc.get());
			KycMapping kycMapping = new KycMapping();
			kycMapping.setUniqueHexCode(multiHash.toString());
			kycMapping.setPrimaryIdNumber(kycRequest.getPrimaryIdNumber()); // Need to encrypt it.
			kycHexMappingRepository.save(kycMapping);
			kycRepository.deleteById(kycRequest.getPrimaryIdNumber());
			
			//call blockchainservice for setting information
			connectBlockchainService.setInfo(kycRequest.getPrimaryIdNumber(), "abcd@123", multiHash.toString());
		} else if (status.equalsIgnoreCase("REJECTED")) {	
			storedKyc.get().setStatus("REJECTED");
			kycRepository.save(storedKyc.get());
		}
		return "Operation performed successfully !";
	}
	
	/**
	 * 
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/getAllUnverified")
	public List<KycRequest> executeGetAllUnverifiedKyc() throws Exception {
		LOGGER.info("Inside get All unverified data");
		return kycRepository.findAll();
	}
	
	/**
	 * 
	 * @param hexCode
	 * @return
	 * @throws Exception
	 */
	@PostMapping("getDataUsingHex/{hexCode}")
	public KycRequest executeGetDataUsingHex(@PathVariable String hexCode) throws Exception {
		LOGGER.info("Inside get All unverified data");
		return ipfsImplimentation.getData(hexCode);
	}
	
}
