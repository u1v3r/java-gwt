<?php
/**
 * @author Radovan Dvorsky
 * @version 20100219
 */
//require_once ('application/default/models/Db/Mapper/MapperAbstract.php');

class Db_Mapper_Professor extends Db_Mapper_Abstract {
	
	const ROLE_NOT_SET = '0';
	const PREDSEDA = '1';
	const MIESTOPREDSEDA = '2';
	const TAJOMNIK = '3';
	const CLEN = '4';
	
	/**
	 * Ulozi alebo upravy zaznam v tabulke professor (podla toho ci je zadane pid)
	 * 
	 * @param Db_Model_Professor $professor
	 *
	public function save(Db_Model_Professor $professor){				
		
		$data = array(
			'firstname' => $professor->getFirstName(),
			'lastname' => $professor->getLastName(),
			'title_before' => $professor->getTitleBefore(),
			'title_behind' => $professor->getTitleBehind()
		);
		
		if(($pid = $professor->getPid()) === null){
			$this->getTable()->insert($data);
		}
		else {
			$this->getTable()->update($data,array('id = ?',$pid));
		}
		*/
		/*
		if(isset($data[0])){
			$info = $this->info('cols');
			$dataKeys = array_keys($data[0]);
			$infoValues = array_values($info);
			$diff = array_diff($infoValues,$dataKeys);		
		
			if($diff[0] == $this->_primary[1]){
				$adapter = $this->getAdapter();
				$adapter->beginTransaction();
				try{
					for($i = 0; $i < count($data); $i++){
						$adapter->query('INSERT INTO professor(title_before,firstname,lastname,title_behind)
										 VALUES(?,?,?,?)',array(
											$data[$i]['title_before'],
											$data[$i]['firstname'],
											$data[$i]['lastname'],
											$data[$i]['title_behind']
										)
						);
					}
					
					$adapter->commit();					
						
				} catch (Zend_Db_Exception $e){
					
					$adapter->rollBack();
					throw new Db_Exception($e->getMessage());
				}
			}
		}
		else {
			parent::insert($data);
		}
		*/	
	/*	
	}
	*/	
	
}

?>