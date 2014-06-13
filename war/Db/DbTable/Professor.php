<?php

/**
 * Professor
 *  
 * @author Radovan Dvorsky
 * @version 20110213
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_Professor extends Zend_Db_Table_Abstract {
	
	/**
	 * The default table name 
	 */
	protected $_name = 'professor';
	
	/**
	 * Primary key
	 */
	protected $_primary = 'pid';
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_HarmonogramProfessor');	
}