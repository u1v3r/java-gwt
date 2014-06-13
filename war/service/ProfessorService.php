<?php

//require_once ('war/service/ServiceAbstract.php');

class ProfessorService extends ServiceAbstract {

	const UPDATE_TITLE_BEFORE = 0;
	const UPDATE_TITLE_BEHIND = 1;
	const UPDATE_FIRSTNAME = 2;
	const UPDATE_LASTNAME = 3;
	
	private $_mapper;
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_Professor();
	}
	
	/**
	 * Vrati vsetkych profesorov
	 * 
	 * @return array
	 */
	public function fetchAll(){
		$order = $this->_mapper->getTable()->select()->order('lastname');
		return $this->_mapper->getTable()->fetchAll($order)->toArray();
	}
	
	/**
	 * Upravi udaje o profesorovi v databaze
	 * 
	 * @param integer $pid
	 * @param string|null $firstName
	 * @param string|null $lastName
	 * @param string|null $titleBefore
	 * @param string|null $titleBehind
	 * @param integer $updateType Type update musi byt poskytnuty, inak sa pri prazdnom retazci udaje neulozia(nevymazu)
	 * 
	 * @return boolean 
	 */
	public function update($pid,$firstName,$lastName,$titleBefore,$titleBehind,$updateType){
		
		if(empty($pid) || $pid == null){
			throw new ServiceException('pid is empty');
		}
				
		$where = $this->_mapper->getTable()->getAdapter()->quoteInto('pid = ?',$pid,Zend_Db::INT_TYPE);
		$data = array();
		
		if($firstName != null || $updateType == self::UPDATE_FIRSTNAME) $data['firstname'] = $firstName;
		if($lastName != null || $updateType == self::UPDATE_LASTNAME) $data['lastname'] = $lastName;
		if($titleBefore != null || $updateType == self::UPDATE_TITLE_BEFORE) $data['title_before'] = $titleBefore;
		if($titleBehind != null || $updateType == self::UPDATE_TITLE_BEHIND) $data['title_behind'] = $titleBehind;
				
		if(count($data) < 1){
			throw new ServiceException("Update professor data aren't provided");
		}
		
		$rows = $this->_mapper->getTable()->update($data,$where);
		
		return ($rows > 0) ? true : false;
		
	}
	
	
	/**
	 * Vlozi noveho profesora do db
	 * 
	 * @param string $firstname
	 * @param string $lastname
	 * @param string $titleBefore
	 * @param string $titleBehind
	 * 
	 * @return integer id vlozeneho profesora
	 */
	public function add($firstname,$lastname,$titleBefore,$titleBehind){
		
		//meno a priezvisko musi mat kazdy profesor
		if(empty($firstname) || empty($lastname)){
			throw new ServiceException("Firstname and lastname must be provided");
		}
		
		$data = array(
			'title_before' => $titleBefore,
			'title_behind' => $titleBehind,
			'firstname' => $firstname,
			'lastname' => $lastname,
			'short' => new Zend_Db_Expr('NULL'),//v sucasnosti skratka v aplikacii nie je potrebna, preto sa ani neuklada
		);
		
		$id = $this->_mapper->getTable()->insert($data);
		
		return (int)$id;
		
	}
	
	/**
	 * Odstrani profesora z db
	 * 
	 * @param integer $pid
	 * 
	 * @return boolean
	 */
	public function delete($pid){
		
		if(empty($pid)){
			throw new ServiceException("pid is empty");
		}
		
		$where = $this->_mapper->getTable()->getAdapter()->quoteInto('pid = ?',$pid,Zend_Db::INT_TYPE);
		
		
		return ($this->_mapper->getTable()->delete($where) > 0) ? true : false;		
	}
}

?>