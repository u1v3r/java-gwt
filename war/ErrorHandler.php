<?php
define('LOGGER','app.log');

/**
 * Zabazpeci ze chybove spravy PHP budu prevedene na <code>ErrorException</code>, ktoru potom
 * moze zachytit XML-RPC server
 * 
 * @param $errno
 * @param $errstr
 * @param $errfile
 * @param $errline
 * 
 * @return ErrorException
 */
function myErrorHandler($errno, $errstr, $errfile, $errline) {
	
	$logger = new Zend_Log ( new Zend_Log_Writer_Stream ( LOGGER ) );
	
	switch ($errno) {
		case E_NOTICE :
		case E_USER_NOTICE :
			$errors = "Notice";
			$priority = 'notice';
			break;
		case E_WARNING :
		case E_USER_WARNING :
			$errors = "Warning";
			$priority = 'warn';
			break;
		case E_ERROR :
		case E_USER_ERROR :
			$errors = "Fatal Error";
			$priority = 'emerg';
			break;
		case E_STRICT :
			$priority = 'info';
			break;
		default :
			$priority = 'debug';
			$errors = "Unknown";
			break;
	}
	
	$msg = "$errors: $errstr in $errfile on line $errline";
	
	//pridaj do logu
	$logger->$priority($msg);
	
	//nezobrazuj E_STRICT varovania
	if ($errno == E_STRICT || $errno == E_NOTICE) {
		return true;
	}	
	
	
	
	//treba vyhodit vynimku, aby chybu dokazal XMLRPC server spracovat a poslat klientovi
	if (ini_get ( "display_errors" )) {
		throw new ErrorException ( $msg );
	}
	
	return true;
}

set_error_handler("myErrorHandler");