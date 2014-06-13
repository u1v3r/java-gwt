package cz.edukomplex.kosilka.client.content.harmonogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.i18n.client.NumberFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.helper.MyRequestBuilder;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;

public class HarmonogramContent extends Composite {

	private static HarmonogramContentUiBinder uiBinder = GWT
			.create(HarmonogramContentUiBinder.class);

	interface HarmonogramContentUiBinder extends
			UiBinder<Widget, HarmonogramContent> {
	}
	
	private static final int SWAP_UP = -1;	
	private static final int SWAP_DOWN = +1;
	private static final List<String> TIMES = initTimes();
	
	
	@UiField(provided = true) CellTable<HarmonogramCellTableRow> harmonogramsCellTable;
	@UiField HarmonogramUserAddPanel userAddPanel;	
	@UiField ScrollPanel scrollPanel;
		
	private static ApplicationController appManager = ApplicationController.getInstance();
	private ArrayList<HarmonogramCellTableRow> harmonogramCellTableArrayList = new ArrayList<HarmonogramCellTableRow>();	
		
	public HarmonogramContent() {						
		initCellTable();
		initWidget(uiBinder.createAndBindUi(this));	
		initUserAddPanel();
		initCellList();
	}

	private static List<String> initTimes() {
		
		NumberFormat format = NumberFormat.getFormat("00");
		int fromHour = 6;
		int toHour = 22;
		int k = 0;
		String[] times = new String[(toHour - fromHour + 1) * 4];//po 15 minutach
		//times[0] = "čas";//prva hodnota v listboxe
		
		for (int i = fromHour; i <= toHour; i++) {
			for (int j = 0; j < 4 ; j++) {				
				times[k++] = i + ":" + format.format(j * 15);
			}			
		}
		times[0] = "čas";
		
		return Arrays.asList(times);
	}

