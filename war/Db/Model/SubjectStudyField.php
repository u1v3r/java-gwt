<?php
/**
 * @author Radovan Dvorsky
 * @version 20100513
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_SubjectStudyField extends Db_Model_Abstract {
	
	protected $sfid;
	protected $sid;
	
	/**
	 * @return the $sfid
	 */
	public function getSfid() {
		return $this->sfid;
	}

	/**
	 * @return the $sid
	 */
	public function getSid() {
		return $this->sid;
	}

	/**
	 * @param $sfid the $sfid to set
	 */
	public function setSfid($sfid) {
		$this->sfid = $sfid;
	}

	/**
	 * @param $sid the $sid to set
	 */
	public function setSid($sid) {
		$this->sid = $sid;
	}


}

?>