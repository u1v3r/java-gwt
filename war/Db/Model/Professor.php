<?php
/**
 * @author Radovan Dvorsky
 * @version 20100512
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_Professor extends Db_Model_Abstract {
	
	protected $pid;
	protected $short;
	protected $firstname;
	protected $lastname;
	protected $title_before;
	protected $title_behind;
		
	/**
	 * @return the $pid
	 */
	public function getPid() {
		return $this->pid;
	}
	
	/**
	 * @return the $short
	 */
	public function getShort() {
		return $this->short;
	}

	/**
	 * @return the $firstname
	 */
	public function getFirstname() {
		return $this->firstname;
	}

	/**
	 * @return the $lastname
	 */
	public function getLastname() {
		return $this->lastname;
	}

	/**
	 * @return the $title_before
	 */
	public function getTitle_before() {
		return $this->title_before;
	}

	/**
	 * @return the $title_behind
	 */
	public function getTitle_behind() {
		return $this->title_behind;
	}

	/**
	 * @param $pid the $pid to set
	 */
	public function setPid($pid) {
		$this->pid = $pid;
	}

	/**
	 * @param $short the $short to set
	 */
	public function setShort($short) {
		$this->short = $short;
	}
	
	/**
	 * @param $firstname the $firstname to set
	 */
	public function setFirstname($firstname) {
		$this->firstname = $firstname;
	}

	/**
	 * @param $lastname the $lastname to set
	 */
	public function setLastname($lastname) {
		$this->lastname = $lastname;
	}

	/**
	 * @param $title_before the $title_before to set
	 */
	public function setTitle_before($title_before) {
		$this->title_before = $title_before;
	}

	/**
	 * @param $title_behind the $title_behind to set
	 */
	public function setTitle_behind($title_behind) {
		$this->title_behind = $title_behind;
	}
}

?>