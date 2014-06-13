package cz.edukomplex.kosilka.client.helper;

import java.util.Date;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;

public class AuthManager {	
	
	
	public static void login(String username,String kontext, String password,final boolean pernament){
			
		Date now = new Date();//aktualny timestamp
		final Date cookieExpireTime = new Date(now.getTime() + (1000 * 60 * 60 * 24));//1 den
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "auth.authenticate";
		Object[] params = new Object[]{username,password,kontext};
		
		XmlRpcRequest<String> authRequest = new XmlRpcRequest<String>(client, methodName, params, 
				new AsyncCallback<String>() {

			@Override
			public void onFailure(Throwable caught) {				
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(String result) {
				if(result.isEmpty()){
					Window.alert("Chyba pri prihlasovani");
					return;
				}
				
				Cookies.setCookie("sessionID", result,cookieExpireTime);
				
				if(pernament){
					Cookies.setCookie("pernament", "1",cookieExpireTime);
				}
				
				Window.Location.reload();
			}
		});
		
		authRequest.execute();
	}
	
	public static void logout(){
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "auth.logout";
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(client, methodName, null, 
				new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result == false){
					Window.alert("Odhlásenie sa nepodarilo");
					return;
				}
				
				Cookies.removeCookie("sessionID");
				Cookies.removeCookie("pernament");				
				Window.Location.reload();
			}
		});
		
		request.execute();
	}
}
