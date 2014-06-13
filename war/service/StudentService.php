<?php
class StudentService extends ServiceAbstract {
	
	public $_mapper;
	
	public function __construct(){

		$this->_mapper = new Db_Mapper_Student();
		
	}	
	
	public function fetchStudentsByIDTridy(){
		
		$ID_tr = (int)$this->getParam('ID_tr');
				
		if($ID_tr == 0){
			throw new ServiceException('ID_tr in service student.fetchStudentsByIDTridy is not set');
			return;
		} 
		
		$select = $this->_mapper->getTable()->select();
		$select->setIntegrityCheck(false)
		->from('Student',array())
		->columns(array('ID_st','Jmeno','Prijmeni','Login','Heslo'))
		->join('Student_has_Tridy','Student_has_Tridy.Student_ID_st = Student.ID_st',array())
		->where('Student_has_Tridy.Tridy_ID_tr = ?',$ID_tr)
		->where('Student.ukonceni != "U"')//Ukoncene studium pred SZZ (nezobrazovat vo vysledku)
		->order('Student.Prijmeni');

		echo $this->toXml($this->_mapper->fetchAll($select));
		
	}

}

?>