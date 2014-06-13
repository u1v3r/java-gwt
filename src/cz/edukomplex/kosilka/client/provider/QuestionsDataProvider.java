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
import cz.edukomplex.kosilka.client.model.QuestionModel;
import cz.edukomplex.kosilka.client.model.SubjectModel;

public class QuestionsDataProvider extends ListDataProvider<QuestionModel> implements MyDataProvider<QuestionModel>{
		
	private SubjectModel subject;
	
	public QuestionsDataProvider(SubjectModel subject){
		super();
		this.subject = subject;
	}
	
	public QuestionsDataProvider(){
		super();
	}
	
	@Override
	protected void onRangeChanged(HasData<QuestionModel> display) {		
		fetchQuestions();
	}
	
	private void fetchQuestions() {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "question.fetchQuestions";
		Object[] params = new Object[]{this.subject.getSid()};
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> request = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
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
		request.execute();
		
	}

	private void populateDataProvider(ArrayList<HashMap<String, String>> result) {
		
		if(result.size() > 0){
			int i = 1;
			for (HashMap<String, String> hashMap : result) {
												
				QuestionModel row = new QuestionModel(
						hashMap.get("qid"),
						this.subject.getSid(), 
						hashMap.get("qn"),
						hashMap.get("text")
				);
				
				super.getList().add(row);
			}
		}		
	}
	
	public void update(){
		updateRowData(0, super.getList());
	}

}
