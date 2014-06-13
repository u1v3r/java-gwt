<?php
/**
 * 
 * @author Radovan Dvorsky
 * @version 20100513
 *
 */
abstract class Db_Mapper_Abstract{
	
	const DBTABLE_PREFIX = 'Db_DbTable_';
	const MAPPER_PREFIX = 'Db_Mapper_';
	const MODEL_PREFIX = 'Db_Model_';
	const MODEL = 'Model';
	const DBTABLE = 'DbTable';
	
	/**
	 * Odkaz na instanciu triedy 
	 * 
	 * @var Zend_Db_Table_Abstract
	 */
	protected $_dbTable;	
	
	/*
	 * Nastavi atribut<code>$_DbTable</code>
	 */
	protected function setTable(Zend_Db_Table_Abstract $dbTable){
		
		if(!$dbTable instanceof Zend_Db_Table_Abstract){
			throw new Exception($dbTable . ' is not instance of Zend_Db_Table_Abstract');
		}
		
		$this->_dbTable = $dbTable;
		
		return $this;		
	}
	
	/**
	 * Vrati instanciu Zend_Db_Table_Abstract (resp. Db_DbTable_*).
	 * 
	 * Napr. ak na triede Db_Mapper_Professor zavolame medotu <code>$professor->getTable()</code>,
	 * tak metoda vrati instanciu triedy Db_DbTable_Profesor	 * 
	 * 
	 * @return Zend_Db_Table_Abstract
	 */
	public function getTable(){
		
		if( $this->_dbTable === null){
						
			/*
			 * Vytvori meno triedy Db_DbTable_*
			 */
			$class = $this->_getClassName(self::DBTABLE);
			
			$this->setTable(new $class);
		}
		
		return $this->_dbTable;
	}
	
	/**
	 * Vyberie z databaze vsetky data, ktore patria Db_Modelu
	 * 
	 * @param Zend_Db_Select $select
	 * 
	 * @return array
	 */
	public function fetchAll(Zend_Db_Table_Select $select = null, array $foreignModelClassNames = null){			
		
					
		$arrayList = array();
		
		if($foreignModelClassNames != null){
			$foreignTableVars = array();
			
			foreach($foreignModelClassNames as $foreignModelClassName){				
				$foreignModelVars = $this->_getClassVars($foreignModelClassName);
				$dbTableClassName = 'Db_DbTable_' . $this->_getEndClassPart($foreignModelClassName);
				$foreignTableVars[$foreignModelClassName] = $this->_getUnicateVars(new $foreignModelClassName,new $dbTableClassName);
			}
		}
		
		/*
		 * Z aktualnej triedy Db_Mapper_* zisti meno triedy Db_Model_*
		 */	
		$modelClassName = $this->_getClassName(self::MODEL);

		
		$vars = $this->_getUnicateVars();
		
		/*
		 * Z db vyberie vsetky zaznamy z prislusnej tabulky
		 */
		$dbResult = $this->getTable()->fetchAll($select);
		
		/*
		 * Vytvori prislusnu instanciu triedy Db_Model_* a naplni ju datami.
		 * Jednotlive triedy vlozi do pola $arrayList
		 */
		foreach($dbResult as $row){
			$model = new $modelClassName();
			foreach($vars as $col){
				$method = 'set' . ucfirst($col);		
				$model->$method($row->$col);				
			}			

			if($foreignModelClassNames != null){									
					
					foreach($foreignTableVars as $modelInKey => $array){
						$tempForeignModel = new $modelInKey();							
						foreach($array as $foreignTableVar){					
							$setMethod = 'set' . ucfirst($foreignTableVar);																											
							$tempForeignModel->$setMethod($row->$foreignTableVar);
						}

						$model->setForeignModel($tempForeignModel);
					}
					
					
				
			}
				
			$arrayList[] = $model;
		}		
		
		return $arrayList;
	}
	
