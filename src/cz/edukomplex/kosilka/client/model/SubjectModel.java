package cz.edukomplex.kosilka.client.model;

import com.google.gwt.view.client.ProvidesKey;

public class SubjectModel implements Comparable<SubjectModel>{
	
	private String sid;
	private String name;
	private String shortName;
	
	public static final ProvidesKey<SubjectModel> KEY_PROVIDER = new ProvidesKey<SubjectModel>() {
		
		@Override
		public Object getKey(SubjectModel item) {
			return item.sid;
		}
	};
	
	public SubjectModel(){}
	
	public SubjectModel(String sid, String name,String shortName) {
		super();
		this.sid = sid;
		this.name = name;
		this.shortName = shortName;
	}
	
	public String getSid(){
		return this.sid;
	}	
	
	public void setSid(String sid){
		this.sid = sid;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	@Override
	public int compareTo(SubjectModel o) {
		return this.name.compareToIgnoreCase(o.name);
	}

	@Override
	public String toString() {
		return "SubjectModel [sid=" + sid + ", name=" + name + ", shortName="
				+ shortName + "]";
	}	
}