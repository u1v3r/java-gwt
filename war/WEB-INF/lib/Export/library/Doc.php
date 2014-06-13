<?php
require_once 'DocumentAbstract.php';

class DocException extends Exception
{}
/**
 * Templating class for Doc file
 * You need PHP 5.2 at least
 * You need Zip Extension or PclZip library
 * Encoding : ISO-8859-1
 * Last commit by $Author: neveldo & Radovan Dvorsky $
 * Date - $Date: 2009-06-17 11:11:57 +0200 (mer., 17 juin 2009) $
 * SVN Revision - $Rev: 42 $
 * Id : $Id: Doc.php 42 2009-06-17 09:11:57Z neveldo $
 *
 * @copyright  GPL License 2008 - Julien Pauli - Cyril PIERRE de GEYER - Anaska (http://www.anaska.com)
 * @license    http://www.gnu.org/copyleft/gpl.html  GPL License
 * @version 1.3
 */

class Doc extends DocumentAbstract
{
    /**
     * Class constructor
     *
     * @param string $filename the name of the odt file
     * @throws DocumentException
     */
    public function __construct($filename, $config = array())
    {
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

    /**
     * Internal save
     *
     * @throws DocException
     * @return void
     */
    protected function _save()
    {
    	$handler = fopen($this->tmpfile,"w");  	    	
        $this->_parse();
        fwrite($handler,$this->contentXml);
        fclose($handler);       
    }
}

?>