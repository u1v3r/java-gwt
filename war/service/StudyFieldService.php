<?php

class StudyFieldService extends ServiceAbstract {
	
	private $_mapper;
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_StudyField();
	}
	
	/**
	 * Vrati vsetky obory
	 * 
	 * @return array
	 */
	public function fetchAll(){			
		$order = $this->_mapper->getTable()->select()->order('name');
		return $this->_mapper->getTable()->fetchAll($order)->toArray();			
	}
}

?>