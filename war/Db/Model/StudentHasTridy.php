<?php
/**
 * @author Radovan Dvorsky
 * @version 20100513
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_StudentHasTridy extends Db_Model_Abstract {
	
	protected $Student_ID_st;
	protected $Tridy_ID_tr;
	
	
	/**
	 * @return the $Student_ID_st
	 */
	public function getStudent_ID_st() {
		return $this->Student_ID_st;
	}

	/**
	 * @return the $Tridy_ID_tr
	 */
	public function getTridy_ID_tr() {
		return $this->Tridy_ID_tr;
	}

	/**
	 * @param $Student_ID_st the $Student_ID_st to set
	 */
	public function setStudent_ID_st($Student_ID_st) {
		$this->Student_ID_st = $Student_ID_st;
	}

	/**
	 * @param $Tridy_ID_tr the $Tridy_ID_tr to set
	 */
	public function setTridy_ID_tr($Tridy_ID_tr) {
		$this->Tridy_ID_tr = $Tridy_ID_tr;
	}	
}

?>