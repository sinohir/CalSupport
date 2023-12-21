package com.example.app.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.example.app.domain.Login;


@Mapper
public interface LoginDao {
	Login selectAuthAdByLoginId(String loginId) throws Exception;
	Login selectAuthOpByLoginId(String loginId) throws Exception;
	Login selectAuthAsByLoginId(String loginId) throws Exception;
	Login selectAuthCuByLoginId(String loginId) throws Exception;

	List<Login>selectAssistantList() throws Exception;
	List<Login>selectOperatorList() throws Exception;
	List<Login>selectAdminList() throws Exception;
	
	void newLoginAsId(Login loginInfo) throws Exception;
	void newLoginAsPass(Login loginInfo) throws Exception;
	void newLoginOpId(Login loginInfo) throws Exception;
	void newLoginOpPass(Login loginInfo) throws Exception;
	void newLoginAdId(Login loginInfo) throws Exception;
	void newLoginAdPass(Login loginInfo) throws Exception;
	
	void insertNewAsAcc(Login loginInfo) throws Exception;
	void insertNewOpAcc(Login loginInfo) throws Exception;
	void insertNewAdAcc(Login loginInfo) throws Exception;
	
	void deleteAsById(Integer id);
	void deleteOpById(Integer id);
	void deleteAdById(Integer id);
	
} 