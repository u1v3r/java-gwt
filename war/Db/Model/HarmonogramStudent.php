<?php

//require_once ('war/Db/Model/Abstract.php');

class Db_Model_HarmonogramStudent extends Db_Model_Abstract {
	
	protected $order;
	protected $hid;
	protected $ID_st;
	protected $time;
		

	/**
	 * @return the $order
	 */
	public function getOrder() {
		return $this->order;
	}

	/**
	 * @param $order the $order to set
	 */
	public function setOrder($order) {
		$this->order = $order;
	}
	
	/**
	 * @return the $hid
	 */
	public function getHid() {
		return $this->hid;
	}

	/**
	 * @return the $ID_st
	 */
	public function getID_st() {
		return $this->ID_st;
	}

	/**
	 * @return the $time
	 */
	public function getTime() {
		return $this->time;
	}

	/**
	 * @param $hid the $hid to set
	 */
	public function setHid($hid) {
		$this->hid = $hid;
	}

	/**
	 * @param $ID_st the $ID_st to set
	 */
	public function setID_st($ID_st) {
		$this->ID_st = $ID_st;
	}

	/**
	 * @param $time the $time to set
	 */
	public function setTime($time) {
		$this->time = $time;
	}

}

?>