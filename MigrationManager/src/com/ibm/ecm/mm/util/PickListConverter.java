package com.ibm.ecm.mm.util;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import org.primefaces.component.picklist.PickList;
import org.primefaces.model.DualListModel;

public class PickListConverter implements Converter {	
	@Override
	public Object getAsObject(FacesContext arg0, UIComponent arg1, String arg2) {
	    Object ret = null;
	    if (arg1 instanceof PickList) {
	        Object dualList = ((PickList) arg1).getValue();
	        DualListModel<Object> dl = (DualListModel<Object>) dualList;
	        for (Object o : dl.getSource()) {
	            String ostr = o.toString();
	            if (arg2.equals(ostr)) {
	                ret = o;
	                break;
	            }
	        }
	        if (ret == null)
	            for (Object o : dl.getTarget()) {
	                String ostr = o.toString();
	                if (arg2.equals(ostr)) {
	                    ret = o;
	                    break;
	                }
	            }
	    }
	    return ret;
	}

	@Override
	public String getAsString(FacesContext arg0, UIComponent arg1, Object arg2) {
		return arg2.toString();
	}
}
