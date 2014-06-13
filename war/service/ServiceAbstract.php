<?php
/**
 * 
 * @author Radvoan Dvorsky
 * @version 20101212
 */
abstract class ServiceAbstract {

	protected $_xml = null;
	protected $_post = null;
		
	/**
	 * @deprecated pouzivaj Zend_XmlRpc
	 * Prevedie pole objektov na xml
	 * 
	 * @param array $data
	 * @return string
	 */
	public function toXml(array $data){
		
		if(count($data) < 1){
			return array();
		};
		
		$attribs = $this->_getClassAttribs($data[0]);		
		$xmlRootTag = strtolower($this->_getEndClassPart(get_class($data[0])));//na zaklade poslednej casti mena triedy zisti nazov

		$writer = new XMLWriter();
		$writer->openUri('php://output');
		$writer->setIndent(true);
		$writer->startDocument('1.0','UTF-8');
		$writer->startElement($xmlRootTag . 's');
		
		foreach($data as $row){
			
			$writer->startElement($xmlRootTag);						
			foreach($attribs as $attrib => $value){
				if(substr($attrib,0,1) != "_"){//nepouzivaj atribury zacinajuce na "_"
					$writer->writeAttribute($attrib,$row[$attrib]);
				}				
			}

			/*
			 * Ak vysledok vratil nejake foreignModels (tj. prepojene tabulky z databaze na zaklade id)
			 * tak ich ulozi do do elementu "foreignmodels"
			 */
			if(count($foreignModels = $row->getForeignModels()) > 0 ){
				$writer->startElement('foreignmodels');
				foreach ($foreignModels as $className => $object){
					$writer->startElement(strtolower($this->_getEndClassPart($className)));
					$objectAttribs = $this->_getClassAttribs($object);
					foreach($objectAttribs as $key => $value){
						$getter = 'get' . strtoupper($key);
						if(!is_array($objectValue = $object->$getter())){
							$writer->writeAttribute($key,$objectValue);
						}
					}
					$writer->endElement();	
				}
				$writer->endElement();
			}
			$writer->endElement();
		}
		$writer->endElement();
		$writer->endDocument();				
	}
	
	/**
	 * @return the $postData
	 */
	public function getPost() {
		return $this->_post;
	}

	/**
	 * @param $postData the $postData to set
	 */
	public function setPost($post) {
		$this->_post = $post;
	}
	
	public function getParam($key){
		return $this->_post[$key];
	}
	
	public function setParam($key,$value){
		$this->_post[$key] = $value;
	}
	
	private function _getEndClassPart($className){		
		return array_pop(explode('_',$className));
	}
	
	private function _getClassAttribs($class){
		$attribs = array();
		
		$reflection = new ReflectionClass($class);	
		$attribsAll = $reflection->getDefaultProperties();
				
		foreach($attribsAll as $key => $value){
			if(substr($key,0,1) != '_'){				
				$attribs[$key] = $value;
			}
		}
		
		return $attribs;
	}
}

?>