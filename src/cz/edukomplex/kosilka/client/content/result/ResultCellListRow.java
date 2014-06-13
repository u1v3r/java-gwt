package cz.edukomplex.kosilka.client.content.result;

public class ResultCellListRow {
	
	public String hid;
	public String harmonogramName;
	public String date;
	
	public ResultCellListRow(){}
	
	public ResultCellListRow(String hid, String harmonogramName, String date) {
		this.hid = hid;
		this.harmonogramName = harmonogramName;
		this.date = date;
	}
	
	public String getHid() {
		return hid;
	}
	public void setHid(String hid) {
		this.hid = hid;
	}
	public String getHarmonogramName() {
		return harmonogramName;
	}
	public void setHarmonogramName(String harmonogramName) {
		this.harmonogramName = harmonogramName;
	}
	public String getDate() {
		return date;
	}
	public void setDate(String date) {
		this.date = date;
	}
	
}
