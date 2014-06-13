package cz.edukomplex.kosilka.client.model;

public class YearModel {
	
	private String yid;
	private String yearName;
	
	public YearModel(){}
	
	public YearModel(String yid, String yearName) {
		this.yid = yid;
		this.yearName = yearName;
	}

	public String getYid() {
		return yid;
	}

	public void setYid(String yid) {
		this.yid = yid;
	}

	public String getYearName() {
		return yearName;
	}

	public void setYearName(String yearName) {
		this.yearName = yearName;
	}
}