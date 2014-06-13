package cz.edukomplex.kosilka.client.content.block;

import java.util.List;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.cell.client.ButtonCell;
import com.google.gwt.cell.client.EditTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.QuestionModel;
import cz.edukomplex.kosilka.client.model.SubjectModel;
import cz.edukomplex.kosilka.client.provider.QuestionsDataProvider;

public class QuestionPanel extends Composite {

	private static QuestionPanelUiBinder uiBinder = GWT
			.create(QuestionPanelUiBinder.class);

	interface QuestionPanelUiBinder extends UiBinder<Widget, QuestionPanel> {
	}
	
	@UiField(provided = true) CellTable<QuestionModel> questionsCellTable;
	@UiField TextBox subjectNameTextBox;
	@UiField TextBox subjectShortTextBox;
	@UiField TextBox questionTextBox;
	@UiField Button addQuestionBtn;
	@UiField Button saveBtn;
	@UiField ScrollPanel questionsScrollPanel;
	@UiField HTMLPanel hidePanel; 
	
	private QuestionsDataProvider questionsDataProvider;
	private SubjectModel subject;
	private ApplicationController appController = ApplicationController.getInstance();
	
	public QuestionPanel(SubjectModel subject, int height) {
		this();
		this.subject = subject;
		hidePanel.setVisible(true);
		questionsScrollPanel.setHeight(height + "px"); 
		questionsDataProvider = new QuestionsDataProvider(subject);
			
		subjectNameTextBox.setValue(subject.getName());
		subjectShortTextBox.setValue(subject.getShortName());
		
		initCellTable();	
		
	}

	public QuestionPanel() {
		
		questionsCellTable = new CellTable<QuestionModel>(
				0, appController.getCellTableResources(), QuestionModel.KEY_PROVIDER);
		questionsDataProvider = new QuestionsDataProvider();
		
		initWidget(uiBinder.createAndBindUi(this));
	}

	private void initCellTable() {
		
		final Column<QuestionModel, String> questionNumber = new Column<QuestionModel, String>(new EditTextCell()) {
			
			@Override
			public String getValue(QuestionModel object) {
				return (Integer.valueOf(object.getQn()) + 1) + "";//v db sa cisluje od 0, preto treba +1
			}
		};
		questionNumber.setFieldUpdater(new FieldUpdater<QuestionModel, String>() {
			
			@Override
			public void update(int index, QuestionModel object, String value) {
				try{					
					int number = Integer.valueOf(value);//kontrola ci je cislo
					if(number <= 0) throw new NumberFormatException();//nesmie byt zaporne					
					updateQuestionNumber(object,value);
				}catch(NumberFormatException e){
					EditTextCell cell = (EditTextCell)questionNumber.getCell();
					cell.clearViewData(QuestionModel.KEY_PROVIDER.getKey(object));
					questionsCellTable.redraw();
					return;
				}
			}
		});
		
		final Column<QuestionModel, String> questionText = new Column<QuestionModel, String>(new EditTextCell()) {
			
			@Override
			public String getValue(QuestionModel object) {
				return object.getText();
			}
		};
		questionText.setFieldUpdater(new FieldUpdater<QuestionModel, String>() {
			
			@Override
			public void update(int index, final QuestionModel object, final String value) {
				
				if(value.isEmpty()){
					EditTextCell column = (EditTextCell)questionText.getCell();
					column.clearViewData(QuestionModel.KEY_PROVIDER.getKey(object));
					questionsCellTable.redraw();
					return;
				}
				
				//ak sa nic nezmenilo
				if(object.getText().equals(value)){
					return;
				}
									
				XmlRpcClient client = MyXmlRpcClient.createClient();
				String methodName = "question.update";
				Object[] params = new Object[]{object.getQid(),value};
					
				XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(client, methodName, params, new AsyncCallback<Boolean>() {
	
					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}
	
					@Override
					public void onSuccess(Boolean result) {
						if(result){
							object.setText(value);
							return;
						}						
					}
				});
				request.execute();
				
			}
		});
		
		Column<QuestionModel, String> removeBtn = new Column<QuestionModel, String>(new ButtonCell()) {
			
			@Override
			public String getValue(QuestionModel object) {
				return "X";
			}
		};
		removeBtn.setFieldUpdater(new FieldUpdater<QuestionModel, String>() {
			
			@Override
			public void update(int index, QuestionModel object, String value) {
				removeQuestion(index,object);				
			}
		});
		
		questionsCellTable.setColumnWidth(removeBtn, "15px");
		questionsCellTable.setColumnWidth(questionNumber, "15px");
		questionsCellTable.addColumn(questionNumber);
		questionsCellTable.addColumn(questionText);
		questionsCellTable.addColumn(removeBtn);
							
