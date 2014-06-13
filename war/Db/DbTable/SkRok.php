<?php

/**
 * SkRok
 * 
 * @author Radovan Dvorsky
 * @version 20100513
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_SkRok extends Zend_Db_Table_Abstract {
	/**
	 * The default table name 
	 */
	protected $_name = 'Sk_rok';
	
	/**
	 * Primary key
	 */	
	protected $_primary = 'ID_sk';
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_TTridy');
	
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

