package com.example.app.controller;

import java.util.List;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.dao.CustomerDao;
import com.example.app.dao.LoginDao;
import com.example.app.domain.CustomerInfo;
import com.example.app.domain.Login;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/admin")
public class AdminCtrl {
	
	private static final int NUM_PER_PAGE = 10;
		
		@Autowired
		CustomerDao customerDao; 
		@Autowired
		LoginDao loginDao;
		@Autowired
		NumericCov numericCov;
		
		Integer page0 = 1;
	/*
		@GetMapping("login")
		public String login(RedirectAttributes redirectAttribute ){
			return "redirect:/login/admin";
		}
		*/
		@GetMapping("/0")
		public String cuListGet(
			@RequestParam(name = "page", defaultValue = "0") Integer page,
			HttpSession session, Model model) throws Exception {
			if(page == 0) page = page0;
			else page0 = page;
			int offset = (page - 1) * NUM_PER_PAGE;
			List<CustomerInfo> customerList = customerDao.selectPartial(offset, NUM_PER_PAGE);
			Integer totalPages = (int)((customerDao.count() + NUM_PER_PAGE -1) / NUM_PER_PAGE);
			model.addAttribute("adName", (String)session.getAttribute("adName"));
			model.addAttribute("page", page);
			model.addAttribute("totalPages", totalPages);
			model.addAttribute("customers", customerList);
			 return "admin/cuList";
		}	
		@PostMapping("/0")
		public String searchListPost(
				@RequestParam(name = "page", defaultValue = "1") Integer page,
				@RequestParam(name = "keywd") String keyword,
				HttpSession session, Model model) throws Exception {
			List<CustomerInfo> customerList = customerDao.selectByKeyword(keyword);
			model.addAttribute("asName", (String)session.getAttribute("adName"));
			model.addAttribute("page", 1);
			model.addAttribute("totalPages", 1);
			model.addAttribute("customers", customerList);
			return "admin/cuList";
		}

		@GetMapping("/{id}")
		public String listGet(
			@PathVariable("id") Integer id, 
			HttpSession session, Model model) throws Exception {
			model.addAttribute("adName", (String)session.getAttribute("adName"));
			List<Login> stfList;
			if(id == 1) {
				stfList = loginDao.selectAssistantList();
				model.addAttribute("title", "事務担当者リスト");
			} else if(id == 2) {
				stfList = loginDao.selectOperatorList();
				model.addAttribute("title", "作業担当者リスト");
			} else {
				stfList = loginDao.selectAdminList();
				model.addAttribute("title", "管理者リスト");
			}
			model.addAttribute("adName", (String)session.getAttribute("adName"));
			model.addAttribute("stfList", stfList); 
			model.addAttribute("listId", id);
			return "admin/staffList";
		}	
			  
		@GetMapping("/cuShow/{id}")
		public String cuShowGet(@PathVariable("id") Integer id, 
				Model model, HttpSession session) throws Exception {
			CustomerInfo customer = customerDao.selectById(id);
			numericCov.zipString(customer);
			numericCov.phoneString(customer);
			model.addAttribute("adName", (String)session.getAttribute("adName"));
			model.addAttribute("customer", customer);
			return "admin/cuShow";
		}	
		@GetMapping("/show/{listId}/{id}")
		public String showGet(
				@PathVariable("listId") Integer listId,
				@PathVariable("id") Integer id, 
				@RequestParam(name="name") String name,
				@RequestParam(name="loginId") String loginId,
				Model model, 
				HttpSession session) throws Exception{
			model.addAttribute("title", "事務担当者ログイン情報");
			model.addAttribute("listId", listId);
			model.addAttribute("id", id);
			model.addAttribute("name", name);
			model.addAttribute("loginId", loginId);
			return "admin/show";
		}
		@GetMapping("/cuEdit/{id}")
		public String editGet(
			@PathVariable("id") Integer id, 
				Model model, HttpSession session) throws Exception{ 
			CustomerInfo customer;
			if(id==0){
				 customer = new CustomerInfo();
				customer.setId(0);
				model.addAttribute("title", "顧客リストの追加");
			} else {
				customer = customerDao.selectById(id);
				numericCov.zipSep(customer);
				model.addAttribute("title", "顧客情報の編集");
			}
			model.addAttribute("customer", customer);
			model.addAttribute("adName", (String)session.getAttribute("adName"));
			return "admin/cuEdit";
		}

		@PostMapping("/cuEdit/{id}")
		public String editPost(@Valid @PathVariable("id") Integer id,
				@Valid @ModelAttribute("customer")  CustomerInfo customer, 
				Errors errors, 
				HttpSession session,
				Model model,
				RedirectAttributes redirectAttributes) throws Exception{
			//customer.setId(id);
			boolean errMsg = false;
			if((customer.getZip1() != null && customer.getZip2() == null) ||
					(customer.getZip1() == null && customer.getZip2() != null)) {
				model.addAttribute("errMessageZ", "郵便番号を正しく記入してください。");
				errMsg = true;
			}
			if (errors.hasErrors()  || errMsg) {
				//List<ObjectError> allErrors = errors.getAllErrors();
				//for(ObjectError error : allErrors) System.out.println(error)
				
				if(id==0) model.addAttribute("title", "顧客リストの追加");
				else model.addAttribute("title", "顧客情報の編集");
				model.addAttribute("customer", customer);
				model.addAttribute("adName", (String)session.getAttribute("adName"));
				return "admin/cuEdit";
			}
			if(customer.getZip1() != null && customer.getZip1() != null ) 	numericCov.zipMpx(customer);
			if(id==0) {
				customerDao.insert(customer); 
				redirectAttributes.addAttribute("id", customer.getId());
				redirectAttributes.addFlashAttribute("message", "新規に顧客情報を追加しました。");
			}else {
			customerDao.update(customer);
			redirectAttributes.addFlashAttribute("message", "顧客情報を編集しました。");
			}
			return "redirect:/admin/cuShow/{id}";
		}	

