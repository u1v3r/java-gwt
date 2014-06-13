package cz.edukomplex.kosilka.client.helper;

import com.google.gwt.core.client.GWT;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestBuilder.Method;
import com.google.gwt.http.client.URL;

public class MyRequestBuilder {
	
	private final static String SERVICE_HANDLER = "ServiceHandler.php";
	
	public static RequestBuilder createService(Method httpMethod, String service, String action){
		
		String url = URL.encode(GWT.getHostPageBaseURL() + SERVICE_HANDLER + "?s=" + service + "&a=" + action);
		
		RequestBuilder rb = new RequestBuilder(httpMethod, url);
		rb.setHeader("X-Requested-With", "XMLHttpRequest");
		rb.setHeader("Content-type", "application/x-www-form-urlencoded");
		return rb;
	}
	
	private MyRequestBuilder() {}
}
