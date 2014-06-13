<?php

/**
 * Harmonogram
 *  
 * @author Radovan Dvorsky
 * @version 20110315
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_Harmonogram extends Zend_Db_Table_Abstract {
	/**
	 * The default table name 
	 */
	protected $_name = 'harmonogram';
	
	/**
	 * Primary key
	 */
	protected $_primary = 'hid';
	
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_HarmonogramStudent','Db_DbTable_HarmonogramProfessor','Db_DbTable_Result');

	
	/**
	 * Parrent tables description 
	 */
	protected $_referenceMap = array(		
		'Year' => array(
			'columns' => array('yid'),
			'refTableClass' => 'Db_DbTable_Year',
			'refColumns' => array('yid'),
			'onDelete' => self::CASCADE,
		)
	);
	
}