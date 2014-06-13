<?php
/**
 * @author Radovan Dvorsky
 * @version 20101217
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_Result extends Db_Model_Abstract {
	
	protected $rid;
	protected $ID_st;
	protected $oponent_grade;
	protected $veduci_grade;
	protected $obhajoba_grade;
	protected $overall_subjects_grade;
	protected $overall_grade;
	protected $note;
	
	/**
	 * @return the $rid
	 */
	public function getRid() {
		return $this->rid;
	}

	/**
	 * @return the $ID_st
	 */
	public function getID_st() {
		return $this->ID_st;
	}

	/**
	 * @return the $oponent_grade
	 */
	public function getOponent_grade() {
		return $this->oponent_grade;
	}

	/**
	 * @return the $veduci_grade
	 */
	public function getVeduci_grade() {
		return $this->veduci_grade;
	}

	/**
	 * @return the $obhajoba_grade
	 */
	public function getObhajoba_grade() {
		return $this->obhajoba_grade;
	}

	/**
	 * @return the $overall_subjects_grade
	 */
	public function getOverall_subjects_grade() {
		return $this->overall_subjects_grade;
	}

	/**
	 * @return the $overall_grade
	 */
	public function getOverall_grade() {
		return $this->overall_grade;
	}

	/**
	 * @return the $note
	 */
	public function getNote() {
		return $this->note;
	}

	/**
	 * @param $rid the $rid to set
	 */
	public function setRid($rid) {
		$this->rid = $rid;
	}

	/**
	 * @param $ID_st the $ID_st to set
	 */
	public function setID_st($ID_st) {
		$this->ID_st = $ID_st;
	}

	/**
	 * @param $oponent_grade the $oponent_grade to set
	 */
	public function setOponent_grade($oponent_grade) {
		$this->oponent_grade = $oponent_grade;
	}

	/**
	 * @param $veduci_grade the $veduci_grade to set
	 */
	public function setVeduci_grade($veduci_grade) {
		$this->veduci_grade = $veduci_grade;
	}

	/**
	 * @param $obhajoba_grade the $obhajoba_grade to set
	 */
	public function setObhajoba_grade($obhajoba_grade) {
		$this->obhajoba_grade = $obhajoba_grade;
	}

	/**
	 * @param $overall_subjects_grade the $overall_subjects_grade to set
	 */
	public function setOverall_subjects_grade($overall_subjects_grade) {
		$this->overall_subjects_grade = $overall_subjects_grade;
	}

	/**
	 * @param $overall_grade the $overall_grade to set
	 */
	public function setOverall_grade($overall_grade) {
		$this->overall_grade = $overall_grade;
	}

	/**
	 * @param $note the $note to set
	 */
	public function setNote($note) {
		$this->note = $note;
	}

	
	
	
}

?>