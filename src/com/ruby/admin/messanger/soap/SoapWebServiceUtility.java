package com.ruby.admin.messanger.soap;

import java.io.IOException;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import android.util.Log;

public class SoapWebServiceUtility {

	public static String callWebService(String envalope, String soapAction, final String resultTag) {
		final DefaultHttpClient httpClient = new DefaultHttpClient();
		// request parameters
		HttpParams params = httpClient.getParams();
		HttpConnectionParams.setConnectionTimeout(params, 600000);
		HttpConnectionParams.setSoTimeout(params, 600000);
		// set parameter
		HttpProtocolParams.setUseExpectContinue(httpClient.getParams(), true);

		// POST the envelope
		HttpPost httppost = new HttpPost(SoapWebServiceInfo.URL);
		// add headers
		httppost.setHeader("soapaction", soapAction);
		httppost.setHeader("Content-Type", "text/xml; charset=utf-8");

		String responseString = null;
		try {

			// the entity holds the request
			HttpEntity entity = new StringEntity(envalope);
			httppost.setEntity(entity);

			// Response handler
			ResponseHandler<String> rh = new ResponseHandler<String>() {
				// invoked when client receives response
				public String handleResponse(HttpResponse response)
						throws ClientProtocolException, IOException {

					// get response entity
				HttpEntity entity = response.getEntity();
					String responseStr = null;
					try {
						SAXParserFactory spf = SAXParserFactory.newInstance();
						SAXParser sp = spf.newSAXParser();
						XMLReader xr = sp.getXMLReader();

						SoapResponseHandler resHandler = new SoapResponseHandler();
						resHandler.setResponseTag(resultTag);
						xr.setContentHandler(resHandler);
						xr.parse(new InputSource(entity.getContent()));
						responseStr = resHandler.getResponse();
					} catch (Exception e) {
						e.printStackTrace();
					} 
					return responseStr;
				}
			};

			responseString = httpClient.execute(httppost, rh);

		} catch (Exception e) {
			e.printStackTrace();
			Log.d("me", "Exc : " + e.toString());

		}

		// close the connection
		httpClient.getConnectionManager().shutdown();
		return responseString;
	}
}
