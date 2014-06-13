package cz.edukomplex.kosilka.client.model;

import com.google.gwt.view.client.ProvidesKey;

public class QuestionModel implements Comparable<QuestionModel>{
	
	private String qid;
	private String sid;
	private String qn;
	private String text;	
	
	public static final ProvidesKey<QuestionModel> KEY_PROVIDER = new ProvidesKey<QuestionModel>() {

		@Override
		public Object getKey(QuestionModel item) {
			return item.getQid();
		}
	};	
	
	public QuestionModel(){}

	public QuestionModel(String qid, String sid, String qn, String text){		
		this.qid = qid;
		this.sid = sid;
		this.qn = qn;
		this.text = text;
	}
	
	public QuestionModel(String qn,String text){
		this.qn = qn;
		this.text = text;
	}
	
	public String getQid() {
		return qid;
	}

	public void setQid(String qid) {
		this.qid = qid;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public String getQn() {
		return qn;
	}

	public void setQn(String qn) {
		this.qn = qn;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public int compareTo(QuestionModel o) {
		return Integer.valueOf(this.qn) - Integer.valueOf(o.qn);
	}
	
}
