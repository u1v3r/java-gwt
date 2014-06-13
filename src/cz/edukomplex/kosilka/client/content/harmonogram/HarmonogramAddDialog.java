package cz.edukomplex.kosilka.client.content.harmonogram;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.datepicker.client.DateBox;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.KosilkaV3;
import cz.edukomplex.kosilka.client.content.result.ResultCellTableRow;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.HarmonogramModel;
import cz.edukomplex.kosilka.client.model.ProfessorModel;
import cz.edukomplex.kosilka.client.style.MyResources;

public class HarmonogramAddDialog extends DialogBox {
		
	private static final int PREDSEDA = 1;
	private static final int MIESTOPREDSEDA = 2;
	private static final int TAJOMNIK = 3;
	private static final int CLEN = 4;
		
	public TextBox nameTextBox = new TextBox();	
	public ListBox studyFieldListBox = new ListBox();
	public ListBox predsedaListBox = new ListBox();
	public ListBox mistopredsedaListBox = new ListBox();
	public ListBox tajomnikListBox = new ListBox();
	public ListBox examPlaceListBox = new ListBox();
	public ListBox harmonogramsListBox = new ListBox();
	public DateBox dateBox = new DateBox();	
	public FormPanel formPanel = new FormPanel();	
	

	private CellTable<ProfessorModel> professorsCellTable = new CellTable<ProfessorModel>(ProfessorModel.KEY_PROVIDER);
	private ArrayList<ProfessorModel> professorsCellTableArrayList = new ArrayList<ProfessorModel>();
	private MultiSelectionModel<ProfessorModel> selectionModel = new MultiSelectionModel<ProfessorModel>(ProfessorModel.KEY_PROVIDER);
	private ApplicationController appController = ApplicationController.getInstance();	
	
		
	public HarmonogramAddDialog() {		
		initCellTable();
		initHarmonogramsListBox();
		initStudyFieldListBox();
		initExamPlaceListBox();
		setAutoHideEnabled(true);
		setModal(false);
		setHTML(appController.constants.newHarmonogramDialogHeader());
		setSize("100%", "100%");
				
		professorsCellTable.setHeight("45px");
		professorsCellTable.setWidth("100%");
		professorsCellTable.setSelectionModel(selectionModel,
				DefaultSelectionEventManager.<ProfessorModel> createCheckboxManager());
		
		setWidget(formPanel);
		formPanel.setSize("100%", "100%");		
		Grid grid = new Grid(10, 2);
		formPanel.setWidget(grid);
		
		grid.setSize("100%", "100%");
		
		Label lblMeno = new Label(appController.constants.harmonogramName());
		grid.setWidget(0, 0, lblMeno);
		grid.setWidget(0, 1, nameTextBox);
		
		Label lblInsert = new Label(appController.constants.insertFromHarmonogram());
		grid.setWidget(1, 0, lblInsert);
		grid.setWidget(1, 1, harmonogramsListBox);
		
		Label lblPredseda = new Label(appController.constants.chairman());
		grid.setWidget(2, 0, lblPredseda);		
		grid.setWidget(2, 1, predsedaListBox);
		
		Label lblMistoPredseda = new Label(appController.constants.vicechairman());
		grid.setWidget(3, 0, lblMistoPredseda);
		grid.setWidget(3, 1, mistopredsedaListBox);
		
		Label lblTajomnik = new Label(appController.constants.secretary());
		grid.setWidget(4, 0, lblTajomnik);
		grid.setWidget(4, 1, tajomnikListBox);
		
		
		ScrollPanel scrollPanel = new ScrollPanel(professorsCellTable);
		scrollPanel.setHeight("220px");
		scrollPanel.setWidth("450px");
		scrollPanel.setStyleName(appController.getApplicationResources().applicationStyle().scrollPanel());
		Label commissionMembersLbl = new Label(appController.constants.commissonMemers());
		grid.setWidget(5, 0, commissionMembersLbl);
		grid.setWidget(5, 1, scrollPanel);
			
		Label lblStudyField = new Label(appController.constants.studyField());
		grid.setWidget(6, 0, lblStudyField);		
		grid.setWidget(6, 1, studyFieldListBox);		
		
		Label lblPlace = new Label(appController.constants.examPlace());
		grid.setWidget(7, 0, lblPlace);
		grid.setWidget(7, 1, examPlaceListBox);
		
		Label lblDtum = new Label(appController.constants.examDate());
		grid.setWidget(8, 0, lblDtum);
		dateBox.setFormat(new DateBox.DefaultFormat(DateTimeFormat.getFormat(HarmonogramModel.OUTPUT_DATE_FORMAT)));
		grid.setWidget(8, 1, dateBox);		
		
		FlowPanel buttons = new FlowPanel();		
		Button btnClose = new Button(appController.constants.cancelBtn());
		btnClose.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				hide();				
			}
		});
		Button btnOk = new Button(appController.constants.okBtn());		
		btnOk.addClickHandler(new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				saveForm();				
			}
		});
		buttons.add(btnClose);
		buttons.add(btnOk);
		grid.setWidget(9, 1, buttons);
		grid.getCellFormatter().setHorizontalAlignment(9, 1, HasHorizontalAlignment.ALIGN_RIGHT);				
		
		
		nameTextBox.setFocus(true);
		center();//vycentruj dialogbox		
	}

	private void initExamPlaceListBox() {		
		examPlaceListBox.addItem("Kunovice", "Kunovicích");
		examPlaceListBox.addItem("Hodonín", "Hodnoníne");
		examPlaceListBox.addItem("Kroměříž", "Kroměříži");		
	}

	/**
	 * Inicializuje <code>CellTable</code>
	 */
	private void initCellTable(){	
		
		
		Column<ProfessorModel, Boolean> checkbox = new Column<ProfessorModel, Boolean>(new CheckboxCell(true,false)) {
			
			@Override
			public Boolean getValue(ProfessorModel object) {
				return selectionModel.isSelected(object);
			}
		};
		
		Column<ProfessorModel, String> professorName = new Column<ProfessorModel, String>(new TextCell()) {
			
			@Override
			public String getValue(ProfessorModel object) {
				return object.getTitleBefore() + " " + object.getLastname() + " " +
						object.getFirstname() + " " + object.getTitleBehind();
				
			}
		};
		
		
		professorsCellTable.setColumnWidth(checkbox, "15px");
		professorsCellTable.addColumn(checkbox);
		professorsCellTable.addColumn(professorName);	
		
		fetchProfessors();
	}
	
	/**
	 * Naplni listbox, ktory obsahuje vsetky harmongramy
	 */
	private void initHarmonogramsListBox() {
		
		harmonogramsListBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				populateForm();				
			}
		});
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> request = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
					client, methodName, null, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());						
					}
		
					@Override
					public void onSuccess(ArrayList<HashMap<String, String>> result) {						
						harmonogramsListBox.addItem(appController.constants.makeChoice(),"first");//prva hodnota v zozname
						for (HashMap<String, String> hashMap : result) {
							harmonogramsListBox.addItem(hashMap.get("harmonogram_name"), hashMap.get("hid"));
						}						
											
					}
		});
		request.execute();
	}
	
	
	/**
	 * Vyplni profesorov na zaklade zvoleneho harmonogramu v listboxe <code>harmonogramsListBox</code>
	 */
	private void populateForm() {
				
		String hid = harmonogramsListBox.getValue(harmonogramsListBox.getSelectedIndex());
		// ak je prva hodnota
		if(hid.equals("first")){
			return;
		}
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.fetchProfessors";
		Object[] params = new Object[]{hid};
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> request = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
				client, methodName, params, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}

			@Override
			public void onSuccess(ArrayList<HashMap<String, String>> result) {
				
				selectionModel.clear();
				
				for (HashMap<String, String> hashMap : result) {
					ProfessorModel professor = new ProfessorModel(
							hashMap.get("pid"), 
							hashMap.get("short"), 
							hashMap.get("firstname"), 
							hashMap.get("lastname"), 
							hashMap.get("title_before"), 
							hashMap.get("title_behind")
					);
					
					int index = professorsCellTableArrayList.indexOf(professor);
					
					switch (Integer.valueOf(hashMap.get("role"))) {
					case PREDSEDA:						
						if(index > -1) predsedaListBox.setSelectedIndex(index);
						break;
					case MIESTOPREDSEDA:						
						if(index > -1) mistopredsedaListBox.setSelectedIndex(index);
						break;
					case TAJOMNIK:						
						if(index > -1) tajomnikListBox.setSelectedIndex(index);
						break;
					case CLEN:
						if(index > -1){
							selectionModel.setSelected(professor, true);
							professorsCellTable.redraw();
						}
						break;
					default:
						break;
					}
				}
				
			}
		});
		request.execute();
		
	}

	/**
	 * Zavola poziadavku na server, ktora vrati vsetkych profesorov
	 * a vlozi ich do <code>ArrayList-u - professorsCellTableArrayList</code>
	 */
	private void fetchProfessors() {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "professor.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> professorRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
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
							
							professorsCellTableArrayList.add(professor);							
							predsedaListBox.addItem(professor.getLastname() + " " + professor.getFirstname(), professor.getPid());
							mistopredsedaListBox.addItem(professor.getLastname() + " " + professor.getFirstname(), professor.getPid());
							tajomnikListBox.addItem(professor.getLastname() + " " + professor.getFirstname(), professor.getPid());
							refreshCellTable();							
						}
					}
					
		});
		professorRequest.execute();
		
	}
	
	/**
	 * Vlozi hodnoty do listoboxu, ktory obsahuje studijne odbory
	 */
	private void initStudyFieldListBox() {
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
							studyFieldListBox.addItem(
									hashMap.get("name"), 
									hashMap.get("sfid")									
							);
						}
						
					}
		});
		request.execute();		
	}
	
	/**
	 * Ulozi formular do databaze
	 */
	protected void saveForm() {
		
		//meno harmonogramu a datum musi byt vypleneny
		if(nameTextBox.getValue().isEmpty()){
			Window.alert(appController.constants.invalidHarmonogramName());
			return;
		}
		if(dateBox.getValue() == null){
			Window.alert(appController.constants.invalidExamDate());
			return;
		}
				
		
		DateTimeFormat dbDateFormat = DateTimeFormat.getFormat(HarmonogramModel.DATABASE_DATE_FORMAT);
		final Date date = new Date(dateBox.getValue().getTime());			
		final String name = nameTextBox.getText();	
		final String examPlace = examPlaceListBox.getValue(examPlaceListBox.getSelectedIndex());
		final String idStudyField = studyFieldListBox.getValue(studyFieldListBox.getSelectedIndex());
		final String predseda = predsedaListBox.getValue(predsedaListBox.getSelectedIndex());
		final String miestopredseda = mistopredsedaListBox.getValue(mistopredsedaListBox.getSelectedIndex());
		final String tajomnik = tajomnikListBox.getValue(tajomnikListBox.getSelectedIndex());
		final String studyFieldName = studyFieldListBox.getItemText(studyFieldListBox.getSelectedIndex());
		final ProfessorModel[] professorModels = selectionModel.getSelectedSet().toArray(new ProfessorModel[]{});
		final List<Object> selectedProfessors = new ArrayList<Object>();


		//ak nie su pridaní clenovia komisie
		if(professorModels.length == 0){
			Window.alert(appController.constants.invalidCommissionMembers());
			return;
		}

		for (int i = 0; i < professorModels.length; i++) {
			selectedProfessors.add(professorModels[i].getPid());
		}

		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.add";
		Object[] params = new Object[]{name,predseda,miestopredseda,tajomnik,selectedProfessors,idStudyField,dbDateFormat.format(date),examPlace};

		XmlRpcRequest<HashMap<String, String>> addHarmonogramRequest = new XmlRpcRequest<HashMap<String, String>>(
				client, methodName, params, new AsyncCallback<HashMap<String, String>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					public void onSuccess(HashMap<String, String> result) {							
						if(Integer.valueOf(result.get("hid")) > 0){
							hide();		
							
							appController.addCellListRow(new HarmonogramCellListRow(
									result.get("hid"),
									name,
									idStudyField,
									studyFieldName,
									date,
									result.get("yid"),
									result.get("month")
							)
							);
							
							//zisti mesiac zvoleny v listobxe
							int month = Integer.valueOf(appController.getApplicationParentWidget().monthListBox.getValue(
									appController.getApplicationParentWidget().monthListBox.getSelectedIndex()
							));
							
							//obnovi listbox s mesiacami
							appController.getApplicationParentWidget().refreshMonthListBox(
									result.get("yid"), month
							);
							
							appController.setInfoMessage(appController.constants.harmonogramAdd());
						}
						else{
							Window.alert(appController.constants.serverError());
						}
					}
				});

		addHarmonogramRequest.execute();

	}
	
	/**
	 * Obnovi <code>CellTable</code> novymi hodnotami
	 */
	private void refreshCellTable(){
		professorsCellTable.setPageSize(professorsCellTableArrayList.size());
		professorsCellTable.setRowCount(professorsCellTableArrayList.size(),true);
		professorsCellTable.setRowData(0, professorsCellTableArrayList);
		center();//vycentruj dialogbox
	}
}