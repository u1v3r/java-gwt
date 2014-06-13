<?php
/**
 * @author Radovan Dvorsky
 * @version 20100513
 */
//require_once ('application/default/models/Db/Mapper/MapperAbstract.php');

class Db_Mapper_Student extends Db_Mapper_Abstract {
	
	/**
	 * Vrati z databaze vsetkych studentov 3. rocnika, ktorí idú robit tento rok skusky
	 * 
	 * @return Zend_Db_Table_Rowset
	 */
	public function fetchStudentsWithGroups(){
		$skRokMapper = new Db_Mapper_SkRok();
		$skRokModel = $skRokMapper->getActual();
		
		$select = $this->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)
		->from('Student' ,array('ID_st','Jmeno','Prijmeni'))		
		->join('Student_has_Tridy','Student.ID_st = Student_has_Tridy.Student_ID_st',array())
		->join('T_Tridy','Student_has_Tridy.Tridy_ID_tr = T_Tridy.ID_tr',array('ID_tr','Zkratka'))
		->where('T_Tridy.Zkratka LIKE \'3%\'')	
		->where('T_Tridy.vznik = ?',$skRokModel->getID_sk(),Zend_Db::INT_TYPE)	
		->order('Student.prijmeni');	
		
		return $this->getTable()->fetchAll($select);	
	}
	
	/**
	 * Vrati z databaze vsetkych studentov 3. rocnika, ktorí idú robit tento rok skusky
	 * 
	 * @return array|Db_Model_Student
	 */
	public function fetchActualStudents(Db_Model_SkRok $skRokModel = null, Db_Model_TTridy $group = null){
		
		if($skRokModel == null){
			$skRokMapper = new Db_Mapper_SkRok();
			$skRokModel = $skRokMapper->getActual();
		}
		
		$select = $this->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)
		->from('Student' ,array('ID_st','Jmeno','Prijmeni','Login','kontext'))		
		->join('Student_has_Tridy','Student.ID_st = Student_has_Tridy.Student_ID_st',array())
		->join('T_Tridy','Student_has_Tridy.Tridy_ID_tr = T_Tridy.ID_tr',array('ID_tr'))
		->where('T_Tridy.Zkratka LIKE \'3%\'')	
		->where('T_Tridy.vznik = ?',$skRokModel->getID_sk(),Zend_Db::INT_TYPE)	
		->order('Student.prijmeni');
		
		if($group != null){
			$select->where('T_Tridy.ID_tr = ?',$group->getID_tr(),Zend_Db::INT_TYPE);
		}
		
		return $this->getTable()->fetchAll($select)->toArray();
	}
	
	/**
	 * Vrati z databaze vsetky studentov na zaklade zadanych ID_st
	 * @param array $studentIDs ID studentov
	 * @return array
	 */
	public function fetchStudentsByIDs(array $studentIDs){
		
		if(count($studentIDs) < 1){
			return array();
		}
		
		$ids = rtrim(implode(",",$studentIDs),',');
		
		$select = $this->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)
		->from('Student',array('ID_st','Jmeno','Prijmeni','Login','kontext'))
		->where('Student.ID_st IN (?)',$studentIDs)
		->order('Student.Prijmeni');
		
		return $this->getTable()->fetchAll($select)->toArray();
	}
}

?>