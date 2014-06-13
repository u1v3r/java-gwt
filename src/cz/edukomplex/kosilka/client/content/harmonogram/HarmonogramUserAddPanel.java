package cz.edukomplex.kosilka.client.content.harmonogram;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.RequestException;

import cz.edukomplex.kosilka.client.content.UserAddPanel;
import cz.edukomplex.kosilka.client.helper.MyRequestBuilder;

public class HarmonogramUserAddPanel extends UserAddPanel {
	
	private String idStudyField = null;
	
	public HarmonogramUserAddPanel(){};
	
	public HarmonogramUserAddPanel(String idStudyField) {
		super();
		this.idStudyField = idStudyField;
	}
	
	public void setIdStudyField(String idStudyField){
		this.idStudyField = idStudyField;
	}
	
	@Override
	protected void populateGroupListBox() throws Exception {
		
		if(idStudyField.equals(null)){
			throw new Exception("idStudyField is no set");
		}
		
		String IDsk = yearListBox.getValue(yearListBox.getSelectedIndex());				
		RequestBuilder groupRequest = MyRequestBuilder.createService(RequestBuilder.POST, "TTridy", "fetchByIDskANDIDOboru");				
		
		try {
			groupRequest.sendRequest("IDsk=" + IDsk + "&idStudyField=" + idStudyField , groupRequestCallback);				
		} catch (RequestException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