	private void initCellList() {
		appManager.getHarmonogramSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				onHarmonogramSelected();				
			}
		});
	}

	private void initUserAddPanel() {
		
		userAddPanel.addButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addStudentToHarmonogram();							
			}
			
		});
		
		/*
		userAddPanel.addAllButton.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				addGroupToHarmonogram();							
			}					
		});
		*/
	}

	private void parseCellTableXMLResponse(ArrayList<HashMap<String, String>> result){		
		
		int i = 1;
				
		if(result.size() > 0){
			
			for (HashMap<String, String> hashMap : result) {			
				HarmonogramCellTableRow row = new HarmonogramCellTableRow(
						hashMap.get("hid"), 
						hashMap.get("ID_st"), 
						hashMap.get("Prijmeni") + " " + hashMap.get("Jmeno"), 
						hashMap.get("ID_tr"), 
						hashMap.get("Zkratka"), 
						hashMap.get("time"),
						hashMap.get("ord"),
						hashMap.get("bachelor_thesis"),
						i++
				);				
				
				harmonogramCellTableArrayList.add(row);
			}
		}
		
		refreshCellTable();
		
	}
	
	private void onHarmonogramSelected() {
		
		if(appManager.getSelectedHarmonogram() != null){
			harmonogramCellTableArrayList.clear();			
			userAddPanel.groupListBox.clear();
			userAddPanel.studentListBox.clear();
			
			appManager.updateInfoPanel();
			
			HarmonogramCellListRow selectedHarmonogram = appManager.getSelectedHarmonogram();
			
			userAddPanel.setIdStudyField(selectedHarmonogram.idStudyField);		
			String hid = selectedHarmonogram.hid;
						
			Object[] params = new Object[]{Integer.parseInt(hid),true,false};
			String methodName = "harmonogram.fetchHarmonogram";
			
			XmlRpcClient client = MyXmlRpcClient.createClient();				
			XmlRpcRequest<ArrayList<HashMap<String, String>>> harmonogramRequest = 
				new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
						client, methodName, params, new AsyncCallback<ArrayList<HashMap<String,String>>>() {
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
	
					@Override
					public void onSuccess(
							ArrayList<HashMap<String, String>> result) {							
						parseCellTableXMLResponse(result);							
					}
				});
			
			harmonogramRequest.execute();
		}
	}
		
	

	private void addStudentToHarmonogram() {
				
		/*
		 * V listobxe musi byt oznaceny student
		 */
		if(userAddPanel.studentListBox.getSelectedIndex() >= 0 ){
			String IDst = userAddPanel.studentListBox.getValue(userAddPanel.studentListBox.getSelectedIndex());			
			final String hid = appManager.getSelectedHarmonogram().hid;
			
			XmlRpcClient client = MyXmlRpcClient.createClient();
			String methodName = "harmonogram.addStudent";
			Object[] params = new Object[]{hid,IDst};
			
			XmlRpcRequest<Integer> addStudentRequest = new XmlRpcRequest<Integer>(
					client, methodName, params, new AsyncCallback<Integer>() {

						@Override
						public void onFailure(Throwable caught) {
							//Ak je duplicitny, zobraz varovanie
							if(caught.getMessage().matches("^SQLSTATE.23000.*")){
								Window.alert(appManager.constants.dupliciteStudent());
								return;
							}
							
							Window.alert(caught.getMessage());					
						}

						@Override
						public void onSuccess(Integer result) {
							if(result >= 0){//ak nie je order cislo mensie ako 0 (tzn. chybne)
								
								String order = String.valueOf(result);
								
								HarmonogramCellTableRow row = new HarmonogramCellTableRow(
										hid,
										userAddPanel.studentListBox.getValue(userAddPanel.studentListBox.getSelectedIndex()), 
										userAddPanel.studentListBox.getItemText(userAddPanel.studentListBox.getSelectedIndex()), 
										userAddPanel.groupListBox.getValue(userAddPanel.groupListBox.getSelectedIndex()), 
										userAddPanel.groupListBox.getItemText(userAddPanel.groupListBox.getSelectedIndex()), 
										null,
										order,
										"",//prazdna tema bakalarskej prace
										harmonogramCellTableArrayList.size() + 1//poradove cislo = pocet studentov + 1
								);
								
								harmonogramCellTableArrayList.add(row);	
								appManager.addRowToResultsTable(row);								
								refreshCellTable();
							}
						}
							
			});
			addStudentRequest.execute();			
		}		
	}
	
	/*
	 * NEFUNGUJE
	 */
	private void addGroupToHarmonogram() {
		
		String hid = appManager.getSelectedHarmonogram().hid;
		StringBuffer idStudents = new StringBuffer();
		final ArrayList<HarmonogramCellTableRow> rows = new ArrayList<HarmonogramCellTableRow>();
		
		/*
		 * Vyberie len studentov, ktori nie su v cellTable
		 */
		if(harmonogramCellTableArrayList.size() > 0){
			for (int i = 0; i < userAddPanel.studentListBox.getItemCount(); i++) {
				for (int j = 0; j < harmonogramCellTableArrayList.size(); j++) {
					String idStudent = userAddPanel.studentListBox.getValue(i);
					if(!harmonogramCellTableArrayList.get(j).ID_st.equals(idStudent)){
						
						HarmonogramCellTableRow row = new HarmonogramCellTableRow();														
						row.setHid(hid);
						row.setGroup(userAddPanel.groupListBox.getItemText(userAddPanel.groupListBox.getSelectedIndex()));
						row.setID_st(idStudent);
						row.setID_tr(userAddPanel.groupListBox.getValue(userAddPanel.groupListBox.getSelectedIndex()));
						row.setStudent(userAddPanel.studentListBox.getItemText(i));			
						rows.add(row);						
						
						idStudents.append(idStudent + ",");					
					}				
				}						
			}		
		}
		else {//ak je cellTable prazdna, tak pridaj vsetkych
			for (int i = 0; i < userAddPanel.studentListBox.getItemCount(); i++) {
				
				String idStudent = userAddPanel.studentListBox.getValue(i);
				
				HarmonogramCellTableRow row = new HarmonogramCellTableRow();														
				row.setHid(hid);
				row.setGroup(userAddPanel.groupListBox.getItemText(userAddPanel.groupListBox.getSelectedIndex()));
				row.setID_st(idStudent);
				row.setID_tr(userAddPanel.groupListBox.getValue(userAddPanel.groupListBox.getSelectedIndex()));
				row.setStudent(userAddPanel.studentListBox.getItemText(i));			
				rows.add(row);
				
				
				idStudents.append(idStudent + ",");									
			}
		}
		
		RequestBuilder addGroupToHarmonogramRequest = MyRequestBuilder.createService(
				RequestBuilder.POST, 
				"Harmonogram",
				"addGroupToHarmonogram"
		);
		
		try {
			addGroupToHarmonogramRequest.sendRequest("hid=" + hid + "&students="+ idStudents, new RequestCallback() {
				
				@Override
				public void onResponseReceived(Request request, Response response) {
					Window.alert(response.getText());
					if(response.getText().equals("true")){																	
						harmonogramCellTableArrayList.addAll(rows);
						refreshCellTable();
					}
				}
				
				@Override
				public void onError(Request request, Throwable exception) {
					// TODO Auto-generated method stub
					
				}
			});
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void initCellTable() {
	
		harmonogramsCellTable = new CellTable<HarmonogramCellTableRow>(
				harmonogramCellTableArrayList.size(),appManager.getCellTableResources(),HarmonogramCellTableRow.KEY_PROVIDER);
		
		Column<HarmonogramCellTableRow, String> number = new Column<HarmonogramCellTableRow, String>(new TextCell()) {
			
			@Override
			public String getValue(HarmonogramCellTableRow object) {
				return object.number;
			}
		};
		harmonogramsCellTable.setColumnWidth(number, "15px");
		
		Column<HarmonogramCellTableRow, String> goUp = new Column<HarmonogramCellTableRow, String>(new ButtonCell()) {
			
			@Override
			public String getValue(HarmonogramCellTableRow object) {
				return "↑";
			}
		};
		goUp.setFieldUpdater(new FieldUpdater<HarmonogramCellTableRow, String>() {
			
			@Override
			public void update(int index, HarmonogramCellTableRow object, String value) {
				swapStudents(index,object,SWAP_UP);			
			}
		});
		harmonogramsCellTable.setColumnWidth(goUp,"15px");
			
		
		
		Column<HarmonogramCellTableRow , String> goDown = new Column<HarmonogramCellTableRow, String>(new ButtonCell()) {
			
			@Override
			public String getValue(HarmonogramCellTableRow object) {
				return "↓";
			}
		};
		goDown.setFieldUpdater(new FieldUpdater<HarmonogramCellTableRow, String>() {
			
			@Override
			public void update(int index, HarmonogramCellTableRow object, String value) {
				swapStudents(index,object,SWAP_DOWN);				
			}
		});
		harmonogramsCellTable.setColumnWidth(goDown,"15px");
		
		Column<HarmonogramCellTableRow, String> studentName = new Column<HarmonogramCellTableRow, String>(new TextCell()) {
			
			@Override
			public String getValue(HarmonogramCellTableRow object) {
				return object.getStudent();
			}
		};
		
		Column<HarmonogramCellTableRow, String> group = new Column<HarmonogramCellTableRow, String>(new TextCell()) {

			@Override
			public String getValue(HarmonogramCellTableRow object) {
				return object.getGroup();
			}
		};		
		
		final Column<HarmonogramCellTableRow, String> bachelorThesis = new Column<HarmonogramCellTableRow, String>(new EditTextCell()) {
			
			@Override
			public String getValue(HarmonogramCellTableRow object) {
				
				if(object.bachelorThesis == null){
					object.bachelorThesis = "";
					return "";
				}
				
				return object.getBachelorThesis();
			}
		};
		bachelorThesis.setFieldUpdater(new FieldUpdater<HarmonogramCellTableRow, String>() {
			
			@Override
			public void update(int index, HarmonogramCellTableRow object, String value) {
												
				//ak uzivatel nezmenil hodnotu
				if(object.bachelorThesis.equals(value)){					
					return;
				}
				saveBachelorThesis(object,value);
			}
			
		});
							
		Column<HarmonogramCellTableRow, String> time = new Column<HarmonogramCellTableRow, String>(new SelectionCell(TIMES)) {
			
			
			@Override
			public String getValue(HarmonogramCellTableRow object) {				
				try{
					if(object.time == null){
						object.time = TIMES.get(0);
						return TIMES.get(0);
					}					
					return object.getTime();
				} catch(RuntimeException e){
					object.time = TIMES.get(0);
					return TIMES.get(0);
				}
			}
		};
		time.setFieldUpdater(new FieldUpdater<HarmonogramCellTableRow, String>() {
			
			@Override
			public void update(int index, HarmonogramCellTableRow object, String value) {
				updateTime(object,value);				
			}
		});
		
		Column<HarmonogramCellTableRow, String> delete = new Column<HarmonogramCellTableRow, String>(new ButtonCell()) {

			@Override
			public String getValue(HarmonogramCellTableRow object) {
				return "X";
			}
		};		
		delete.setFieldUpdater(new FieldUpdater<HarmonogramCellTableRow, String>() {
			
			@Override
			public void update(int index, HarmonogramCellTableRow object, String value) {
				deleteStudentFromHarmonogram(index,object);				
			}
		});
		harmonogramsCellTable.setColumnWidth(delete, "15px");
		
		harmonogramsCellTable.addColumn(number,"#");
		harmonogramsCellTable.addColumn(goUp);
		harmonogramsCellTable.addColumn(goDown);
		harmonogramsCellTable.addColumn(studentName,appManager.constants.studentName());
		harmonogramsCellTable.addColumn(group,appManager.constants.group());		
		harmonogramsCellTable.addColumn(bachelorThesis,appManager.constants.bachelorThesis());
		harmonogramsCellTable.addColumn(time,appManager.constants.examTime());		
		harmonogramsCellTable.addColumn(delete,"Odstrániť");	
	}
	
	private void updateTime(HarmonogramCellTableRow object, String value) {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.updateStudentTime";
		Object[] params = new Object[]{appManager.getSelectedHarmonogram().hid,object.ID_st,value};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());					
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result.equals(false)){
							Window.alert("Čas sa nepodarilo zmeniť");
						}
					}
		});
		request.execute();
	}

	private void swapStudents(final int index, final HarmonogramCellTableRow object, final int direction) {
		
		try{
			String hid = appManager.getSelectedHarmonogram().hid;
			String studentUp = object.ID_st;
						
			HarmonogramCellTableRow studentDownObject = harmonogramCellTableArrayList.get(index + direction);
			String studentDown = studentDownObject.ID_st;
			XmlRpcClient client = MyXmlRpcClient.createClient();
			String methodName = "harmonogram.swapStudents";	
			
			/*
			 * Ak sa meni smerom dole treba zamenit parametre
			 */
			if(direction == SWAP_DOWN){
				String tmp = studentUp;
				studentUp = studentDown;
				studentDown = tmp;
			}
			
			Object[] params = new Object[]{hid,studentUp,studentDown};
						
			XmlRpcRequest<HashMap<String, String>> request = new XmlRpcRequest<HashMap<String, String>>(
					client, methodName, params, new AsyncCallback<HashMap<String, String>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());							
						}

						@Override
						public void onSuccess(HashMap<String, String> result) {
							//prebehlo spravne
							if(result.get("success").equals("1")){								
								if(direction == SWAP_UP){
									//zamenit poradie
									object.order = result.get("down");
									harmonogramCellTableArrayList.get(index - 1).order = result.get("up");
								}
								if(direction == SWAP_DOWN){
									//zamenit poradie - opacne
									object.order = result.get("up");
									harmonogramCellTableArrayList.get(index + 1).order = result.get("down");
								}								
								refreshCellTable();
								refreshRowNumbers();
								appManager.refreshResultCellTable();
							}else {
								Window.alert("Študentov sa nepodarilo zameniť");
							}
							
						}
			});
			request.execute();
		} catch(RuntimeException e){
			Window.alert("Poradie nie je možné zmeniť");
		}
	}

	private void deleteStudentFromHarmonogram(final int index,
			HarmonogramCellTableRow object) {		
		
		if(!Window.confirm("Naozaj chcete odstrániť študent " + object.student + " z harmonogramu?")){
			return;
		}
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.deleteStudentByHidIDst";
		Object[] params = new Object[]{object.hid,object.ID_st};
		
		XmlRpcRequest<Boolean> deleteStudentRequest = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result.equals(true)){
							harmonogramCellTableArrayList.remove(index);								
							appManager.refreshResultCellTable();							
							refreshCellTable();
							refreshRowNumbers();
						}						
					}				
		});		
		deleteStudentRequest.execute();
	}
	
	private void saveBachelorThesis(final HarmonogramCellTableRow object, final String value) {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.updateBachelorThesis";
		Object[] params = new Object[]{object.hid,object.ID_st,value};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(client, methodName, params, 
				new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.equals(false)){
					Window.alert(appManager.constants.databaseInsertFailed());
					return;
				}				
									
				object.bachelorThesis = value;//vlozenie temy do arraylistu												
			}
		});
		
		request.execute();
		
	}

	/*
	 * Precisluje znova riadky od 1
	 */
	private void refreshRowNumbers(){
		int i = 1;
		for (HarmonogramCellTableRow row : harmonogramCellTableArrayList) {
			row.setNumber(i++);
		}
	}
	
	public int getHarmonogramRowCount(){
		return harmonogramCellTableArrayList.size();
	}
	
	private void refreshCellTable() {
		Collections.sort(harmonogramCellTableArrayList);	
		//nastavenie velkosti tabulky na velkost zaznamov
		harmonogramsCellTable.setPageSize(harmonogramCellTableArrayList.size());
		//Nastavnie poctu riadkov
		harmonogramsCellTable.setRowCount(harmonogramCellTableArrayList.size(), true);		
	    // Obnovenie tabulky s novymi datami
		harmonogramsCellTable.setRowData(0, harmonogramCellTableArrayList);
	}
}
