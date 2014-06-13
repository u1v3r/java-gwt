<?php

/**
 * StObory
 * 
 * @author Radovan Dvorsky
 * @version 20110213
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_StObory extends Zend_Db_Table_Abstract {
	/**
	 * The default table name 
	 */
	protected $_name = 'St_obory';
	
	/**
	 * Primary key
	 */	
	protected $_primary = 'Id_oboru';
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_TTridy','Db_DbTable_StudyFieldStObory');
	
	public function init(){
		
		/*
		 * Nastav adapter pre druhu databaze
		 */
		$dbAdapter = Zend_Registry::get('db2');
		$this->_setAdapter($dbAdapter);
		$config = $dbAdapter->getConfig();
		
		/*
		 * Inicializuj premenu s menom druhej databaze
		 */
		$this->_dbName = $config['dbname'];
	}
}

