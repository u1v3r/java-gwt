<?php

class TTridyService extends ServiceAbstract{
	
	private $_mapper;
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_TTridy();
	}
	
	public function fetchThirdYear(){
		
		$this->toXml($this->_mapper->fetchGroupsForYear());	
	
	}
	
	public function fetchByIDsk(){
		
		$IDsk = (int)$this->getParam('IDsk');
		
		$select = $this->_mapper->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->where('vznik = ?',$IDsk)
		->from('T_Tridy',array())
		->columns(array('Zkratka','ID_tr','id_oboru','vznik'))
		->where('Zkratka LIKE "?%" OR id_oboru=12',3)//id oboru 12 - DPS, vynimka - lebo sa necisluje na zaciatku
		->order('Zkratka');
		
		echo $this->toXml($this->_mapper->fetchAll($select));
	}
	
	public function fetchByIDskANDIDOboru(){
		$IDsk = (int)$this->getParam('IDsk');
		$idStudyField = (int)$this->getParam('idStudyField');
		$idOboru = array();
		
		$mapper = new Db_Mapper_StudyFieldStObory();
		$obory = $mapper->fetchOboryId($idStudyField);
		
		foreach($obory as $id){
			$idOboru[] = $id['Id_oboru'];
		}
						
		$select = $this->_mapper->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)
		->from('T_Tridy',array())
		->columns(array('Zkratka','ID_tr','id_oboru','vznik'))
		->join('St_obory','St_obory.Id_oboru = T_Tridy.Id_oboru',array())
		->where('T_Tridy.vznik = ?',$IDsk)
		->where('St_obory.Id_oboru IN(?)',$idOboru,Zend_Db::INT_TYPE)		
		->where('T_Tridy.Zkratka LIKE "?%" OR T_Tridy.id_oboru=12',3)//id oboru 12 - DPS, vynimka - lebo sa necisluje na zaciatku
		->order('T_Tridy.Zkratka');
			
				
		echo $this->toXml($this->_mapper->fetchAll($select));
	}

}

?>