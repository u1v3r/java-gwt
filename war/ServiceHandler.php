<?php
/*
 * Ak nie je nastavena spravna http hlavicka, tvari sa ze skript neexistuje 
 */
if($_SERVER['HTTP_X_REQUESTED_WITH'] != 'XMLHttpRequest'){
	header('HTTP/1.1 404 Not Found');
	exit;
}

require_once 'bootstrap.php';
require_once dirname(__FILE__) . '/service/ServiceAbstract.php';

$serviceClassName = $_REQUEST['s'] . 'Service';
$actionName = $_REQUEST['a'];


require_once(dirname(__FILE__) . '/service/' . $serviceClassName . '.php');
$class = new $serviceClassName();
$class->setPost($_POST);
$class->$actionName();

