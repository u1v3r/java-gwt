package cz.edukomplex.kosilka.client.model;

import com.google.gwt.view.client.ProvidesKey;

public class ProfessorModel implements Comparable<ProfessorModel>{
		
	private String pid;
	private String shortcut;
	private String firstname;	
	private String lastname;
	private String titleBefore;
	private String titleBehind;
	
	public final static ProvidesKey<ProfessorModel> KEY_PROVIDER = new ProvidesKey<ProfessorModel>() {
		
		@Override
		public Object getKey(ProfessorModel item) {
			return item.pid;
		}
	};
	
	public ProfessorModel(){};
	
	public ProfessorModel(String pid, String shortcut, String firstname,
			String lastname, String titleBefore, String titleBehind) {
		this.pid = pid;
		this.shortcut = shortcut;
		this.firstname = firstname;
		this.lastname = lastname;
		this.titleBefore = titleBefore;
		this.titleBehind = titleBehind;
	}
	
	public String getPid() {
		return pid;
	}
	public void setPid(String pid) {
		this.pid = pid;
	}
	public String getShortcut() {
		return shortcut;
	}
	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}
	public String getFirstname() {
		return firstname;
	}
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}
	public String getLastname() {
		
		//ak ma profesor titul za priezvisko pridaj za meno ciarku		
		if(titleBehind.isEmpty()){
			return lastname;
		}
		
		return lastname + ",";
	}
	
	public String getLastname(boolean withoutComma){
		
		if(withoutComma) return lastname;
		
		return getLastname();
	}
	
	public void setLastname(String lastname) {
		this.lastname = lastname;
	}
	public String getTitleBefore() {
		return titleBefore;
	}
	public void setTitleBefore(String titleBefore) {
		this.titleBefore = titleBefore;
	}
	public String getTitleBehind() {
		return titleBehind;
	}
	public void setTitleBehind(String titleBehind) {
		this.titleBehind = titleBehind;
	}
	
	@Override
	public boolean equals(Object obj) {
		
		if(obj == this) return true;
		if(obj instanceof ProfessorModel == false) return false;
		
		ProfessorModel professor = (ProfessorModel)obj;		
		if(this.pid.equals(professor.pid) && this.lastname.equals(professor.lastname) && 
				this.firstname.equals(professor.firstname)) return true;
			
		return false;
		
	}

	@Override
	public String toString() {
		return "ProfessorModel [pid=" + pid + ", shortcut=" + shortcut
				+ ", firstname=" + firstname + ", lastname=" + lastname
				+ ", titleBefore=" + titleBefore + ", titleBehind="
				+ titleBehind + "]";
	}

	@Override
	public int compareTo(ProfessorModel o) {
		return this.lastname.compareTo(o.lastname);
	}
}
