<?php
/**
 * @author Radovan Dvorsky
 * @version 20110313
 */

class Db_DbTable_Uchazeci extends Zend_Db_Table_Abstract {
/**
	 * Database name
	 * @var string
	 */
	protected $_dbName; 
	
	/**
	 * Table name
	 * @var string 
	 */
	protected $_name = 'uchazeci';
	
	/**
	 * Primary key
	 * @var string
	 */	
	protected $_primary = 'id_uchazece';
	
	/**
	 * Mena zavislich tabuliek
	 * @var array
	 */
	protected $_dependentTables = array();
	
	/**
	 * Parrent tables description 
	 */
	protected $_referenceMap = array();
	
	public function init(){
		
		/*
		 * Nastav adapter pre druhu databazu
		 */
		$dbAdapter = Zend_Registry::get('db3');
		$this->_setAdapter($dbAdapter);
		$config = $dbAdapter->getConfig();
		
		/*
		 * Inicializuj premenu s menom druhej databazy
		 */
		$this->_dbName = $config['dbname'];
	}
}

?>