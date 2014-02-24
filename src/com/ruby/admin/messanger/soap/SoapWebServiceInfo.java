package com.ruby.admin.messanger.soap;

public class SoapWebServiceInfo {
	
	//public static final String RESULT = "Result";
	
	public static final String URL = "http://rkandro.com/andro/webservice.asmx";
		
	public static final String LOGIN_ENVELOPE = "<?xml version=\"1.0\" encoding=\"utf-8\"?><soap:Envelope xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\" xmlns:soap=\"http://schemas.xmlsoap.org/soap/envelope/\"><soap:Body><GetLogin xmlns=\"http://tempuri.org/\"><UserName>%s</UserName><Password>%s</Password></GetLogin></soap:Body></soap:Envelope>";
	public static final String LOGIN_SOAP_ACTION = "http://tempuri.org/GetLogin";
	public static final String LOGIN_RESULT_TAG = "GetLoginResult";


}
