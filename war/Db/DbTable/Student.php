<?php

/**
 * Student
 *  
 * @author Radovan Dvorsky
 * @version 20100325
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_Student extends Zend_Db_Table_Abstract {

	/**
	 * Database name
	 * @var string
	 */
	protected $_dbName; 
	
	/**
	 * Table name
	 * @var string 
	 */
	protected $_name = 'Student';
	
	/**
	 * Primary key
	 * @var string
	 */	
	protected $_primary = 'ID_st';
	
	/**
	 * Mena zavislich tabuliek
	 * @var array
	 */
	protected $_dependentTables = array('Db_DbTable_Result','Db_DbTable_Harmonogram','Db_DbTable_StutentHasTridy','Db_DbTable_QnForStudent');
	
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