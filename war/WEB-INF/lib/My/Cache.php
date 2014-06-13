<?php

class My_Cache {

	/**
	 * Vytvori meno pre cache
	 * 
	 * @param object | string $class
	 * @param mixed $key
	 * @param Zend_Db_Select $select
	 * 
	 * @return string (md5)
	 */
	public static function getCacheName($class,$key,Zend_Db_Select $select = null){
		
		if(is_object($class)){		
			$class = get_class($class);
		}
		
		return (string)md5($class . (string)$key . $select);		
	}
}

?>