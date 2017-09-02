package com.opsmx.model;

import com.opsmx.enums.ComparisonOperator;

public class FilterCondition {

	private String field;
	private ComparisonOperator comparisonOperator;
	private String value;

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public ComparisonOperator getComparisonOperator() {
		return comparisonOperator;
	}

	public void setComparisonOperator(ComparisonOperator comparisonOperator) {
		this.comparisonOperator = comparisonOperator;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	public FilterCondition(String field,ComparisonOperator operator, String value ){
		this.field = field;
		this.comparisonOperator = operator;
		this.value = value;
	}
	public FilterCondition(){
		
	}
	
}
