<?php
/**
 * @author Radovan Dvorsky
 * @version 20110213
 */
//require_once ('application/default/models/Db/Mapper/MapperAbstract.php');

class Db_Mapper_StudyFieldStObory extends Db_Mapper_Abstract {
	
	/**
	 * Vrati vsetky Id_Oboru
	 * 
	 * @param unknown_type $idStudyField
	 * 
	 * @return array
	 */
	public function fetchOboryId($idStudyField){
		
		$select = $this->getTable()->select(Zend_Db_Table::SELECT_WITH_FROM_PART);
		$select->setIntegrityCheck(false)
		->where('sfid = ?',$idStudyField);
		
		return $this->getTable()->fetchAll($select)->toArray();		
	}
}

?>