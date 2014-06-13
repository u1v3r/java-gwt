<?php
require_once 'AuthenticationException.php';

class AuthenticateService extends ServiceAbstract {
	
	private $_dbTable;
	
	public function __construct(){
		$this->_dbTable = new Db_DbTable_User();
	}
	
	/**
	 * Autentifikuje užívateľa
	 * 
	 * @param string $username
	 * @param string $password
	 * 
	 * @return string Sluzba vrati session id
	 */
	public function authenticate($username,$password,$kontext){
		
		if(empty($username) || empty($password) || empty($kontext)){
			throw new AuthenticationException("Authentication failed");
		}
				
		//prihlasenie pre servisneho uzivatela, ktory nie je v skolskej sieti
		if($kontext == 'admin'){
			
			$where = array();
			$where[] = $this->_dbTable->getAdapter()->quoteInto('username = ?',$username);
			$where[] = $this->_dbTable->getAdapter()->quoteInto('password = ?',sha1($password));
			
			$row = $this->_dbTable->fetchRow($where);
			
			if(count($row) == 0){
				throw new AuthenticationException("Neplatné prihlasovacie údaje");
				return;
			}
		}
		else { //prihlasenie cez skolsky ldap server			
			
			$username = strtolower ( $username );
			$kontext = strtolower ( $kontext );
			
			//prihlasit sa moze len ucitel alebo zamestnanec skoly 
			if($kontext == 'ucitel' || $kontext == 'zamest'){
						
				$connection = ldap_connect ( "192.168.0.250" ); // adresa LDAP serveru			

				//ak je pripojeny
				if ($connection){
					// navazanie spojenia
					if (@ldap_bind ( $connection )){
						$dn = 'cn=' . $username . ',ou=' . $kontext . ',ou=uzivatel,o=vos'; // hladany dotaz v celom kontexte
						$value = "userpassword"; // definicia uzivatelskeho hesla
											
						$result = @ldap_compare ( $connection, $dn, $value, $password ); // porovnanie so zaznamom na servery
						
						if ($result === - 1) {
							throw new AuthenticationException("Neplatné prihlasovacie údaje");
							return;
						} elseif ($result === false) {
							throw new AuthenticationException("Neplatné prihlasovacie údaje");
							return;
						} 
					}
					else {
						throw new AuthenticationException('Nepodarilo sa pripojenie k LDAP serveru');
						return;
					}
				}			
			} else {
				throw new AuthenticationException("Neplatný kontext");
				return;
			}

		}		
		
		//ak existuje odstran staru session
		if(isset($_SESSION['sessionID'])){
			unset($_SESSION['sessionID']);
		}
		
		//vytvor nove session id
		$_SESSION['sessionID'] = sha1($username . uniqid());		
					
		//posli id session klientovi
		return $_SESSION['sessionID'];		
	}
	
	/**
	 * Zrusi session
	 * 
	 * @return boolean
	 */
	public function logout(){
		
		unset($_SESSION['sessionID']);
		return session_destroy();
		
	}
}

?>