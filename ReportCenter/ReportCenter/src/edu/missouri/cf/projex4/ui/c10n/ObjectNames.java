package edu.missouri.cf.projex4.ui.c10n;

import c10n.C10NMessages;
import c10n.annotations.En;

@C10NMessages
public interface ObjectNames {
	
	@En("{1} {2} \u2014 {3} {4}")
	String projectName(String campusName, String projectNumber, String titleLocation, String title);
	
	@En("{0} Agreement {1}")
	String agreementName(String projectName, String agreementNumber);
	
	@En("{0} Amendment {1}")
	String amendmentName(String agreementName, String amendmentNumber);
	
	@En("{0} Contract {1}")
	String contractName(String projectName, String contractNumber);
	
	@En("{0} Change Order {1}")
	String changeOrderName(String contractName, String changeOrderNumber);
	
	@En("{0} T&M Worksheet for {1} {2}")
	String coTimeAndMaterialWorksheet(String changeOrderName, String firmName, String performed);
	
	@En("{0} Work Authorization {1}")
	String workAuthorization(String parentName, String authorizationNumber);
	
	@En("{0} Amendment {1}")
	String workAuthorizationAmendment(String parentName, String amendmentNumber);
	
	@En("{0} Project Payment {1}")
	String projectPaymentName(String projectName, String paymentNumber);
	
	@En("{0} Payment {1}")
	String agreementPaymentName(String agreementName, String paymentNumber);
	
	@En("{0} Contract Payment {1}")
	String contractPaymentName(String contractName, String paymentNumber);
	
	@En("{0} Monthly Payment {1}")
	String monthlyPaymentName(String contractName, String paymentNumber);
	
	@En("{0} Payment {1}")
	String workAuthorizationPaymentName(String contractName, String paymentNumber);
	
	@En("{0} Cost Breakdown")
	String costBreakdownName(String contractName);
}
