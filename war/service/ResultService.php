<?php
require_once 'Export/library/Doc.php';
require_once 'Export/library/Odf.php';
require_once 'Export/library/Html.php';
require_once 'Export/library/zip/PhpZipProxy.php';

class ResultService extends ServiceAbstract {
	
	const PDF_FORMAT = 0;
	const HTML_FORMAT = 1;
	const ODT_FORMAT = 2;
	const WORD_FORMAT = 3;	
	const EXPORT_PATH = './tmp/results';
	const PDF_MAGIC_NUMBER = 6;//cislo pouzivane na vypocet pozicie textu pod tabulkou
	const SAVE_PATH = '/var/www/localhost/htdocs/kosilkaV3/tmp/';
	//const SAVE_PATH = '/var/www/dvorsky/war/tmp/';
	public static $savePath;
	
	public $_mapper;
	
	private $_pdfBottomHeightStart = 370;
	
	public function __construct(){
		self::$savePath = realpath('./');
		$this->_mapper = new Db_Mapper_Result();		
	}
	
	/**
	 * Vrati vsetky hodnotenia pre zvoleny harmonogram podla studentov
	 * 
	 * @param integer $hid
	 * @param boolean $assocc Vytvorit asociativne pole
	 * @param boolean $withUchazeci Vlozit do vysledku aj udaje z tabulky uchazeci
	 * @param array $rid id vysledkov, ktore sa budu exportovat
	 * @param integer $start
	 * @param integer $length
	 * 
	 * @return array
	 */
	public function fetchResults($hid, $assocc = false, $withUchazeci = false,array $rid = null, $start = null,$length = null){
		
		$studentIDs = array();
		$arrayList = array();
		$harmonogramStudentDbTable = new Db_DbTable_HarmonogramStudent();
		$select = $harmonogramStudentDbTable->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false);
		
		//pouzitie LIMIT dotazu
		if($length == null && $start == null){
			$select->from('harmonogram',array())
			->columns(array('hid','harmonogram_name','sfid','exam_place',new Zend_Db_Expr('DATE_FORMAT(date,"%e.%c.%Y") as date')))
			->join('harmonogram_student','harmonogram_student.hid=harmonogram.hid',array('*'))
			->where('harmonogram.hid = ?',$hid,Zend_Db::INT_TYPE);			
		}
		else {			
			$subSelect = $harmonogramStudentDbTable->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
			$subSelect->where('harmonogram_student.hid = ?',$hid,Zend_Db::INT_TYPE)
			->order('harmonogram_student.ord')
			->limit($length,$start);
			
			$select->from(
				array(
					"harmonogram_student" => 
					new Zend_Db_Expr(
						//"(SELECT harmonogram_student.* FROM harmonogram_student WHERE harmonogram_student.hid=3 ORDER BY harmonogram_student.ord LIMIT 0,2)"
						"($subSelect)"
					)
				)
			)
			->join('harmonogram','harmonogram_student.hid=harmonogram.hid',array('hid','harmonogram_name','sfid','exam_place',new Zend_Db_Expr('DATE_FORMAT(date,"%e.%c.%Y") as date')));
		}
		
		$select->join('year','harmonogram.yid=year.yid',array('*'));			
		
		
		//ak je zadane rid, tak exportuj len zvolene, inak vsetko 
		if($rid != null && count($rid) > 0){
			$select->join('result','result.ID_st=harmonogram_student.ID_st',array(
				'rid','oponent_grade','veduci_grade','overall_grade','obhajoba_grade',
				'question_number','overall_subjects_grade','note'
			))
			->where('result.rid IN (?)',$rid,Zend_Db::INT_TYPE);			
		}
		else {
			$select->joinLeft('result','result.ID_st=harmonogram_student.ID_st',array(
				'rid','oponent_grade','veduci_grade','overall_grade','obhajoba_grade',
				'question_number','overall_subjects_grade','note'
			));
		}

		$select->joinLeft('result_subject','result_subject.rid=result.rid',array('grade'))//znamky z predmetov
		->joinLeft('subject','subject.sid=result_subject.sid',array('*'))//predmety		
		->order('subject.name');
		
		
		$results = $this->_mapper->getTable()->fetchAll($select);		
		
		
		foreach($results as $row){
			$studentIDs[] = $row['ID_st'];
		}

		$uchazeciTable = new Db_DbTable_Uchazeci();
		$studentMapper = new Db_Mapper_Student();
		$students = $studentMapper->fetchStudentsByIDs($studentIDs);//vyberie studentov zo skolskej databazy
		
