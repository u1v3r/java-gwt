package cz.edukomplex.kosilka.client.content;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.cell.client.ImageResourceCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.KosilkaV3;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.HarmonogramModel;
import cz.edukomplex.kosilka.client.model.ProfessorModel;
import cz.edukomplex.kosilka.client.provider.ProfessorsDataProvider;

public class InfoPanel extends Composite {

	interface InfoPanelUiBinder extends UiBinder<Widget, InfoPanel> {
	}

	private static InfoPanelUiBinder uiBinder = GWT
			.create(InfoPanelUiBinder.class);
	
	public static final String ROLE_NOT_SET = "0";
	public static final String PREDSEDA = "1";
	public static final String MIESTOPREDSEDA = "2";
	public static final String TAJOMNIK = "3";
	public static final String CLEN = "4";
	
	private static int gwtBug = 0;
	private static final List<String> PLACES = Arrays.asList("Kunovicích","Hodnoníne","Kroměříži");	
	
	@UiField Label panelLabel;
	@UiField TextBox harmonogramName;
	@UiField Button btnSaveHarmongramName;
	@UiField ListBox predsedaListBox;
	@UiField ListBox miestopredsedaListBox;
	@UiField ListBox tajomnikListBox;
	@UiField ListBox addProfessorListBox;		
	@UiField Button btnAddProfessor;	
	@UiField DateBox examDate;	
	@UiField ListBox examPlaceListBox;
	
	@UiField CellTable<ProfessorModel> membersCellTable;
	@UiField public Button toggleInfoPanel;

	private ArrayList<String> professors = new ArrayList<String>();
	private HashMap<String,ProfessorModel> professorsHashMap = new HashMap<String,ProfessorModel>();	
	private ArrayList<ProfessorModel> professorsCellListArrayList = new ArrayList<ProfessorModel>();	
	private SingleSelectionModel<ProfessorModel> selectionModel = new SingleSelectionModel<ProfessorModel>();
	private ApplicationController appController = ApplicationController.getInstance();
	
	public InfoPanel() {		
		initWidget(uiBinder.createAndBindUi(this));
		setVisible(false);
		initCellTable();
		initProfesorListBoxes();
		initExamPlaceListBox();
	}
	
	@UiHandler("btnAddProfessor")
	public void addProfessor(ClickEvent e){
		String pid = addProfessorListBox.getValue(addProfessorListBox.getSelectedIndex());
		addProfessor(pid);
	}
	
	@UiHandler("examDate")
	public void examDateChangeHandler(ValueChangeEvent<Date> e){		
		
		/*
		 * GWT obsahuje bug kvoli ktoremu sa poziadavok zbytocne odosiela dvakrat
		 */				
		if (gwtBug % 2 == 0){
			updateExamDate(e);
		}
		gwtBug++;
	}

	@UiHandler("examPlaceListBox")
	public void examPlaceChangeHandler(ChangeEvent e){
		String place = examPlaceListBox.getValue(examPlaceListBox.getSelectedIndex());
		updateExamPlace(place);
	}
	
	/* 
	private void initOboryListBox() {
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "studyfield.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> request = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
				client, methodName, null, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
						
					}

					@Override
					public void onSuccess(
							ArrayList<HashMap<String, String>> result) {
						
						for (HashMap<String, String> hashMap : result) {
							StudyField studyField = new StudyField(hashMap.get("sfid"), hashMap.get("name"));
							studyFieldListBoxIP.addItem(studyField.obor, studyField.idOboru);
							studyFields.add(studyField.idOboru);
						}
						
					}
		});
		request.execute();
	}
	*/
	
	@UiHandler("btnSaveHarmongramName")
	public void harmonogramNameChange(ClickEvent e){
		String name = harmonogramName.getText();
		if(name.isEmpty()){
			return;
		}
		
		updateHarmonogramName(name);
	}

