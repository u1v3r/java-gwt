<?php

/**
 * StudentHasTridy
 *  
 * @author Radovan Dvorsky
 * @version 20100513
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_StudentHasTridy extends Zend_Db_Table_Abstract {
	
	/**
	 * Database name
	 * @var string
	 */
	protected $_dbName; 
	
	/**
	 * Primary keys	 
	 */
	protected $_primary = array('Student_ID_st','Tridy_ID_tr');
	
	/**
	 * The default table name 
	 */
	protected $_name = 'Student_has_Tridy';	
		
	/**
	 * Parrent tables description 
	 */	
	protected $_referenceMap = array(
		'Group' => array(
			'columns' => array('Tridy_ID_tr'),
			'refTableClass' => 'Db_DbTable_TTridy',
			'refColumns' => array('ID_tr')				
		),
		'Student' => array(
			'columns' => array('Student_ID_st'),
			'refTableClass' => 'Db_DbTable_Student',
			'refColumns' => array('ID_st')				
		)
	);
	
	public function init(){
		
		/*
		 * Nastav adapter pre druhu databazu
		 */
		$this->_setAdapter(Zend_Registry::get('db2'));
		$config = Zend_Registry::get('config');
		
		/*
		 * Inicializuj premenu s menom druhej databaze
		 */
		$this->_dbName = $config->database2->params->dbname;
	}
}