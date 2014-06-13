<?php
/**
 * HarmonogramStudent
 *  
 * @author Radovan Dvorsky
 * @version 20110213
 */

class Db_DbTable_HarmonogramStudent extends Zend_Db_Table_Abstract {
	
	/**
	 * Primary keys	 
	 */
	protected $_primary = array('hid','ID_st');
	
	/**
	 * The default table name 
	 */
	protected $_name = 'harmonogram_student';	
		
	/**
	 * Parrent tables description 
	 */	
	protected $_referenceMap = array(
		'Harmonogram' => array(
			'columns' => array('hid'),
			'refTableClass' => 'Db_DbTable_Harmonogram',
			'refColumns' => array('hid'),
			'onDelete' => self::CASCADE,	
		),
		'Student' => array(
			'columns' => array('ID_st'),
			'refTableClass' => 'Db_DbTable_Student',
			'refColumns' => array('ID_st'),				
		)
	);
}

?>