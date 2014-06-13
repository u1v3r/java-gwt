<?php
/**
 * @author Radovan Dvorsky
 * @version 20110213
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_StudyFieldStObory extends Db_Model_Abstract {
	
	protected $sfid;
	protected $Id_oboru;
	
	/**
	 * @return the $sfid
	 */
	public function getSfid() {
		return $this->sfid;
	}

	/**
	 * @return the $Id_oboru
	 */
	public function getId_oboru() {
		return $this->Id_oboru;
	}

	/**
	 * @param $sfid the $sfid to set
	 */
	public function setSfid($sfid) {
		$this->sfid = $sfid;
	}

	/**
	 * @param $Id_oboru the $Id_oboru to set
	 */
	public function setId_oboru($Id_oboru) {
		$this->Id_oboru = $Id_oboru;
	}

}

?>