package cz.edukomplex.kosilka.client.provider;

import java.util.ArrayList;
import java.util.HashMap;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import cz.edukomplex.kosilka.client.content.block.StudyFieldCellListRow;
import cz.edukomplex.kosilka.client.helper.MyDataProvider;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;

public class StudyFieldsDataProvider extends ListDataProvider<StudyFieldCellListRow> implements MyDataProvider<StudyFieldCellListRow> {
		
		
	@Override
	protected void onRangeChanged(HasData<StudyFieldCellListRow> display) {
		fetchStudyFields();
	}

	/**
	 * RPC request, ktory vrati vsetky studijne obory a naplní celllist so studijnymi oborami, 
	 * ktore sluzia na priradovanie predmetov do oborov
	 */
	private void fetchStudyFields() {
				 
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "studyfield.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> oboryRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
				client, methodName, null, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(
							ArrayList<HashMap<String, String>> result) {
						populataDataProvider(result);
						update();
					}
		});		
			
		oboryRequest.execute();
		
	}

	public void update() {
		updateRowData(0, super.getList());		
	}

	private void populataDataProvider(ArrayList<HashMap<String, String>> result) {
		
		for (HashMap<String, String> hashMap : result) {
			StudyFieldCellListRow row = new StudyFieldCellListRow(
					hashMap.get("sfid"), 
					hashMap.get("name")								
			);
			super.getList().add(row);
		}
	}
}
