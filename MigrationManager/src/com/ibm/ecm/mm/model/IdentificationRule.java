package com.ibm.ecm.mm.model;

public class IdentificationRule extends DataTableElement {
	private int id;
	private String logicalOperator;
	private String attribute;
	private String relationalOperator;
	private String value;
	private String leftParen;
	private String rightParen;
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}	
	public String getValue() {
		return value;
	}
	public void setValue(String value) {
		this.value = value;
	}
	public String getLogicalOperator() {
		return logicalOperator;
	}
	public void setLogicalOperator(String logicalOperator) {
		this.logicalOperator = logicalOperator;
	}
	public String getAttribute() {
		return attribute;
	}
	public void setAttribute(String attribute) {
		this.attribute = attribute;
	}
	public String getRelationalOperator() {
		return relationalOperator;
	}
	public void setRelationalOperator(String relationalOperator) {
		this.relationalOperator = relationalOperator;
	}
	public String getLeftParen() {
		if (leftParen == null)
			return "";
		return leftParen;
	}
	public void setLeftParen(String leftParen) {
		this.leftParen = leftParen;
	}
	public String getRightParen() {
		if (rightParen == null)
			return "";
		return rightParen;
	}
	public void setRightParen(String rightParen) {
		this.rightParen = rightParen;
	}
	

}
