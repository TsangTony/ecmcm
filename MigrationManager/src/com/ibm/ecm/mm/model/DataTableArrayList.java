package com.ibm.ecm.mm.model;

import java.util.ArrayList;
import java.util.Collections;


public class DataTableArrayList<E extends DataTableElement> extends ArrayList<E> {

	private static final long serialVersionUID = 3203716812417040146L;
	private Class<E> eClass;
	private ArrayList<E> removedList; 
	
	public DataTableArrayList(Class<E> eClass) {
		this.eClass = eClass;
		removedList = new ArrayList<E>();
	}
	
	public ArrayList<E> getRemovedList() {
		return removedList;
	}

	public void setRemovedList(ArrayList<E> removedList) {
		this.removedList = removedList;
	}
	
    public E newInstance() {
	    try {
			return eClass.newInstance();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		return null;
    }
    
	public void addNew() {
		E e = newInstance();
		e.setPriority(this.size()+1);
		e.setNew(true);
		this.add(e);
	}
	
	public void remove(E e) {
		for (int i = e.getPriority(); i < this.size(); i++)
			this.get(i).setPriority(i);
		
		super.remove(e);
		if (!e.isNew())
			getRemovedList().add(e);
	}
	
	public void moveUp(E e) {		
		this.get(this.indexOf(e)-1).setPriority(e.getPriority());
		e.setPriority(e.getPriority()-1);
		Collections.swap(this, this.indexOf(e)-1, this.indexOf(e));
	}
	
	public void moveDown(E e) {		
		this.get(this.indexOf(e)+1).setPriority(e.getPriority());
		e.setPriority(e.getPriority()+1);
		Collections.swap(this, this.indexOf(e)+1, this.indexOf(e));
	}
	
}
