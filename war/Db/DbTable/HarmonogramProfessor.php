<?php

/**
 * HarmonogramProfessor
 *  
 * @author Radovan Dvorsky
 * @version 20100219
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_HarmonogramProfessor extends Zend_Db_Table_Abstract {
	
	/**
	 * Primary keys	 
	 */
	protected $_primary = array('pid','hid');
	
	/**
	 * The default table name 
	 */
	protected $_name = 'harmonogram_professor';	
		
	/**
	 * Parrent tables description 
	 */	
	protected $_referenceMap = array(
		'Professor' => array(
			'columns' => array('pid'),
			'refTableClass' => 'Db_DbTable_Professor',
			'refColumns' => array('pid'),
			'onDelete' => self::CASCADE,	
		),
		'Harmonogram' => array(
			'columns' => array('hid'),
			'refTableClass' => 'Db_DbTable_Harmonogram',
			'refColumns' => array('hid'),
			'onDelete' => self::CASCADE,	
		)
	);
}