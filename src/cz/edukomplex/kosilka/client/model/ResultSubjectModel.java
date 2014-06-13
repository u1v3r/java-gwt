package cz.edukomplex.kosilka.client.model;

public class ResultSubjectModel {

	private String sid;
	private String rid;
	private String name;
	private String shortName;
	private String grade;		

	public ResultSubjectModel(){}

	public ResultSubjectModel(String sid, String rid, String name,
			String shortName, String grade) {
		this.sid = (sid == null) ? "": sid;
		this.rid = (rid == null) ? "": rid;
		this.name = (name == null) ? "": name;
		this.shortName = (shortName == null) ? "": shortName;
		this.grade = (grade == null) ? "": grade;
	}

	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = (sid == null) ? "": sid;
	}
	public String getRid() {
		return rid;
	}
	public void setRid(String rid) {
		this.rid = (rid == null) ? "": rid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = (name == null) ? "": name;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = (shortName == null) ? "": shortName;
	}
	public String getGrade() {
		return grade;
	}
	public void setGrade(String grade) {
		this.grade = (grade == null) ? "": grade;
	}

	@Override
	public String toString() {
		return "ResultSubjectModel [sid=" + sid + ", rid=" + rid + ", name="
				+ name + ", shortName=" + shortName + ", grade=" + grade + "]";
	}	

}

