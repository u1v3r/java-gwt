package cz.edukomplex.kosilka.client.model;

public class StOboryModel {
	
	private String idOboru;
	private String obor;
		
	public StOboryModel(){}
	
	public StOboryModel(String idOboru, String obor) {		
		this.idOboru = idOboru;
		this.obor = obor;
	}
	
	public String getIdOboru() {
		return idOboru;
	}
	
	public void setIdOboru(String idOboru) {
		this.idOboru = idOboru;
	}
	
	public String getObor() {
		return obor;
	}
	
	public void setObor(String obor) {
		this.obor = obor;
	}	
}