		/*
		 * Na zaklade ID_st vlozi do vysledku studentov
		 */
		foreach($students as $student){
			$arrayList[$student['ID_st']] = array(
				'ID_st' => $student['ID_st'],
				'Jmeno' => $student['Jmeno'],
				'Prijmeni' => $student['Prijmeni']
			);
			
			/*
			 * Prida udaje(rod cislo,dat narodenia...) z tabulky uchazci
			 * (pouziva sa pri exporte protokolu o SZZ)
			 */
			if($withUchazeci){

				/*
				 * K spajaniu dochadza na zaklade kontextu a loginu, 
				 * !!! ID_st a id_uchazece sa nezhoduju !!!
				 */
				$uchazeciSelect = $uchazeciTable->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
				$uchazeciSelect->from('uchazeci',array(
					'studium_typ',
					new Zend_Db_Expr('DATE_FORMAT(nar_den,"%e.%c.%Y") as nar_den'),
					'rodcislo1','rodcislo2','nar_obec','stat_prislusnost'
					)
				)
				->where('kontext = ?',$student['kontext'])
				->where('Login = ?',$student['Login'])
				->limit(1);
				
				$uchazec = $uchazeciTable->fetchRow($uchazeciSelect);
				if(count($uchazec) == 1){//db musi vratit len jeden riadok
					$uchazec = $uchazec->toArray();
					foreach($uchazec as $key => $value){					
						$arrayList[$student['ID_st']][$key] = $value;	
					}				
				}
				else {//HACK aby bolo mozne exportovat harmonogramy aj pri neexistujucom udaji v tabulke					
					$arrayList[$student['ID_st']]['studium_typ'] = 'neexistuje';
					$arrayList[$student['ID_st']]['nar_den'] = 'neexistuje';
					$arrayList[$student['ID_st']]['rodcislo1'] = 'neexistuje';
					$arrayList[$student['ID_st']]['rodcislo2'] = 'neexistuje';
					$arrayList[$student['ID_st']]['nar_obec'] = 'neexistuje';
					$arrayList[$student['ID_st']]['stat_prislusnost'] = 'neexistuje';
				}
				
			}
		}
	
		foreach($results as $result){
			$arrayList[$result['ID_st']]['rid'] = $result['rid'];
			$arrayList[$result['ID_st']]['harmonogram_name'] = $result['harmonogram_name'];
			$arrayList[$result['ID_st']]['oponent_grade'] = $result['oponent_grade'];
			$arrayList[$result['ID_st']]['veduci_grade'] = $result['veduci_grade'];
			$arrayList[$result['ID_st']]['obhajoba_grade'] = $result['obhajoba_grade'];
			$arrayList[$result['ID_st']]['overall_grade'] = $result['overall_grade'];
			$arrayList[$result['ID_st']]['question_number'] = $result['question_number'];
			$arrayList[$result['ID_st']]['overall_subjects_grade'] = $result['overall_subjects_grade'];
			$arrayList[$result['ID_st']]['ord'] = $result['ord'];
			$arrayList[$result['ID_st']]['note'] = $result['note'];
			$arrayList[$result['ID_st']]['date'] = $result['date'];
			$arrayList[$result['ID_st']]['exam_place'] = $result['exam_place'];
			$arrayList[$result['ID_st']]['akademicky_rok'] = $result['year_name'];
			$arrayList[$result['ID_st']]['bachelor_thesis'] = $result['bachelor_thesis'];
			$arrayList[$result['ID_st']]['results'][] = array(				
				'rid' => $result['rid'],				
				'sid' => $result['sid'],
				'grade' => $result['grade'],
				'short' => $result['short'],
				'name' => $result['name'],
			);			
		}
		
