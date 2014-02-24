package com.ruby.admin.messanger.soap;

import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class SoapResponseHandler extends DefaultHandler {
	private String response;
	private String responseTag;
	private StringBuilder builder;
	
	public String getResponse() {
		return response;
	}
	
	public void setResponseTag(String responseTag) {
		this.responseTag = responseTag;
	}
	
	@Override
	public void characters(char[] ch, int start, int length)
			throws SAXException {
		super.characters(ch, start, length);
		builder.append(ch, start, length);
	}

	@Override
	public void endElement(String uri, String localName, String qName)
			throws SAXException {
		super.endElement(uri, localName, qName);
		if (localName.equalsIgnoreCase(responseTag)){
			response = builder.toString();
		}
	}

	@Override
	public void startDocument() throws SAXException {
		super.startDocument();
		builder = new StringBuilder();
	}
	
}
