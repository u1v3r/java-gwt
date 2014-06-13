<?php

/**
 * User
 * 
 * @author Radovan Dvorsky
 * @version 20110429
 */

require_once 'Zend/Db/Table/Abstract.php';

class Db_DbTable_User extends Zend_Db_Table_Abstract {
	
	/**
	 * The default table name 
	 */
	protected $_name = 'user';
	
	/**
	 * Primary key
	 */	
	protected $_primary = 'uid';
}

