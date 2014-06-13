<?php
require_once 'zip/PclZipProxy.php';
require_once 'zip/PhpZipProxy.php';
require_once 'Segment.php';

class DocumentException extends Exception
{}
abstract class DocumentAbstract {
	
protected $config = array(
    	'ZIP_PROXY' => 'PhpZipProxy',
    	'DELIMITER_LEFT' => '{',
    	'DELIMITER_RIGHT' => '}',
		'PATH_TO_TMP' => './tmp/'
   	);
    protected $file;
    protected $contentXml;
    protected $tmpfile;
    protected $images = array();
    protected $vars = array();
    protected $segments = array();
    const PIXEL_TO_CM = 0.026458333;
    
   
    /**
     * Assing a template variable
     *
     * @param string $key name of the variable within the template
     * @param string $value replacement value
     * @param bool $encode if true, special XML characters are encoded
     * @throws DocumentException
     * @return Odf
     */
    public function setVars($key, $value, $encode = true, $charset = 'UTF-8')
    {
        if (strpos($this->contentXml, $this->config['DELIMITER_LEFT'] . $key . $this->config['DELIMITER_RIGHT']) === false) {
            throw new DocumentException("var $key not found in the document");
        }
        $value = $encode ? htmlspecialchars($value) : $value;
        $value = ($charset == 'ISO-8859') ? utf8_encode($value) : $value;
        $this->vars[$this->config['DELIMITER_LEFT'] . $key . $this->config['DELIMITER_RIGHT']] = str_replace("\n", "<text:line-break/>", $value);
        return $this;
    }

    /**
     * Move segment tags for lines of tables
     * Called automatically within the constructor
     *
     * @return void
     */    
    protected function _moveRowSegments()
    {
    	// Search all possible rows in the document
    	$reg1 = "#<table:table-row[^>]*>(.*)</table:table-row>#smU";
		preg_match_all($reg1, $this->contentXml, $matches);
		for ($i = 0, $size = count($matches[0]); $i < $size; $i++){
			// Check if the current row contains a segment row.*
			$reg2 = '#\[!--\sBEGIN\s(row.[\S]*)\s--\](.*)\[!--\sEND\s\\1\s--\]#sm';
			if (preg_match($reg2, $matches[0][$i], $matches2)) {
				$balise = str_replace('row.', '', $matches2[1]);
				// Move segment tags around the row
				$replace = array(
					'[!-- BEGIN ' . $matches2[1] . ' --]'	=> '',
					'[!-- END ' . $matches2[1] . ' --]'		=> '',
					'<table:table-row'							=> '[!-- BEGIN ' . $balise . ' --]<table:table-row',
					'</table:table-row>'						=> '</table:table-row>[!-- END ' . $balise . ' --]'
				);
				$replacedXML = str_replace(array_keys($replace), array_values($replace), $matches[0][$i]);
				$this->contentXml = str_replace($matches[0][$i], $replacedXML, $this->contentXml);
			}
		}
    }
    /**
     * Merge template variables
     * Called automatically for a save
     *
     * @return void
     */
    protected function _parse()
    {
        $this->contentXml = str_replace(array_keys($this->vars), array_values($this->vars), $this->contentXml);
    }
    /**
     * Add the merged segment to the document
     *
     * @param Segment $segment
     * @throws OdfException
     * @return Odf
     */
    public function mergeSegment(Segment $segment)
    {
        if (! array_key_exists($segment->getName(), $this->segments)) {
            throw new DocumentException($segment->getName() . 'cannot be parsed, has it been set yet ?');
        }
        $string = $segment->getName();
		// $reg = '@<text:p[^>]*>\[!--\sBEGIN\s' . $string . '\s--\](.*)\[!--.+END\s' . $string . '\s--\]<\/text:p>@smU';
		$reg = '@\[!--\sBEGIN\s' . $string . '\s--\](.*)\[!--.+END\s' . $string . '\s--\]@smU';
        $this->contentXml = preg_replace($reg, $segment->getXmlParsed(), $this->contentXml);
        return $this;
    }
    /**
     * Display all the current template variables
     * 
     * @return string
     */
    public function printVars()
    {
        return print_r('<pre>' . print_r($this->vars, true) . '</pre>', true);
    }
    /**
     * Display the XML content of the file from odt document
     * as it is at the moment
     *
     * @return string
     */
    public function __toString()
    {
        return $this->contentXml;
    }
    /**
     * Display loop segments declared with setSegment()
     * 
     * @return string
     */
    public function printDeclaredSegments()
    {
        return '<pre>' . print_r(implode(' ', array_keys($this->segments)), true) . '</pre>';
    }
    /**
     * Declare a segment in order to use it in a loop
     *
     * @param string $segment
     * @throws OdfException
     * @return Segment
     */
    public function setSegment($segment)
    {
        if (array_key_exists($segment, $this->segments)) {
            return $this->segments[$segment];
        }
        // $reg = "#\[!--\sBEGIN\s$segment\s--\]<\/text:p>(.*)<text:p\s.*>\[!--\sEND\s$segment\s--\]#sm";
        $reg = "#\[!--\sBEGIN\s$segment\s--\](.*)\[!--\sEND\s$segment\s--\]#sm";
        if (preg_match($reg, html_entity_decode($this->contentXml), $m) == 0) {
            throw new DocumentException("'$segment' segment not found in the document");
        }
        $this->segments[$segment] = new Segment($segment, $m[1], $this);
        return $this->segments[$segment];
    }
    /**
     * Save the file on the disk
     * 
     * @param string $file name of the desired file
     * @throws OdfException
     * @return void
     */
    public function saveToDisk($file = null)
    {
        if ($file !== null && is_string($file)) {
        	if (file_exists($file) && !(is_file($file) && is_writable($file))) {
            	throw new DocumentException('Permission denied : can\'t create ' . $file);
        	}
            $this->_save();                       
            copy($this->tmpfile, $file);
            unlink($this->tmpfile);                        
        } else {
            $this->_save();
        }
    }
    /**
     * Internal save
     *
     * @throws OdfException
     * @return void
     */
    protected function _save(){}
    
    /**
     * Export the file as attached file by HTTP
     *
     * @param string $name (optionnal)
     * @throws OdfException
     * @return void
     */
    public function exportAsAttachedFile($name="")
    {
        $this->_save();
        if (headers_sent($filename, $linenum)) {
            throw new DocumentException("headers already sent ($filename at $linenum)");
        }
        
        if( $name == "" )
        {
        		$name = md5(uniqid()) . ".odt";
        }
        
        header('Content-type: application/vnd.oasis.opendocument.text');
        header('Content-Disposition: attachment; filename="'.$name.'"');
        readfile($this->tmpfile);
    }
    /**
     * Returns a variable of configuration 
     * 
     * @return string The requested variable of configuration
     */
    public function getConfig($configKey)
    {
    	if (array_key_exists($configKey, $this->config)) {
    		return $this->config[$configKey];
    	}
    	return false;
    }
    /**
     * Returns the temporary working file
     * 
     * @return string le chemin vers le fichier temporaire de travail
     */
    public function getTmpfile()
    {
    	return $this->tmpfile;
    }
    /**
     * Delete the temporary file when the object is destroyed
     */    
    public function __destruct() {
          if (file_exists($this->tmpfile)) {
        	unlink($this->tmpfile);
        }
    }
}	
	
?>