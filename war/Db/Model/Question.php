<?php
/**
 * @author Radovan Dvorsky
 * @version 20100220
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_Question extends Db_Model_Abstract {
	
	protected $qid;
	protected $sid;
	protected $qn;
	protected $text;
	
	/**
	 * @return the $qid
	 */
	public function getQid() {
		return $this->qid;
	}

	/**
	 * @return the $sid
	 */
	public function getSid() {
		return $this->sid;
	}

	/**
	 * @return the $qn
	 */
	public function getQn() {
		return $this->qn;
	}

	/**
	 * @return the $text
	 */
	public function getText() {
		return $this->text;
	}

	/**
	 * @param $qid the $qid to set
	 */
	public function setQid($qid) {
		$this->qid = $qid;
	}

	/**
	 * @param $sid the $sid to set
	 */
	public function setSid($sid) {
		$this->sid = $sid;
	}

	/**
	 * @param $qn the $qn to set
	 */
	public function setQn($qn) {
		$this->qn = $qn;
	}

	/**
	 * @param $text the $text to set
	 */
	public function setText($text) {
		$this->text = $text;
	}
}

?>