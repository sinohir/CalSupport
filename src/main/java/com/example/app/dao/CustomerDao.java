package com.example.app.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.CustomerInfo;
import com.example.app.domain.Login;

@Mapper
public interface CustomerDao {
	
	List<CustomerInfo> selectPartial
 		(@Param("offset") int offset, @Param("num") int num) throws Exception; 
	List<CustomerInfo> selectByKeyword(String keyword) throws Exception; 
	CustomerInfo selectById(int id) throws Exception;
	//Login selectLoginInfoById(int id) throws Exception;
	void insert(CustomerInfo custInfo) throws Exception;
	void update(CustomerInfo custInfo) throws Exception;
	void newLoginId(Login loginInfo) throws Exception;
	void newLoginPass(Login loginInfo) throws Exception;
	void deleteById(Integer id);
	Long count() throws Exception;	
}
