package cz.edukomplex.kosilka.client.content.harmonogram;

import java.util.Date;



public class HarmonogramCellListRow implements Comparable<HarmonogramCellListRow> {
	
	public String hid;
	public String harmonogramName;	
	public String idStudyField;
	public String studyFieldName;	
	public Date date;
	public String yid;
	public String month;
	
	public HarmonogramCellListRow(){}

	public HarmonogramCellListRow(String hid, String harmonogramName,
			String idStudyField, String studyFieldName, Date date, String yid, String month) {
		this.hid = hid;
		this.harmonogramName = harmonogramName;
		this.idStudyField = idStudyField;
		this.studyFieldName = studyFieldName;
		this.date = date;
		this.yid = yid;
		this.month = month;
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
	
	public String getYid() {
		return yid;
	}

	public void setYid(String yid) {
		this.yid = yid;
	}

	public void setHarmonogramName(String harmonogramName) {
		this.harmonogramName = harmonogramName;
	}
	
	public String getIdStudyField() {
		return idStudyField;
	}

	public void setIdStudyField(String idStudyField) {
		this.idStudyField = idStudyField;
	}

	public String getStudyFieldName() {
		return studyFieldName;
	}

	public void setStudyFieldName(String studyFieldName) {
		this.studyFieldName = studyFieldName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	} 
		
	public String getMonth() {
		return month;
	}

	public void setMonth(String month) {
		this.month = month;
	}

	@Override
	public int compareTo(HarmonogramCellListRow o) {
		if(date.before(o.date)){
			return -1;
		}
		if(date.after(o.date)){
			return 1;
		}
		
		return 0;		
	}

	@Override
	public String toString() {
		return "HarmonogramCellListRow [hid=" + hid + ", harmonogramName="
				+ harmonogramName + ", idStudyField=" + idStudyField
				+ ", studyFieldName=" + studyFieldName + ", date=" + date
				+ ", yid=" + yid + "]";
	}	
}
