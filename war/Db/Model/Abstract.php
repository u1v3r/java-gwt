<?php
/**
 * 
 * @author Radovan Dvorsky
 * @version 20100512
 *
 */
abstract class Db_Model_Abstract implements ArrayAccess{
	
	private $_foreignModel = array();
	
	public function __construct(array $options = null){
			
		if(is_array($options)){
			$this->_setOptions($options);
		}
	}
	
	/**
	 * @param mixed $offset
	 * 
	 * @return boolean
	 */
	public function offsetExists($offset) {		
		
		$method = $this->_getGetterMethod($offset);
		
		$value = $this->$method();
		
		return !empty($value) ? true : false;					
	}

	/**
	 * @param mixed $offset
	 * 
	 * @return mixed | null
	 */
	public function offsetGet($offset) {
		
		$method = $this->_getGetterMethod($offset);		
		
		return $this->$method();		
	}

	/**
	 * @param mixed $offset
	 * @param mixed $value
	 * 
	 * @return mixed
	 */
	public function offsetSet($offset, $value) {
		
		$method = $this->_getSetterMethod($offset);
		
		$possibleMethods = get_class_methods($this);		
		
		if(!in_array($method,$possibleMethods)){
			throw new Exception('Method ' . $method . ' doesn\'n exist in class ' . get_class($this));
		}
		
		$this->$method($value);		
	}

	/**
	 * @param mixed $offset
	 */
	public function offsetUnset($offset) {
		
		$method = $this->_getSetterMethod($offset);
		
		$possibleMethods = get_class_methods($this);
		
		if(!in_array($method,$possibleMethods)){
			throw new Exception('Method ' . $method . ' doesn\'n exist in class ' . get_class($this));
		}
		
		$this->$method("");
		
	}

	public function __set($name, $value){
		
		$method = $this->_getSetterMethod($name);
		
        if (('mapper' == $name) || !method_exists($this, $method)) {
            throw new Exception('Invalid property: ' . $name . ' = ' . $value);
        }        
        
        $this->$method($value);		
	}
	
	/**
	 * Set
	 * 
	 * @param Db_Model_Abstract $foreignModel
	 * 
	 */
	public function setForeignModel(Db_Model_Abstract $foreignModel){
		$this->_foreignModel[get_class($foreignModel)] = $foreignModel;
	}
	
	/**
	 * 
	 * @param string $modelClassName
	 * 
	 * @return Db_Model_Abstract
	 */
	public function getForeignModel($modelClassName){		
		return $this->_foreignModel[$modelClassName];		
	}
	
	/**
	 * Get
	 * 
	 * @return array
	 */
	public function getForeignModels(){
		return $this->_foreignModel;
	}
	
	public function __get($name){
		
		$method = $this->_getGetterMethod($name);
        
		if (('mapper' == $name) || !method_exists($this, $method)) {
            throw new Exception('Invalid property: ' . $name);
        }
        
        return $this->$method();
	}
	
	/**
	 * Vrati objekt ako pole
	 * 
	 * @return array
	 */
	public function toArray(){
		
		$data = array();
		$reflection = new ReflectionClass($this);
		$vars = $reflection->getDefaultProperties();

		foreach($vars as $name => $value){
			$method = $this->_getGetterMethod($name);
			$data[$name] = $this->$method();
		}
		
		return $data;	
	}
	
	/**
	 * Pomocou setterov nastavi atributy triedy
	 * @param array $options
	 * @return self
	 */
	protected function _setOptions(array $options){		
		
		if(is_array($options)){
							
			/*
			 * Nacitaj mena metod triedy do pola
			 */
			$methods = get_class_methods($this);
			
			foreach($options as $key => $value){
				
				/*
				 * Vytvor meno metody
				 */
				$method = $this->_getSetterMethod($key);				
				
				/*
				 * Ak metoda existuje v triede
				 */
				if(in_array($method,$methods)){					
					$this->$method($value);					
				}
				else {
					throw new Exception('Method ' . $method . ' doesn\'t exist in class ' . get_class($this));
				}	
				
			}
			
			return $this;
		}
	}
	
	/**
	 * Vrati getter pre dany atribut
	 * 
	 * @param string $attrib
	 * 
	 * @return string
	 */
	private function _getGetterMethod($attrib){
		return 'get' . ucfirst($attrib);
	}
	
	/**
	 * Vrati setter pre dany atribut
	 * 
	 * @param string $attrib
	 * 
	 * @return string
	 */
	private function _getSetterMethod($attrib){
		return 'set' . ucfirst($attrib);
	}
}

?>