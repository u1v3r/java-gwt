<?php
/**
 * BlockStObory
 * 
 * @author Radovan Dvorsky
 * @version 20110213
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_StudyFieldStObory extends Zend_Db_Table_Abstract {	
	
	/**
	 * The default table name 
	 */
	protected $_name = 'studyfield_st_obory';
	
	/**
	 * Primary key
	 */
	protected $_primary = array('sfid','Id_oboru');
	
	/**
	 * Parrent tables description 
	 */	
	protected $_referenceMap = array(
		'StudyField' => array(
			'columns' => array('sfid'),
			'refTableClass' => 'Db_DbTable_StudyField',
			'refColumns' => array('sfid'),
			'onDelete' => self::CASCADE,	
		),
		'Obor' => array(
			'columns' => array('Id_oboru'),
			'refTableClass' => 'Db_DbTable_StObory',
			'refColumns' => array('Id_oboru'),				
		)
	);

}

