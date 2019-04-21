package com.attrahackathon.request;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "kyc_mapping")
public class KycMapping {
	
	@Id
	private String primaryIdNumber;
	
	private String uniqueHexCode;

	public String getPrimaryIdNumber() {
		return primaryIdNumber;
	}

	public void setPrimaryIdNumber(String primaryIdNumber) {
		this.primaryIdNumber = primaryIdNumber;
	}

	public String getUniqueHexCode() {
		return uniqueHexCode;
	}

	public void setUniqueHexCode(String uniqueHexCode) {
		this.uniqueHexCode = uniqueHexCode;
	}

}
