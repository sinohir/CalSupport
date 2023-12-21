package com.example.app.dao;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.OperationRecord;
import com.example.app.domain.OrderSheet;

@Mapper
public interface OrderSheetDao {
/*---------------------------Asistant use------------------------------------------*/
	void newSheet(OrderSheet orderSheet) throws Exception;
	void updateAsComment(@Param("id") int id, @Param("comment") String comment) throws Exception; 
	List<OrderSheet> selectListBeforeOperation() throws Exception;
	List<OrderSheet> selectListUnderOperation() throws Exception;
	List<OrderSheet> selectListAfterOperation() throws Exception;
	OrderSheet selectViewById(Integer id) throws Exception;
	OrderSheet selectViewAfterInceptById(Integer id) throws Exception;
	void updateByShipping(OrderSheet orderSheet) throws Exception;

/*-----------------------------------Operator use--------------------------------------*/	
	void inception(OperationRecord opRecord) throws Exception;
	void updateOpComment(@Param("id") int id, @Param("comment") String comment) throws Exception; 
	void updateByOpCompletion(OrderSheet orderSheet) throws Exception;
	List<OrderSheet> selectListUnderOperationWithOpId(Integer id) throws Exception;
	List<OrderSheet> selectListAfterOperationWithOpId(Integer id) throws Exception;
/*---------------------------------Customer use----------------------------------------*/
	List<OrderSheet> selectListByCuId(Integer cuId) throws Exception;
}