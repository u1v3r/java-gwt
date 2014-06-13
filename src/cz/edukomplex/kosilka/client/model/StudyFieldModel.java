package cz.edukomplex.kosilka.client.model;

public class StudyFieldModel {
	
	private String idOboru;
	private String obor;
	
	public StudyFieldModel(){}
	
	public StudyFieldModel(String idOboru, String obor) {
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

	@Override
	public String toString() {
		return "StudyField [idOboru=" + idOboru + ", obor=" + obor + "]";
	}
	
	/*
	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj instanceof StudyField == false) return false;
		StudyField studyField = (StudyField)obj;		
		if(this.idOboru == studyField.idOboru && this.obor == studyField.obor){
			return true;		
		}
		else {
			return false;
		}
	}
	*/
}
