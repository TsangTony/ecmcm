package com.ibm.ecm.mm.bean;

import java.io.IOException;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import com.ibm.ecm.mm.util.GenerationManager;

public class GenerationBean {
	public void generate() {
		
		FacesContext ctx = FacesContext.getCurrentInstance();
		HttpServletResponse response = (HttpServletResponse) ctx.getExternalContext().getResponse();
		
		response.setContentType("text/xml");
		response.setHeader("Content-Disposition", "attachment;filename=file.xml");  

		ServletOutputStream out;
		try {
			out = response.getOutputStream();
			out.write(GenerationManager.generate());
			out.flush(); 
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		finally {
			ctx.responseComplete();
		}
		
	}
}