	/**
	 * Vyberie jeden riadok z databaze
	 *
	 * @param string|Zend_Db_Table_Select 		$where  OPTIONAL An SQL WHERE clause or Zend_Db_Table_Select object.
	 * @param string|array             			$order  OPTIONAL An SQL ORDER clause.
	 * 
	 * @return Db_Model_Abstract|null
	 */	 
	public function fetchRow($where, $order = null){
						
		/*
		 * Zaisti aby sa zbytocne neselectovali db udaje, ktore neobsahuje Db_Model_*
		 */
		if(is_string($where)){
			$select = $this->getTable()->select();
			$select->where($where);
			$select->from($this->_getDbTableName(),$this->_getUnicateVars());
			$rowSet = $this->getTable()->fetchRow($select,$order);			
		}
		else {
			$where->from($this->_getDbTableName(),$this->_getUnicateVars());
			$rowSet = $this->getTable()->fetchRow($where,$order);
		}
				
		/*
		 * Ak databaza vratila nejaky zaznam
		 */
		if($rowSet != null){
			$modelClassName = $this->_getClassName(self::MODEL);			
			return new $modelClassName($rowSet->toArray());
		}
	}
	
	
	/**
	 * Na zaklade primarneho kluca najde odpovedajuci zaznam
	 * 
	 * @param Db_Model_Abstract $model
	 */
	public function find(Db_Model_Abstract $model){
		
		if(!$model instanceof Db_Model_Abstract){
			throw new Exception('$model is not instance of Db_Model_Abstract');
		}
		
		
		$method = 'get' . ucfirst($this->_getPrimaryKeyName());		
		$result = $this->getTable()->find($model->$method());
		
		
		if($result instanceof Zend_Db_Table_Rowset){
			$result = $result->current();
		}
		
		$modelClassName = $this->_getClassName(self::MODEL);
		
		return new $modelClassName($result->toArray());
		
	}
	
	/**
	 * Ulozi vsetky atributy modelu do databaze
	 * 
	 * @param Db_Model_Abstract $model
	 * @param boolean $forceInsert
	 * 
	 * 
	 * @return mixed Last primary key
	 */
	public function save(Db_Model_Abstract $model, $forceInsert = false){
		
		if(!$model instanceof Db_Model_Abstract){
			throw new Db_Exception('$class is not instance of Db_Model_Abstract');
		}
		
		$dbData = array();		
		$classVars = $this->_getClassVars($model);
		
		foreach($classVars as $var => $value){			
			$method = 'get' . ucfirst($var);			
			$dbData[$var] = $model->$method();		 
		}
		
		try{			
			/*
			 * Vytvor meno metody na ziskanie primarneho klucu
			 */	
			$primaryKeyName = $this->_getPrimaryKeyName();	
			$primaryKeyMethod = 'get' . ucfirst($primaryKeyName);			
			
						
			/*
			 * Ak je nastaveny primaryn kluc tak update, inak insert
			 */
			if(($primaryKey = $model->$primaryKeyMethod()) != null && $forceInsert == false){							
				unset($dbData[$this->_getPrimaryKeyName()]);							
				
				/*
				 * Odstrani vsetky polozky s hodnotou NULL aby ich nevkladalo
				 */
				foreach($dbData as $key => $value){
					if($value == null){
						unset($dbData[$key]);
					}
				}
				
				$return = $this->getTable()->update(
					$dbData,$this->getTable()->getAdapter()->quoteInto($primaryKeyName . ' = ?',$primaryKey)
				);				
			}
			else{				
				$return = $this->getTable()->insert($dbData);
			}
			
			return $return;
			
		} catch(Zend_Db_Exception $e){
			throw new Db_Exception($e->getMessage());
		}
	}
	
	/**
	 * To iste ako <code>save(Db_Model_Abstract $model)</code> pre pole
	 * 
	 * @param array $array
	 */
	public function saveArray(array $array){
		
		if(is_array($array)){
			foreach($array as $data){
				/*
				 * Ak nie je obsah pola instanciou Db_Model_Abscract, tak 
				 * vytvor novu instanciu prislusneho modelu a tu uloz,
				 * inak rovno uloz  
				 */	
				if(!$data instanceof Db_Model_Abstract){
					$modelClassName = $this->_getClassName(self::MODEL);
					$data = new $modelClassName($data);
				}
				
				$this->save($data);
			}
		}
	}
	
