<?php

/**
 * Question
 *  
 * @author Radovan Dvorsky
 * @version 20110213
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_Question extends Zend_Db_Table_Abstract {
	
	/**
	 * The default table name 
	 */
	protected $_name = 'question';
	
	/**
	 * Primary key
	 */
	protected $_primary = 'qid';
	
	/**
	 * Parrent tables description 
	 */
	protected $_referenceMap = array(
		'Subject' => array(
			'columns' => array('sid'),
			'refTableClass' => 'Db_DbTable_Subject',
			'refColumns' => array('sid'),
			'onDelete' => self::CASCADE
		)	
	);
	
	
	/**
	 * Mena zavislich tabuliek
	 * @var array
	 */
	protected $_dependentTables = array('Db_DbTable_QnForStudent');
	
	

}