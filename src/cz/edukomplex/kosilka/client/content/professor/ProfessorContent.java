package cz.edukomplex.kosilka.client.content.professor;

import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
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
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.ProfessorModel;
import cz.edukomplex.kosilka.client.provider.ProfessorsDataProvider;

public class ProfessorContent extends Composite {

	private static ProfessorContentUiBinder uiBinder = GWT
			.create(ProfessorContentUiBinder.class);

	interface ProfessorContentUiBinder extends
			UiBinder<Widget, ProfessorContent> {
	}
	
	@UiField(provided = true) CellTable<ProfessorModel> professorsCellTable;
	@UiField Button createNewProfessor;
	
	private static final int UPDATE_TITLE_BEFORE = 0;
	private static final int UPDATE_TITLE_BEHIND = 1;
	private static final int UPDATE_FIRSTNAME = 2;
	private static final int UPDATE_LASTNAME = 3;
	
	private ProfessorsDataProvider professorsDataProvider = new ProfessorsDataProvider();
	private ApplicationController appController = ApplicationController.getInstance();
	
	public ProfessorContent() {
		professorsCellTable = new CellTable<ProfessorModel>(0, appController.getCellTableResources(), ProfessorModel.KEY_PROVIDER);
		initWidget(uiBinder.createAndBindUi(this));
		initCellTable();
	}

	private void initCellTable() {
				
		professorsCellTable.setSelectionModel(new SingleSelectionModel<ProfessorModel>());
		professorsCellTable.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		Column<ProfessorModel,String> titleBefore = new Column<ProfessorModel, String>(new EditTextCell()) {
			
			@Override
			public String getValue(ProfessorModel object) {
				return object.getTitleBefore();
			}
		};
		titleBefore.setFieldUpdater(new FieldUpdater<ProfessorModel, String>() {
			
			@Override
			public void update(int index, ProfessorModel object, String value) {
				
				//ak sa hodnota nezmenila
				if(object.getTitleBefore().equals(value)) return;
				
				updateProfessor(object,value,UPDATE_TITLE_BEFORE);							
			}
		});
		
		
		Column<ProfessorModel,String> firstName = new Column<ProfessorModel, String>(new EditTextCell()) {
			
			@Override
			public String getValue(ProfessorModel object) {
				return object.getFirstname();
			}
		};
		firstName.setFieldUpdater(new FieldUpdater<ProfessorModel, String>() {
			
			@Override
			public void update(int index, ProfessorModel object, String value) {
				
				//ak sa hodnota nezmenila
				if(object.getFirstname().equals(value)) return;
				
				updateProfessor(object, value, UPDATE_FIRSTNAME);
				
			}
		});
		
		Column<ProfessorModel,String> lastName = new Column<ProfessorModel, String>(new EditTextCell()) {
			
			@Override
			public String getValue(ProfessorModel object) {
				return object.getLastname(true);
			}
		};
		lastName.setFieldUpdater(new FieldUpdater<ProfessorModel, String>() {
			
			@Override
			public void update(int index, ProfessorModel object, String value) {
								
				//ak sa hodnota nezmenila
				if(object.getLastname(true).equals(value)) return;
				
				updateProfessor(object, value, UPDATE_LASTNAME);				
			}
		});
		
				
		Column<ProfessorModel,String> titleBehind = new Column<ProfessorModel, String>(new EditTextCell()) {
			
			@Override
			public String getValue(ProfessorModel object) {
				return object.getTitleBehind();
			}
		};
		titleBehind.setFieldUpdater(new FieldUpdater<ProfessorModel, String>() {
			
			@Override
			public void update(int index, ProfessorModel object, String value) {
				
				//ak sa hodnota nezmenila
				if(object.getTitleBehind().equals(value)) return;
				
				updateProfessor(object, value, UPDATE_TITLE_BEHIND);				
			}
		});
		
		Column<ProfessorModel, String> deleteProfessor = new Column<ProfessorModel, String>(new ButtonCell()) {
			
			@Override
			public String getValue(ProfessorModel object) {
				return "X";
			}
		};
		
		deleteProfessor.setFieldUpdater(new FieldUpdater<ProfessorModel, String>() {
			
			@Override
			public void update(int index, ProfessorModel object, String value) {
				deleteProfessor(index,object);				
			}
		});
		
		professorsCellTable.addColumn(titleBefore,appController.constants.titleBefore());
		//professorsCellTable.setColumnWidth(titleBefore, "60px");
		professorsCellTable.addColumn(firstName,appController.constants.firstname());
		//professorsCellTable.setColumnWidth(firstName, "60px");
		professorsCellTable.addColumn(lastName,appController.constants.lastname());
		//professorsCellTable.setColumnWidth(lastName, "60px");
		professorsCellTable.addColumn(titleBehind,appController.constants.titleBehind());
		//professorsCellTable.setColumnWidth(titleBehind, "60px");		
		professorsCellTable.addColumn(deleteProfessor);
		
		professorsCellTable.setVisibleRange(0, 999);
		professorsDataProvider.addDataDisplay(professorsCellTable);
		
	}

	private void deleteProfessor(final int index, ProfessorModel object) {
		
		String method = "professor.delete";
		Object[] params = new Object[]{object.getPid()};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				MyXmlRpcClient.createClient(), method, params, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				return;
				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.equals(false)){
					Window.alert(appController.constants.databaseInsertFailed());
					return;
				}
								
				professorsDataProvider.getList().remove(index);
				professorsDataProvider.update();				
			}
		});
		
		request.execute();		
		
	}

	private void updateProfessor(final ProfessorModel object, final String value, final int updateType){
			
		//kontrola na spravy rozsah updataType
		if(updateType < 0 && updateType > 3){
			return;
		}
					
		String method = "professor.update";
		Object[] params = null;
		
		
		switch (updateType) {
			case UPDATE_TITLE_BEFORE:
				params = new Object[]{object.getPid(),null,null,value,null,updateType};
				break;
			case UPDATE_TITLE_BEHIND:
				params = new Object[]{object.getPid(),null,null,null,value,updateType};
				break;
			case UPDATE_FIRSTNAME:
				params = new Object[]{object.getPid(),value,null,null,null,updateType};
				break;
			case UPDATE_LASTNAME:
				params = new Object[]{object.getPid(),null,value,null,null,updateType};
				break;
			default:
				break;		
		}
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				MyXmlRpcClient.createClient(), method, params, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result == false){
					Window.alert(appController.constants.databaseInsertFailed());
					professorsCellTable.redraw();
					return;
				}
				
				switch (updateType) {
					case UPDATE_TITLE_BEFORE:
						object.setTitleBefore(value);
						break;
					case UPDATE_TITLE_BEHIND:
						object.setTitleBehind(value);
						break;
					case UPDATE_FIRSTNAME:
						object.setFirstname(value);
						break;
					case UPDATE_LASTNAME:
						object.setLastname(value);
						break;
					default:
						break;			
				}
				
			}
		});
		
		request.execute();		
	}

	@UiHandler("createNewProfessor")
	public void createNewProfessor(ClickEvent e){
		new NewProfessorDialog(professorsDataProvider).show();
	}
}