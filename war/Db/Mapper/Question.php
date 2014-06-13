<?php
/**
 * @author Radovan Dvorsky
 * @version 20100219
 */
//require_once ('application/default/models/Db/Mapper/MapperAbstract.php');

class Db_Mapper_Question extends Db_Mapper_Abstract {
	
	/**
	 * Vrati pocet otazok k danemu predmetu
	 * 
	 * @param int $subjectId
	 */
	public function getQuestionsCount($subjectId){
		
		$select = $this->select();
		$select->from('question',array('COUNT(qid) as count'))
		->where('sid = ?',$subjectId);			
		$questions = $this->fetchAll($select);
			
		return $questions->current()->count;
	}
		
	public function orderQuestions($subjectId){
		
		$adapter = $this->getAdapter();
		$i = 0;
		
		try{
			
			$adapter->beginTransaction();
			$select = $this->select();
			
			$questionsRowset = $this->fetchAll(
				$select->where(
					$adapter->quoteInto('sid = ?',$subjectId,Zend_Db::INT_TYPE)
				)
			);
			
			foreach($questionsRowset as $questionsRow){
				$this->update(array(
					'qn' => $i
					),
					$adapter->quoteInto('qid = ?',$questionsRow->qid)					
				);				
				$i++;
			}
			
			$cache = Zend_Registry::get('cache');
			$cache->remove('questions'. $subjectId);
			
			$adapter->commit();
			
		} catch (Zend_Exception $e){
			$adapter->rollBack();
			throw new Db_Exception($e->getMessage());
		}		
	}
	
}

?>