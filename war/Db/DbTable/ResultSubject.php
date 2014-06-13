<?php

/**
 * ResultSubject
 *  
 * @author Radovan Dvorsky
 * @version 20101107
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_ResultSubject extends Zend_Db_Table_Abstract {
	
	/**
	 * Primary keys	 
	 */
	protected $_primary = array('rid','sid');
	
	/**
	 * The default table name 
	 */
	protected $_name = 'result_subject';	
		
	/**
	 * Parrent tables description 
	 */	
	protected $_referenceMap = array(
		'Result' => array(
			'columns' => array('rid'),
			'refTableClass' => 'Db_DbTable_Result',
			'refColumns' => array('rid'),
			'onDelete' => self::CASCADE,	
		),
		'Subject' => array(
			'columns' => array('sid'),
			'refTableClass' => 'Db_DbTable_Subject',
			'refColumns' => array('sid'),
			'onDelete' => self::CASCADE,	
		)
	);
}