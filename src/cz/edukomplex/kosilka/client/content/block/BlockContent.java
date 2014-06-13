package cz.edukomplex.kosilka.client.content.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.SubjectModel;
import cz.edukomplex.kosilka.client.provider.StudyFieldsDataProvider;
import cz.edukomplex.kosilka.client.provider.SubjectsDataProvider;

public class BlockContent extends Composite {

	interface BlockContentUiBinder extends UiBinder<Widget, BlockContent> {
	}

	private static BlockContentUiBinder uiBinder = GWT
			.create(BlockContentUiBinder.class);
	
	
	@UiField(provided = true) CellList<StudyFieldCellListRow> studyFieldsCellList;
	@UiField(provided = true) CellTable<SubjectModel> subjectsCellTable;		
	@UiField ListBox subjectsListBox;		
	@UiField Button addSubjectBtn;	
	@UiField Button newSubjectBtn;	
	@UiField SimplePanel questionPanelContainer;
	@UiField HTMLPanel hidePanel;
	
	private HashMap<String, SubjectModel> subjectsMap = new HashMap<String, SubjectModel>();
	private StudyFieldsDataProvider studyFieldsDataProvider = new StudyFieldsDataProvider();
	private SubjectsDataProvider subjectsDataProvider = new SubjectsDataProvider();	
	private ApplicationController appController = ApplicationController.getInstance();
	
	public BlockContent() {				
		
		//inicializacia Cell widgetov, musi byt este pred createAndBindUi()
		subjectsCellTable = new CellTable<SubjectModel>(
				0,appController.getCellTableResources(),SubjectModel.KEY_PROVIDER);		
		studyFieldsCellList = new CellList<StudyFieldCellListRow>(new StudyFieldCell());
		
		//vygenerovanie UI
		initWidget(uiBinder.createAndBindUi(this));
		
		//priradenie data providera do Cell widgetov
		studyFieldsDataProvider.addDataDisplay(studyFieldsCellList);		
		
		
		//inicializacia struktur Cell widgetov
		initStudyFieldsCellList();	
		initSubjectsCellTable();	
		
		//inicializacia QuestionPanelu a vlozenie do containeru
		questionPanelContainer.setWidget(new QuestionPanel());		
	}
	
	
	@UiHandler("addSubjectBtn")
	public void addSubject(ClickEvent e){
		
		
		final String sid = subjectsListBox.getValue(subjectsListBox.getSelectedIndex());
				
		String idStudyField = appController.getSelectedStudyField().idStudyField;
				 
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "subject_studyfield.add";
		Object[] params = new Object[]{sid,idStudyField};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params,	new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Boolean result) {
						if(result){
							//ziska vkladany subject na zaklade sid z hashmap
							SubjectModel row = subjectsMap.get(sid);
							
							//vlozi do zoznamu
							subjectsDataProvider.getList().add(row);
							
							//zoradi podla mena
							Collections.sort(subjectsDataProvider.getList());
							
							//obnovenie zoznamu
							subjectsDataProvider.update();
						}
						else {
							Window.alert(appController.constants.addSubjectFail());	
						}
						
					}
		});
		request.execute();			
	}
	
	
	@UiHandler("newSubjectBtn")
	public void createNewSubject(ClickEvent e){
		new NewSubjectDialog().show();
	}

	
	private void removeSubjectFromCellTable(final SubjectModel subject, final int arrayListIndex) {
				
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "subject.deleteSubjectFromObor";
		Object[] params = new Object[]{subject.getSid(),appController.getSelectedStudyField().idStudyField,true};
		
		XmlRpcRequest<Boolean> subjectReqeuest = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Boolean result) {
						/*
						 * Ak sa podarilo odstranit
						 */
						if(result){
							subjectsDataProvider.getList().remove(arrayListIndex);
							subjectsDataProvider.update();
						}
						else {							
							Window.alert(appController.constants.removeSubjectFail());
						}
					}
		});
		subjectReqeuest.execute();
	}

	private void initStudyFieldsCellList(){
		
		studyFieldsCellList.setSelectionModel(appController.getStudyFieldSelectionModel());
		
		appController.getStudyFieldSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {				
				onStudyFieldSelected();				
			}
		});		
	}
	
	private void initSubjectsCellTable() {
						
		subjectsCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		SingleSelectionModel<SubjectModel> selectionModel = new SingleSelectionModel<SubjectModel>();
		subjectsCellTable.setSelectionModel(selectionModel);
		
		Column<SubjectModel, String> subjectName = new Column<SubjectModel, String>(new ClickableTextCell()) {
			
			@Override
			public String getValue(SubjectModel object) {
				return object.getName();
			}
		};
		
		subjectName.setFieldUpdater(new FieldUpdater<SubjectModel, String>() {
			
			@Override
			public void update(int index, SubjectModel object, String value) {				
	
				//vlozenie widgetu do containeru
				questionPanelContainer.setWidget(new QuestionPanel(object,calcQuestionPanelHeight()));			
			}
		});
		
		Column<SubjectModel, String> deleteButton = new Column<SubjectModel, String>(new ButtonCell()) {
			
			@Override
			public String getValue(SubjectModel object) {
				return "X";
			}
		};
		deleteButton.setFieldUpdater(new FieldUpdater<SubjectModel, String>() {
			
			@Override
			public void update(int index, SubjectModel object, String value) {
				removeSubjectFromCellTable(object,index);				
			}
		});
		
		subjectsCellTable.setColumnWidth(deleteButton, "15px");
		subjectsCellTable.addColumn(subjectName,appController.constants.subject());
		subjectsCellTable.addColumn(deleteButton);
		
		subjectsCellTable.setVisibleRange(0, 999);//musi obsahovat nejake cislo, inak sa nezobrazi
		subjectsDataProvider.addDataDisplay(subjectsCellTable);		
	}	

	private int calcQuestionPanelHeight() {
		
		//celkova vyska obsahu stranky - vrchny bod subjectcelltable - vyska button - 17 (kvoli border a inym nepresnostiam)
		return (Window.getClientHeight() - subjectsCellTable.getAbsoluteTop()) - addSubjectBtn.getOffsetHeight() - 17;
	}


	public void initSubjectListBox() {
		
		subjectsListBox.clear();
		subjectsMap.clear();
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "subject.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> subjectRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
				client, methodName, null, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(
							ArrayList<HashMap<String, String>> result) {
						
						for (HashMap<String, String> hashMap : result) {
							
							SubjectModel subject = new SubjectModel(
									hashMap.get("sid"), 
									hashMap.get("name"), 
									hashMap.get("short")
							);
							
							subjectsListBox.addItem(
									subject.getName(),
									subject.getSid()
							);
							
							subjectsMap.put(subject.getSid(), subject);
						}						
					}
		});
		subjectRequest.execute();		
	}
	
	public void onStudyFieldSelected() {

		
		if(appController.getSelectedStudyField() != null){			
					
			//pri prvom pristupe inicializuj listbox
			if(subjectsListBox.getItemCount() == 0)	initSubjectListBox();
			
			//ovladacie prvky su pre uzivatela neviditelne az pokial neklikne na st. odbor
			hidePanel.setVisible(true);
			subjectsCellTable.setVisible(true);
			
			//vysictit zoznam a stiahnut nove predmety
			subjectsDataProvider.getList().clear();
			subjectsDataProvider.fetchSubjects();
		}
	}
	
	public void clearSubjectsListBox(){
		subjectsListBox.clear();
	}
}