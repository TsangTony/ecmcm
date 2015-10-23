package com.ibm.ecm.mm.model;

import java.util.ArrayList;

public class IdentifiedDocInstances extends ArrayList<IdentifiedDocInstance> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2957177664108043439L;
	private ArrayList<IdentifiedDocInstance> latestSnapshotInstances;
	
	public IdentifiedDocInstances() {
		setLatestSnapshotInstances(new ArrayList<IdentifiedDocInstance>());
	}
	
	public ArrayList<IdentifiedDocInstance> getLatestSnapshotInstances() {
		return latestSnapshotInstances;
	}
	public void setLatestSnapshotInstances(ArrayList<IdentifiedDocInstance> latestSnapshotInstances) {
		this.latestSnapshotInstances = latestSnapshotInstances;
	}
}
