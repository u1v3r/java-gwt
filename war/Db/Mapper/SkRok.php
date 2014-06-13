<?php
/**
 * @author Radovan Dvorsky
 * @version 20100513
 */
//require_once ('application/default/models/Db/Mapper/MapperAbstract.php');

class Db_Mapper_SkRok extends Db_Mapper_Abstract {
	
	/**
	 * Vrati ID_Sk z databaze na zaklade aktualneho roka
	 * 
	 * @return Db_Model_SkRok
	 */
	public function getActual(){
		
		$actualYear = (int)date('Y');
		$dbYear = ($actualYear - 1) . '/' . $actualYear;
		$select = $this->getTable()->select();
		$where = $select->where('skolni_rok = ?',$dbYear);
		
		return $this->fetchRow($where);		
	}
}

?>