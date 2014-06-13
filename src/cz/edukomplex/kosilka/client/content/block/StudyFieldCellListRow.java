package cz.edukomplex.kosilka.client.content.block;

public class StudyFieldCellListRow {
	
	public String idStudyField;
	public String name;
	
	public StudyFieldCellListRow(){}
	
	public StudyFieldCellListRow(String idStudyField, String name) {
		this.idStudyField = idStudyField;
		this.name = name;
	}
	
	public String getIdStudyField() {
		return idStudyField;
	}
	public void setIdStudyField(String idStudyField) {
		this.idStudyField = idStudyField;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
}
