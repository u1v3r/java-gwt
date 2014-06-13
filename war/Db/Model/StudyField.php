<?php
/**
 * @author Radovan Dvorsky
 * @version 20110213
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_StudyField extends Db_Model_Abstract {
	
	protected $sfid;
	protected $name;
	
	/**
	 * @return the $sfid
	 */
	public function getSfid() {
		return $this->sfid;
	}

	/**
	 * @return the $name
	 */
	public function getName() {
		return $this->name;
	}

	/**
	 * @param $sfid the $sfid to set
	 */
	public function setSfid($sfid) {
		$this->sfid = $sfid;
	}

	/**
	 * @param $name the $name to set
	 */
	public function setName($name) {
		$this->name = $name;
	}

		

}

?>