<?php
/**
 * 
 * @author Radovan Dvorsky
 * @version 20101212
 */
class HarmonogramService extends ServiceAbstract{
	
	const YEAR = 0;
	const MONTH = 1;
	const DAY = 2;
	
	private $_mapper;
	private $_dbTable;
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_Harmonogram();
		$this->_dbTable = new Db_DbTable_Harmonogram();
	}
	
	/**
	 * Odstrani studenta z hramonogramu. Takisto dojde k
	 * odstraneniu vsetkych pridelenych znamok.
	 * 
	 * @param integer $hid
	 * @param integer $IDst
	 */
	public function deleteStudentByHidIDst($hid,$IDst){
		
		try{
				
			$adapter = $this->_mapper->getTable()->getAdapter();
			$adapter->beginTransaction();			
			$adapter->query(
							'DELETE FROM harmonogram_student WHERE hid = ? AND ID_st = ?',
							array((int)$hid,(int)$IDst)
			);	
			$adapter->query(
							'DELETE FROM result WHERE hid = ? AND ID_st = ?',
							array((int)$hid,(int)$IDst)
			);
			
			$adapter->commit();
			
			return true;
		
		} catch (Db_Exception $e){
			$adapter->rollBack();
			throw new Db_Exception($e->getMessage());
			return false;
		}		
	}
	
	/**
	 * Prida studenta studenta do hramonogramu
	 * 
	 * @param integer $hid ID harmonogramu
	 * @param integer $IDst ID studenta
	 * 
	 * @return integer vracia order cislo, ak chyba tak zaporne
	 */
	public function addStudent($hid,$IDst){
		
		$order = $this->getMaxOrder($hid);

		if($order == -1){
			$order = 0;
		}
		else {
			$order++;
		}
		
		$data = array(
			'hid' => (int)$hid,
			'ID_st' => (int)$IDst,
			'ord' => $order
		);		
		
		$mapper = new Db_Mapper_HarmonogramStudent();
		$result = $mapper->getTable()->insert($data);
		
		//vracia pole dvoch vlozenych id hid a ID_st
		return (count($result) > 1) ? $order : -1;
	}
	
	/**
	 * NEFUNGUJE
	 * 
	 * @deprecated NEFUNGUJE
	 */
	public function addGroupToHarmonogram(){
		
		$hid = (int)$this->getParam('hid');
		$students = $this->getParam('students');
		$sql = "INSERT INTO harmonogram_student(hid,ID_st) VALUES ";
		
		$students = explode(",",rtrim($students,','));
		
		
		foreach ($students	as $student) {
			if((int)$student == 0){
				echo 'false';
				return;
			}
			$sql .= "(" . (int)$hid . "," . (int)$student ."),";
		}	
		
		echo $sql = rtrim($sql,",");
		$result = $this->_mapper->getTable()->getAdapter()->query($sql);
				
		echo ($result->rowCount() > 0) ? 'true' : 'false';
		
	}
	
	
	/**
	 * Vytvori novy harmonogram
	 * 
	 * @param string $name
	 * @param string $predseda
	 * @param string $miestopredseda
	 * @param string $tajomnik
	 * @param array $professors
	 * @param string $sfid
	 * @param string $date
	 * @param string $examPlace
	 * 
	 * @return struc id vlozeneho harmonogramu(hid) a id roku(yid)
	 */
	public function add($name,$predseda,$miestopredseda,$tajomnik,$professors,$sfid,$date,$examPlace){
		
		if(empty($name) || empty($predseda) || empty($miestopredseda) || 
			empty($tajomnik) || empty($professors) || empty($sfid) || empty($date) || empty($examPlace)){
			return 0;		
		}
		
		try{
			$adapter = $this->_mapper->getTable()->getAdapter();
			$adapter->beginTransaction();			
			
			$yid = $this->_getYidFromDate($date);			
			$month = $this->_getMonthFromDate($date);
			
			$harmonogramData = array(
				'harmonogram_name' => $name,
				'sfid' => $sfid,
				'date' => $date,
				'exam_place' => $examPlace,
				'yid' => $yid,
				'month' => $month,
			);
			
			$hid = (int)$this->_mapper->getTable()->insert($harmonogramData);			
			
			$stmt = $adapter->prepare('INSERT INTO harmonogram_professor(hid,pid,role) VALUES(?,?,?)');
			foreach($professors as $professor){							
				$stmt->execute(array($hid,$professor,Db_Mapper_Professor::CLEN));
			}
			
			$stmt = $adapter->prepare('INSERT INTO harmonogram_professor(hid,pid,role) VALUES(?,?,?)');
			$stmt->execute(array($hid,$predseda,Db_Mapper_Professor::PREDSEDA));
			$stmt->execute(array($hid,$miestopredseda,Db_Mapper_Professor::MIESTOPREDSEDA));
			$stmt->execute(array($hid,$tajomnik,Db_Mapper_Professor::TAJOMNIK));
						
			$adapter->commit();
			
			return array('hid' => "$hid",'yid' => "$yid",'month' => "$month");
		}catch(Zend_Db_Exception $e){						
			$adapter->rollBack();	
			throw new Db_Exception($e->getMessage());			
			return 0;
		}		
	}
	
	/**
	 * Odstrani cely harmonogram vratene v nom priradenych studentov
	 * 
	 * @param integer $hid
	 * 
	 * @return boolean
	 */
	public function remove($hid){		
				
		$result = $this->_mapper->delete(new Db_Model_Harmonogram(array('hid' => (int)$hid)));
		
		return ($result > 0) ? true : false;
	}
	
	/**
	 * Vráti všetky harmonogramy
	 * 
	 * @param integer|null $yid Ak je zadane tak vyberie len harmonogramy z daneho roku
	 * @param integer|null $month Mesiac pre ktory sa maju harmonogramy stahovat
	 * @return array
	 */
	public function fetchAll($yid = null,$month = null){
		
		$harmonograms = array();
		
		$select = $this->_mapper->getTable()->select(Zend_Db_Table::SELECT_WITH_FROM_PART);
		$select->setIntegrityCheck(false)
		->join('studyfield','studyfield.sfid = harmonogram.sfid',array('studyfield.name as studyfield_name'))	
		->order('harmonogram.date');		
		
		//ak je zadany rok, tak stahuj len harmonogramy pre dany ak. rok
		if($yid != null){
			$select->where('harmonogram.yid = ?',$yid,Zend_Db::INT_TYPE);
		}	
		
		//ak je zadany mesiac, tak vyber len harmonogramy pre zvoleny mesiac
		if($month != null){
			$select->where('harmonogram.month = ?',$month,Zend_Db::INT_TYPE);
		}
		
		
		$harmonograms = $this->_mapper->getTable()->fetchAll($select)->toArray();
				
		
		return $harmonograms;
		
	}
	
	/**
	 * Vrati vsetky harmonogrami podla hid
	 * 
	 * @param integer $hid id harmonogramu
	 * @param boolean $students Vrati aj studentov pridelenych do harmonogramu
	 * @param boolean $professors Vrati aj profesorov pridelenych do harmonogramu
	 * 
	 * @return array|false 
	 */
	public function fetchHarmonogram($hid,$students = false,$professors = false){		
		
		$harmonograms = array();
		$select = $this->_mapper->getTable()->select(Zend_Db_Table::SELECT_WITH_FROM_PART);
		$select->setIntegrityCheck(false);
		
		/*
		 * Prida do vysledku clenov komisie
		 */
		if($professors){
			$select->joinLeft('harmonogram_professor','harmonogram.hid=harmonogram_professor.hid',array('role'))
			->joinLeft('professor','professor.pid=harmonogram_professor.pid');
		}
		
		/*
		 * Prida do vysledku studenov pridelenych do harmonogramu
		 */
		if($students){
			$select->join('harmonogram_student','harmonogram.hid=harmonogram_student.hid')
			->order('harmonogram_student.ord');
		}	

		
		$select->where('harmonogram.hid = ?',$hid)
		->order('harmonogram.harmonogram_name');

		
		$harmonograms = $this->_mapper->getTable()->fetchAll($select)->toArray();
		
		if(count($harmonograms) == 0){
			return array();
		}
		
		/*
		 * NEFUNGUJE A PRAVDEPODOBNE SA TO ANI NEPOUZIVA
		 * ak nebude chybat, tak odstranit
		 *
		if($professors){
			$idOboru = $harmonograms[0]['Id_oboru'];
			
			$oboryMapper = new Db_Mapper_StObory();
			$row = $oboryMapper->getTable()->fetchRow($oboryMapper->getTable()->getAdapter()->quoteInto('St_obory.Id_oboru = ?',$idOboru));
			
			for ($i = 0; $i < count($harmonograms); $i++) {
				$harmonograms[$i]['Obor'] = $row['Obor'];
			}
		}
		*/
		
		if($students){
			/*
			 * Prepojenie so skolskou databazou; Prida mena studentov a zkratku tried
			 */
			$ID_st = array ();
			
			for($i = 0; $i < count ( $harmonograms ); $i ++) {
				$ID_st [$i] = $harmonograms [$i]['ID_st'];
			}
			
			$idStudentov = rtrim ( implode ( ",", $ID_st ), ',' );
			
			$studentDbTable = new Db_DbTable_Student ();
			$adapter = $studentDbTable->getAdapter ();
			$query = $adapter->query ( "SELECT Student.ID_st,Student.Jmeno, Student.Prijmeni,T_Tridy.Zkratka,T_Tridy.ID_tr FROM Student
								 JOIN Student_has_Tridy ON Student_has_Tridy.Student_ID_st=Student.ID_st
								 JOIN T_Tridy ON T_Tridy.ID_tr=Student_has_Tridy.Tridy_ID_tr
								 WHERE Student.ID_st IN($idStudentov)
								 AND T_Tridy.Zkratka LIKE '%3%' OR T_Tridy.id_oboru=12 
								 ORDER BY Student.Prijmeni" );
			//OR T_Tridy.id_oboru=12 pridava studentov DPS, ktori maju odlisne cislovanie tried
			
			$studentsResult = $query->fetchAll ();
				
			
			for($i = 0; $i < count ( $harmonograms ); $i ++) {
				for($j = 0; $j < count ( $studentsResult ); $j ++) {
					if ($harmonograms [$i] ['ID_st'] == $studentsResult [$j] ['ID_st']) {					
						$harmonograms [$i] ['Jmeno'] = $studentsResult [$j] ['Jmeno'];
						$harmonograms [$i] ['Prijmeni'] = $studentsResult [$j] ['Prijmeni'];
						$harmonograms [$i] ['ID_tr'] = $studentsResult [$j] ['ID_tr'];
						$harmonograms [$i] ['Zkratka'] = $studentsResult [$j] ['Zkratka'];
					}
				}
			}
		}
		
		
		return (count($harmonograms) > 0) ? $harmonograms : false;		
	}
	
	/**
	 * Prida profesora do komisie ako normalneho clena
	 * 
	 * @param integer $hid
	 * @param integer $pid
	 * 
	 * @return boolean
	 */
	public function addProfessor($hid,$pid){
		
		$mapper = new Db_Mapper_HarmonogramProfessor();
		
		$id = $mapper->getTable()->insert(array(
			'hid' => $hid, 'pid' => $pid,'role' => Db_Mapper_HarmonogramProfessor::CLEN)
		);
		
		
		return ($id > 0 || is_array($id)) ? true : false;
	}
	
	/**
	 * Odstrani clena z komisie
	 * 
	 * @param integer $hid
	 * @param integer $pid
	 * 
	 * @return boolean
	 */
	public function removeProfessor($hid,$pid){
		$sql = array();		
		$mapper = new Db_Mapper_HarmonogramProfessor();
		$adapter = $mapper->getTable()->getAdapter();
		$sql[] = $adapter->quoteInto('hid = ?',$hid, Zend_Db::INT_TYPE);
		$sql[] = $adapter->quoteInto('pid = ?',$pid, Zend_Db::INT_TYPE);
					
		$rows = $mapper->getTable()->delete($sql);
		
		return ($rows > 0) ? true : false;
	}
	
	/**
	 * Upravi profesora v hramonograme
	 * 
	 * @param integer $hid
	 * @param integer $pid
	 * @param string $role
	 * 
	 * @return boolean
	 */
	public function updateProfesor($hid,$pid,$role){
		
		$sql = array();
		$mapper = new Db_Mapper_HarmonogramProfessor();
		$adapter = $mapper->getTable()->getAdapter();
		$sql[] = $adapter->quoteInto('hid = ?',$hid, Zend_Db::INT_TYPE);
		$sql[] = $adapter->quoteInto('role = ?',$role);
		
		
		$rows = $mapper->getTable()->update(array('pid' => $pid), $sql);		
		if($rows == 0){
			$id = $mapper->getTable()->insert(array(
				'pid' => $pid,'hid' => $hid, 'role' => $role)
			);
		}
		else{
			return true;
		}
				
		return (count($id) > 0) ? true : false;
	}
	
	/**
	 * Zmení meno harmongramu
	 * 
	 * @param integer $hid
	 * @param string $name
	 * 
	 * @return boolean
	 */
	public function updateHarmonogramName($hid,$name){
		
		$sql = $this->_mapper->getTable()->getAdapter()->quoteInto('hid = ?',$hid,Zend_Db::INT_TYPE);
		
		$rows = $this->_mapper->getTable()->update(array('harmonogram_name' => $name),$sql);
		
		return ($rows > 0) ? true : false;
	}
	
	
	/**
	 * Vrati najvyssie cislo order pre harmonogram
	 * 
	 * @param $hid
	 * 
	 * @return integer Najvyssia hodnota order pre dany harmonogram
	 */
	public function getMaxOrder($hid){
		
		$mapper = new Db_Mapper_HarmonogramStudent();
		$select = $mapper->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->from('harmonogram_student',array( 'max' => 'MAX(harmonogram_student.ord)'))
		->where('hid=?',$hid,Zend_Db::INT_TYPE)
		->limit(1);
		
		$result = $mapper->getTable()->fetchAll($select)->toArray();		
		$max = $result[0]['max'];
		
		//ak je to prvy zaznam
		if(strlen($max) == 0){
			return -1;
		}
		
		return $max;		
	}
	
	/**
	 * Vymeni poradie studentov v harmonogramu
	 * 
	 * @param integer $hid
	 * @param integer $upStudent Student ktory ma ist vyssie v harmonograme
	 * @param integer $downStudent Student ktory ma ist nizsie v harmonograme
	 * 
	 * @return struct
	 */
	public function swapStudents($hid,$upStudent,$downStudent){
		
		/*
		 * Pracuje na principe, ze zisti order obidvoch studentov a potom ich medzi
		 * sebou zameni
		 */
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		
		try{
			$adapter->beginTransaction();
			
			//select na zistenie order
			$harmonogramStudentDbTable = new Db_DbTable_HarmonogramStudent();
			$selectUp = $harmonogramStudentDbTable->select();
			$selectUp->where('hid = ?',$hid,Zend_Db::INT_TYPE)
			->where('ID_st = ?',$upStudent,Zend_Db::INT_TYPE)
			->limit(1);
			
			//podmienka pre update
			$upWhere[] = $adapter->quoteInto('hid = ?',$hid,Zend_Db::INT_TYPE);
			$upWhere[] = $adapter->quoteInto('ID_st = ?',$upStudent,Zend_Db::INT_TYPE);
			
			//zisti order prveho studenta
			$upStudentDbOrder = $adapter->fetchRow($selectUp);
			$orderUp = $upStudentDbOrder['ord'];			
			
			//select na zistenie order druheho studenta
			$selectDown = $harmonogramStudentDbTable->select();
			$selectDown->where('hid = ?',$hid,Zend_Db::INT_TYPE)
			->where('ID_st = ?',$downStudent,Zend_Db::INT_TYPE)
			->limit(1);
			
			//update podmienka pre druheho studenta
			$downWhere[] = $adapter->quoteInto('hid = ?',$hid,Zend_Db::INT_TYPE);
			$downWhere[] = $adapter->quoteInto('ID_st = ?',$downStudent,Zend_Db::INT_TYPE);			
			
			//zisti order druheho studenta
			$downStudentOrder = $adapter->fetchRow($selectDown);
			$orderDown = $downStudentOrder['ord'];
			
			//zamenit order navzojom
			$dataUp = array(
				'ord' => $orderDown
			);			
			$adapter->update('harmonogram_student',$dataUp,$upWhere);
			
			$dataDown = array(
				'ord' => $orderUp
			);
			$adapter->update('harmonogram_student',$dataDown,$downWhere);
			
			//ak prebehlo tak commit
			$adapter->commit();
			
			//a vrati hodnoty order pre obidvoch studentov
			return array('up' => $orderUp, 'down' => $orderDown,'success' => "1");
			
		} catch(Zend_Db_Exception $e){
			$adapter->rollBack();
			echo $e->getMessage();
			return array('success' => "0");
		}		
	}
	
	/**
	 * Upravi datum skusky
	 * 
	 * @param integer $hid
	 * @param string $date
	 * 
	 * @return struct array(status,yid,month)
	 */
	public function updateExamDate($hid,$date){
		
		$where = $this->_mapper->getTable()->getAdapter()->quoteInto('hid = ?',$hid,Zend_Db::INT_TYPE);
		$yid = $this->_getYidFromDate($date);
		$month = $this->_getMonthFromDate($date);
		
		$result = $this->_mapper->getTable()->update(array('date' => $date,'yid' => $yid,'month' => $month),$where);
		
		$bool = ($result > 0) ? "true" : "false";
		
		return array('status' => $bool,'yid' => $yid,'month' => $month);	
	}
	
	/**
	 * Nastavi študentovi začiatok skúšky
	 * 
	 * @param integer $hid
	 * @param integer $IDst
	 * @param string $time
	 * 
	 * @return boolean
	 */
	public function updateStudentTime($hid,$IDst,$time){
		
		$where = array();
		$mapper = new Db_Mapper_HarmonogramStudent();
		
		$where[] = $mapper->getTable()->getAdapter()->quoteInto('hid = ?',$hid,Zend_Db::INT_TYPE);
		$where[] = $mapper->getTable()->getAdapter()->quoteInto('ID_st = ?',$IDst,Zend_Db::INT_TYPE);
		
		$result = $mapper->getTable()->update(array('time' => $time),$where);
		
		return ($result > 0) ? true : false;				
	}
	
	
	/**
	 * Upravi miesto skusky
	 * 
	 * @param $hid
	 * @param $place
	 * 
	 * @return boolean
	 */
	public function updateExamPlace($hid,$place){
		
		$sql = $this->_mapper->getTable()->getAdapter()->quoteInto('hid = ?',$hid,Zend_Db::INT_TYPE);
		
		$rows = $this->_mapper->getTable()->update(array('exam_place' => $place),$sql);
		
		return ($rows > 0) ? true : false;
	}
	
	/**
	 * Upravi temu bakalraskej prace
	 * 
	 * @param integer $hid
	 * @param integer $IDst
	 * @param string $bachelorThesis
	 * 
	 * @return boolean
	 */
	public function updateBachelorThesis($hid,$IDst,$bachelorThesis){
		
		$where = array();
		$dbTable = new Db_DbTable_HarmonogramStudent();
		
		$where[] = $dbTable->getAdapter()->quoteInto('hid = ?',$hid,Zend_Db::INT_TYPE);
		$where[] = $dbTable->getAdapter()->quoteInto('ID_st = ?',$IDst,Zend_Db::INT_TYPE);
		
		$rows = $dbTable->update(array('bachelor_thesis' => $bachelorThesis),$where);
		
		return ($rows > 0) ? true : false;
	}
	
	
	/**
	 * Vrati vsetkych clenov komisie
	 * 
	 * @param integer $hid
	 * 
	 * @return array
	 */
	public function fetchProfessors($hid){
		
		if((int)$hid < 0){
			throw new Db_Exception('Invalid input parameter in function fetchProfessors: ' . $hid);
		};
		
		$harmoProfessorTable = new Db_DbTable_HarmonogramProfessor();
		$select = $harmoProfessorTable->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)
		->from('harmonogram_professor','*')
		->join('professor','professor.pid=harmonogram_professor.pid',array('*'))
		->where('harmonogram_professor.hid = ?',$hid,Zend_Db::INT_TYPE)
		->order('harmonogram_professor.role')
		->order('professor.lastname');
		
		return $harmoProfessorTable->fetchAll($select)->toArray();		
	}
	
	private function _getYidFromDate($date){
			
		$dateArray = explode('-',$date);
			
		/*
		 * od januara do augusta je este stary akademicky rok, novy akademicky rok
		 * zacina od septembra
		 */
		//ak je v momentalnom roku (napr. 2011) mesiac od 1-8, tak je este akademicky rok 2010/2011		
		if($dateArray[self::MONTH] >= 1 && $dateArray[self::MONTH] <= 8){
			$year = ($dateArray[self::YEAR] - 1) . '/' . $dateArray[self::YEAR];
		}else {
			//a ak je v momentalnom roku (napr. 2011) mesiac 9-12, tak je uz novy akademicky rok 2011/2012
			$year = $dateArray[self::YEAR] . '/' . ($dateArray[self::YEAR] + 1);
		}
		
		$yearTable = new Db_DbTable_Year();
		$yearRow = $yearTable->fetchRow($yearTable->getAdapter()->quoteInto('year.year_name = ?',$year));
		
		//ak neexistuje treba vytvorit
		if($yearRow == null){
			return $yid = $yearTable->insert(array('year_name' => $year));
		}
		
		return $yearRow->yid;		
	}	
	
	/**
	 * Vrati mesiac z vlozeneho datumu
	 * 
	 * @param $date
	 */
	private function _getMonthFromDate($date){
		
		$explode = explode('-',$date);
		
		//ak je vlozeny datum s vo formate 2011-06-27, treba odstranit z mesiaca nulu
		if($explode[self::MONTH][0] == "0"){
			return $explode[self::MONTH][1];
		}
		
		return $explode[self::MONTH];
	}
	
	/**
	 * Vrati pocet harmonogramov v danom mesiaci vo zvolenom roku
	 * 
	 * @param integer $yid
	 * 
	 * @return struct
	 */
	public function getHarmonogramsCount($yid){
		
		
		$select = $this->_dbTable->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		
		$select->from('harmonogram','count(*) as harmonograms_count')
		->columns('month')
		->setIntegrityCheck(false)	
		->where('harmonogram.yid = ?',$yid,Zend_Db::INT_TYPE)
		->group('month');
		
		$results = $this->_dbTable->fetchAll($select)->toArray();
		$data = array();	
		
		for ($i = 1; $i <= 12 ; $i++) {			
			$data[(int)$i] = (int)0;
		}
		
		foreach ($results as $result){
			$data[(int)$result['month']] = (int)$result['harmonograms_count'];
		}
		
		//print_r($data);die;
		return $data;	
						
	}
	
}
?>