package com.example.app.dao;

import java.util.Date;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.example.app.domain.CalData;
import com.example.app.domain.OperationRecord;

@Mapper
public interface OperationDao {
	
	List<CalData> selectDataByEntryId(Integer entry_id) throws Exception;
	
	OperationRecord selectOpRecordByEntryId(Integer entry_id) throws Exception;
	Date selectCompletionDateByEntryId(Integer entry_id) throws Exception;
	
	void insertOpRecord(OperationRecord opRecord) throws Exception; 
	void updateOpNote(@Param("id") int id, @Param("note") String note) throws Exception; 
	void insertListWithDataBef(@Param("entry_id") int entryId, 
			@Param("dataList") List<CalData> dataList) throws Exception;
	void insertListWithDataAft(@Param("entry_id") int entryId, 
			@Param("dataList") List<CalData> dataList) throws Exception;
	void updateListWithDataAft(@Param("entry_id") int entryId, 
			@Param("dataList") List<CalData> dataList) throws Exception;
	void updateOpRecordByBefData(OperationRecord opRecord) throws Exception; 
	void updateOpRecordByAftData(OperationRecord opRecord) throws Exception;
}
