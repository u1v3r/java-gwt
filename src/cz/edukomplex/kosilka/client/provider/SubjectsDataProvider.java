package cz.edukomplex.kosilka.client.provider;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.content.block.StudyFieldCellListRow;
import cz.edukomplex.kosilka.client.helper.MyDataProvider;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.SubjectModel;

public class SubjectsDataProvider extends ListDataProvider<SubjectModel> implements
		MyDataProvider<SubjectModel> {
	
	private ApplicationController appController = ApplicationController.getInstance();
	
	@Override
	protected void onRangeChanged(HasData<SubjectModel> display) {
		fetchSubjects();
	}
	
	public void fetchSubjects() {
		
		if(appController.getSelectedStudyField() != null){
			
			StudyFieldCellListRow studyField = appController.getSelectedStudyField();
			
			/*
			 * Posle poziadavku na Subject serivice
			 */
			XmlRpcClient client = MyXmlRpcClient.createClient();
			String methodName = "subject.fetchSubjectsByStudyFieldId";
			Object[] params = new Object[]{studyField.idStudyField};
			
			XmlRpcRequest<ArrayList<HashMap<String, String>>> subjectsRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
					client, methodName, params, new AsyncCallback<ArrayList<HashMap<String, String>>>() {
	
						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());						
						}
	
						@Override
						public void onSuccess(
								ArrayList<HashMap<String, String>> result) {						
							populateDataProvider(result);
							update();
						}
					
			});
			
			subjectsRequest.execute();
		}
	}

	private void populateDataProvider(ArrayList<HashMap<String, String>> result) {
		
		super.getList().clear();
		List<SubjectModel> data = super.getList();
		
		for (HashMap<String, String> hashMap : result) {
			SubjectModel row = new SubjectModel(
					hashMap.get("sid"), 
					hashMap.get("name"),
					hashMap.get("short")
			);
			data.add(row);
		}
	}

	@Override
	public void update() {		
		updateRowData(0, super.getList());		
	}
}