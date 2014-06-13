<?php

//require_once ('war/Db/Model/Abstract.php');

class Db_Model_ResultSubject extends Db_Model_Abstract {
	
	protected $rid;
	protected $sid;
	protected $grade;
	
	
	/**
	 * @return the $rid
	 */
	public function getRid() {
		return $this->rid;
	}

	/**
	 * @return the $sid
	 */
	public function getSid() {
		return $this->sid;
	}

	/**
	 * @return the $grade
	 */
	public function getGrade() {
		return $this->grade;
	}

	/**
	 * @param $rid the $rid to set
	 */
	public function setRid($rid) {
		$this->rid = $rid;
	}

	/**
	 * @param $sid the $sid to set
	 */
	public function setSid($sid) {
		$this->sid = $sid;
	}

	/**
	 * @param $grade the $grade to set
	 */
	public function setGrade($grade) {
		$this->grade = $grade;
	}

	
}

?>