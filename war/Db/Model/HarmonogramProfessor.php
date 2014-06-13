<?php

//require_once ('war/Db/Model/Abstract.php');

class Db_Model_HarmonogramProfessor extends Db_Model_Abstract {
	
	protected $hid;
	protected $pid;
	protected $role;
	
	/**
	 * @return the $hid
	 */
	public function getHid() {
		return $this->hid;
	}

	/**
	 * @return the $pid
	 */
	public function getPid() {
		return $this->pid;
	}

	/**
	 * @return the $role
	 */
	public function getRole() {
		return $this->role;
	}

	/**
	 * @param $hid the $hid to set
	 */
	public function setHid($hid) {
		$this->hid = $hid;
	}

	/**
	 * @param $pid the $pid to set
	 */
	public function setPid($pid) {
		$this->pid = $pid;
	}

	/**
	 * @param $role the $role to set
	 */
	public function setRole($role) {
		$this->role = $role;
	}	
}

?>