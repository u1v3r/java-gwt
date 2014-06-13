<?php

/**
 * Result
 *  
 * @author Radovan Dvorsky
 * @version 20100604
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_Result extends Zend_Db_Table_Abstract {
	/**
	 * The default table name 
	 */
	protected $_name = 'result';
	
	/**
	 * Primary key
	 */
	protected $_primary = 'rid';
	
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_ResultSubject');
	
	/**
	 * Parrent tables description 
	 */
	protected $_referenceMap = array(		
		'Student' => array(
			'columns' => array('ID_st'),
			'refTableClass' => 'Db_DbTable_Student',
			'refColumns' => array('ID_st')
		),
		'Harmonogram' => array(
			'columns' => array('hid'),
			'refTableClass' => 'Db_DbTable_Harmonogram',
			'refColumns' => array('hid'),
			'onDelete' => self::CASCADE,
		)
	);

}

