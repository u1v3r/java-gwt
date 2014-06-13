<?php
/**
 * @author Radovan Dvorsky
 * @version 20100513
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_SkRok extends Db_Model_Abstract {
	
	protected $ID_sk;
	protected $skolni_rok;
	
	
	/**
	 * @return the $ID_sk
	 */
	public function getID_sk() {
		return $this->ID_sk;
	}

	/**
	 * @return the $skolni_rok
	 */
	public function getSkolni_rok() {
		return $this->skolni_rok;
	}

	/**
	 * @param $ID_sk the $ID_sk to set
	 */
	public function setID_sk($ID_sk) {
		$this->ID_sk = $ID_sk;
	}

	/**
	 * @param $skolni_rok the $skolni_rok to set
	 */
	public function setSkolni_rok($skolni_rok) {
		$this->skolni_rok = $skolni_rok;
	}

	
}

?>