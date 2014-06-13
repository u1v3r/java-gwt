package cz.edukomplex.kosilka.client.model;

public class CommissionModel {
	
	private String cid;
	private String name;
	
	public CommissionModel(){}
	
	public CommissionModel(String cid, String name) {
		this.cid = cid;
		this.name = name;
	}
	
	public String getCid() {
		return cid;
	}
	public void setCid(String cid) {
		this.cid = cid;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}	
}
