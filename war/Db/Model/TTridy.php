<?php
/**
 * @author Radovan Dvorsky
 * @version 20100312  
 */
//require_once ('application/default/models/Db/Model/ModelAbstract.php');

class Db_Model_TTridy extends Db_Model_Abstract {
	
	protected $ID_tr;
	protected $Zkratka;
	protected $id_oboru;
	protected $vznik;
	protected $Students;
	
	/**
	 * @return the $ID_tr
	 */
	public function getID_tr() {
		return $this->ID_tr;
	}

	/**
	 * @return the $Zkratka
	 */
	public function getZkratka() {
		return $this->Zkratka;
	}

	/**
	 * @return the $id_oboru
	 */
	public function getId_oboru() {
		return $this->id_oboru;
	}

	/**
	 * @return the $vznik
	 */
	public function getVznik() {
		return $this->vznik;
	}

	/**
	 * @param $ID_tr the $ID_tr to set
	 */
	public function setID_tr($ID_tr) {
		$this->ID_tr = $ID_tr;
	}

	/**
	 * @param $Zkratka the $Zkratka to set
	 */
	public function setZkratka($Zkratka) {
		$this->Zkratka = $Zkratka;
	}

	/**
	 * @param $id_oboru the $id_oboru to set
	 */
	public function setId_oboru($id_oboru) {
		$this->id_oboru = $id_oboru;
	}

	/**
	 * @param $vznik the $vznik to set
	 */
	public function setVznik($vznik) {
		$this->vznik = $vznik;
	}
	
	/**
	 * @return the $Students
	 */
	public function getStudents() {
		return $this->Students;
	}

	/**
	 * @param $Students the $Students to set
	 */
	public function setStudents($Students) {
		$this->Students = $Students;
	}
	
	public function addStudent(Db_Model_Student $student){
		
		if(!$student instanceof Db_Model_Student){
			throw new Exception('$student is not instance of Db_Model_Student');
		}
		
		$this->Students[] = $student;
	}
	
}

?>