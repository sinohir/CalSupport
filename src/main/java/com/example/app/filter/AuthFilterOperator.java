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

public class AuthFilterOperator implements Filter{
	public void doFilter(ServletRequest request,
		ServletResponse response, FilterChain chain) 
				throws IOException, ServletException {			 
	
		
			HttpServletRequest req = (HttpServletRequest) request;
			HttpServletResponse res = (HttpServletResponse) response;
			HttpSession session = req.getSession();
			
			if (session.getAttribute("opName") == null) {
			 	res.sendRedirect("../login/operation");
			return;
		}			 	
		 chain.doFilter(request, response);
		 }
}
