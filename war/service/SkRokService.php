<?php

//require_once ('war/service/ServiceAbstract.php');

class SkRokService extends ServiceAbstract {

	private $_mapper;
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_SkRok();
	}
	
	public function fetchAll(){	
		$cache = Zend_Registry::get('cache');		
		$cacheName = My_Cache::getCacheName($this,'fetchAll');		
		
		if(!($xml = $cache->load($cacheName))){					
			
			ob_start();
			$this->toXml($this->_mapper->fetchAll(
				$this->_mapper->getTable()->select()->order('skolni_rok DESC')->limit(5))
			);			
			$xml = ob_get_clean();
			$cache->save($xml,$cacheName);
							
		}
		
		echo $xml;
	}
}

?>