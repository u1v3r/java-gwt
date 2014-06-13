<?php
require_once 'DocumentAbstract.php';

/**
 * Templating class for odt file
 * You need PHP 5.2 at least
 * You need Zip Extension or PclZip library
 * Encoding : ISO-8859-1
 * Last commit by $Author: neveldo & Radovan Dvorsky $
 * Date - $Date: 2009-06-17 11:11:57 +0200 (mer., 17 juin 2009) $
 * SVN Revision - $Rev: 42 $
 * Id : $Id: Odf.php 42 2009-06-17 09:11:57Z neveldo $
 *
 * @copyright  GPL License 2008 - Julien Pauli - Cyril PIERRE de GEYER - Anaska (http://www.anaska.com)
 * @license    http://www.gnu.org/copyleft/gpl.html  GPL License
 * @version 1.3
 */

class Odf extends DocumentAbstract
{
    
    /**
     * Class constructor
     *
     * @param string $filename the name of the odt file
     * @throws OdfException
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
        if (! class_exists($this->config['ZIP_PROXY'])) {
            throw new DocumentException($this->config['ZIP_PROXY'] . ' class not found - check your php settings');
        }
        $zipHandler = $this->config['ZIP_PROXY'];
        $this->file = new $zipHandler();
        if ($this->file->open($filename) !== true) {
            throw new DocumentException("Error while Opening the file '$filename' - Check your odt file");
        }
        if (($this->contentXml = $this->file->getFromName('content.xml')) === false) {
            throw new DocumentException("Nothing to parse - check that the content.xml file is correctly formed");
        }
		
        $this->file->close();        
        $tmp = $this->config['PATH_TO_TMP'] . md5(uniqid());
        copy($filename, $tmp);
        $this->tmpfile = $tmp;
        $this->_moveRowSegments();
    }
    
    /**
     * Assign a template variable as a picture
     *
     * @param string $key name of the variable within the template
     * @param string $value path to the picture
     * @throws OdfException
     * @return Odf
     */
    public function setImage($key, $value)
    {
        $filename = strtok(strrchr($value, '/'), '/.');
        $file = substr(strrchr($value, '/'), 1);
        $size = @getimagesize($value);
        if ($size === false) {
            throw new DocumentException("Invalid image");
        }
        list ($width, $height) = $size;
        $width *= self::PIXEL_TO_CM;
        $height *= self::PIXEL_TO_CM;
        $xml = <<<IMG
<draw:frame draw:style-name="fr1" draw:name="$filename" text:anchor-type="char" svg:width="{$width}cm" svg:height="{$height}cm" draw:z-index="3"><draw:image xlink:href="Pictures/$file" xlink:type="simple" xlink:show="embed" xlink:actuate="onLoad"/></draw:frame>
IMG;
        $this->images[$value] = $file;
        $this->setVars($key, $xml, false);
        return $this;
    }
    
    /**
     * Internal save
     *
     * @throws OdfException
     * @return void
     */
    protected function _save()
    {
    	$this->file->open($this->tmpfile);    	
        $this->_parse();
        
        if (! $this->file->addFromString('content.xml', $this->contentXml)) {
            throw new DocumentException('Error during file export');
        }
        foreach ($this->images as $imageKey => $imageValue) {
            $this->file->addFile($imageKey, 'Pictures/' . $imageValue);
        }
        $this->file->close(); // seems to bug on windows CLI sometimes
    }
}

?>