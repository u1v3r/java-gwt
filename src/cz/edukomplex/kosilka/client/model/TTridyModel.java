package cz.edukomplex.kosilka.client.model;

public class TTridyModel {
	
	private String ID_tr;
	private String Zkratka;
	private String id_oboru;
	private String vznik;
	
	
	public TTridyModel(){}
	
	public TTridyModel(String iD_tr, String zkratka, String id_oboru,
			String vznik) {
		this.ID_tr = iD_tr;
		this.Zkratka = zkratka;
		this.id_oboru = id_oboru;
		this.vznik = vznik;
	}
	public String getID_tr() {
		return ID_tr;
	}
	public void setID_tr(String iD_tr) {
		ID_tr = iD_tr;
	}
	public String getZkratka() {
		return Zkratka;
	}
	public void setZkratka(String zkratka) {
		Zkratka = zkratka;
	}
	public String getId_oboru() {
		return id_oboru;
	}
	public void setId_oboru(String id_oboru) {
		this.id_oboru = id_oboru;
	}
	public String getVznik() {
		return vznik;
	}
	public void setVznik(String vznik) {
		this.vznik = vznik;
	}
	
}
