<?php

class Db_DbTable_Year extends Zend_Db_Table_Abstract {
	
	/**
	 * The default table name 
	 */
	protected $_name = 'year';
	
	/**
	 * Primary key
	 */
	protected $_primary = 'yid';
	
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_Harmonogram');
	
}

?>