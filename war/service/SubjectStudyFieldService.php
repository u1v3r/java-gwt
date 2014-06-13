<?php

class SubjectStudyFieldService extends ServiceAbstract {
	
	private $_mapper;
	
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_SubjectStudyField();	
	}
	
	/** Priradi predmet oboru
	 * 
	 * @param integer $sid
	 * @param integer $sfid
	 * 
	 * @return boolean
	 */
	public function add($sid,$sfid){
		
		$sid = (int)$sid;
		$sfid = (int)$sfid;
		
		
		if($sid == 0 || $sfid == 0){
			return false;
		}
		
		$model = new Db_Model_SubjectStudyField(array(
			'sid' => $sid,
			'sfid' => $sfid
			)
		);
				
		return ($this->_mapper->save($model,true) > 0) ? true : false;		
	}
}

?>