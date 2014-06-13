package cz.edukomplex.kosilka.client.content.harmonogram;

import com.google.gwt.view.client.ProvidesKey;


public class HarmonogramCellTableRow implements Comparable<HarmonogramCellTableRow>{	
	
	public String number;	
	public String hid;
	public String ID_st;
	public String student;
	public String ID_tr;
	public String group;
	public String time;
	public String order;
	public String bachelorThesis;
	
	public static final ProvidesKey<HarmonogramCellTableRow> KEY_PROVIDER = new ProvidesKey<HarmonogramCellTableRow>() {
		
		@Override
		public Object getKey(HarmonogramCellTableRow item) {
			return item.ID_st;
		}
	};
	
	public HarmonogramCellTableRow(){}	
	
	
	public HarmonogramCellTableRow(String hid, String iD_st, String student,
			String iD_tr, String group, String time, String order,
			String bachelorThesis, int number) {
		this.hid = hid;
		this.ID_st = iD_st;
		this.student = student;
		this.ID_tr = iD_tr;
		this.group = group;
		this.time = time;
		this.order = order;
		this.bachelorThesis = bachelorThesis;
		this.number = String.valueOf(number);
	}
	
	public String getNumber() {
		return number;
	}


	public void setNumber(int number) {
		this.number = String.valueOf(number);
	}


	public String getHid() {
		return hid;
	}


	public void setHid(String hid) {
		this.hid = hid;
	}


	public String getID_st() {
		return ID_st;
	}


	public void setID_st(String iD_st) {
		ID_st = iD_st;
	}


	public String getStudent() {
		return student;
	}


	public void setStudent(String student) {
		this.student = student;
	}


	public String getID_tr() {
		return ID_tr;
	}


	public void setID_tr(String iD_tr) {
		ID_tr = iD_tr;
	}


	public String getGroup() {
		return group;
	}


	public void setGroup(String group) {
		this.group = group;
	}


	public String getTime() {
		return time;
	}


	public void setTime(String time) {
		this.time = time;
	}


	public String getOrder() {
		return order;
	}


	public void setOrder(String order) {
		this.order = order;
	}


	public String getBachelorThesis() {
		return bachelorThesis;
	}


	public void setBachelorThesis(String bachelorThesis) {
		this.bachelorThesis = bachelorThesis;
	}


	@Override
	public int compareTo(HarmonogramCellTableRow o) {
		return Integer.valueOf(this.order) - Integer.valueOf(o.order);
	}

	@Override
	public String toString() {
		return "HarmonogramCellTableRow [hid=" + hid + ", ID_st=" + ID_st
				+ ", student=" + student + ", ID_tr=" + ID_tr + ", group="
				+ group + ", time=" + time + ", order=" + order
				+ ", bachelorThesis=" + bachelorThesis + "]";
	}
}