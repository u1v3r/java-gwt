package cz.edukomplex.kosilka.client.content.result;

import java.util.Comparator;
import java.util.HashMap;

import cz.edukomplex.kosilka.client.model.ResultSubjectModel;

public class ResultCellTableRow implements Comparable<ResultCellTableRow>{
	
	
	public static final Comparator<ResultCellTableRow> NAME_COMPARATOR = new Comparator<ResultCellTableRow>() {
				
		@Override
		public int compare(ResultCellTableRow o1, ResultCellTableRow o2) {
			
			if (o1 == o2) {
				return 0;
			}

			// Compare the name columns.
			if (o1 != null) {
				return (o2 != null) ? o1.lastName.compareTo(o2.lastName) : 1;
			}
			return -1;
		}

	};
 
	
	public String rid;
	public String ID_st;
	public String firstname;
	public String lastName;
	public String questionNumber = "";
	public String oponentGrade = "";
	public String veduciGrade = "";
	public String obhajobaGrade = "";
	public String overallSubjectsGrade = "";
	public String overallGrade = "";
	public String note = "";
	public String order;	

	public HashMap<String,ResultSubjectModel> subjectsHashMap = new HashMap<String, ResultSubjectModel>();
	
	
	public ResultCellTableRow(){}	
	
	public ResultCellTableRow(String rid, String iD_st, String firstname,
			String lastName, String questionNumber, String oponentGrade,
			String veduciGrade, String obhajobaGrade,
			String overallSubjectsGrade, String overallGrade, String note, String order) {		
		
		/*
		 * Po kompilacii ma javascript problem s NULL hodnotami a vyhadzuje error,
		 * preto nesmie byt hodnota nikdy NULL 		
		 */
		this.rid = (rid == null) ? "": rid;
		this.ID_st = iD_st;
		this.firstname = firstname;
		this.lastName = lastName;
		this.questionNumber = (questionNumber == null) ? "": questionNumber;
		this.oponentGrade = (oponentGrade == null) ? "": oponentGrade;
		this.veduciGrade = (veduciGrade == null) ? "": veduciGrade;
		this.obhajobaGrade = (obhajobaGrade == null) ? "": obhajobaGrade;
		this.overallSubjectsGrade = (overallSubjectsGrade == null) ? "": overallSubjectsGrade;
		this.overallGrade = (overallGrade == null) ? "": overallGrade;
		this.note = (note == null) ? "": note;
		this.order = order;
	}

	public String getRid() {
		return rid;
	}

	public void setRid(String rid) {
		this.rid = (rid == null) ? "": rid;
	}

	public String getID_st() {
		return ID_st;
	}

	public void setID_st(String iD_st) {
		ID_st = iD_st;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getQuestionNumber() {
		return questionNumber;
	}

	public void setQuestionNumber(String questionNumber) {
		this.questionNumber = (questionNumber == null) ? "": questionNumber;
	}

	public String getOponentGrade() {
		return oponentGrade;
	}

	public void setOponentGrade(String oponentGrade) {
		this.oponentGrade = (oponentGrade == null) ? "": oponentGrade;
	}

	public String getVeduciGrade() {
		return veduciGrade;
	}

	public void setVeduciGrade(String veduciGrade) {
		this.veduciGrade = (veduciGrade == null) ? "": veduciGrade;
	}

	public String getObhajobaGrade() {
		return obhajobaGrade;
	}

	public void setObhajobaGrade(String obhajobaGrade) {
		this.obhajobaGrade = (obhajobaGrade == null) ? "": obhajobaGrade;
	}

	public String getOverallSubjectsGrade() {
		return overallSubjectsGrade;
	}

	public void setOverallSubjectsGrade(String overallSubjectsGrade) {
		this.overallSubjectsGrade = (overallSubjectsGrade == null) ? "": overallSubjectsGrade;
	}

	public String getOverallGrade() {
		return overallGrade;
	}

	public void setOverallGrade(String overallGrade) {
		this.overallGrade = (overallGrade == null) ? "": overallGrade;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = (note == null) ? "": note;
	}
	
	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	@Override
	public String toString() {
		return "ResultCellTableRow [rid=" + rid + ", ID_st=" + ID_st
				+ ", firstname=" + firstname + ", lastName=" + lastName
				+ ", questionNumber=" + questionNumber + ", oponentGrade="
				+ oponentGrade + ", veduciGrade=" + veduciGrade
				+ ", obhajobaGrade=" + obhajobaGrade
				+ ", overallSubjectsGrade=" + overallSubjectsGrade
				+ ", overallGrade=" + overallGrade + ", note=" + note
				+ ", order=" + order + ", subjectsHashMap=" + subjectsHashMap
				+ "]";
	}

	@Override
	public int compareTo(ResultCellTableRow o) {
		return Integer.valueOf(this.order) - Integer.valueOf(o.order);
	}
}