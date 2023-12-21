package com.example.app.filter;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthFilterCustomer implements Filter{
	public void doFilter(ServletRequest request,
		ServletResponse response, FilterChain chain) 
				throws IOException, ServletException {			 
	
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			HttpSession session = req.getSession();
			
			if (session.getAttribute("cuId") == null) {
			 	res.sendRedirect("../login/customer");
			return;
		}			 	
		 chain.doFilter(request, response);
		 }
}