	/**
	 * Vrati vsetky udaje z DB vratene dat, ktore pochadzaju zo zavislej tabulky
	 *  
	 * @param $table
	 * @param $intersectionTable
	 * @param $model
	 * 
	 * @return array
	 */
	public function fetchWithDependentRowset($table, $intersectionTable, Db_Model_Abstract $model, 
		Zend_Db_Table_Select $order = null,$depentedTable = null,$ruleKey = null){

		$arrayList = array();
		
		$tableEndPart = $this->_getEndClassPart($table);		
		/*
		 * Vytvori meno metody na pridanie objektu zo zavislej triedy
		 */
		$dependentVarMethodName = 'add' . ucfirst($tableEndPart);
		
		if(!(in_array($dependentVarMethodName,get_class_methods($model)))){
			throw new Exception('Method ' . $dependentVarMethodName . ' doesn\'t exist in ' . get_class($model));
		}
		
		$primaryKeyMethod = 'get' . ucfirst($this->_getPrimaryKeyName());
		
		/*
		 * Na zaklade primarneho kluca najde riadok v DB
		 */
		//$row = $this->getTable()->find($model->$primaryKeyMethod())->current();
		$select = $this->getTable()->select(Zend_Db_Table::SELECT_WITHOUT_FROM_PART);
		$select->from($this->_getDbTableName(),array($this->_getPrimaryKeyName()))
		->where($this->_getPrimaryKeyName() . '= ?',$model->$primaryKeyMethod(),Zend_Db::INT_TYPE);
		
		$row = $this->getTable()->fetchRow($select);
		
				
		/*
		 * Zisti meno "modelu" odpovedajuceho danemu "mapperu"
		 */
		$modelClassName = $this->_getClassName(self::MODEL);
		
		/*
		 * Vytvori instanciu modelu
		 */
		$model = new $modelClassName($row->toArray());
		
		try{			
			/*
			 * Relacia 1:N
			 */
			if($intersectionTable == null && $depentedTable != null){
				
				$rowset = $row->findDependentRowset($depentedTable,$ruleKey,$order);
				
			}else {			
				/*
				 * Z db vyberie vsetky zavisle riadky, relacia M:N
				 */
				$rowset = $row->findManyToManyRowset($table,$intersectionTable,null,null,$order);
			}
			/*
			 * Ak nastane vynimka Zend_Db_Statement_Mysqli_Exception, tak je treba spravit prepojenie
			 * "rucne"
			 */
		} catch(Zend_Db_Exception $e){
			
			/*
			 * Pri pri prepojeni tabuliek z roznych databaz dochadza k chybe,
			 * preto je treba cele prepojenie spravit rucne
			 */
			$tableMapperClassName = self::MAPPER_PREFIX . $this->_getEndClassPart($table);
			$tableMapper = new $tableMapperClassName();
			
			/*
			 * Zisti primarny kluc v vychodzej tabulky, resp. prvej tabulky, ku ktorej sa bude
			 * pripajat tabulka $table cez $intersectionTable
			 */
			$mapperPrimaryKeyNameArray = $this->getTable()->info(Zend_Db_Table_Abstract::PRIMARY);			
			$mapperPrimaryKeyName = $mapperPrimaryKeyNameArray[1];
			
			/*
			 * Vytvori metodu na ziskanie hodnoty primarneho klucu z vychodzieho modelu (parameter $model)
			 */
			$mapperPrimaryKeyMethod = 'get' . ucfirst($mapperPrimaryKeyName);
			
			/*
			 * Vytvorenie db where podmienky
			 */
			$select = $this->getTable()->select(Zend_Db_Table::SELECT_WITH_FROM_PART)->setIntegrityCheck(false);			
			$select->where(
				$this->getTable()->getAdapter()->quoteInto($this->_getDbTableName() . '.' . $mapperPrimaryKeyName . ' = ?',$model->$mapperPrimaryKeyMethod())
			);	
			
			/*
			 * Vytvori instanciu intersection table a zisti primarny kluc
			 */
			$intersectionClass = new $intersectionTable;
			$tableMapperPrimaryKeyName = $this->_getPrimaryKeyName($tableMapper);
			
			
			/*
			 * Vytvori "join" podmienku na pripojenie M:N (intercesction) tabulky
			 */
			$joinCondition = $this->_getDbTableName() . '.' . $this->_getPrimaryKeyName() . '='
						   . $this->_getDbTableName($intersectionClass) . '.' . $this->_getPrimaryKeyName();
			
				   
			$select->join($this->_getDbTableName($intersectionClass),$joinCondition,array($tableMapperPrimaryKeyName));
			$intersectionRowset = $this->getTable()->fetchAll($select);
			
			/*
			 * Ak nevratilo ziadny zaznam, znamena ze este nema prideleny ziadny zaznam v Db
			 */
			if(count($intersectionRowset) > 0){
				/*
				 * Bude obsahovat vsetky id, na zaklade ktorych sa bude pripajat posledna(tretia) tabulka
				 */
				$whereIDs = '';		
				
				/*
				 * Vlozi do premennej vsetky potrebne id
				 */
				foreach($intersectionRowset as $intersectionRow){
					$whereIDs .= $intersectionRow->$tableMapperPrimaryKeyName . ',';
				}
				
				/*
				 * Odstrani nepotrebnu ciarku na konci retazca
				 */
				$whereIDs = rtrim($whereIDs,',');
				/*
				 * Zisti vsetky atributy modelu, pouziva sa na to aby db nemusela tahat vsetky stlpce z tabulky
				 */				
				$tableDbCols = $tableMapper->getTable()->info(Zend_Db_Table_Abstract::COLS);
								
				$select = $tableMapper->getTable()->select()->setIntegrityCheck(false);
				$select->from(array($this->_getDbTableName($tableMapper)),$tableDbCols);
				$select->where($tableMapperPrimaryKeyName . ' IN(' . $whereIDs . ')' );	
				
				$rowset = $tableMapper->getTable()->fetchAll($select);
			}
			else {
				/*
				 * Ak db nenvratila ziadny zaznam, vytvori prazdne pole, aby nedochadzalo k chybe pri foreach
				 */
				$rowset = array();	
			}
		}
						
		/*
		 * Vytvori instanciu zavisleho modelu
		 */
		$dependModelClassName = self::MODEL_PREFIX . $this->_getEndClassPart($table);
		$dependModelVars = $this->_getClassVars($dependModelClassName);
		
		/*
		 * Vlozi zavisly model do hlavneho modelu
		 */
		foreach($rowset as $oneRow){
			$model->$dependentVarMethodName(
				new $dependModelClassName(
					array_intersect_key($oneRow->toArray(),$dependModelVars)
				)
			);	
		}
		
		/*
		 * Kvoli kompatibilite s <code>fetchAll()</code> musi funkcia vracať pole
		 */
		$arrayList[] = $model;
		
		return $arrayList;
	}
	
