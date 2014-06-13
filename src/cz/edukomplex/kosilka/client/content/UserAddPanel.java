package cz.edukomplex.kosilka.client.content;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestCallback;
import com.google.gwt.http.client.RequestException;
import com.google.gwt.http.client.Response;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.xml.client.Element;
import com.google.gwt.xml.client.NodeList;

import cz.edukomplex.kosilka.client.helper.MyRequestBuilder;
import cz.edukomplex.kosilka.client.helper.MyXMLParser;
import cz.edukomplex.kosilka.client.i18n.KosilkaV3Constants;

public class UserAddPanel extends Composite {

	private static UserAddPanelUiBinder uiBinder = GWT
			.create(UserAddPanelUiBinder.class);

	interface UserAddPanelUiBinder extends UiBinder<Widget, UserAddPanel> {
	}
	
	public KosilkaV3Constants constants = GWT.create(KosilkaV3Constants.class);
	
	@UiField public ListBox yearListBox;
	@UiField public ListBox groupListBox;
	@UiField public ListBox studentListBox;
	@UiField public Button addButton;
	//@UiField public Button addAllButton;
		
	public UserAddPanel() {
		initWidget(uiBinder.createAndBindUi(this));
		yearListBox.addChangeHandler(yearListBoxChangeHandler);		
		groupListBox.addChangeHandler(groupListBoxChangeHandler);
		reset();
		populateYearListBox();	
		yearListBox.addItem(constants.makeChoice());		
	}
	
	protected void pupulateStudentListBox() {
		
		String IDtr = groupListBox.getValue(groupListBox.getSelectedIndex());				
		RequestBuilder studentRequest = MyRequestBuilder.createService(RequestBuilder.POST,"Student","fetchStudentsByIDTridy");
		
		try {
			studentRequest.sendRequest("ID_tr=" + IDtr, studentRequestCallback);
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void populateGroupListBox() throws Exception {
		
		String IDsk = yearListBox.getValue(yearListBox.getSelectedIndex());				
		RequestBuilder groupRequest = MyRequestBuilder.createService(RequestBuilder.POST, "TTridy", "fetchByIDsk");				
		
		try {
			groupRequest.sendRequest("IDsk=" + IDsk , groupRequestCallback);				
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void populateYearListBox() {
		
		RequestBuilder yearRequest = MyRequestBuilder.createService(RequestBuilder.POST, "SkRok", "fetchAll");
		
		try {
			yearRequest.sendRequest(null, yearRequestCallback);
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}	
	
	public void reset(){
		yearListBox.clear();		
		groupListBox.clear();
		studentListBox.clear();
	}
	
		
	protected RequestCallback yearRequestCallback = new RequestCallback() {
		
		@Override
		public void onResponseReceived(Request request, Response response) {
			parseYearXMLResponse(response.getText());			
		}
		
		@Override
		public void onError(Request request, Throwable exception) {
			Window.alert(exception.getMessage());
		}
	};

	
	protected RequestCallback groupRequestCallback = new RequestCallback() {
		
		@Override
		public void onResponseReceived(Request request, Response response) {			
			groupListBox.clear();
			parseGroupXMLResponse(response.getText());
			/*
			 * Ak je len jedna triedu tak rovno zavolaj funkciu na zobrazenie studentov
			 */
			if(groupListBox.getItemCount() == 1){
				pupulateStudentListBox();
			}
		}
		
		@Override
		public void onError(Request request, Throwable exception) {
			Window.alert(exception.getMessage());
		}
	};
	
	protected RequestCallback studentRequestCallback = new RequestCallback() {
		
		@Override
		public void onResponseReceived(Request request, Response response) {
			studentListBox.clear();
			parseStudentXMLResponse(response.getText());
			
		}
		
		@Override
		public void onError(Request request, Throwable exception) {
			Window.alert(exception.getMessage());
			
		}
	};
	
	private void parseStudentXMLResponse(String response) {		
		Element root = (Element)MyXMLParser.parseXML(response);
		NodeList students = root.getElementsByTagName("student");
				
		for (int i = 0; i < students.getLength(); i++) {
			Element student = (Element)students.item(i);
			studentListBox.addItem(
					student.getAttribute("Prijmeni") + " " + student.getAttribute("Jmeno"),
					student.getAttribute("ID_st")
			);
		}	
	}

	private void parseGroupXMLResponse(String response) {		
		Element root = (Element)MyXMLParser.parseXML(response);
		NodeList groups = root.getElementsByTagName("ttridy");
		
		groupListBox.addItem(constants.makeChoice());
			
		for (int i = 0; i < groups.getLength(); i++) {
			Element group = (Element)groups.item(i);			
			groupListBox.addItem(group.getAttribute("Zkratka"), group.getAttribute("ID_tr"));
		}			
	}

	private void parseYearXMLResponse(String response) {		
		Element root = (Element)MyXMLParser.parseXML(response);
		NodeList years = root.getElementsByTagName("skrok");
				
		for (int i = 0; i < years.getLength(); i++) {
			Element year = (Element)years.item(i);			
			yearListBox.addItem(year.getAttribute("skolni_rok"), year.getAttribute("ID_sk"));
		}		
	}
	
	private ChangeHandler groupListBoxChangeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			if(groupListBox.getSelectedIndex() != 0) pupulateStudentListBox();			
		}
	}; 
	
	private ChangeHandler yearListBoxChangeHandler = new ChangeHandler() {
		
		@Override
		public void onChange(ChangeEvent event) {
			try {
				if(yearListBox.getSelectedIndex() != 0)	populateGroupListBox();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	};
}