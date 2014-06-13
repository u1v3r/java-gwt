<?php
/**
 * @author Radovan Dvorsky
 * @version 20100514
 */
//require_once ('application/default/models/Db/Mapper/MapperAbstract.php');

class Db_Mapper_TTridy extends Db_Mapper_Abstract {
	
	/**
	 * Vrati vsetky triedy z tretieho rocnika, ktore idu k skuskam
	 * 
	 * @param Db_Model_SkRok $year
	 * @return array
	 */
	public function fetchGroupsForYear(Db_Model_SkRok $year = null){
		
		if($year == null){
			$yearMapper = new Db_Mapper_SkRok();
			$year = $yearMapper->getActual(); 
		}
		
		$TTridyMapper = new Db_Mapper_TTridy();
		$select = $TTridyMapper->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->from('T_Tridy',array('ID_tr','Zkratka','id_oboru','vznik'))
		->where('Zkratka LIKE \'3%\'')
		->where('vznik = ?',$year->getID_sk(),Zend_Db::INT_TYPE)
		->order('Zkratka');
			
		return $TTridyMapper->fetchAll($select);
		
	}
	
}

?>