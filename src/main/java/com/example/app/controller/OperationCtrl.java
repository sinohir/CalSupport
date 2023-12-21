package com.example.app.controller;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.dao.CustomerDao;
import com.example.app.dao.OperationDao;
import com.example.app.dao.OrderSheetDao;
import com.example.app.domain.CalData;
import com.example.app.domain.CustomerInfo;
import com.example.app.domain.OperationRecord;
import com.example.app.domain.OrderSheet;
import com.example.app.domain.UploadForm;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@RequestMapping("/operation")

@Controller
public class OperationCtrl {

	@Autowired OperationDao operationDao;
	@Autowired OrderSheetDao orderSheetDao;
	@Autowired CustomerDao customerDao;
	@Autowired NumericCov numericCov;
	
	@GetMapping("/{listId}")
	public String potalGet( @PathVariable("listId") Integer listId,
		HttpSession session, Model model) throws Exception {
		List<OrderSheet> orderSheetList; 
		switch(listId) {
			case 1: orderSheetList = orderSheetDao.selectListUnderOperationWithOpId(1);break;
			case 2: orderSheetList = orderSheetDao.selectListAfterOperationWithOpId(1); break;
			default:  orderSheetList = orderSheetDao.selectListBeforeOperation(); break;
		}
		model.addAttribute("orderSheetList", orderSheetList);
		model.addAttribute("listId", listId);
		model.addAttribute("opName", (String)session.getAttribute("opName"));
		return "operation/list";
	}
	
	@GetMapping("/customer/{listId}")
	public String showCustomer(
		@PathVariable("listId") Integer listId,
		@RequestParam(name="cuId") Integer cuId,
		Model model, HttpSession session) throws Exception {
		CustomerInfo customerInfo = customerDao.selectById(cuId);
		numericCov.zipString(customerInfo);
		numericCov.phoneString(customerInfo);
		model.addAttribute("opName", (String)session.getAttribute("opName"));
		model.addAttribute("customer", customerInfo);
		model.addAttribute("listId", listId);
		return "operation/customer";
	}
	@GetMapping("/checkin/0")
	public String checkinPrimaryGet(
		@ModelAttribute("opRecord") OperationRecord opRecord,
		@RequestParam(name="itemId") Integer itemId,
		Model model, HttpSession session) throws Exception {
		OrderSheet orderSheet = orderSheetDao.selectViewById(itemId);
		model.addAttribute("opName", (String)session.getAttribute("opName"));
		model.addAttribute("orderSheet", orderSheet);
		model.addAttribute("listId", 0);
		return "operation/checkin";
}
	@GetMapping("/checkin/{listId}")
	public String checkinGet(
			//@PathVariable("itemId") Integer itemId, 
			@PathVariable("listId") Integer listId, 
			@RequestParam(name="itemId") Integer itemId,
			Model model, HttpSession session) throws Exception {
			OrderSheet orderSheet = orderSheetDao.selectViewById(itemId);
			model.addAttribute("opName", (String)session.getAttribute("opName"));
			model.addAttribute("orderSheet", orderSheet);
			model.addAttribute("listId", listId);
			model.addAttribute("itemId", itemId);
			if(listId ==1) {
				String memo = orderSheet.getOpComment();
				if(memo == null) memo = "";
				model.addAttribute("memo", memo);
			}
			return "operation/checkin";
	}
	@PostMapping("/checkin/0")
	public String checkinReceptionPost(
			@ModelAttribute OperationRecord opRecord,
			RedirectAttributes redirectAttributes) throws Exception {
		opRecord.setOpId(1);
		operationDao.insertOpRecord(opRecord);
		orderSheetDao.inception(opRecord);
		redirectAttributes.addFlashAttribute("message", "新規の作業受付を完了しました。");
		return "redirect:/operation/1";
	}	
	@PostMapping("/checkin/{listId}")
	public String checkinWorkingPost(
			@PathVariable("listId") Integer listId,
			@RequestParam(name = "memo") String memo,
			@RequestParam(name = "memoOrg") String memoOrg,
			@RequestParam(name = "itemId") Integer itemId,
			RedirectAttributes redirectAttributes, 
			HttpSession session) throws Exception {
			if(!memo.equals(memoOrg)) {
				orderSheetDao.updateOpComment( itemId, memo);
				redirectAttributes.addFlashAttribute("message", "メモを更新しました。");	
			}
			return "redirect:/operation/" + listId;
		}
	@GetMapping("/opSheet/{listId}")
	public String opSheetGet(
				@PathVariable("listId") Integer listId,
				@RequestParam(name="itemId") Integer itemId,
				Model model, 
				HttpSession session) throws Exception {
		OperationRecord opRecord = operationDao.selectOpRecordByEntryId(itemId);
		if(operationDao.selectCompletionDateByEntryId(itemId) !=null) opRecord.setCompletion(true);
		String note = opRecord.getOpNote();
		if(note == null) note = "";
		model.addAttribute("note", note);
		model.addAttribute("opName", (String)session.getAttribute("opName"));
		model.addAttribute("opRecord", opRecord);
		model.addAttribute("uploadForm", new UploadForm());
		model.addAttribute("itemId", itemId);
		model.addAttribute("listId", listId);
		return "operation/opSheet";
	}
	@PostMapping("/opSheet/0/0")
	public String opComplation0Post(
				@ModelAttribute OrderSheet orderSheet,
				@RequestParam(name = "itemId") Integer itemId,
				Model model) throws Exception {
		orderSheet.setEntryId(itemId);
		return "operation/completion0";
	}
	@PostMapping("/opSheet/0/1")
	public String opComplation1Post(
			@Valid @ModelAttribute OrderSheet orderSheet, Errors errors,
			Model model) throws Exception {
		if (errors.hasFieldErrors("laborMin")) return "operation/completion0";
		return "operation/completion1";
	}
	@PostMapping("/opSheet/0/2")
	public String opComplation1Post(
			@ModelAttribute OrderSheet orderSheet,
			RedirectAttributes redirectAttributes) throws Exception {
		orderSheetDao.updateByOpCompletion(orderSheet);
		redirectAttributes.addFlashAttribute("message", "作業完了の処理を致しました、お疲れ様でした。");	
		return "redirect:/operation/2";
	}
	@PostMapping("/opSheet/{listId}")
	public String opSheetPost(
				@PathVariable("listId") Integer listId,
				@RequestParam(name = "note") String note,
				@RequestParam(name = "noteOrg") String noteOrg,
				@RequestParam(name = "itemId") Integer itemId, 
				RedirectAttributes redirectAttributes) throws Exception {
		if(!note.equals(noteOrg)) {
			operationDao.updateOpNote(itemId, note);
			redirectAttributes.addFlashAttribute("message", "作業ノートを更新しました。");	
		}
		redirectAttributes.addAttribute("itemId", itemId);
		return "redirect:/operation/checkin/1";
	}
	