		questionsCellTable.setVisibleRange(0, 999);//musi obsahovat nejake cislo, inak sa nezobrazi		
		questionsDataProvider.addDataDisplay(questionsCellTable);
	}

	private void updateQuestionNumber(final QuestionModel object, final String value) {
		
		for (QuestionModel question : questionsDataProvider.getList()) {
			if(value.equals((Integer.valueOf(question.getQn()) + 1) + "")){//v db su cisla od 0, preto +1
				Window.alert("Otázka s čílsom " + value + " sa už v predmete nachádza");
				return;
			}
		}
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "question.updateQuestionNumber";
		Object[] params = new Object[]{object.getQid(),value};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(client, methodName, params, 
				new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(result.equals(false)){
					Window.alert(appController.constants.databaseInsertFailed());
					return;
				}			
				
				object.setQn((Integer.valueOf(value) - 1) + "");//treba odcitat -1, v db od 0
				questionsDataProvider.update();
			}
		});
		
		request.execute();		
	}
	
	private void removeQuestion(final int index, QuestionModel object) {
		 
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "question.delete";
		Object[] params = new Object[]{object.getQid()};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(client, methodName, params, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(!result){
					Window.alert(appController.constants.deleteQuestionFailed());
					return;
				}
				
				questionsDataProvider.getList().remove(index);
				questionsDataProvider.update();
			}
		});
		
		request.execute();
	}
	
	
	@UiHandler("addQuestionBtn")
	public void handleAddQuestionBtn(ClickEvent e){
		addQuestion();
	}
	
	
	@UiHandler("saveBtn")
	public void handleSaveBtn(ClickEvent e){
		
		String subjectName = subjectNameTextBox.getValue();
		String subjectShort = subjectShortTextBox.getValue();
		
		//kontorla ci sa aspon jedna hodnota zmenila
		if(subjectName.equals(this.subject.getName()) && subjectShort.equals(this.subject.getShortName())){
			return;
		}
		
		//kontrola ci hodnoty nie su prazdne
		if(subjectName.isEmpty() || subjectShort.isEmpty()){
			Window.alert(appController.constants.subjectNameAndShortucut());
			return;
		}		
		
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "subject.update";
		Object[] params = new Object[]{subject.getSid(),subjectName,subjectShort};
		
		//upravi aktualny subject, ktory sluzi pre porovanvanie na zmenu
		this.subject = new SubjectModel(this.subject.getSid(), subjectName, subjectShort);
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(client, methodName, params, new AsyncCallback<Boolean>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Boolean result) {
				if(!result){
					Window.alert(appController.constants.addSubjectFail());
					return;
				}				
				appController.refreshBlockContent();
			}
		});
		request.execute();
		
	}
	
	private void addQuestion(){
		/*
		 * Overenie ci je vyplnene meno a skratka
		 */
		if(subjectNameTextBox.getText().isEmpty() || subjectShortTextBox.getText().isEmpty()){
			Window.alert(appController.constants.subjectNotSet());
			return;
		}
		
		/*
		 * Textbox s otazkou nesmie byt prazdny
		 */
		if(questionTextBox.getText().isEmpty()){
			return;
		}
		
		String qn = "0";
		List<QuestionModel> data = questionsDataProvider.getList();
		
		if(!data.isEmpty()){			
			//Zisti poradie otazky z posledneho prvku			 
			qn = data.get(data.size()-1).getQn();
			qn = (Integer.valueOf(qn) + 1) + "";
		}		
		
		final String questionNumber = qn;
		final String text = questionTextBox.getText();
					
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "question.add";
		Object[] params = new Object[]{subject.getSid(),qn,text};
			
		XmlRpcRequest<Integer> request = new XmlRpcRequest<Integer>(client, methodName, params, new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Integer result) {
				if(result > 0){
					QuestionModel model = new QuestionModel(
							String.valueOf(result), subject.getSid(), questionNumber, text
					);					
					questionsDataProvider.getList().add(model);						
					questionsDataProvider.update();
					questionsScrollPanel.scrollToBottom();
					return;
				}
				
				Window.alert(appController.constants.addQeustionFailed());
			}
		});
		request.execute();		
		
		//vycisti textbox
		questionTextBox.setText("");		
		
	}
	
	@UiHandler("questionTextBox")
	public void handleReturnEvent(KeyDownEvent e){
		
		if(e.getNativeKeyCode() == KeyCodes.KEY_ENTER){
			addQuestion();
		}
	}
}
