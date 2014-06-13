package cz.edukomplex.kosilka.client.content.professor;

import java.util.Collections;

import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.ProfessorModel;
import cz.edukomplex.kosilka.client.provider.ProfessorsDataProvider;

public class NewProfessorDialog extends DialogBox {

	private static NewProfessorDialogUiBinder uiBinder = GWT
			.create(NewProfessorDialogUiBinder.class);

	interface NewProfessorDialogUiBinder extends
			UiBinder<Widget, NewProfessorDialog> {
	}
	
	@UiField TextBox titleBeforeTextBox;
	@UiField TextBox titleBehindTextBox;
	@UiField TextBox firstNameTextBox;
	@UiField TextBox lastNameTextBox;
	@UiField Button okBtn;
	@UiField Button cancelBtn;
	
	private ApplicationController appController = ApplicationController.getInstance();
	private ProfessorsDataProvider professorsDataProvider;
	
	public NewProfessorDialog(ProfessorsDataProvider professorsDataProvider) {
		this.professorsDataProvider = professorsDataProvider;
		setWidget(uiBinder.createAndBindUi(this));
		center();
		setText(appController.constants.createNewProfessor());
	}
	
	@UiHandler("cancelBtn")
	public void closeDialog(ClickEvent e){
		hide();
	}
	
	@UiHandler("okBtn")
	public void createProfessor(ClickEvent e){
		
		final String firstName = firstNameTextBox.getValue();
		final String lastName = lastNameTextBox.getValue();
		final String titleBefore = titleBeforeTextBox.getValue();
		final String titleBehind = titleBehindTextBox.getValue();
		
		//meno a priezvisko musi byt vyplnene
		if(firstName.isEmpty() || lastName.isEmpty()){
			return;
		}
		
		String method = "professor.add";
		Object[] params = new Object[]{firstName,lastName,titleBefore,titleBehind};
		
		XmlRpcRequest<Integer> request = new XmlRpcRequest<Integer>(
				MyXmlRpcClient.createClient(), method, params, new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				return;
				
			}

			@Override
			public void onSuccess(Integer result) {
				if(result < 1){
					Window.alert(appController.constants.databaseInsertFailed());
					return;
				}
				
				ProfessorModel row = new ProfessorModel(
						String.valueOf(result),
						null,
						firstName,
						lastName,
						titleBefore,
						titleBehind
				);
				
				//vlozenie do zoznamu a update
				professorsDataProvider.getList().add(row);
				
				//zoradenie podla mena
				Collections.sort(professorsDataProvider.getList());
				professorsDataProvider.update();
				
				//skrytie dialogboxu
				hide();
			}
		});
		
		request.execute();
		
	}

}
