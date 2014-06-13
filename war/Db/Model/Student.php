<?php
/**
 * @author Radovan Dvorsky
 * @version 20100513
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_Student extends Db_Model_Abstract {
	
	protected $ID_st;
	protected $Jmeno;
	protected $Prijmeni;
	protected $Login;
	
	/**
	 * @return the $ID_st
	 */
	public function getID_st() {
		return $this->ID_st;
	}

	/**
	 * @return the $Jmeno
	 */
	public function getJmeno() {
		return $this->Jmeno;
	}

	/**
	 * @return the $Prijmeni
	 */
	public function getPrijmeni() {
		return $this->Prijmeni;
	}

	/**
	 * @return the $Login
	 */
	public function getLogin() {
		return $this->Login;
	}

	/**
	 * @param $ID_st the $ID_st to set
	 */
	public function setID_st($ID_st) {
		$this->ID_st = $ID_st;
	}

	/**
	 * @param $Jmeno the $Jmeno to set
	 */
	public function setJmeno($Jmeno) {
		$this->Jmeno = $Jmeno;
	}

	/**
	 * @param $Prijmeni the $Prijmeni to set
	 */
	public function setPrijmeni($Prijmeni) {
		$this->Prijmeni = $Prijmeni;
	}

	/**
	 * @param $Login the $Login to set
	 */
	public function setLogin($Login) {
		$this->Login = $Login;
	}
	
}

?>