	/**
	 * Odstrani zaznam z Db
	 * 
	 * Ak tabulka obsahuje viac primerynch klucov tak sa pouzije nazov
	 * toho poslednoho
	 * 
	 * @param Db_Model_Abstract $model
	 * 
	 * @return int Pocet odstranenych riadkov
	 */
	public function delete(Db_Model_Abstract $model){
		
		if(!$model instanceof Db_Model_Abstract){
			throw new Exception('$model is not instance of Db_Model_Abstract');
		}	
		
		/*
		 * Meno metody na ziskanie primary key
		 */
		$primaryKeyMethod = 'get' . ucfirst($this->_getPrimaryKeyName());
		
		$id = $model->$primaryKeyMethod();
		$where = $this->getTable()->getAdapter()->quoteInto($this->_getPrimaryKeyName() . ' = ?',$id,Zend_Db::INT_TYPE);
		return $this->getTable()->delete($where);				
	}
	
	
	/**
	 * Vrati cele meno triedy na zaklade zvoleneho typu
	 * pre aktualnu triedu ktora dedi abstrakntu tridu
	 * 
	 * Moznosti (case sensitive!!!):
	 *  - DbTable  
	 *  - Model
	 * 
	 * @param String $type
	 * @return String Db_Model_* | Db_DbTable_*
	 */
	protected function _getClassName($type){
		
		switch ($type){
			case self::DBTABLE: $classPrefix = self::DBTABLE_PREFIX;
				break;
			case self::MODEL: $classPrefix = self::MODEL_PREFIX;				
				break;
			default:
				throw new Exception('Prefix ' . $type . ' doesn\'t exist');
				break;
		}
		
		$classEndPartArray = explode('_',get_class($this));
		$classEndPart = array_pop($classEndPartArray);
		$class = $classPrefix . $classEndPart;
			
		return $class;
	}
	
	
	/**
	 * Vracia mena vsetkych atributov zvolenej triedy
	 * @param array $class
	 */
	protected function _getClassVars($class){
		
		$attribs = array();
		
		$reflection = new ReflectionClass($class);	
		$attribsAll = $reflection->getDefaultProperties();
				
		foreach($attribsAll as $key => $value){
			if(substr($key,0,1) != '_'){				
				$attribs[$key] = $value;
			}
		}
		
		return $attribs;
	}
	
