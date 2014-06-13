<?php

/**
 * StudyField
 * 
 * @author Radovan Dvorsky
 * @version 20110213
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_StudyField extends Zend_Db_Table_Abstract {
	/**
	 * The default table name 
	 */
	protected $_name = 'studyfield';
	
	/**
	 * Primary key
	 */	
	protected $_primary = 'sfid';
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_SubjectStudyField','Db_DbTable_StudyFieldStObory');
}

