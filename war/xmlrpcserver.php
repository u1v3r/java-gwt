<?php
session_start();

try{
	require_once 'bootstrap.php';
	require_once 'ErrorHandler.php';
	require_once 'service/ServiceAbstract.php';
	require_once 'service/ServiceException.php';
	require_once 'service/HarmonogramService.php'; 
	require_once 'service/ResultService.php';
	require_once 'service/SubjectService.php';
	require_once 'service/OborService.php';
	require_once 'service/SubjectStudyFieldService.php';
	require_once 'service/ProfessorService.php';
	require_once 'service/StudentService.php';
	require_once 'service/StudyFieldService.php';
	require_once 'service/QuestionService.php';
	require_once 'service/YearService.php';
	require_once 'service/AuthenticateService.php';
		
	$cacheFile = dirname(__FILE__) . '/tmp/xmlrpc.cache';//cesta k cache suboru
	Zend_XmlRpc_Value::setGenerator(new Zend_XmlRpc_Generator_XmlWriter());
	Zend_XmlRpc_Server_Fault::attachFaultException('Exception');//vyhadzuj vsetky vynimky
	$server = new Zend_XmlRpc_Server();
	
	// nastavenie sluzieb (ak je pridana nova sluzba treba odstranit cache subor /tmp/xmlrpc.cache)
	if (!Zend_XmlRpc_Server_Cache::get($cacheFile, $server)) {		
		$server->setClass(new HarmonogramService(),'harmonogram');
		$server->setClass(new ResultService(),'result');
		$server->setClass(new SubjectService(),'subject');
		$server->setClass(new OborService(),'obor');
		$server->setClass(new SubjectStudyFieldService(),'subject_studyfield');
		$server->setClass(new ProfessorService(),'professor');
		$server->setClass(new StudentService(),'student');
		$server->setClass(new StudyFieldService(),'studyfield');
		$server->setClass(new QuestionService(),'question');
		$server->setClass(new YearService(),'year');
		$server->setClass(new AuthenticateService(),'auth');
		Zend_XmlRpc_Server_Cache::save($cacheFile, $server);
	}
	
	//z poziadavku vytvori objekt
	$request = new Zend_XmlRpc_Request_Http();	
	
	//ak ide o prihlasenie alebo odhlasenie tak nekontroluj session
	if($request->getMethod() == 'auth.authenticate' || $request->getMethod() == 'auth.logout'){
		echo $server->handle($request);
	}
	else {
		//musi byt vytvorena session a zaroven musi klient posielat v hlavicke id session
		
		/* zbytocna kontrola, pravdepodobne mozne odstranit
		if(!array_key_exists('sessionID',$_SESSION) || !array_key_exists('sessionID',$_REQUEST)){
			throw new AuthenticationException('Authentication failed');
			return;
		}
		*/
		
		
		/*
		 * id session sa musi zhodovat s id session posielanej v hlavicke
		 */
		if($_SESSION['sessionID'] != $_REQUEST['sessionID']){
			throw new AuthenticationException('Authentication failed');
			return;
		}
		
		//ak sa zhoduje vykonaj dotaz
		echo $server->handle($request);
	}
		
} catch (Exception $e){

	/*
	 * Vypis chyby ak nastane skor ako je vytvorena trieda Zend_XmlRpc_Server, ktora
	 * generuje xml pre kazdu vynimku
	 */
	echo '<?xml version="1.0" encoding="UTF-8"?>';
	echo '<methodResponse><fault><value><struct><member><name>faultCode</name><value><int>2002</int></value>
		</member><member><name>faultString</name><value><string>' . $e->getMessage(). '</string></value>
		</member></struct></value></fault></methodResponse>';
}
	