		// z asociativneho pola spravi klasicke pole
		return ($assocc) ? $arrayList : array_values($arrayList);
	}
	
			
	/**
	 * Ulozi hodnotenie oponenta
	 * 
	 * @param integer $IDst
	 * @param string $grade
	 * 
	 * @return int Ak sa ulozilo vrati ulozene id, inak 0
	 */
	public function saveOponentGrade($IDst,$grade){
		
		$IDst = (int)$IDst;
		
		if($IDst == 0){
			throw new Exception('Invalid input');
			return 0;
		}
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		$result = $adapter->query("INSERT INTO result(ID_st,oponent_grade) VALUES($IDst,'$grade')
		ON DUPLICATE KEY UPDATE oponent_grade='$grade'");
				
		return ($result->rowCount() > 0) ? (int)$adapter->lastInsertId() : 0;
		
	}
	
	/**
	 * Ulozi hodnotenie veduceho
	 * 
	 * @param integer $IDst
	 * @param string $grade
	 * 
	 * @return int Ak sa ulozilo vrati ulozene id, inak 0
	 */
	public function saveVeduciGrade($IDst,$grade){
		
		$IDst = (int)$IDst;
		
		if($IDst == 0){
			throw new Exception('Invalid input');
			return 0;
		}
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		$result = $adapter->query("INSERT INTO result(ID_st,veduci_grade) VALUES($IDst,'$grade')
		ON DUPLICATE KEY UPDATE veduci_grade='$grade'");
				
		return ($result->rowCount() > 0) ? (int)$adapter->lastInsertId() : 0;
	}

	/**
	 * Ulozi hodnotenie z obhajoby
	 * 
	 * @param integer $IDst
	 * @param string $grade
	 * 
	 * @return int Ak sa ulozilo vrati ulozene id, inak 0
	 */
	public function saveObhajobaGrade($IDst,$grade){
		
		$IDst = (int)$IDst;
		
		if($IDst == 0){
			throw new Exception('Invalid input');
			return 0;
		}
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		$result = $adapter->query("INSERT INTO result(ID_st,obhajoba_grade) VALUES($IDst,'$grade')
		ON DUPLICATE KEY UPDATE obhajoba_grade='$grade'");
				
		return ($result->rowCount() > 0) ? (int)$adapter->lastInsertId() : 0;
	}
	
	/**
	 * Ulozi celkove hodnotenie
	 * 
	 * @param integer $IDst
	 * @param string $grade
	 * 
	 * @return int Ak sa ulozilo vrati ulozene id, inak 0
	 */
	public function saveOverallGrade($IDst,$grade){
		
		$IDst = (int)$IDst;
		
		if($IDst == 0){
			throw new Exception('Invalid input');
			return 0;
		}
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		$result = $adapter->query("INSERT INTO result(ID_st,overall_grade) VALUES($IDst,'$grade')
		ON DUPLICATE KEY UPDATE overall_grade='$grade'");
		
		
		return ($result->rowCount() > 0) ? (int)$adapter->lastInsertId() : 0;
	}
	
	/**
	 * Ulozi celkove hodnotenie za predmety
	 * 
	 * @param integer $IDst
	 * @param string $grade
	 * 
	 * @return int Ak sa ulozilo vrati ulozene id, inak 0
	 */
	public function saveOberallSubjectsGrade($IDst,$grade){
		$IDst = (int)$IDst;
		
		if($IDst == 0){
			throw new Exception('Invalid input');
			return 0;
		}
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		$result = $adapter->query("INSERT INTO result(ID_st,overall_subjects_grade) VALUES($IDst,'$grade')
		ON DUPLICATE KEY UPDATE overall_subjects_grade='$grade'");
		
		
		return ($result->rowCount() > 0) ? (int)$adapter->lastInsertId() : 0;
	}
	
	/**
	 * Ulozi hodnotenie z predmetu do databazy
	 * 
	 * @param integer $IDst
	 * @param integer $rid
	 * @param integer $sid
	 * @param string $grade
	 * 
	 * @return integer Ak sa ulozilo vrati ulozene id, inak 0
	 */
	public function saveSubjectGrade($IDst,$rid,$sid,$grade){
		
		$IDst = (int)$IDst;
		$rid = (int)$rid;
		$sid = (int)$sid;
					
		if($IDst == 0 || $sid == 0){
			throw new Exception('Invalid input');
			return 0;
		}
				
		/*
		 * Ak nie je nastavene $rid znamena to, ze student este nebol pridany do tabulky result
		 */
		if($rid == 0 || empty($rid)){
			try{
				$rid = $this->_mapper->getTable()->insert(array('ID_st' => $IDst));
			} catch(Db_Exception $e){
				/*
				 * Ak sa ihned po vlozeni zaznamu do tabulky result vlozi aj zaznam do result_subject
				 */
				$res = $this->_mapper->fetchAll($this->_mapper->getTable()->select()->where('ID_st = ?',$IDst,Zend_Db::INT_TYPE));
				$rid = $res[0]->rid;
			}
		}
		
		//ak je prazdne vloz NULL
		empty($grade) ? $grade = new Zend_Db_Expr('NULL') : $grade = "'$grade'";
		
		$adapter = $this->_mapper->getTable()->getAdapter();
		$result = $adapter->query("INSERT INTO result_subject(rid,sid,grade) 
			VALUES($rid,$sid,$grade)
			ON DUPLICATE KEY UPDATE grade=$grade");
		
			
		return ($result->rowCount() > 0) ? (int)$rid : 0;
	}
	
	
	/**
	 * Ulozi cislo otazky do databazy
	 * 
	 * @param integer $IDst
	 * @param integer $questionNubemr Ak je hodnota zaporna, vlozi do db hodnotu NULL
	 * 
	 * @return string Ak sa podarilo ulozit tak rid, inak -1
	 */
	public function setQuestionNumber($IDst,$questionNumber){
		
		$IDst = (int)$IDst;
				
		if($IDst == 0){
			return "-1";
		}
		
		if(empty($questionNumber)) $questionNumber = NULL;
		
		$adapter = $this->_mapper->getTable()->getAdapter();
				
		$result = $adapter->query('INSERT INTO result(ID_st,question_number) VALUES(?,?)
		ON DUPLICATE KEY UPDATE question_number=?',array($IDst,$questionNumber,$questionNumber));
		
		$lastId = $adapter->lastInsertId();
		
		return ($lastId > 0) ? $lastId : "-1";		
	}
	
	/**
	 * Exportuje vyslednu tabulku z harmonogramu
	 * 
	 * @param integer $hid
	 * @param integer $sfid
	 * @param integer $format Format exportu HTML alebo PDF
	 * @param array $rid 
	 * 
	 * @return string
	 */
	public function exportTable($hid,$sfid,$format,$rid){
			
		switch ($format) {
			case self::PDF_FORMAT:
				$result = $this->_exportTableToPdf($hid,$sfid,$rid);
				break;
			case self::HTML_FORMAT:
				$result = $this->_exportTableToHtml($hid,$sfid,$rid);
				break;
			default:
				$result = $this->_exportTableToPdf($hid,$sfid,$rid);
			break;
		}
	
		return $result;
	}
	
	private function _exportTableToHtml($hid,$sfid,$rid,$onlyHtmlContent = false){
			
			$content = "";
			$subjectService = new SubjectService();
			$studyFieldTable = new Db_DbTable_StudyField();
			$harmonogramService = new HarmonogramService();
			$gradeWidth = 70;//
			$subjectWidth = 50;
			$rows = 0;	
			$i = 1;		
			$members = array();//neobsahuje predsedu,podpredsedu a tajomnika
			$membersAll = array();//obsahuje vsetkych profesorov
			
			$harmonograms = $this->fetchResults($hid,false,false,$rid);
			$subjects = $subjectService->fetchSubjectsByStudyFieldId($sfid);
			$studyFieldName = $studyFieldTable->fetchRow(
				$studyFieldTable->getAdapter()->quoteInto('sfid = ?',$sfid,Zend_Db::INT_TYPE)
			)->name;
			$commissionMembers = $harmonogramService->fetchProfessors($hid);
						
			/*
			 * Rozdriedenie clenov komisie podla ulohy
			 */
			foreach ($commissionMembers as $member){
				
				//ak ma titul za menom, treba pridat ciarku za priezvisko
				$lastname = (strlen($member['title_behind']) > 0) ? $member['lastname'] . ',' : $member['lastname'];
				
				$name = $member['title_before'] . ' ' . $member['firstname'] . ' ' . 
						$lastname . ' ' . $member['title_behind'];
						
				switch ($member['role']){
					case Db_Mapper_Professor::PREDSEDA : $chairman = $name;
						break;
					case Db_Mapper_Professor::MIESTOPREDSEDA : $vicechairman = $name;
						break;
					case Db_Mapper_Professor::TAJOMNIK : $secretary = $name;
						break;
					case Db_Mapper_Professor::CLEN : $members[] = $name;
						break;
				}	
				$membersAll[] = $name;			 	
			}
			
			
			$filename = md5($harmonograms[0]['harmonogram_name']) . '.html';
			$abs_path = realpath(self::EXPORT_PATH);
			
			$handle = fopen($abs_path . '/' . $filename,'w');
			
			$content .= '<html>
							<head>
							<meta http-equiv="Content-Type" content="text/html;charset=utf-8" />
							<title>' . $harmonograms[0]['harmonogram_name'] . '</title>
							<style type="text/css">								
								table, body {color: black;}
								table.students td{border: 1px solid black;padding: 5px;}
								table.students td.name{text-align:left;min-width:150px;}
								table.students td.grade{width: 70px;}
								table.students td.grade,th.grade{text-align:center;}
								table.students th{border: 1px solid black;text-align:center;padding: 5px;font-weight:bold;}
								.left{text-align:left;}	
								table.professors td{padding: 8px}
								td.bold{font-weight:bold;}			
							</style>
							
							</head>
							
							<body>';
			
			$content .= '<h3>Evidence záverečné zkoušky</h3>';
			$content .= $studyFieldName . ' ' . $harmonograms[0]['date'] . '<br/>';
			
			$content .= '
			<table style="border: 1px solid black;border-collapse:collapse;" class="students">
				<tr>
					<th></th>
					<th></th>
					<th colspan="3">OBHAJOBA</th>
					<th colspan="' . (count($subjects) + 2) . '">ODBORNÝ BLOK</th>
					<th rowspan="2" class="grade">Celkové hodnocení státní zkoušky</th>
				</tr>
			
				<tr>
					<th>P.č.</th>
					<th>Příjmení a jméno</th>
					<th class="grade">Hodnocení vedoucího</th>
					<th class="grade">Hodnocení oponenta</th>
					<th class="grade">Celkové hodnocení</th>
					<th class="grade">Odborný blok č. otázky</th>';
					foreach ($subjects as $subject) {
						$content .= '<th>' . $subject['short'] .'</th>';
					}		
			$content .= '<th class="grade">Celkové hodnocení</th>
			   </tr>';
		
			foreach ( $harmonograms as $harmonogram ) {
				$content .= '<tr>';
				$resultsArray = array ();
				for($j = 0; $j < count ( $harmonogram ['results'] ); $j ++) {
					$resultsArray [$harmonogram ['results'] [$j] ['sid']] = $harmonogram ['results'] [$j] ['grade'];
				}
				
				
				$content .= '<td class="left">' . $i++ .'.</td>
							<td class="name">'. $harmonogram['Prijmeni'] . ' ' . $harmonogram['Jmeno'] . '</td>
							<td class="grade">'. $harmonogram['veduci_grade'] . '</td>
							<td class="grade">'. $harmonogram['oponent_grade'] . '</td>
							<td class="grade bold">'. $harmonogram['obhajoba_grade'] . '</td>
							<td class="grade">'. $harmonogram['question_number'] . '</td>';
				
							$j = 0;
							foreach ($subjects as $subject) {				
								if(strlen($resultsArray[$subject['sid']]['grade']) == 0){
										//NEKONTROLUJE PREDMETY
										//throw new ServiceException("Študent " . $harmonogram['Prijmeni'] . ' ' . $harmonogram['Jmeno'] . 
										//" nemá pridelené známky zo všetkých predmetov.");
										//return;
								}
															
								if(isset($resultsArray[$subject['sid']])){									
									$content .= '<td class="grade">' . $resultsArray[$subject['sid']]['grade'] . '</td>';
								}
								else {			
									$content .= '<td></td>';
								}
								$j++;
							}
							
				$content .= '<td class="grade bold">'. $harmonogram['overall_subjects_grade'] . '</td>
							<td class="grade bold">'. $harmonogram['overall_grade'] . '</td>';

				$content .= '</tr>';
			}		
			$content .= '</table>';
			
			$content .= '<br/>';
			
			$dotline = '......................................................';
			
			$content .= '<table class="professors">';
			$content .= '<tr><td colspan="5"><strong>Komise:</strong></td></tr>';
			$content .= '<tr><td><strong>Předseda:</strong><br/>' . $chairman . '</td><td>' . $dotline . '</td></tr>';
			$content .= '<tr><td><strong>Místopředseda:</strong><br/>' . $vicechairman . '</td><td>' . $dotline . '</td></tr>';			
			$content .= '</table>';
			
			$content .= '<table class="professors"><tr><td colspan="2"><strong>Členové:</strong></td></tr>';
			
						
			for ($i = 0; $i < count($members); $i++) {
				
				//kazdu druhu iteraciu ukonci riadok
				if($i % 2 == 0){
					$content .= '<tr><td>' . $members[$i] . '</td><td>' . $dotline . '</td>';//zaciatok riadku
				}
				else {
					$content .= '<td>' . $members[$i] . '</td><td>' . $dotline . '</td></tr>';//koniec riadku
				}
			}
			
			$content .= '</tr></table>';//pre istotu ukonci riadok, ak je posledny neparny mohol by zostat neukonceny
			
			
			$content .= '<table class="professors">';			
			$content .= '<tr><td><strong>Tejemík:</strong><br/>' . $secretary . '</td><td>' . $dotline . '</td></tr>';
			$content .= '<tr><td><strong>Rektor:</strong><br/>h. prof. Ing. Oldřich Kratochvíl, Ph.D.,Dr.h.c., MBA</td><td>' . $dotline . '</td></tr>';			
			$content .= '</table>';
			
			
			$content .= '</body></html>';
			
			fwrite($handle,$content,strlen($content));
			fclose($handle);
			
			return ($onlyHtmlContent == true) ? $content : $filename;
	}
	
	private function _exportTableToPdf($hid,$sfid,$rid){
			
			require_once 'mpdf.php';		
			
			$htmlFile = $this->_exportTableToHtml($hid,$sfid,$rid);//najskor exportuj do HTML			
			$filename = substr($htmlFile,0,strpos($htmlFile,'.')) . '.pdf';//vytvor nazov suboru s koncovkou pdf
			
			$htmlH = fopen(realpath(self::EXPORT_PATH . '/' . $htmlFile),'r');
			$htmlContent = fread($htmlH,filesize(self::EXPORT_PATH . '/' . $htmlFile));
			fclose($htmlH);
			
			$mpdf = new mPDF('utf-8','A4-L');//A4 na sirku
			
			$mpdf->WriteHTML($htmlContent);
						
			/*
			 * Zapis do noveho suboru
			 * 
			 */
			ob_start();
			$mpdf->Output();
			$content = ob_get_clean();
			
			$h = fopen(realpath(self::EXPORT_PATH) . '/' . $filename,'w+');
			fwrite($h,$content);
			fclose($h);		
			
			return $filename;
		
	}
	
	/**
	 * Vygeneruje protokol pre kazdeho studenta a ulozi
	 * do zip archivu
	 * 
	 * @param integer $hid
	 * @param integer $sfid
	 * @param integer $format Format exportu
	 * @param array $rid
	 * 
	 * @return string
	 */
	public function exportProtocol($hid,$sfid,$format,$rid){
				
		$files = array();//obsahuje mena vsetky vytvorenych suborov
			
		/*
		 * Známky a hodnotenia
		 */
		$results = $this->fetchResults($hid,false,true,$rid);
			
		/*
		 * Členovia komisie
		 * TODO: Zmenit na volanie sluzby harmonogram.fetchProfessors
		 */
		$harmonogramTable = new Db_DbTable_Harmonogram();			
		$select = $harmonogramTable->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)			
		->join('harmonogram_professor','harmonogram_professor.hid=harmonogram.hid',array('role'))
		->join('professor','professor.pid=harmonogram_professor.pid',array('*'))
		->where('hid = ?',$hid,Zend_Db::INT_TYPE)
		->order('lastname');
		$commissioMembers = $harmonogramTable->fetchAll($select)->toArray();
		$members = array();//neobsahuje predsedu,podpredsedu a tajomnika
		$membersAll = array();//obsahuje vsetkych profesorov
				
		/*
		 * Nazov studijneho oboru
		 */
		$studyFieldTable = new Db_DbTable_StudyField();			
		$studyFieldRow = $studyFieldTable->find($sfid)->current();
		$studyField = $studyFieldRow['name'];
				
		/*
		 * Rozdriedenie clenov komisie podla ulohy
		 */
		foreach ($commissioMembers as $member){
			
			//ak ma titul za menom, treba pridat ciarku za priezvisko
			$lastname = (strlen($member['title_behind']) > 0) ? $member['lastname'] . ',' : $member['lastname'];
			
			$name = $member['title_before'] . ' ' . $member['firstname'] . ' ' . 
					$lastname . ' ' . $member['title_behind'];
					
			switch ($member['role']){
				case Db_Mapper_Professor::PREDSEDA : $chairman = $name;
					break;
				case Db_Mapper_Professor::MIESTOPREDSEDA : $vicechairman = $name;
					break;
				case Db_Mapper_Professor::TAJOMNIK : $secretary = $name;
					break;
				case Db_Mapper_Professor::CLEN : $members[] = $name;
					break;
			}	
			$membersAll[] = $name;			 	
		}
			
		foreach($results as $result){
			
			$studentName = $result['Jmeno'] . ' ' . $result['Prijmeni'];
			
			if(empty($result['bachelor_thesis'])){
				throw new Exception('Študent ' . $studentName . ' nemá pridelenú tému bakalráskej práce.');
				return;
			}
			
			switch ($format) {
				case self::ODT_FORMAT: 
					$document = new Odf('protokol-oo.odt');
					$fileType = '.odt';				
					break;
				case self::WORD_FORMAT: 
					$document = new Doc('protokol-word.doc');
					$fileType = '.doc';				
					break;
				case self::HTML_FORMAT: 
					$document = new Doc('protokol-html.html');
					$fileType = '.html';				
					break;
				default:
					$document = new Odf('protokol-oo.odt');
					$fileType = '.odt';
				break;
			}
						
			$filename = $this->_removeAccents($result['Prijmeni'] . '_' . $result['Jmeno'] . $fileType);
			$files[] = $filename;
							
			
			/*
			 * Cast s osobnymi udajmi studenta
			 */
			$document->setVars('studyfield_name', $studyField);
			$document->setVars('year', $result['akademicky_rok']);
			$document->setVars('student_name', $studentName);
			$document->setVars('birth_date', $result['nar_den']);
			$document->setVars('identity_number', $result['rodcislo1'] . '/' . $result['rodcislo2']);
			$document->setVars('exam_date', $result['date']);
			$document->setVars('birth_place', $result['nar_obec']);
			$document->setVars('nationality', $result['stat_prislusnost']);
			$document->setVars('study_form', $result['studium_typ']);
			$document->setVars('chairman', $chairman);
			$document->setVars('vice_chairman', $vicechairman);			
				
			/*
			 * Vypis mien clenov komisie
			 */
			$commissionMembers = $document->setSegment('commission_members');
			$commissionMembers2 = $document->setSegment('commission_members_2');
			foreach($members as $memberName) {					
				$commissionMembers->setVars('member',$memberName);
				$commissionMembers2->setVars('member',$memberName);
				$commissionMembers->merge();
				$commissionMembers2->merge();
			}
			$document->mergeSegment($commissionMembers);
			$document->mergeSegment($commissionMembers2);
			
			$document->setVars('secretary', $secretary);
			$document->setVars('obhajoba_grade', $this->_makeGrade($result['obhajoba_grade']));
			$document->setVars('overall_subjects_grade', $this->_makeGrade($result['overall_subjects_grade']));
			
			/*
			 * Vytvorenie podpisov oblasti s menami profesorov
			 */
			$signatures = $document->setSegment('signatures');
			$signatures2 = $document->setSegment('signatures_2');
			foreach($members as $professor) {
				$signatures->setVars('professor_name',$professor);
				$signatures->merge();
				$signatures2->setVars('professor_name',$professor);
				$signatures2->merge();
			}
			$document->mergeSegment($signatures);
			$document->mergeSegment($signatures2);
			
			$document->setVars('exam_place',$result['exam_place']);
			$document->setVars('bc_theme',$result['bachelor_thesis']);
			$document->setVars('veduci_grade',$this->_makeGrade($result['veduci_grade']));
			$document->setVars('oponent_grade',$this->_makeGrade($result['oponent_grade']));
			$document->setVars('obhajoba_grade',$this->_makeGrade($result['obhajoba_grade']));
			$document->setVars('question_number',$result['question_number']);
			
			switch ($result['overall_grade']) {
				case 'P':
					$document->setVars('overall_result','prospěl(a)');				
				break;
				case 'PV':
					$document->setVars('overall_result','prospěl(a) s vyznamenáním');				
				break;
				case 'N':
					$document->setVars('overall_result','neprospěl(a)');				
				break;
				default:
					throw new Exception('Value ' . $result["overall_result"] . ' for overall result doesn\'t exist');
				break;
			}
			
			
			
			if(empty($result['question_number'])){
				throw new DocumentException('Nepridelili ste číslo otázky niektorému študentovi');
			}
			/*
			 * Vygenerovanie otazok na zaklade cisla, ktore si student vytiahol
			 */
			$questionsData = $this->_fetchQuestions($sfid,$result['question_number']);				
			$questions = $document->setSegment('questions');				
			$i = 1;
			foreach($questionsData as $question){
				$questions->setVars('pos',$i++ . ')');
				$questions->setVars('subject_shortname',$question['short']);
				$questions->setVars('question',$question['text']);
				$questions->merge();
			}
			$document->mergeSegment($questions);
			
			/*
			if(count($questionsData) != count($result['results'])){
				throw new Exception('Niektorý z predmetov neobsahuje otázku so zvoleným číslom');
			}*/
			
			/*
			 * Vypis vysledkov z jednotlivych premetov vratane jednotlivych skratiek
			 * predmetov a ich celych mien
			 */
			$subjectsLegend = $document->setSegment('subjects_legend');
			$questionsResult = $document->setSegment('questions_result');
			$subjectsCount = count($result['results']);//pouziva sa pri urceni posledneho predmetu		
			$j = 0;
						
			foreach ($result['results'] as $subject){				
				$questionsResult->setVars('pos',++$j . ')');
				$questionsResult->setVars('subject_shortname',$subject['short']);
				
				$questionsResult->setVars('subject_result',$this->_makeGrade($subject['grade']));
				$questionsResult->merge();
				
				//ak je posledny predmet, tak nedavaj ciarku
				$legend = $subject['short'] . ': ' . $subject['name'] . (($j == $subjectsCount) ? '' : ', ');				
				$subjectsLegend->setVars('subject_legend',$legend);
				$subjectsLegend->merge();
			}
						
			$document->mergeSegment($subjectsLegend);
			$document->mergeSegment($questionsResult);
			
			// Ulozi protokol studenta do tmp
			$document->saveToDisk( self::SAVE_PATH . $filename);				
			
		}		
		 
		//Ak je html tak neukladaj do zipu
		if($format == self::HTML_FORMAT){
			if(count($results) > 1){
				throw new DocumentException("Pri exporte do HTML môže byť exportovaný len jeden študent");
			}
			//HACK na zachovanie kompatibility s exportom do archivu
			copy(self::SAVE_PATH . $filename,realpath(self::EXPORT_PATH) . '/' . $filename);
			unlink(self::SAVE_PATH . $filename);			
			return $filename;		
		}
		else {
			// Prida vsetky vytvorene protokoly do archivu a nesledne vymaze
			$archiveName = uniqid() . '.zip';
			if(!$this->_saveToArchive($files,$archiveName)){
				throw new DocumentException('Nastala chyba pri ukladaní do archívu');
			}
		}
				
		return $archiveName;
			
	}
	
	/**
	 * Vlozi prazdny riadok bez hodnoteni do tabulky
	 * 
	 * @param integer $IDst
	 * @param integer $hid
	 * 
	 * @return integer
	 */
	public function insertRow($IDst,$hid){
		
		$rid = $this->_mapper->getTable()->insert(array('ID_st' => $IDst,'hid' => $hid));
		
		return (int)$rid;		
	}
	
	/**
	 * Vrati sadu otazok pre zvoleny obor na zaklade zvoleneho
	 * cisla otazky
	 * 
	 * @param int $sfid
	 * @param int $questionNumber
	 * 
	 * @return array
	 */
	private function _fetchQuestions($sfid,$questionNumber){
		
		//cislo otazky pri vybere treba znizit o 1 - cisla otazok sa v tabulke question cisluju od 0
		$questionNumber = $questionNumber - 1;
				
		$studyFieldTable = new Db_DbTable_StudyField();
		$select = $studyFieldTable->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->setIntegrityCheck(false)
		->from('studyfield','*')
		->join('subject_studyfield','subject_studyfield.sfid=studyfield.sfid',array())
		->join('subject','subject.sid=subject_studyfield.sid',array('*'))
		->join('question','question.sid=subject.sid',array('*'))
		->order('subject.name')
		->where('question.qn = ?',$questionNumber,Zend_Db::INT_TYPE)
		->where('studyfield.sfid = ?',$sfid,Zend_Db::INT_TYPE);
		
		
		return $studyFieldTable->fetchAll($select)->toArray();		
	}
	
	
	private function _saveToArchive(array $files,$filename){
		
		$archive = new PhpZipProxy();
		$archive->open(self::SAVE_PATH . '/results/' . $filename);
		
		foreach ($files as $file){
			$archive->addFile(self::SAVE_PATH . $file,$file);			
		}
		$return = $archive->close();
		
		//odsranenie docasnych suborov
		foreach($files as $file){
			unlink(self::SAVE_PATH . $file);
		}
		
		return $return;
	}
	
	/**
	 * Odstrani diakritiku
	 * 
	 * @param string $str
	 * 
	 * @return string
	 */
	private function _removeAccents($str){  

		$charTable = Array(
			'ä'=>'a','Ä'=>'A','á'=>'a','Á'=>'A','à'=>'a','À'=>'A','ã'=>'a',
			'Ã'=>'A','â'=>'a','Â'=>'A','č'=>'c','Č'=>'C','ć'=>'c','Ć'=>'C',
			'ď'=>'d','Ď'=>'D','ě'=>'e','Ě'=>'E','é'=>'e','É'=>'E','ë'=>'e',
			'Ë'=>'E','è'=>'e','È'=>'E','ê'=>'e','Ê'=>'E','í'=>'i','Í'=>'I',
			'ï'=>'i','Ï'=>'I','ì'=>'i','Ì'=>'I','î'=>'i','Î'=>'I','ľ'=>'l',
			'Ľ'=>'L','ĺ'=>'l','Ĺ'=>'L','ń'=>'n','Ń'=>'N','ň'=>'n','Ň'=>'N',
			'ñ'=>'n','Ñ'=>'N','ó'=>'o','Ó'=>'O','ö'=>'o','Ö'=>'O','ô'=>'o',
			'Ô'=>'O','ò'=>'o','Ò'=>'O','õ'=>'o','Õ'=>'O','ő'=>'o','Ő'=>'O',
			'ř'=>'r','Ř'=>'R','ŕ'=>'r','Ŕ'=>'R','š'=>'s','Š'=>'S','ś'=>'s',
			'Ś'=>'S','ť'=>'t','Ť'=>'T','ú'=>'u','Ú'=>'U','ů'=>'u','Ů'=>'U',
			'ü'=>'u','Ü'=>'U','ù'=>'u','Ù'=>'U','ũ'=>'u','Ũ'=>'U','û'=>'u',
			'Û'=>'U','ý'=>'y','Ý'=>'Y','ž'=>'z','Ž'=>'Z','ź'=>'z','Ź'=>'Z'
		);
		
		return strtr($str, $charTable);
	}
	
	
	private function _makeGrade($grade){
		
		if(empty($grade)){
			throw new ServiceException("Zabudli ste zadať niektorú známku");
		}
		
		switch ($grade) {
			case 'A':
			$return = 'excelentní ' . $grade;
			break;
			case 'B':
			$return = 'výborný ' . $grade;
			break;
			case 'C':
			$return = 'velmi dobrý ' . $grade;
			break;
			case 'D':
			$return = 'dobrý ' . $grade;
			break;
			case 'E':
			$return = 'dostatečný ' . $grade;
			break;
			case 'F':
			$return = 'nevyhovující ' . $grade;
			break;
			default:
				throw new ServiceException('Grade ' . $grade . ' not exists');
			break;		
		}
		
		
		return $return;		
	}
}

?>