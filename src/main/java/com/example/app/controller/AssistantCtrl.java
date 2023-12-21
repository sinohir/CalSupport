package com.example.app.controller;

import java.text.DecimalFormat;
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
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.dao.CustomerDao;
import com.example.app.dao.OrderSheetDao;
import com.example.app.domain.CustomerInfo;
import com.example.app.domain.OrderSheet;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/assistant")
public class AssistantCtrl {

	@Autowired
	OrderSheetDao orderSheetDao;
	@Autowired
	CustomerDao		customerDao;
	@Autowired
	NumericCov 		numericCov;
		
	
		@GetMapping("/{listId}")
		public String potalGet( @PathVariable("listId") Integer listId,
			HttpSession session, Model model) throws Exception {
			List<OrderSheet> orderSheetList; 
			switch(listId) {
				case 1: orderSheetList = orderSheetDao.selectListUnderOperation(); break;
				case 2: orderSheetList = orderSheetDao.selectListAfterOperation(); break;
				default:  orderSheetList = orderSheetDao.selectListBeforeOperation(); break;
			}
			model.addAttribute("orderSheetList", orderSheetList);
			model.addAttribute("listId", listId);
			model.addAttribute("asName", (String)session.getAttribute("asName"));
			return "assistant/list";
		}

		@GetMapping("/check/{id1}")
		public String invoiceGet(
				@PathVariable("id1") Integer listId, 
				@RequestParam(name="itemId") Integer itemId,
				Model model, HttpSession session) throws Exception {
			 OrderSheet orderSheet;
			 if(listId == 0) orderSheet = orderSheetDao.selectViewById(itemId);
			 else orderSheet = orderSheetDao.selectViewAfterInceptById(itemId);
			String stack = orderSheet.getAsComment();
			if(stack == null) stack = "";
			model.addAttribute("asName", (String)session.getAttribute("asName"));
			model.addAttribute("orderSheet", orderSheet);
			model.addAttribute("itemId", itemId);
			model.addAttribute("memo", stack);
			return "assistant/check";
		}	
		@PostMapping("/check/{id1}") 
			public String invoicePost( 
				@PathVariable("id1") Integer listId,
				@RequestParam(name="itemId") Integer itemId,
				@RequestParam(name = "memo") String memo,
				@RequestParam(name = "memoOrg") String memoOrg,
				RedirectAttributes redirectAttributes,
				HttpSession session) throws Exception {
					if(!memo.equals(memoOrg)) {
						orderSheetDao.updateAsComment( itemId, memo);
						redirectAttributes.addFlashAttribute("message", "メモを更新しました。");	
					}
					return "redirect:/assistant/"+ listId;
			}
		@PostMapping("/check/0/0")
		public String shipping0Post(
				@ModelAttribute OrderSheet orderSheet,
				@RequestParam(name = "itemId") Integer itemId,
				Model model) throws Exception {
			orderSheet.setEntryId(itemId);
			return "assistant/shipping0";
		}
		@PostMapping("/check/0/1")
		public String shipping1Post(
				@Valid @ModelAttribute OrderSheet orderSheet, Errors errors,
				Model model) throws Exception {
			if (errors.hasFieldErrors("laborCharge")) return "assistant/shipping0";
			DecimalFormat decFormat = new DecimalFormat("###,###");
			String fCharge = decFormat.format(orderSheet.getLaborCharge());
			model.addAttribute("fCharge", fCharge);
			return "assistant/shipping1";
		}
		@PostMapping("/check/0/2")
		public String opComplation1Post(
				@ModelAttribute OrderSheet orderSheet,
				RedirectAttributes redirectAttributes) throws Exception {
			orderSheetDao.updateByShipping(orderSheet);
			redirectAttributes.addFlashAttribute("message", "出荷処理を完了しました、お疲れ様でした。");	
			return "redirect:/assistant/2";
		}
			
		@GetMapping("/search")
		public String customerListGet(
			@RequestParam(name = "page", defaultValue = "1") Integer page,
			HttpSession session, Model model) throws Exception {
				final int NUM_PER_PAGE = 10;
				int offset = (page - 1) * NUM_PER_PAGE;
				List<CustomerInfo> customerList = customerDao.selectPartial(offset, NUM_PER_PAGE);
				Integer totalPages = (int)((customerDao.count() + NUM_PER_PAGE -1) / NUM_PER_PAGE);
				model.addAttribute("asName", (String)session.getAttribute("asName"));
				model.addAttribute("page", page);
				model.addAttribute("totalPages", totalPages);
				model.addAttribute("customers", customerList);
				return "assistant/search";
		}
		@PostMapping("/search")
		public String searchListPost(
				@RequestParam(name = "page", defaultValue = "1") Integer page,
				@RequestParam(name = "keywd") String keyword,
				HttpSession session, Model model) throws Exception {
			List<CustomerInfo> customerList = customerDao.selectByKeyword(keyword);
			model.addAttribute("asName", (String)session.getAttribute("asName"));
			model.addAttribute("page", 1);
			model.addAttribute("totalPages", 1);
			model.addAttribute("customers", customerList);
			return "assistant/search";
		}
		
		@GetMapping("/customer/{id1}/{id2}")
		public String showCustomer(
					@PathVariable("id1") Integer listId,
					@PathVariable("id2") Integer linkId,
					@RequestParam(name="cuId") Integer cuId,
					Model model, 
					HttpSession session) throws Exception {
			 CustomerInfo customer = customerDao.selectById(cuId);
			 numericCov.zipString(customer);
			 numericCov.phoneString(customer);
			 model.addAttribute("asName", (String)session.getAttribute("asName"));
			 model.addAttribute("customer", customer);
			 model.addAttribute("listId", listId);
			 model.addAttribute("linkId", linkId);
			 return "assistant/customer";
		}
		@PostMapping("/customer/0/0")
		public String customerPost0( 
				@ModelAttribute OrderSheet orderSheet,
				Model model, 
				HttpSession session) throws Exception{ 
			model.addAttribute("asName", (String)session.getAttribute("asName"));
			model.addAttribute ("orderSheet", orderSheet);
			return "assistant/add";
		}
		@PostMapping("/customer/0/1")
		public String fillCustomerInfo( 
				@Valid @ModelAttribute OrderSheet orderSheet, Errors errors,
				Model model, 
				HttpSession session) throws Exception{ 
			model.addAttribute("asName", (String)session.getAttribute("asName"));			
			model.addAttribute ("orderSheet", orderSheet);
			if (errors.hasErrors()) {
				return "assistant/add";
			}
			return "assistant/confirm";
		}
	
		@PostMapping("/customer/0/2")
		public String confirmCustomerInfo( 
				@ModelAttribute OrderSheet orderSheet,
				RedirectAttributes redirectAttributes,
				Model model, 
				HttpSession session) throws Exception{ 
			model.addAttribute("asName", (String)session.getAttribute("asName"));
			orderSheet.setAsId(1);
			orderSheetDao.newSheet(orderSheet);
			redirectAttributes.addFlashAttribute("message", "新規の受付を完了しました。");
			return "redirect:/assistant/0";
		}
}
