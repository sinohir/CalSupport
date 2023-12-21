package com.example.app.controller;

import org.springframework.stereotype.Controller;

import com.example.app.domain.CustomerInfo;

@Controller
public class NumericCov {
	protected void zipString(CustomerInfo cuInfo) {
		Integer zip = cuInfo.getZip();
		if(zip ==null) return;
		StringBuilder sbZip = new StringBuilder("%07d".formatted(zip));
		cuInfo.setZipStr(sbZip.insert(3, "-").toString());
	}
	protected void zipSep(CustomerInfo cuInfo) {
		Integer zip = cuInfo.getZip();
		if(zip  == null) return;
		cuInfo.setZip1(zip / 10000); 
		cuInfo.setZip2(zip % 10000); 
	}
	protected void zipMpx(CustomerInfo cuInfo) {
		cuInfo.setZip(cuInfo.getZip1()*10000 + cuInfo.getZip2()); 
	}
	protected void phoneString(CustomerInfo cuInfo) {
		Integer phoneNum = cuInfo.getPhone();
		if(phoneNum==null) return;
		String phone = phoneNum.toString();
		int length = phone.length();
		cuInfo.setPhoneStr(cuInfo.getPhoneEx().toString()
								+ cuInfo.getPhoneDm().toString() 
								+ '-' + phone.substring(0,length-4) 
								+ '-' + phone.substring(length-4));
	}	
}
