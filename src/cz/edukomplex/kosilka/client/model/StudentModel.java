package cz.edukomplex.kosilka.client.model;

public class StudentModel {
	
	private String ID_st;
	private String jmeno;
	private String prijmeni;
	private String login;
	private String heslo;
	
	
	public StudentModel(){}
	
	public StudentModel(String iD_st, String jmeno, String prijmeni,
			String login, String heslo) {		
		this.ID_st = iD_st;
		this.jmeno = jmeno;
		this.prijmeni = prijmeni;
		this.login = login;
		this.heslo = heslo;
	}
	public String getID_st() {
		return ID_st;
	}
	public void setID_st(String iD_st) {
		ID_st = iD_st;
	}
	public String getJmeno() {
		return jmeno;
	}
	public void setJmeno(String jmeno) {
		this.jmeno = jmeno;
	}
	public String getPrijmeni() {
		return prijmeni;
	}
	public void setPrijmeni(String prijmeni) {
		this.prijmeni = prijmeni;
	}
	public String getLogin() {
		return login;
	}
	public void setLogin(String login) {
		this.login = login;
	}
	public String getHeslo() {
		return heslo;
	}
	public void setHeslo(String heslo) {
		this.heslo = heslo;
	}
	
}
