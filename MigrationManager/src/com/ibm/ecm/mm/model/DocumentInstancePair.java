package com.ibm.ecm.mm.model;

public class DocumentInstancePair {
	private IdentifiedDocInstance documentInstance1;
	private IdentifiedDocInstance documentInstance2;
	
	public DocumentInstancePair() {
		setDocumentInstance1(new IdentifiedDocInstance());
		setDocumentInstance2(new IdentifiedDocInstance());
	}
	
	public IdentifiedDocInstance getDocumentInstance1() {
		return documentInstance1;
	}
	public void setDocumentInstance1(IdentifiedDocInstance documentInstance1) {
		this.documentInstance1 = documentInstance1;
	}
	public IdentifiedDocInstance getDocumentInstance2() {
		return documentInstance2;
	}
	public void setDocumentInstance2(IdentifiedDocInstance documentInstance2) {
		this.documentInstance2 = documentInstance2;
	}
}
