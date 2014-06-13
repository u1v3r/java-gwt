<?php

class SubjectService extends ServiceAbstract {
	
	private $_mapper;

	public function __construct(){
		$this->_mapper = new Db_Mapper_Subject();
	}
	
	/**
	 * Vrtáti vsetky predmety
	 * 
	 * @return array
	 */
	public function fetchAll(){
		$order = $this->_mapper->getTable()->select()->order("name");
		return $this->_mapper->getTable()->fetchAll($order)->toArray();
	}
	
	/**
	 * Vrati vsetky predmety patriace danemu oboru
	 * 
	 * @param integer $sfid
	 * 
	 * @return array|boolean
	 */
	public function fetchSubjectsByStudyFieldId($sfid){
		$id = (int)$sfid;
		
		if($id == 0){
			return false;
		}
		
		$select = $this->_mapper->getTable()->select(Zend_Db_Table::SELECT_WITH_FROM_PART);
		$select->setIntegrityCheck(false)
		->where('subject_studyfield.sfid = ?',$id)		
		->join('subject_studyfield','subject_studyfield.sid = subject.sid',array())
		->order('subject.name');
			
		return $this->_mapper->getTable()->fetchAll($select)->toArray();
	}
	
	
	/**
	 * @deprecated Namiesto nej pouzi <code>fetchSubjectsByStudyFieldId()</code>
	 * 
	 * Vrati vsetky predmety, ktore ma student priradene v ramci svojho oboru resp. triedy
	 
	 * @param integer $IDst
	 * 
	 * @return array|boolean
	 */
	public function fetchSubjectsByIDStudenta($IDst){
		$idStudenta = (int)$IDst;
		
		if($IDst == 0){
			return false;
		}
		
		/*
		 * Zisti id oboru
		 */
		$studentMapper = new Db_Mapper_Student();
		$select = $studentMapper->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)
		->from('Student',array())
		->join('Student_has_Tridy','Student_has_Tridy.Student_ID_st=Student.ID_st',array())
		->join('T_Tridy','T_Tridy.ID_tr=Student_has_Tridy.Tridy_ID_tr',array())
		->join('St_obory','St_obory.Id_oboru=T_Tridy.Id_oboru',array('Id_oboru'))
		->where('Student.ID_st = ?',$idStudenta);
				
		$IDOboru = $studentMapper->getTable()->fetchAll($select)->current()->Id_oboru;		
		
		$select = $this->_mapper->getTable()->select(Zend_Db_Table::SELECT_WITH_FROM_PART);
		$select->setIntegrityCheck(false)
		->join('subject_st_obory','subject_st_obory.sid=subject.sid',array())
		->where('subject_st_obory.Id_oboru = ?',$IDOboru)
		->order('subject.name');
		
		return $this->_mapper->getTable()->fetchAll($select)->toArray();
	}
	
	
	/**
	 * Odstrani pridelenie predmetu pre dany obor
	 * 
	 * @param integer $sid
	 * @param integer $sfid
	 * @param boolean $withGrades Urcuje ci sa maju odstranit aj pridelene hodnotenia v tabulke result_subject
	 * 
	 * @return boolean
	 */
	public function deleteSubjectFromObor($sid,$sfid,$withGrades = false){
		
		$sfid = (int)$sfid;
		$sid = (int)$sid;
		
		if($sid == 0 || $sfid == 0){
			return false;
		}
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		try{
			$adapter->beginTransaction();

			if($withGrades){
				/*
				 * Odstrani aj pridelene znamky z premdetu z tabulky
				 * result_subject
				 * 
				 * Ak vrati hodnotu 0, znamena to ze este nebolo pridelene ziadne hodnotenie
				 * a preto je nutne nasledne odstranit data samostatnym prikazom z tabulky
				 * subject_studyfield
				 */
				$removedRows = $this->_mapper->getTable()->getAdapter()->query(
				   'DELETE result_subject,subject_studyfield FROM subject,result_subject,result,subject_studyfield
					WHERE subject_studyfield.sid=subject.sid
					AND subject.sid=result_subject.sid
					AND result.rid=result_subject.rid
					AND subject_studyfield.sfid = ?
					AND subject_studyfield.sid = ?',
					array($sfid,$sid)
				); 			
			}
			
			/*
			 * odstrani iba ked predchadzajuci prikaz neodstrani nic - cize zaznamy neexistuju
			 * alebo ked je explicitne nastavene aby sa odstranilo len priradenie
			 */
			if($removedRows->rowCount() == 0 || $withGrades == false){
				$this->_mapper->getTable()->getAdapter()->query(
								'DELETE FROM subject_studyfield WHERE sid = ? AND sfid = ?',
								array($sid,$sfid)							
				);	
			}
			
			$adapter->commit();
			return true;
				
		} catch(Db_Exception $e){
			$adapter->rollBack();
			return false;
		}
		
		
		
	}
	
	/**
	 * Ulozi predmet do db
	 * 
	 * @param string $subjectNamed
	 * @param string $subjectShort	 
	 * 
	 * @return boolean
	 */
	public function add($subjectName,$subjectShort){
		
		if(empty($subjectName) || empty($subjectShort)){
			return false;
		}
						
		$sid = $this->_mapper->getTable()->insert(array(
				'short' => $subjectShort,
				'name' => $subjectName,			
			)
		);			
			
		return ($sid > 0) ? true : false;			
	}
	
	/**
	 * Upravi ulozeny predmet
	 * 
	 * @param integer $sid
	 * @param string $subjectName
	 * @param string $shortName
	 * 
	 * @return boolean
	 */
	public function update($sid,$subjectName,$shortName){
		
		if((int)$sid == 0 || empty($subjectName) || empty($shortName) ){
			return false;
		}
		
		$data = array(
			'name' => $subjectName,
			'short' => $shortName,		
		); 
		
		$where = $this->_mapper->getTable()->getAdapter()->quoteInto('sid = ?',$sid,Zend_Db::INT_TYPE);
			
		$this->_mapper->getTable()->update($data,$where);
		
		return true;		
	}
}

?>