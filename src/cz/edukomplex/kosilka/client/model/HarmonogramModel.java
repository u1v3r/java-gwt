package cz.edukomplex.kosilka.client.model;

public class HarmonogramModel {
	
	public static final String DATABASE_DATE_FORMAT = "yyyy-MM-dd";
	public static final String OUTPUT_DATE_FORMAT = "dd.MM.yyyy";
	
	private String hid;
	private String ID_st;	
	private String time;
	private String order;
		
	public HarmonogramModel(){}
	
	public HarmonogramModel(String hid, String iD_st, String time, String order) {		
		this.hid = hid;
		this.ID_st = iD_st;
		this.time = time;
		this.order = order;
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
}
