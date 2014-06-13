package cz.edukomplex.kosilka.client.provider;

import java.util.ArrayList;
import java.util.HashMap;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import cz.edukomplex.kosilka.client.helper.MyDataProvider;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.ProfessorModel;

public class ProfessorsDataProvider extends ListDataProvider<ProfessorModel> implements
		MyDataProvider<ProfessorModel> {
	
	@Override
	protected void onRangeChanged(HasData<ProfessorModel> display) {		
		fetchProfessors();		
		super.onRangeChanged(display);
	}
	
	private void fetchProfessors() {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "professor.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> request = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
				client, methodName, null, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(ArrayList<HashMap<String, String>> result) {
						populateDataProvider(result);
						update();																	
					}
		});
		request.execute();
		
	}

	private void populateDataProvider(ArrayList<HashMap<String, String>> result) {
		
		for (HashMap<String, String> hashMap : result) {
			ProfessorModel professor = new ProfessorModel(
					hashMap.get("pid"), 
					hashMap.get("short"), 
					hashMap.get("firstname"), 
					hashMap.get("lastname"), 
					hashMap.get("title_before"), 
					hashMap.get("title_behind")
			);
			
			super.getList().add(professor);
		}
		
	}

	@Override
	public void update() {
		super.updateRowData(0, super.getList());		
	}

}