	public void initPanel(){
		
		setVisible(true);		
		String hid = appController.getSelectedHarmonogram().hid;
		predsedaListBox.setSelectedIndex(0);
		miestopredsedaListBox.setSelectedIndex(0);
		tajomnikListBox.setSelectedIndex(0);
		professorsCellListArrayList.clear();
				
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.fetchHarmonogram";
		Object[] params = new Object[]{hid,false,true};
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> request = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
				client, methodName, params, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(
							ArrayList<HashMap<String, String>> result) {
						populate(result);
					}
		});
		
		request.execute();
	}

	@UiHandler("miestopredsedaListBox")
	public void miestoPredsedaChangeHandler(ChangeEvent e){
		String pid = miestopredsedaListBox.getValue(miestopredsedaListBox.getSelectedIndex());
		updateProfessor(pid,MIESTOPREDSEDA);
	}

	@UiHandler("predsedaListBox")
	public void predsedaChangeHandler(ChangeEvent e){
		String pid = predsedaListBox.getValue(predsedaListBox.getSelectedIndex());
		updateProfessor(pid,PREDSEDA);
	}

	/**
	 * Sluzi na skrytie napisu pri zmensovani
	 * 
	 * @param show
	 */
	public void showPanelLabel(boolean show){		
		panelLabel.setVisible(show);
	}
	
	@UiHandler("tajomnikListBox")
	public void tajomnikChangeChandler(ChangeEvent e){
		String pid = tajomnikListBox.getValue(tajomnikListBox.getSelectedIndex());
		updateProfessor(pid,TAJOMNIK);
	}	

	@UiHandler("toggleInfoPanel")
	public void toggleInfoPanel(ClickEvent e){
		appController.toggleInfoPanel();
	}
	
	private void addProfessor(final String pid) {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.addProfessor";
		Object[] params = new Object[]{appController.getSelectedHarmonogram().hid,pid};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());								
							}

							@Override
							public void onSuccess(Boolean result) {
								if(result.equals(false)){
									Window.alert("Člena komisie sa nepodarilo pridať");
								}
								else {
									professorsCellListArrayList.add(professorsHashMap.get(pid));
									refreshCellTable();
								}
							}
		});
		
		request.execute();
	}
	
	private void initCellTable() {
		
		
		selectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {
				String name = selectionModel.getSelectedObject().getLastname() + " " + selectionModel.getSelectedObject().getFirstname();				
				if(Window.confirm("Naozaj chcete odstrániť profesora " + name + " z harmonogramu?")){
					removeProfessor();				
				}
			}
		});
		
		membersCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		membersCellTable.setSelectionModel(selectionModel);
		
		Column<ProfessorModel, String> professor = new Column<ProfessorModel, String>(new TextCell()) {
			
			@Override
			public String getValue(ProfessorModel object) {
				return object.getTitleBefore() + object.getLastname() + " " + object.getFirstname() + " " + object.getTitleBehind();
			}
		};
		
		Column<ProfessorModel, ImageResource> removeProfessor = new Column<ProfessorModel, ImageResource>(new ImageResourceCell()) {

			@Override
			public ImageResource getValue(ProfessorModel object) {
				return appController.getApplicationResources().delete();
			}
		};
		
		membersCellTable.addColumn(professor);
		membersCellTable.addColumn(removeProfessor);
	}
	
	private void initExamPlaceListBox() {
		for (String place : PLACES) {
			examPlaceListBox.addItem(place);
		}		
	}
	
	private void initProfesorListBoxes() {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "professor.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> request = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
				client, methodName, null, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(
							ArrayList<HashMap<String, String>> result) {
						
						for (HashMap<String, String> hashMap : result) {
							ProfessorModel professor = new ProfessorModel(
									hashMap.get("pid"), 
									hashMap.get("short"), 
									hashMap.get("firstname"), 
									hashMap.get("lastname"), 
									hashMap.get("title_before"), 
									hashMap.get("title_behind")
							);
							
							predsedaListBox.addItem(
									professor.getTitleBefore() + " " + professor.getLastname() + " " + 
										professor.getFirstname() + " " + professor.getTitleBehind(), 
									professor.getPid()
							);
							miestopredsedaListBox.addItem(
									professor.getTitleBefore() + " " + professor.getLastname() + " " + 
										professor.getFirstname() + " " + professor.getTitleBehind(), 
										professor.getPid()
							);
							tajomnikListBox.addItem(
									professor.getTitleBefore() + " " + professor.getLastname() + " " + 
										professor.getFirstname() + " " + professor.getTitleBehind(), 
										professor.getPid()
							);
							addProfessorListBox.addItem(
									professor.getTitleBefore() + " " + professor.getLastname() + " " + 
									professor.getFirstname() + " " + professor.getTitleBehind(), 
									professor.getPid()
							);
							professors.add(professor.getPid());
							professorsHashMap.put(professor.getPid(), professor);
						}											
					}
		});
		request.execute();
	}
	
	private void populate(ArrayList<HashMap<String, String>> result) {	
				
		if(result.size() > 0){			
			DateTimeFormat databaseFormat = DateTimeFormat.getFormat(HarmonogramModel.DATABASE_DATE_FORMAT);			
			Date date = databaseFormat.parse(result.get(0).get("date"));

			harmonogramName.setText(result.get(0).get("harmonogram_name"));			
			
			examDate.setValue(date);
			examDate.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(HarmonogramModel.OUTPUT_DATE_FORMAT)));
								
			examPlaceListBox.setSelectedIndex(PLACES.indexOf((result.get(0).get("exam_place"))));
			
			for (HashMap<String, String> professor : result) {
				String role = professor.get("role");				
				if(role != null){
					if(role.equals(PREDSEDA)){
						int indexPredseda = professors.indexOf(professor.get("pid"));					
						predsedaListBox.setSelectedIndex(indexPredseda);
					}
					if(role.equals(MIESTOPREDSEDA)){
						int indexMiestopredseta = professors.indexOf(professor.get("pid"));
						miestopredsedaListBox.setSelectedIndex(indexMiestopredseta);
					}
					if(role.equals(TAJOMNIK)){
						int indexTajomnik = professors.indexOf(professor.get("pid"));
						tajomnikListBox.setSelectedIndex(indexTajomnik);
					}
					if(role.equals(CLEN)){					
						ProfessorModel row = new ProfessorModel(
								professor.get("pid"), 
								professor.get("short"), 
								professor.get("firstname"), 
								professor.get("lastname"), 
								professor.get("title_before"), 
								professor.get("title_behind")
						);
						professorsCellListArrayList.add(row);
					}
				}			
			}
		}
		
		refreshCellTable();
		
	}
	
	private void refreshCellTable() {		
		membersCellTable.setPageSize(professorsCellListArrayList.size());
		membersCellTable.setRowCount(professorsCellListArrayList.size(),true);
		membersCellTable.setRowData(0, professorsCellListArrayList);
	}

	private void removeProfessor() {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.removeProfessor";
		Object[] params = new Object[]{appController.getSelectedHarmonogram().hid,selectionModel.getSelectedObject().getPid()};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());					
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result.equals(true)){
							professorsCellListArrayList.remove(selectionModel.getSelectedObject());
							refreshCellTable();
						}
						else {
							Window.alert("Profesora sa nepodarilo odstrániť");
						}
					}
		});
		
		request.execute();
		
	}

	private void updateExamDate(final ValueChangeEvent<Date> event) {
		
		DateTimeFormat dbFormat = DateTimeFormat.getFormat(HarmonogramModel.DATABASE_DATE_FORMAT);
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.updateExamDate";
		Object[] params = new Object[]{appController.getSelectedHarmonogram().hid,dbFormat.format(event.getValue())};
		
		XmlRpcRequest<HashMap<String, String>> request = new XmlRpcRequest<HashMap<String, String>>(
				client, methodName, params, new AsyncCallback<HashMap<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());					
					}

					@Override
					public void onSuccess(HashMap<String, String> result) {
						
						KosilkaV3 parent = appController.getApplicationParentWidget();
						
						if (result.get("status").equals("true")) {				
							String selectedYid = parent.yearsListBox.getValue(
									parent.yearsListBox.getSelectedIndex());
							String selectedMonth = parent.monthListBox.getValue(
									parent.monthListBox.getSelectedIndex());
							
							String newYid = result.get("yid");
							String newMonth = result.get("month");
														
							//ak doslo pri zmene datumu k zmene roku alebo mesiacu do ktoreho harmonogram patri
							if(selectedYid.equals(newYid) && selectedMonth.equals(newMonth)){
								appController.getSelectedHarmonogram().date = event.getValue();
								appController.redrawCellList();
							}else {
								//ak sa nezhoduju tak odstran z celllistu
								parent.harmonogramsCellListArrayList.remove(appController.getSelectedHarmonogram());
								parent.refreshHarmonogramsCellList();
								if(parent.harmonogramsCellListArrayList.size() > 0){//ak je v arrayliste este nejaky harmonogram tak vyber prvy
									appController.getHarmonogramSelectionModel().setSelected(
											parent.harmonogramsCellListArrayList.get(0), true);
								}
								
								//obnovi listobx s mesiacmi
								appController.getApplicationParentWidget().refreshMonthListBox();
																								
								//zobrazi varovanie, ze harmonogram bol presunuty do ineho obdobia, nez je momentalne vybraty
								Window.alert(appController.constants.harmonogramMoved());								
							}
							
							//informacia o zmene datumu
							appController.setInfoMessage(appController.constants.examDateUpdate());
							
						}else{
							Window.alert(appController.constants.databaseInsertFailed());
						}
					}
		});
		
		request.execute();
	}
	
	private void updateExamPlace(String place) {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.updateExamPlace";
		Object[] params = new Object[]{appController.getSelectedHarmonogram().hid,place};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());								
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result.equals(false)) {
									appController.setInfoMessage(appController.constants.examPlaceUpdateFailed(), true);
									return;
								}
								
								appController.setInfoMessage(appController.constants.examPlaceUpdate());
							}
		});
		
		request.execute();
	}
	
	private void updateHarmonogramName(final String name) {
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.updateHarmonogramName";
		Object[] params = new Object[]{appController.getSelectedHarmonogram().hid,name};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());							
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result.equals(false)) {
									appController.setInfoMessage(appController.constants.harmonogramNameUpdateFailed(), true);
									return;
								}
								
								appController.getSelectedHarmonogram().harmonogramName = name;
								appController.redrawCellList();
								
								appController.setInfoMessage(appController.constants.harmonogramNameUpdate());
							}
		});
		
		request.execute();
	}
	
	private void updateProfessor(String pid, String role) {
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.updateProfesor";
		Object[] params = new Object[]{appController.getSelectedHarmonogram().hid,pid,role};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

							@Override
							public void onFailure(Throwable caught) {
								Window.alert(caught.getMessage());								
							}

							@Override
							public void onSuccess(Boolean result) {
								if (result.equals(false)) {
									appController.setInfoMessage(appController.constants.professorUpdateFailed(), true);
									
								}
							}
		});
		
		request.execute();
	}
}