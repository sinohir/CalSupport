package com.example.app.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.app.dao.OperationDao;
import com.example.app.dao.OrderSheetDao;
import com.example.app.domain.CalData;
import com.example.app.domain.OperationRecord;
import com.example.app.domain.OrderSheet;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/customer")
public class CustomerCtrl {
	@Autowired 
	OrderSheetDao orderSheetDao;
	@Autowired 
	OperationDao operationDao;
	
	
	@GetMapping("/0")
	public String orderSheetList(
			HttpSession session, 
			Model model) throws Exception {
		Integer cuId = (Integer)session.getAttribute("cuId");
		List<OrderSheet> orderSheetList = orderSheetDao.selectListByCuId(cuId);
		model.addAttribute(orderSheetList);
		return "customer/list";
	}

	@GetMapping("/1")
	public String opRecord() {
		return "redirect:/customer";
	}
	@PostMapping("/1")
	public String opRecord(
		@RequestParam(name="itemId") Integer itemId,	
		HttpSession session, 
		Model model) throws Exception {
		OperationRecord opRecord = operationDao.selectOpRecordByEntryId(itemId);
		model.addAttribute("opRecord", opRecord);
		model.addAttribute("itemId", itemId);
		return "customer/opSheet";
	}
	
	@GetMapping("/2")
	public String dummy2() {
		return "redirect:/customer";
	}
	@PostMapping("/2")
	public String showCalData(
			@RequestParam(name="itemId") Integer itemId,
			HttpSession session,
			Model model) throws Exception{	
		List<CalData> dataList = operationDao.selectDataByEntryId(itemId);
		Integer disp0 = 0;
		for(CalData data: dataList) {
			String unit = "";
			String func = "";
			String param = data.getParam();
			char code = data.getCode().charAt(0);			
			if(param.equals("")) func = "DC";
			else	if(code == 'f' || code == 'F') param += "V";
			else { param += "Hz"; func = "AC";}		
			data.setDParam(param);
			switch (code) {
				case 'v':	unit = "mV";	func += unit;		break;
				case 'V':	unit = "V";		func += unit;		break;
				case 'u':	unit = "μA";		func += unit;		break;
				case 'a':	unit = "mA";	func += unit;		break;
				case 'A':	unit = "A";		func += unit;		break;
				case 'o':	unit = "Ω"; 		func = "OHM";	break;
				case 'O':  unit = "kΩ";		func = "OHM";	break;
				case 'M':  unit = "MΩ";	func = "OHM";	break;
				case 'f':	unit = "Hz";		func = "周波数";	break;
				case 'F':	unit = "kHz";	func = "周波数";	break;
			}
			data.setFunc(func);
			data.setUnit(unit);
			if(data.getDisplay0() != null) disp0 = 1;
		}
		model.addAttribute("opName", (String)session.getAttribute("opName"));
		model.addAttribute("disp0", disp0);
		model.addAttribute("itemId", itemId);
		model.addAttribute("dataList", dataList);
		return "customer/calDataView";
	}	
}
