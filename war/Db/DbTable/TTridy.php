<?php

/**
 * TTridy
 *  
 * @author Radovan Dvorsky
 * @version 20100513
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_TTridy extends Zend_Db_Table_Abstract {

	/**
	 * Database name
	 * @var string
	 */
	protected $_dbName; 
	
	/**
	 * Table name
	 * @var string 
	 */
	protected $_name = 'T_Tridy';
	
	/**
	 * Primary key
	 * @var string
	 */	
	protected $_primary = 'ID_tr';
	
	/**
	 * Mena zavislich tabuliek
	 * @var array
	 */
	protected $_dependentTables = array('Db_DbTable_StutentHasTridy');
	
	/**
	 * Parrent tables description 
	 */
	protected $_referenceMap = array(
		'Rok' => array(
			'columns' => 'vznik',
			'refTableClass' => 'Db_DbTable_SkRok',
			'refColumns' => 'skolni_rok'
		),
		'Odbor' => array(
			'columns' => 'Id_oboru',
			'refTableClass' => 'Db_DbTable_StObory',
			'refColumns' => 'Id_oboru'
		),
	);
	
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