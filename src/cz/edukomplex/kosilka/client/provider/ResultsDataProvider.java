package cz.edukomplex.kosilka.client.provider;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.view.client.AsyncDataProvider;
import com.google.gwt.view.client.HasData;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.Range;

import cz.edukomplex.kosilka.client.content.result.ResultCellTableRow;
import cz.edukomplex.kosilka.client.helper.MyDataProvider;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.ResultSubjectModel;

public class ResultsDataProvider extends AsyncDataProvider<ResultCellTableRow> implements MyDataProvider<ResultCellTableRow> {
	
	public static final int PAGE_ROWS_COUNT = 15; 
	
	private List<ResultCellTableRow> list = new ArrayList<ResultCellTableRow>();
	private String hid = null;	
	private HasData<ResultCellTableRow> display = null;
	private Range range = null;
	
	public ResultsDataProvider(){
		super();
	}
	
	public ResultsDataProvider(String hid){
		super();
		this.hid = hid;
	}
		
	@Override
	protected void onRangeChanged(HasData<ResultCellTableRow> display) {
		
		if(hid == null){
			throw new NullPointerException("hid is null");
		}
		
		this.display = display;			
		this.range = display.getVisibleRange();
		fetchResults(this.range);		
	}
	
	private void fetchResults(Range range) {
		
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "result.fetchResults";
		
		Object[] params = new Object[]{hid,false,false,null,range.getStart(),range.getLength()};
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> restultsRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
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
		restultsRequest.execute();	
	}

	/**
	 * Spracuje vysledky prijate z <code>XML-RPC</code> volania a vlozi ich do {@link ListDataProvider}
	 * 
	 * @param result
	 */
	private void populateDataProvider(ArrayList<HashMap<String, String>> result) {		
		
		//Window.alert("size:" + result.size() + "");
		
		if(result.size() > 0){
			this.list.clear();
			List<ResultCellTableRow> data = this.list;
			
			for (@SuppressWarnings("rawtypes")
			Iterator iterator = result.iterator(); iterator.hasNext();) {
				
				
					@SuppressWarnings("unchecked")
					HashMap<String, String> hashMap = (HashMap<String, String>) iterator
					.next();
										
					ResultCellTableRow row = new ResultCellTableRow(
							hashMap.get("rid"),
							hashMap.get("ID_st"), 
							hashMap.get("Jmeno"), 
							hashMap.get("Prijmeni"), 
							hashMap.get("question_number"),
							hashMap.get("oponent_grade"),
							hashMap.get("veduci_grade"), 
							hashMap.get("obhajoba_grade"), 
							hashMap.get("overall_subjects_grade"), 
							hashMap.get("overall_grade"),  
							hashMap.get("note"),
							hashMap.get("ord")
					);				
					data.add(row);
			}
				
			int i = 0;
			for (@SuppressWarnings("rawtypes")
			Iterator iterator2 = result.iterator(); iterator2.hasNext();) {
				
				@SuppressWarnings("unchecked")
				HashMap<String, ArrayList<HashMap<String, String>>> resultsHashMap = 
					(HashMap<String, ArrayList<HashMap<String, String>>>) iterator2.next();
				
				
				ArrayList<HashMap<String, String>> results = resultsHashMap.get("results");
				
				for (int j = 0; j < results.size(); j++) {
					ResultSubjectModel subjectRow = new ResultSubjectModel(
							results.get(j).get("sid"), 
							results.get(j).get("rid"),
							results.get(j).get("name"), 
							results.get(j).get("short"), 
							results.get(j).get("grade")
					);					
					data.get(i).subjectsHashMap.put(subjectRow.getSid(),subjectRow);
				}		
				
				i++;			
				
			}			
			
			//defaultne zoradenie podla order
			Collections.sort(data);			
		}
	}	
		
	public void update(){	
		updateRowData(display.getVisibleRange().getStart(), this.list);
	}
	
	public void setHid(String hid){		
		this.hid = hid;
	}
	
	public List<ResultCellTableRow> getList(){
		return this.list;
	}
	
	public Range getRange(){
		return this.range;
	}
}
