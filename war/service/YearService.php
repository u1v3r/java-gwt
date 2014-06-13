<?php

class YearService extends ServiceAbstract {

	private $_dbTable;
	
	public function __construct(){
		$this->_dbTable = new Db_DbTable_Year();
	}
	
	/**
	 * Vrati vsetky roky
	 * 
	 * @return array
	 */
	public function fetchAll(){
		
		$order = $this->_dbTable->select()->order("year_name DESC");
		
		return $this->_dbTable->fetchAll($order)->toArray();		
	}
}

?>