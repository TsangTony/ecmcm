package com.ibm.ecm.mm.util;

import java.util.ArrayList;

import com.ibm.ecm.mm.model.IdentificationRule;

public class IdentificationManager {

	public static ArrayList<IdentificationRule> createObsoleteRules(int startPriority) {
		String[] obsoleteKeywords = {"Obsolete", "Obselete", "Obsolote",  "Obsolate", "Archive", "Archieve", "/Old"};
		boolean isFirstRow = true;
		ArrayList<IdentificationRule> identificationRules = new ArrayList<IdentificationRule>();
		for (String obsoleteKeyword : obsoleteKeywords) {
			IdentificationRule identificationRule = new IdentificationRule();
			identificationRule.setPriority(startPriority);
			if (isFirstRow) {
				if (identificationRule.getPriority() > 1)
					identificationRule.setLogicalOperator("And");
				isFirstRow = false;
			}
			else 
				identificationRule.setLogicalOperator("And");
			identificationRule.setAttribute("File Path");
			identificationRule.setRelationalOperator("Not contains");
			identificationRule.setValue(obsoleteKeyword);
			identificationRule.setNew(true);
			identificationRules.add(identificationRule);
		}
		return identificationRules;
	}
	
}