		@GetMapping("/websvc/{listId}/{id}")
		public String wevsvcGet(
				@PathVariable("listId") Integer listId, 	
				@PathVariable("id") Integer id, 
				@RequestParam(name="name") String name,
				@RequestParam(name="loginId") String loginId,
				Model model, HttpSession session) throws Exception{ 
			Login loginInfo = new Login();
			loginInfo.setId(id);
			loginInfo.setLoginId(loginId);
			loginInfo.setName(name);
			model.addAttribute("listId", listId);
			model.addAttribute("id0", loginId);
			model.addAttribute("loginInfo", loginInfo);
			model.addAttribute("adName", (String)session.getAttribute("adName"));
			return "admin/websvc";
		}
		
		@PostMapping("/websvc/{listId}/{id}")
		public String websvcPost(
				@PathVariable("listId") Integer listId,
				@PathVariable("id") Integer id,
				@RequestParam(name="id0") String loginId0,
				@Valid @ModelAttribute("loginInfo") Login loginInfo,
				Errors errors, 
				HttpSession session,
				Model model,
				RedirectAttributes redirectAttributes) throws Exception{
			model.addAttribute("id0", loginId0);
			model.addAttribute("loginInfo", loginInfo);
			model.addAttribute("adName", (String)session.getAttribute("adName"));
			if(id == 0) {
				if(errors.hasErrors()) {
					if(loginInfo.getLoginPass().length() < 4  ) model.addAttribute("errMessage", "登録にはパスワードが必須です。");
					return "admin/websvc";
				}
				loginInfo.setLoginPass( BCrypt.hashpw(loginInfo.getLoginPass(), BCrypt.gensalt()));
				if(listId == 1) loginDao.insertNewAsAcc(loginInfo);
				else if(listId == 2) loginDao.insertNewOpAcc(loginInfo);
				else if(listId == 3) loginDao.insertNewAdAcc(loginInfo);
				redirectAttributes.addFlashAttribute("message", "新規登録を完了しました。");
				return "redirect:/admin/{listId}";
			}
			if (errors.hasFieldErrors("loginId")) {
				//List<ObjectError> allErrors = errors.getAllErrors();
				//for(ObjectError error : allErrors) System.out.println(error)
				return "admin/websvc";
			}
			if(errors.hasErrors()) {
				if(loginId0.length() < 1) {
					model.addAttribute("errMessage", "登録にはパスワードが必須です。");
					return "admin/websvc";
				} else if(!loginInfo.getLoginId().equals(loginId0)){
					if(listId == 0)customerDao.newLoginId(loginInfo);
					else if(listId == 1) loginDao.newLoginAsId(loginInfo);
					else if(listId == 2) loginDao.newLoginOpId(loginInfo);
					else loginDao.newLoginAdId(loginInfo);
					redirectAttributes.addFlashAttribute("message", "WebサービスのIDを更新をしました。");	
				}
			} else{
				loginInfo.setLoginPass( BCrypt.hashpw(loginInfo.getLoginPass(), BCrypt.gensalt()));
				if(listId == 0) customerDao.newLoginPass(loginInfo);
				else if(listId == 1) loginDao.newLoginAsPass(loginInfo);
				else if(listId == 2) loginDao.newLoginOpPass(loginInfo);
				else loginDao.newLoginAdPass(loginInfo);
				redirectAttributes.addFlashAttribute("message", "Webサービスのパスワードを更新をしました。");
			}
			if(listId == 0) return "redirect:/admin/cuShow/{id}";
			redirectAttributes.addAttribute("name", loginInfo.getName());
			redirectAttributes.addAttribute("loginId", loginInfo.getLoginId());
			return "redirect:/admin/show/{listId}/{id}"; 
		}	
		
		@GetMapping("/delMember/{listId}/{id}")
		public String delMemberGet(
				@PathVariable("listId") Integer listId,
				@PathVariable("id") Integer id,
				@RequestParam(name="name") String name,
				RedirectAttributes redirectAttributes) {
			if(listId==0) customerDao.deleteById(id);
			else if(listId==1) loginDao.deleteAsById(id);
			else if(listId==2) loginDao.deleteOpById(id);
			else if(listId==3) loginDao.deleteAdById(id);
			redirectAttributes.addFlashAttribute("message", name+"の削除を完了しました。");
			return "redirect:/admin/{listId}";
		}
		
		@ExceptionHandler(DataAccessException.class)
	    public String dataAccessExceptionHandler(DataAccessException e, Model model) {

			if(e.getMessage().indexOf("'customer_list.phone_UNIQUE'") != -1)
				 model.addAttribute("message", "電話番号が既に登録済のものです。");
			else model.addAttribute("message", e);
			
	        // 例外クラスのメッセージをModelに登録
	        model.addAttribute("error", "内部サーバーエラー（DB)");
	        
	        // HTTPのエラーコード（500）をModelに登録
	        model.addAttribute("status", HttpStatus.INTERNAL_SERVER_ERROR);
	        return "error";
	    }
}


