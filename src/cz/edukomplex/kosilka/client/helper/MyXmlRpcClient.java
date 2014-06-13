package cz.edukomplex.kosilka.client.helper;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.google.gwt.core.client.GWT;

public class MyXmlRpcClient {
	
	private final static String XML_RPC_PATH = GWT.getHostPageBaseURL() + "xmlrpcserver.php";
	private final static boolean DEBUG_MODE = true;
	
	private MyXmlRpcClient(){}		
	
	public static XmlRpcClient createClient(){
		
		XmlRpcClient client = new XmlRpcClient(XML_RPC_PATH);
		client.setDebugMode(DEBUG_MODE);
		
		return client;
	}
}