	@PostMapping("/opSheet/1/{beaf}")
	public String opUploadPost(
				@PathVariable("beaf") Integer beaf,
				//@PathVariable("listId") Integer listId,
				@RequestParam(name="firstCal") Integer firstCal,
				@RequestParam(name="itemId") Integer itemId,
				@Valid UploadForm uploadForm, 
				RedirectAttributes redirectAttributes) throws Exception {
		MultipartFile uploadData = uploadForm.getUpData();
		InputStreamReader isr = new InputStreamReader(uploadData.getInputStream());
		BufferedReader br = new BufferedReader(isr);	 
		String csvLine = br.readLine();
		 String[] header = csvLine.split(",");
		 List<CalData> dataList = new ArrayList<>(); 
		 for(int n=1; (csvLine = br.readLine()) != null; n++) {
			 String[] data = csvLine.split(",");
			 CalData calData = new CalData(); 
			 calData.setOrder(n);
			 calData.setCode(data[0]);
			 calData.setMRange(data[1]);
			 calData.setApplied(data[2]);
			 calData.setParam(data[3]);
			 calData.setDisplay0(data[4]);
			 calData.setLower(data[5]);
			 calData.setUpper(data[6]);
			 dataList.add(calData);
		 }
		 br.close();
		 OperationRecord opRecord = new OperationRecord();
		 opRecord.setEntryId(itemId);
		 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		 Date date = sdf.parse(header[2]);
		 Calendar calendar = Calendar.getInstance();
         calendar.setTime(date);
         calendar.add(Calendar.DAY_OF_MONTH, Integer.parseInt(header[5]));
		 opRecord.setDueDate(calendar.getTime());
         if(beaf==0) {
			 opRecord.setDate0(date);
			 opRecord.setTemp0(header[3]);
			 opRecord.setHumid0(header[4]);
			 operationDao.insertListWithDataBef(itemId, dataList);
			 operationDao.updateOpRecordByBefData(opRecord);
			 redirectAttributes.addFlashAttribute("message", "整備前校正データをアップロードしました。");
		 } else {
			 opRecord.setDate1(date);
			 opRecord.setTemp1(header[3]);
			 opRecord.setHumid1(header[4]);
			 if(firstCal==0) operationDao.updateListWithDataAft(itemId, dataList);
			 else operationDao.insertListWithDataAft(itemId, dataList);
			 operationDao.updateOpRecordByAftData(opRecord);
			 redirectAttributes.addFlashAttribute("message", "整備後校正データをアップロードしました。");
		 }
		 redirectAttributes.addAttribute("listId", 1);
		 redirectAttributes.addAttribute("itemId", itemId);
		 return "redirect:/operation/opSheet/1";
	}	
	@GetMapping("/caldata/{listId}")
	public String showCalData(
			@PathVariable("listId") Integer listId,
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
		model.addAttribute("listId", listId);
		model.addAttribute("itemId", itemId);
		model.addAttribute("dataList", dataList);
		return "operation/calDataView";
	}	
}