	/**
	 * Vratcia meno konkretnej db tabulky v databaze
	 * 
	 * @param string $class
	 */
	protected function _getDbTableName($class = null){
		
		if($class === null){		
			return $this->getTable()->info(Zend_Db_Table_Abstract::NAME);
		}
		else {			
			if($class instanceof Db_Mapper_Abstract){
				return $class->getTable()->info(Zend_Db_Table_Abstract::NAME);
			}
			elseif($class instanceof Zend_Db_Table_Abstract){
				return $class->info(Zend_Db_Table_Abstract::NAME);				
			}
			else {
				throw new Exception('$class is not instance of Zend_Db_Table_Abstract or Db_Mapper_Abstract');
			}			
		}	
	}
	
	/*
	 * Vrati poslednu cast z nazvu triedy za separatorom "_"
	 * 
	 * @return string
	 */
	protected function _getEndClassPart($class){
		
		/*
		 * Rozdeli meno triedy do pola. Napr. ak je meno Db_DbTble_Professor,
		 * a vytvori pole <code>array(0 => Db, 1 => Mapper, => 2 Professor)</code>
		 */		
		$classEndPartArray = explode('_',is_string($class) ? $class : get_class($class));
		
		return array_pop($classEndPartArray);
	}
	
	/**
	 * Zisti meno primary key tabulky
	 * 
	 * Ak nie je zadany param. $class, tak vrati primerny kluc mapperu, ktory danu triedu vola.
	 * Ak je zadany param. $class, tak zisti primerny kluc u danej triedy $class
	 * 
	 * POZOR: Ak ma tabulky viac primarnych klucov tak sa pouzije ten posledny
	 * 
	 * @param mixed $class
	 * 
	 * @return string 
	 */
	protected function _getPrimaryKeyName($class = null){
				
		if($class === null){
			$primaryKeyArray = $this->getTable()->info(Zend_Db_Table_Abstract::PRIMARY);
			$primaryKey = $primaryKeyArray[1];
		}
		else {
			if($class instanceof Db_Mapper_Abstract){
				$primaryKeyArray = $class->getTable()->info(Zend_Db_Table_Abstract::PRIMARY);
				$primaryKey = $primaryKeyArray[1];
			}
			elseif($class instanceof Zend_Db_Table_Abstract){
				$primaryKeyArray = $class->info(Zend_Db_Table_Abstract::PRIMARY);
				$primaryKey = $primaryKeyArray[1];
			}
			else {
				throw new Exception('$class is not instance of Zend_Db_Table_Abstract or Db_Mapper_Abstract');
			}
		}
		
		return $primaryKey;
	}	
	
	/**
	 * Urobi prenik medzi stlpcamim v db a premennyma v modely a vrati
	 * len premenne, ktore su spolocne
	 * 
	 * @return array
	 */
	protected function _getUnicateVars(Db_Model_Abstract $model = null,Zend_Db_Table_Abstract $dbTable = null){
		
		if($model == null){
			/*
			 * Z aktualnej triedy Db_Mapper_* zisti meno triedy Db_Model_*
			 */	
			$modelClassName = $this->_getClassName(self::MODEL);
		}
		else {
			$modelClassName = get_class($model);
		}
		
		/*
		 * Zisti mena atributov triedy Db_Model_*
		 * 
		 * pozn. ak sa poziva info() priamo na tabulky z db, moze nastat
		 * problem ak v modely nie su definovane vsetky premene pre stlpce, ktore
		 * sa nachadzaju v db 
		 */	
		if($dbTable == null){	
			$tableCols = $this->getTable()->info(Zend_Db_Table_Abstract::COLS);
		}
		else {
			$tableCols = $dbTable->info(Zend_Db_Table_Abstract::COLS);
		}
		
		$classVars = $this->_getClassVars($modelClassName);
		$classVars = array_keys($classVars);		
		
		return array_intersect($classVars,$tableCols);
	}
}
?>