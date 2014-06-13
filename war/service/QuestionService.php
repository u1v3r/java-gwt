<?php


class QuestionService extends ServiceAbstract {
	
	private $_mapper;
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_Question();
	}
	
	/**
	 * Vrati vsetky otazky zo zvoleneho predmetu
	 * 
	 * @param $sid
	 * 
	 * @return array
	 */
	public function fetchQuestions($sid){
			
		$sid = (int)$sid;
		
		if($sid <= 0){
			return array();
		}
		
		$select = $this->_mapper->getTable()->select(Zend_Db_Table::SELECT_WITH_FROM_PART);
		$select->setIntegrityCheck(false)
		->join('subject','subject.sid=question.sid',array('subject.name','subject.short'))
		->where('subject.sid = ?',$sid,Zend_Db::INT_TYPE)
		->order('question.qn'); 
		
		return $this->_mapper->getTable()->fetchAll($select)->toArray();		
	}
	
	/**
	 * Upravi otazku
	 * 
	 * @param integer $qid
	 * @param string $text
	 * 
	 * @return boolean
	 */
	public function update($qid,$text){
		
		if((int)$qid == 0 || empty($text)){
			return false;
		}	
		
		$data = array(
			'text' => $text 
		);
		
		$where = $this->_mapper->getTable()->getAdapter()->quoteInto('qid = ?',$qid,Zend_Db::INT_TYPE);
		 
		
		return ($this->_mapper->getTable()->update($data,$where) > 0) ? true : false;		
	}
	
	/**
	 * Odstrani otazku
	 * 
	 * @param integer $qid
	 * @return boolean
	 */
	public function delete($qid){
		
		$where = $this->_mapper->getTable()->getAdapter()->quoteInto('qid = ?',$qid,Zend_Db::INT_TYPE);
				
		return ($this->_mapper->getTable()->delete($where) > 0) ? true : false;		
	}
	
	/**
	 * Vlozi novu otazku do db
	 *  
	 * @param integer $sid
	 * @param integer $questionNumber
	 * @param integer $text
	 *  
	 * @return integer id otazky | -1 - ak nastane chyba 
	 */
	public function add($sid,$questionNumber,$text){		
		
		$data = array(
			'sid' => $sid,
			'qn' => $questionNumber,
			'text' => $text
		);
			
		$qid = $this->_mapper->getTable()->insert($data);
			
		return ($qid > 0) ? (int)$qid : -1;	
		
	}
	
	/**
	 * Zmeni poradove cislo otazky
	 * 
	 * @param integer $qid
	 * @param integer $questionNumber
	 * 
	 * @return boolean
	 */
	public function updateQuestionNumber($qid,$questionNumber){
		
		$questionNumber--;//treba odcitat -1, v db sa cisluje od 0
		
		$where = $this->_mapper->getTable()->getAdapter()->quoteInto('qid = ?',$qid,Zend_Db::INT_TYPE);
		
		$rows = $this->_mapper->getTable()->update(array('qn' => $questionNumber),$where);
		
		return ($rows > 0) ? true : false;		
	}
	
	
	/**
	 * Vrati maximalny pocet otazok zo vsetkych predmetov
	 * 
	 * @return integer
	 */
	public function getMaxQuestionsCount(){
		
		$query = "SELECT MAX( c ) as maxCount
					FROM (					
					SELECT COUNT( question.qid ) AS c
					FROM subject
					JOIN question ON subject.sid = question.sid
					GROUP BY subject.name
					) AS t1";
		
		$result = $this->_mapper->getTable()->getAdapter()->query($query)->fetch();
		
		return (int)$result['maxCount'];		
	}
}

?>