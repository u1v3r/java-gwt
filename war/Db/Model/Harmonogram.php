<?php
/**
 * @author Radovan Dvorsky
 * @version 20110213
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_Harmonogram extends Db_Model_Abstract {

	protected $hid;
	protected $harmonogram_name;
	protected $sfid;
	protected $date;
	protected $exam_place;
	
	/**
	 * @return the $hid
	 */
	public function getHid() {
		return $this->hid;
	}

	/**
	 * @return the $harmonogram_name
	 */
	public function getHarmonogram_name() {
		return $this->harmonogram_name;
	}

	/**
	 * @return the $sfid
	 */
	public function getSfid() {
		return $this->sfid;
	}

	/**
	 * @return the $date
	 */
	public function getDate() {
		return $this->date;
	}

	/**
	 * @param $hid the $hid to set
	 */
	public function setHid($hid) {
		$this->hid = $hid;
	}

	/**
	 * @param $harmonogram_name the $harmonogram_name to set
	 */
	public function setHarmonogram_name($harmonogram_name) {
		$this->harmonogram_name = $harmonogram_name;
	}

	/**
	 * @param $sfid the $sfid to set
	 */
	public function setSfid($sfid) {
		$this->sfid = $sfid;
	}

	/**
	 * @param $date the $date to set
	 */
	public function setDate($date) {
		$this->date = $date;
	}

	/**
	 * @return the $exam_place
	 */
	public function getExam_place() {
		return $this->exam_place;
	}

	/**
	 * @param $exam_place the $exam_place to set
	 */
	public function setExam_place($exam_place) {
		$this->exam_place = $exam_place;
	}

}

?>