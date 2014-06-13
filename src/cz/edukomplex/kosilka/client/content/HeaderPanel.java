package cz.edukomplex.kosilka.client.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Widget;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.content.settings.Settings;
import cz.edukomplex.kosilka.client.helper.AuthManager;

public class HeaderPanel extends Composite{

	private static HeaderPanelUiBinder uiBinder = GWT
			.create(HeaderPanelUiBinder.class);

	interface HeaderPanelUiBinder extends UiBinder<Widget, HeaderPanel> {
	}
	
	private static final int TIMER = 5000;//5s
	
	private ApplicationController appController = ApplicationController.getInstance();
	
	@UiField Anchor logout;	
	@UiField Anchor settings;
	@UiField HTMLPanel messageBox;
	@UiField Label messageBoxText;
	
	public HeaderPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		logout.setHref("#");
		settings.setHref("#");
	}
	
	@UiHandler("logout")
	public void handleLogout(ClickEvent e){
		AuthManager.logout();
	}
	
	@UiHandler("settings")
	public void handleSettings(ClickEvent e){
		/*
		 * Code-splitting - sposobi, ze kod sa stiahne az v pripade zavolania
		 * http://code.google.com/webtoolkit/doc/latest/DevGuideCodeSplitting.html
		 */
		GWT.runAsync(new RunAsyncCallback() {
			
			@Override
			public void onSuccess() {
				appController.setAppContent(new Settings());				
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert("AJAX: Download settings code failed");
			}
		});
		
	}

	public void setMesageBoxText(String text) {
		messageBox.setVisible(true);
		messageBoxText.setText(text);
		
		new Timer() {			
			@Override
			public void run() {
				messageBox.setVisible(false);
				messageBoxText.setText("");
			}
		}.schedule(TIMER);
				
	}
	
	public void setMesageBoxText(String text, boolean error) {
		
		messageBox.setVisible(true);
		messageBox.setStyleName(appController.getApplicationResources().applicationStyle().errorMessageBox());
		messageBoxText.setText(text);
		
		new Timer() {			
			@Override
			public void run() {
				messageBox.setVisible(false);
				messageBoxText.setText("");
				messageBox.setStyleName(appController.getApplicationResources().applicationStyle().infoMessageBox());
			}
		}.schedule(TIMER);
				
	}
	
	public void showLoadingMessage(){		
		messageBox.setVisible(true);
		messageBoxText.setText("Loading...");
	}
	
	public void hideLoadingMessage(){
		messageBox.setVisible(false);
		messageBoxText.setText("");
	}
}