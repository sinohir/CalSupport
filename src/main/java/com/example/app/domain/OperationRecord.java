package com.example.app.domain;

import java.util.Date;

import lombok.Data;

@Data
public class OperationRecord {
	Integer	entryId;
	String		invoiceId;
	String		model;
	String		serial;
	Integer	opId;
	Date		date0;
	String		temp0;
	String		humid0;
	Date		date1;
	String		temp1;
	String		humid1;
	Date		dueDate;
	String		opNote;
	Boolean	completion=false;
}

