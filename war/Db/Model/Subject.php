<?php
/**
 * @author Radovan Dvorsky
 * @version 20100512
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_Subject extends Db_Model_Abstract {
	
	protected $sid;
	protected $short;
	protected $name;
	protected $name_eng;
	
	
	/**
	 * @return the $sid
	 */
	public function getSid() {
		return $this->sid;
	}

	/**
	 * @return the $name
	 */
	public function getName() {
		return $this->name;
	}

	/**
	 * @return the $short
	 */
	public function getShort() {
		return $this->short;
	}

	/**
	 * @return the $name_eng
	 */
	public function getName_eng() {
		return $this->name_eng;
	}
		
	/**
	 * @param $sid the $sid to set
	 */
	public function setSid($sid) {
		$this->sid = $sid;
	}
	
	/**
	 * @param $short the $short to set
	 */
	public function setShort($short) {
		$this->short = $short;
	}

	/**
	 * @param $name the $name to set
	 */
	public function setName($name) {
		$this->name = $name;
	}
	
	/**
	 * @param $name_eng the $name_eng to set
	 */
	public function setName_eng($name_eng) {
		$this->name_eng = $name_eng;
	}
}

?>