<?php
/**
 * SubjectStudField
 * 
 * @author Radovan Dvorsky
 * @version 20100513
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_SubjectStudyField extends Zend_Db_Table_Abstract {	
	
	/**
	 * The default table name 
	 */
	protected $_name = 'subject_studyfield';
	
	/**
	 * Primary key
	 */
	protected $_primary = array('sfid','sid');
	
	/**
	 * Parrent tables description 
	 */	
	protected $_referenceMap = array(
		'Subject' => array(
			'columns' => array('sid'),
			'refTableClass' => 'Db_DbTable_Subject',
			'refColumns' => array('sid'),
			'onDelete' => self::CASCADE,	
		),
		'StudyField' => array(
			'columns' => array('sfid'),
			'refTableClass' => 'Db_DbTable_StudyField',
			'refColumns' => array('sfid'),
			'onDelete' => self::CASCADE,				
		)
	);

}

