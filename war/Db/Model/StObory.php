<?php
/**
 * @author Radovan Dvorsky
 * @version 20100513
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_StObory extends Db_Model_Abstract {
	
	protected $Id_oboru;
	protected $Obor;
	
	/**
	 * @return the $Id_oboru
	 */
	public function getId_oboru() {
		return $this->Id_oboru;
	}

	/**
	 * @return the $Obor
	 */
	public function getObor() {
		return $this->Obor;
	}

	/**
	 * @param $Id_oboru the $Id_oboru to set
	 */
	public function setId_oboru($Id_oboru) {
		$this->Id_oboru = $Id_oboru;
	}

	/**
	 * @param $Obor the $Obor to set
	 */
	public function setObor($Obor) {
		$this->Obor = $Obor;
	}

	
	

}

?>