package com.example.app.controller;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.app.dao.LoginDao;
import com.example.app.domain.Login;

import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;

@Controller
@RequestMapping("/login")
public class LoginCtrl {

	@Autowired
	LoginDao loginDao;
	
	@GetMapping("/assistant")
	 public String assistant(Model model) {
		model.addAttribute("login", new Login());
		model.addAttribute("who", "事務担当者");
		return "login";
	 } 
	@PostMapping("/assistant")
	 public String assistant(
			 @Valid Login login,
			 Errors errors,
			 HttpSession session,
			 Model model) throws Exception {
		model.addAttribute("who", "事務担当者");
		 if (errors.hasFieldErrors("loginId") || errors.hasFieldErrors("loginPass") ) return "login";
		 Login archive = loginDao.selectAuthAsByLoginId(login.getLoginId());
		 if (archive == null || !BCrypt.checkpw(login.getLoginPass(), archive.getLoginPass())) {
			 errors.rejectValue("loginId", "error.incorrect_id_password");
			 return "login";
		 }
		 session.setAttribute("asName", archive.getName());
		 return "redirect:/assistant/0";
	}
	@GetMapping("/operation")
	 public String operation(Model model) {
		model.addAttribute("login", new Login());
		model.addAttribute("who", "作業担当者");
		return "login";
	 } 
	@PostMapping("/operation")
	 public String operation(
			 @Valid Login login,
			 Errors errors,
			 HttpSession session,
			 Model model) throws Exception {
		model.addAttribute("who", "作業担当者");
		 if (errors.hasFieldErrors("loginId") || errors.hasFieldErrors("loginPass") ) return "login";
		 Login archive = loginDao.selectAuthOpByLoginId(login.getLoginId());
		 if (archive == null || !BCrypt.checkpw(login.getLoginPass(), archive.getLoginPass())) {
			 errors.rejectValue("loginId", "error.incorrect_id_password");
			 return "login";
		 }
		 session.setAttribute("opName", archive.getName());
		 return "redirect:/operation/0";
	}
	@GetMapping("/admin")
	 public String admin(Model model) {
		model.addAttribute("login", new Login());
		model.addAttribute("who", "管理者");
		return "login";
	 } 
	@PostMapping("/admin")
	 public String admin(
			 @Valid Login login,
			 Errors errors,
			 HttpSession session,
			 Model model) throws Exception {
		model.addAttribute("who", "管理者");
		 if (errors.hasFieldErrors("loginId") || errors.hasFieldErrors("loginPass") ) return "login";
		 Login archive = loginDao.selectAuthAdByLoginId(login.getLoginId());
		 if (archive == null || !BCrypt.checkpw(login.getLoginPass(), archive.getLoginPass())) {
			 errors.rejectValue("loginId", "error.incorrect_id_password");
			 return "login";
		 }
		 session.setAttribute("adName", archive.getName());
		 return "redirect:/admin/0";
	}
	@GetMapping("/customer")
	 public String customer(Model model) {
		model.addAttribute("login", new Login());
		model.addAttribute("who", "会員");
		return "login";
	 } 
	@PostMapping("/customer")
	 public String customer(
			 @Valid Login login,
			 Errors errors,
			 HttpSession session,
			 Model model) throws Exception {
		model.addAttribute("who", "会員");
		 if (errors.hasFieldErrors("loginId") || errors.hasFieldErrors("loginPass") ) return "login";
		 Login archive = loginDao.selectAuthCuByLoginId(login.getLoginId());
		 if (archive == null || !BCrypt.checkpw(login.getLoginPass(), archive.getLoginPass())) {
			 errors.rejectValue("loginId", "error.incorrect_id_password");
			 return "login";
		 }
		 session.setAttribute("cuId", archive.getId());
		 return "redirect:/customer/0";
	}
	@GetMapping("/logout/{path}")
	 public String logout(
			 @PathVariable("path") Integer path,
			 HttpSession session, 
			 RedirectAttributes redirectAttributes) {
		 session.invalidate();
		 redirectAttributes.addFlashAttribute("message", "ログアウトしました。");
		 switch(path) {
		 	case 0: return		"redirect:/login/admin";
		 	case 1:	 return		"redirect:/login/operation";
		 	case 2:	 return		"redirect:/login/assistant";
		 	default: return	"redirect:/login/customer";
		 }
	 }
}