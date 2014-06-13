package cz.edukomplex.kosilka.client.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.KosilkaV3;
import cz.edukomplex.kosilka.client.helper.AuthManager;

public class LoginPage extends Composite {

	private static LoginPageUiBinder uiBinder = GWT
			.create(LoginPageUiBinder.class);

	interface LoginPageUiBinder extends UiBinder<Widget, LoginPage> {
	}
	
	public KosilkaV3 parent;
	
	private ApplicationController appManager = ApplicationController.getInstance();
	
	@UiField TextBox usernameTextBox;
	@UiField TextBox kontextTextBox;
	@UiField PasswordTextBox passwordTextBox;
	@UiField CheckBox permanentLoginCheckBox;
	@UiField Button loginBtn;

	public LoginPage(KosilkaV3 parent) {
		this.parent = parent;
		initWidget(uiBinder.createAndBindUi(this));
		
		//key event ENTER
		passwordTextBox.addKeyDownHandler(new KeyDownHandler() {
			
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode() == KeyCodes.KEY_ENTER){
					checkLoginData();
				}				
			}
		});
	}
	
	@UiHandler("loginBtn")
	public void handleLoginBtn(ClickEvent e){		
		checkLoginData();		
	}
	
	private void checkLoginData(){
		
		String username = usernameTextBox.getText();
		String password = passwordTextBox.getText();
		String kontext = kontextTextBox.getText();
		
		if(username.isEmpty()){
			Window.alert(appManager.constants.userNameEmpty());
			return;
		}
		
		if(kontext.isEmpty()){
			Window.alert(appManager.constants.kontextEmpty());
			return;
		}
		
		if(password.isEmpty()){
			Window.alert(appManager.constants.passwordEmpty());
			return;
		}	
		
		if(!kontext.equals("ucitel") && !kontext.equals("admin") && !kontext.equals("zamest")){
			Window.alert(appManager.constants.invalidKontext());
			return;
		}
		
		
		AuthManager.login(username, kontext, password, permanentLoginCheckBox.getValue());
	}	
}
