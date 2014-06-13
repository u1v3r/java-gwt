package cz.edukomplex.kosilka.client.content.block;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
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

public class NewSubjectDialog extends DialogBox {

	private static NewSubjectDialogUiBinder uiBinder = GWT
			.create(NewSubjectDialogUiBinder.class);

	interface NewSubjectDialogUiBinder extends
			UiBinder<Widget, NewSubjectDialog> {
	}
	
	@UiField TextBox subjectNameTextBox;
	@UiField TextBox subjectShortTextBox;
	@UiField Button okBtn;
	@UiField Button cancelBtn;
		
	private ApplicationController appManager = ApplicationController.getInstance();
			
	public NewSubjectDialog() {				
		setWidget(uiBinder.createAndBindUi(this));		
		center();
		setText(appManager.constants.newSubjectBtn());		
	}
	
	@UiHandler("cancelBtn")
	public void handleCloseBtn(ClickEvent e){
		this.hide();
	}
	
	@UiHandler("okBtn")
	public void handleOkBtn(ClickEvent e){
		saveSubject();		
	}
		
	private void saveSubject() {
		
		if(subjectNameTextBox.getText().length() < 0 && subjectShortTextBox.getText().length() < 0){
			Window.alert(appManager.constants.subjectNameAndShortucut());
			return;
		}
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "subject.add";
		Object[] params = new Object[]{subjectNameTextBox.getText(),subjectShortTextBox.getText()};
		
		XmlRpcRequest<Boolean> request = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());						
					}

					@Override
					public void onSuccess(Boolean result) {
						if(!result){
							Window.alert(appManager.constants.addSubjectFail());
							return;
						}
						
						appManager.refreshBlockContent();
						hide();						
					}
				}
		);
		request.execute();
		
	}
}
