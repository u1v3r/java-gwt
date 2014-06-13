<?php
require_once 'DocumentAbstract.php';

class Html extends DocumentAbstract {
	
	function __construct() {
		if (! is_array($config)) {
    		throw new DocumentException('Configuration data must be provided as array');
    	}
    	foreach ($config as $configKey => $configValue) {
    		if (array_key_exists($configKey, $this->config)) {
    			$this->config[$configKey] = $configValue;
    		}
    	}
                
        $handler = @fopen($filename,"r");
        if($handler == false){
        	throw new DocumentException('File ' . $filename . ' not found');
        }
        
        $this->contentXml = fread($handler,filesize($filename));
        fclose($handler);
                
        $tmp = $this->config['PATH_TO_TMP'] . md5(uniqid());
        copy($filename, $tmp);
        $this->tmpfile = $tmp;
        $this->_moveRowSegments();
	}
	
	protected function _save() {
		$handler = fopen($this->tmpfile,"w");  	    	
        $this->_parse();
        fwrite($handler,$this->contentXml);
        fclose($handler); 
	}
}

?>