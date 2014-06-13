<?php

class OborService extends ServiceAbstract {
	
	private $_mapper;
	
	public function __construct(){
		$this->_mapper = new Db_Mapper_StObory();
	}
	
	/**
	 * Vrati vsetky obory
	 * 
	 * @return array
	 */
	public function fetchAll(){			
		$order = $this->_mapper->getTable()->select()->order('Obor');
		return $this->_mapper->getTable()->fetchAll($order)->toArray();
		
		/*
		for ($i = 0; $i < count($data); $i++) {
			
			$skratka = "";
			
			$menoAForma = explode('-',$data[$i]['Obor']);			
			$celemeno = explode(' ',rtrim($menoAForma[0]));
			
			
			
			for ($j = 0; $j < count($celemeno); $j++) {
					
				if($celemeno[$j] != 'a'){
					$skratka .= substr($celemeno[$j],0,1);
				}
			};
			
			$data[$i]['Obor'] = strtoupper($skratka) . (isset($menoAForma[1]) ? ' -' . substr($menoAForma[1],0,5) : null);	
			
		};
		*/			
	}
}

?>