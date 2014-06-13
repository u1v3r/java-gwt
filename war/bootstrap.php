<?php
// bootstrap.php

set_include_path(
	    	'./' .			 
			PATH_SEPARATOR .
			'./service' . 
			PATH_SEPARATOR .
			'./WEB-INF/lib' . 
			PATH_SEPARATOR .
			'./WEB-INF/lib/mpdf51' . 
			PATH_SEPARATOR .		
			get_include_path()
);

/*
 * Nastavenie autoloader
 */
require_once 'Zend/Loader/Autoloader.php';	
$autoloader = Zend_Loader_Autoloader::getInstance();
$autoloader->registerNamespace('Db_');
$autoloader->registerNamespace('My_');

/*
 * Nastavenie DB
 */
//tabulky aplikacie
$db = new Zend_Db_Adapter_Pdo_Mysql(array(
    'host'     => 'localhost',
    'username' => 'root',
    'password' => 'root',
    'dbname'   => 'kosilka'
));

//tabulky Student,TTridy,SkRok...
$db2 = new Zend_Db_Adapter_Pdo_Mysql(array(
    'host'     => 'localhost',
    'username' => 'root',
    'password' => 'root',
    'dbname'   => 'kosilkaF'
));

//tabulka uchazeci
$db3 = new Zend_Db_Adapter_Pdo_Mysql(array(
    'host'     => 'localhost',
    'username' => 'root',
    'password' => 'root',
    'dbname'   => 'kosilkaU'
));

Zend_Db_Table::setDefaultAdapter($db);
$db->query("SET NAMES 'utf8'");
$db2->query("SET NAMES 'utf8'");
$db3->query("SET NAMES 'utf8'");
Zend_Registry::set('db',$db);
Zend_Registry::set('db2',$db2);
Zend_Registry::set('db3',$db3);
    	
/*
 * Nastavenie cache
 */
$frontendOptions = array(
	'caching' => true,
	'automatic_serialization' => false,
	'lifetime' => 7200, // cas platnosti cache 2 hod.    		
);
				
		
$backendOptions = array(
	'cache_dir' => './tmp'
);
		
$cache = Zend_Cache::factory('Core','File',$frontendOptions,$backendOptions);
Zend_Registry::set('cache',$cache);
