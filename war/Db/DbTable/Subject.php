<?php
/**
 * Subject
 *  
 * @author Radovan Dvorsky
 * @version 20110213
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_Subject extends Zend_Db_Table_Abstract {
	/**
	 * The default table name 
	 */
	protected $_name = 'subject';
	
	/**
	 * Primary key
	 */	
	protected $_primary = array('sid');
	
	/**
	 * Zavisle tabulky
	 */
	protected $_dependentTables = array('Db_DbTable_Question','Db_DbTable_ResultSubject','Db_DbTable_SubjectStudyField');
	

}