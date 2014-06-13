package cz.edukomplex.kosilka.client.content.result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.ClickableTextCell;
import com.google.gwt.cell.client.FieldUpdater;
import com.google.gwt.cell.client.SelectionCell;
import com.google.gwt.cell.client.ValueUpdater;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.Header;
import com.google.gwt.user.cellview.client.SimplePager;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionChangeEvent;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramCellListRow;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramCellTableRow;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.ResultSubjectModel;
import cz.edukomplex.kosilka.client.model.SubjectModel;
import cz.edukomplex.kosilka.client.provider.ResultsDataProvider;

public class ResultContent extends Composite {

	interface ResultContentUiBinder extends UiBinder<Widget, ResultContent> {
	}

	private static ResultContentUiBinder uiBinder = GWT
			.create(ResultContentUiBinder.class);


	@UiField SimplePanel cellTableWrapper;	
	@UiField SimplePanel actionPanel;
	@UiField ScrollPanel scrollPanel;
	@UiField ListBox exportTableListBox;
	@UiField Button exportTableButton;
	@UiField ListBox exportProtocolListBox;
	@UiField Button exportProtocolButton;
	@UiField HorizontalPanel pagerWrapper;	
	
	public static final int NAME_COLUMN = 1;
	private static final int PDF_FORMAT = 0;
	private static final int HTML_FORMAT = 1;
	private static final int ODT_FORMAT = 2;
	private static final int WORD_FORMAT = 3;
	private static final String EXPORT_PATH = GWT.getHostPageBaseURL() + "tmp/results/";
	private static final List<String> GRADES = Arrays.asList("","A","B","C","D","E","F");
	private static final List<String> OVERALL_GRADES = Arrays.asList("","P","PV","N");
	private static final List<String> QUESTIONS_LISTBOX = Arrays.asList("","1","2","3","4","5","6","7","8","9",
			"10","11","12","13","14","15","16","17","18","19","20","21","22","23","24","25");
	
	private static final ProvidesKey<ResultCellTableRow> KEY_PROVIDER = new ProvidesKey<ResultCellTableRow>() {

		@Override
		public Object getKey(ResultCellTableRow item) {
			return item.ID_st;
		}
	};
	
	private ArrayList<SubjectModel> subjectsArrayList = new ArrayList<SubjectModel>();
	private CellTable<ResultCellTableRow> resultsCellTable;
	private ResultsDataProvider resultsDataProvider = new ResultsDataProvider();	
	private MultiSelectionModel<ResultCellTableRow> tableSelectionModel = new MultiSelectionModel<ResultCellTableRow>(KEY_PROVIDER);	
	private SimplePager pager = new SimplePager();
	private ApplicationController appManager = ApplicationController.getInstance();
	
	public ResultContent() {		
		appManager.getApplicationResources().applicationStyle().ensureInjected();			
		initWidget(uiBinder.createAndBindUi(this));
		initCellList();			
	}
	
	
	/**
	 * Prida novy riadok do {@link CellTable} tabulky a ulozi hodnoty do db 
	 * 
	 * @param row Riadok, ktory sa ma pridat
	 */
	public void addRowToTable(final HarmonogramCellTableRow row){
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "result.insertRow";
		Object[] params = new Object[]{row.ID_st,row.hid};
		
		XmlRpcRequest<Integer> request = new XmlRpcRequest<Integer>(client, methodName, params, 
				new AsyncCallback<Integer>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(Integer result) {
				int rid = result;
				if(rid < 0){//nastala chyba
					Window.alert(appManager.constants.databaseInsertFailed());
					return;
				}
				
				String lastName = row.student.substring(0, row.student.indexOf(" "));
				String firstName = row.student.substring(row.student.indexOf(" "), row.student.length());
				
				//Vytvorenie riadku v CellTable
				ResultCellTableRow resultRow = new ResultCellTableRow();
				resultRow.setID_st(row.ID_st);
				resultRow.setRid(String.valueOf(rid));
				resultRow.setFirstname(firstName);
				resultRow.setLastName(lastName);
				resultRow.setOrder(row.order);				
				resultsDataProvider.getList().add(resultRow);
				resultsDataProvider.update();
			}
		});
		
		request.execute();
	}
	
	@UiHandler("exportProtocolButton")
	public void handleProtocolExportButton(ClickEvent event){
		int format = Integer.parseInt(exportProtocolListBox.getValue(exportProtocolListBox.getSelectedIndex()));		
		export("exportProtocol",format);		
	}
	
	@UiHandler("exportTableButton")
	public void handleTableExportButton(ClickEvent event){
		int format = Integer.parseInt(exportTableListBox.getValue(exportTableListBox.getSelectedIndex()));
		export("exportTable",format);		
	}
	
	/**
	 * Inicializuje {@link CellTable}
	 */
	public void initCellTable() {
		
		//musi byt vybrana moznost z menu
		if(appManager.getSelectedHarmonogram() != null){
			
			//nastavenie hid pre dataProvider
			resultsDataProvider.setHid(appManager.getSelectedHarmonogram().hid);
			
			//vycistenie zoznamov
			subjectsArrayList.clear();
			resultsDataProvider.getList().clear();
						
			 //vytvori strukturu tabulky			 
			createTable();			
		}
	}
	
	/**
	 * Vytvori celu strukturu {@link CellTable} bez dat. Data vlozi do tabulky az metoda
	 * <code>initCellTable()</code>.
	 */
	private void createTable() {
		
			//ak nie je vybraty harmonogram tak nevytvaraj
			if(appManager.getSelectedHarmonogram() == null) return;		
					
			final String idStudyField = appManager.getSelectedHarmonogram().idStudyField;			
			XmlRpcClient client = MyXmlRpcClient.createClient();			
					
			String methodName = "subject.fetchSubjectsByStudyFieldId";
			Object[] params = new Object[]{idStudyField};
			
			XmlRpcRequest<ArrayList<HashMap<String, String>>> subjectRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(
					client, methodName, params, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert(caught.getMessage());
						}

						@Override
						public void onSuccess(ArrayList<HashMap<String, String>> result) {
							
							for (@SuppressWarnings("rawtypes")
							Iterator iterator = result.iterator(); iterator
									.hasNext();) {
								@SuppressWarnings("unchecked")
								HashMap<String, String> hashMap = (HashMap<String, String>) iterator
										.next();
																
								SubjectModel row = new SubjectModel(
										hashMap.get("sid"), 
										hashMap.get("name"),
										hashMap.get("short")
								);
								subjectsArrayList.add(row);
								
							}
							
							/*
							 * Ak trieda nema pridelen ziadne predmety tak nezobrazuj tabulku
							 */
							if(subjectsArrayList.size() == 0){
								Window.alert("Trieda nemá pridelené predmety");
								resultsDataProvider.getList().clear();
								//refreshCellTable();
								resultsCellTable.setVisible(false);
								return;
							}
														
							resultsCellTable = new CellTable<ResultCellTableRow>(
									appManager.getHarmonogramRowsCount(),appManager.getCellTableResources(),KEY_PROVIDER);
							resultsCellTable.setSelectionModel(tableSelectionModel, 
									DefaultSelectionEventManager.<ResultCellTableRow> createCheckboxManager());
							
							
							Column<ResultCellTableRow, Boolean> checkbox = new Column<ResultCellTableRow, Boolean>(new CheckboxCell(true, false)) {
								
								@Override
								public Boolean getValue(ResultCellTableRow object) {
									try{
										//kontrola ci su zadane vsetky hodnotenia
										/*
										 * TODO: odstranit kontrolu
										 */
										if(isRowEmpty(object)){
											tableSelectionModel.setSelected(object, false);
											return false;
										}
										return tableSelectionModel.isSelected(object);
									} catch (Exception e){
										Window.alert(e.getMessage());
										tableSelectionModel.clear();
										return false;
									}
								}
							};
							
														
							Column<ResultCellTableRow, String> studentName = new Column<ResultCellTableRow, String>(new ClickableTextCell()) {
								
								@Override
								public String getValue(ResultCellTableRow object) {
									return object.lastName + " " + object.firstname;
								}
							};
							studentName.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
								
								@Override
								public void update(int index, ResultCellTableRow object, String value) {	
									ArrayList<Integer> rid = new ArrayList<Integer>();
									rid.add(Integer.valueOf(object.rid));
									export("exportProtocol",HTML_FORMAT,rid);//exportuje len jedneho studenta na zaklade rid									
								}
							});
							
							final Column<ResultCellTableRow, String> questionNumber = new Column<ResultCellTableRow, String>(new SelectionCell(QUESTIONS_LISTBOX)) {
								
								@Override
								public String getValue(ResultCellTableRow object) {
									return object.questionNumber;
								}
							};
							questionNumber.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
								
								@Override
								public void update(int index, ResultCellTableRow object, String value) {
									
									try{										
										//ak je nevybrane moze byt aj prazdny znak spravne
										if(!value.isEmpty()){
											Integer.parseInt(value);
										}
										
										saveQuestionNumber(object,value,index);										
									}catch(NumberFormatException e){
										/*
										 * Treba vymazat zadanu hodnotu z Cell
										 */
										//EditTextCell questionCell = (EditTextCell)questionNumber.getCell();
										//questionCell.clearViewData(KEY_PROVIDER.getKey(object));
										//resultsCellTable.redraw();
										return;
									}																
								}								
							});
							
							Column<ResultCellTableRow, String> veduciGrade = new Column<ResultCellTableRow, String>(new SelectionCell(GRADES)) {

								@Override
								public String getValue(ResultCellTableRow object) {
									if(object.veduciGrade == null){
										return GRADES.get(0);
									}
									return object.veduciGrade;
								}
							};
							veduciGrade.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
								
								@Override
								public void update(int index, ResultCellTableRow object, String value) {
									saveGrade(											
											object,
											value,
											"result.saveVeduciGrade"
									);
									object.veduciGrade = value;
								}						
							});
							
							Column<ResultCellTableRow, String> oponentGrade = new Column<ResultCellTableRow, String>(new SelectionCell(GRADES)) {
								
								@Override
								public String getValue(ResultCellTableRow object) {
									if(object.oponentGrade == null){
										return GRADES.get(0);
									}
									return object.oponentGrade;
								}
							};
							oponentGrade.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
								
								@Override
								public void update(int index, ResultCellTableRow object, String value) {
									saveGrade(											
											object,
											value,
											"result.saveOponentGrade"
									);
									object.oponentGrade = value;
								}						
							});
							
														
							Column<ResultCellTableRow, String> obhajobaGrade = new Column<ResultCellTableRow, String>(new SelectionCell(GRADES)) {

								@Override
								public String getValue(ResultCellTableRow object) {
									if(object.obhajobaGrade == null){
										return GRADES.get(0);
									}
									return object.obhajobaGrade;
								}
							};
							obhajobaGrade.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
								
								@Override
								public void update(int index, ResultCellTableRow object, String value) {
									saveGrade(
											object,
											value,
											"result.saveObhajobaGrade"
									);					
									object.obhajobaGrade = value;
								}			
																
							});
							
							
							Column<ResultCellTableRow, String> overalSubjectsGrade = new Column<ResultCellTableRow, String>(new SelectionCell(GRADES)) {

								@Override
								public String getValue(ResultCellTableRow object) {
									if(object.overallSubjectsGrade == null){
										return GRADES.get(0);
									}
									return object.overallSubjectsGrade;
								}
							};
							overalSubjectsGrade.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
								
								@Override
								public void update(int index, ResultCellTableRow object, String value) {
									saveGrade(object, value, "result.saveOberallSubjectsGrade");
									object.overallSubjectsGrade = value;
								}
							});
							
							Column<ResultCellTableRow, String> overallGrade = new Column<ResultCellTableRow, String>(new SelectionCell(OVERALL_GRADES)) {

								@Override
								public String getValue(ResultCellTableRow object) {
									if(object.overallGrade == null){
										return OVERALL_GRADES.get(0);
									}
									return object.overallGrade;
								}
							};
							overallGrade.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
								
								@Override
								public void update(int index, ResultCellTableRow object, String value) {
									saveGrade(
											object,
											value,
											"result.saveOverallGrade"
									);		
									object.overallGrade = value;
								}

																	
							});
							
							
							Header<Boolean> checkboxHeader = new Header<Boolean>(new CheckboxCell()) {
								
								@Override
								public Boolean getValue() {
									return false;
								}
							};
							checkboxHeader.setUpdater(new ValueUpdater<Boolean>() {
								
								@Override
								public void update(Boolean value) {
									if(value){//vyber vsetky moznosti
										for (ResultCellTableRow row : resultsDataProvider.getList()) {											
											if(isRowEmpty(row)) continue;																						
											tableSelectionModel.setSelected(row, true);
										}
									}
									else {//odznac vsetky moznosti
										tableSelectionModel.clear();
									}
								}								
							});					
							
							//vlozenie jednotlivych stlpcov do tabulky
							resultsCellTable.setColumnWidth(checkbox, "5px");
							resultsCellTable.addColumn(checkbox, checkboxHeader);
							resultsCellTable.addColumn(studentName, appManager.constants.studentName());
							resultsCellTable.addColumn(questionNumber,appManager.constants.question());							
							resultsCellTable.addColumn(veduciGrade,appManager.constants.supervisor());
							resultsCellTable.addColumn(oponentGrade,appManager.constants.opponent());
							resultsCellTable.addColumn(obhajobaGrade,appManager.constants.defense());
							
							//automaticke generovanie hlavicky tabulky pre predmety
							for (int i = 0; i < subjectsArrayList.size(); i++) {
								final int j = i;							
								Column<ResultCellTableRow, String> subjectColumn = new Column<ResultCellTableRow, String>(new SelectionCell(GRADES)) {
																				
									@Override
									public String getValue(ResultCellTableRow object) {
										/*
										 * Vrat hodnotenie, ak je pridelene; Inak v listobxe zobraz <prazdne>  
										 */
										if(subjectsArrayList.size() > 0){
											try{												
												return object.subjectsHashMap.get(subjectsArrayList.get(j).getSid()).getGrade();												
											} catch(Exception e){												
												return GRADES.get(0);
											}
										}
										else {													
											return GRADES.get(0);
										}
									}
								};
								
								subjectColumn.setFieldUpdater(new FieldUpdater<ResultCellTableRow, String>() {
									
									@Override
									public void update(int index, ResultCellTableRow object, String value) {										
										/*
										 * Pri strankovani sa od druhej strany nezhoduje premenna index s hodnotami 
										 * indexov v resultsDataProvider. 
										 * Hodnoty v resultsDataProvider su na kazdej novej stranke cislovane od 0, ale 
										 * parameter funkcie index vracia hodnoty inkrementalne od zaciatku.
										 * 
										 */
										
										//index prvej hodnoty na stranke
										int pageStartIndex = resultsDataProvider.getRange().getStart();
										
										saveSubjectGrade(												
												Integer.valueOf(resultsDataProvider.getList().get(index - pageStartIndex).ID_st),//ID_studenta
												Integer.valueOf(subjectsArrayList.get(j).getSid()),//sid
												object,//riadok tabulky
												value//vybrana znamka
										);																		
									}										
								});
								
								resultsCellTable.addColumn(subjectColumn, subjectsArrayList.get(i).getShortName());
							}
									
							
							
							resultsCellTable.addColumn(overalSubjectsGrade,appManager.constants.subjects());
							resultsCellTable.addColumn(overallGrade,appManager.constants.overall());
																					
							cellTableWrapper.setWidget(resultsCellTable);//vlozi widget
							resultsCellTable.setWidth("100%");
													
							//vyvola event, ktory naplni tabulku datami
							resultsCellTable.setVisibleRange(0, ResultsDataProvider.PAGE_ROWS_COUNT);//nastavenie poctu riadkov viditelnych na jednej strane
							resultsDataProvider.addDataDisplay(resultsCellTable);//vlozenie dat do dataProvider
												
									
							//nastavenie zoradovania podla mena - pri pouziti ListDataProvider nefunguje spravne
							/*
							List<ResultCellTableRow> list = resultsDataProvider.getList();
							ListHandler<ResultCellTableRow> sortHandler = new ListHandler<ResultCellTableRow>(list);							
							resultsCellTable.getColumn(NAME_COLUMN).setSortable(true);//je mozne zoradovat podla mena
							sortHandler.setComparator(resultsCellTable.getColumn(NAME_COLUMN),ResultCellTableRow.NAME_COMPARATOR);//nastavenie komparatora
							resultsCellTable.addColumnSortHandler(sortHandler);//vlozenie sort handler
							*/
							
							//nastavenie celkoveho poctu riadkov pre strankovanie
							resultsCellTable.setRowCount(appManager.getHarmonogramRowsCount());
							
							//nastavenie strankovania		
							pager.setDisplay(resultsCellTable);							
							pager.setRangeLimited(false);							
							pagerWrapper.add(pager);
						}						
					
			});					
			subjectRequest.execute();
			
			/*
			String qeustionMethodName = "question.getMaxQuestionsCount";
			XmlRpcRequest<Integer> questionsRequest = new XmlRpcRequest<Integer>(client, qeustionMethodName, null, new AsyncCallback<Integer>() {

				@Override
				public void onFailure(Throwable caught) {
					Window.alert(caught.getMessage());					
				}

				@Override
				public void onSuccess(Integer result) {
					if(result > 0){
						QUESTIONS_LISTBOX.add(" ");
						for (int i = 1; i <= result; i++) {
							QUESTIONS_LISTBOX.add(String.valueOf(i));
						}						
					}	
					
					
				}
			});
			questionsRequest.execute();
			*/		
	}	
	

	/**
	 * Sluzi na export vysledkov bud v podobe protokolu alebo suhrnej tabulky. 
	 * Exportuje vysledky pre zvolenych studentov na zaklade checkbox-ov 
	 * 
	 * @param exportMethod typ exportu, su dve moznosti bud <code>exportProtocol</code> alebo 
	 * <code>exportTable</code>. Tieto dve metody mapuju sluzby, ktore poskytuje server
	 * @param format format do ktoreho sa bude exportovat
	 */
	private void export(String exportMethod,final int format) {
		
		boolean empty = false;
		
		try{
			//vybrati studenti v tabulke
			ArrayList<ResultCellTableRow> rows = new ArrayList<ResultCellTableRow>(tableSelectionModel.getSelectedSet());
			if(rows.size() < 1){//kontrola ci je vybrana minimalne jedna polozka
				Window.alert(appManager.constants.selectResultCheckboxWarining());
				return;
			}
			
			//vytvori ArrayList, ktory obsahuje len rid vybranych vysledkov
			ArrayList<Integer> rid = new ArrayList<Integer>();
			for (ResultCellTableRow cellRow : rows) {
				rid.add(Integer.valueOf(cellRow.rid));
				if(exportMethod.equals("exportTable")){
					//kontrola si nie je niektoy riadok prazdny
					if(isRowEmpty(cellRow,true)){
						empty = true;
					}
				}
			}

			//ak je niektore prazdne zobraz potvrdzovaci dialog
			if(empty){
				boolean confirm = Window.confirm("Údaje niektorého študenta chýbajú. Želáte " +
						"si tabuľku naozaj exportovať?");
				if(confirm == false) return;
			}
			
			//ak je vsetko v poriadku tak export
			export(exportMethod, format,rid);			
			 
		} catch (NullPointerException e) {
			Window.alert("Najskôr musíte vybrat niektorý harmonogram z ľavého stĺpca");
		}		
	}
	
	/**
	 * Exportuje len explicitne zvolenych studentov ako parameter
	 * 
	 * @param exportMethod typ exportu, su dve moznosti bud <code>exportProtocol</code> alebo 
	 * <code>exportTable</code>. Tieto dve metody mapuju sluzby, ktore poskytuje server
	 * @param format format do ktoreho sa bude exportovat
	 * @param rid <code>ArrayList</code>, ktory obsahuje id exportovanych vysledkov
	 */
	private void export(String exportMethod,final int format, ArrayList<Integer> rid){
		
		HarmonogramCellListRow selectedRow = appManager.getSelectedHarmonogram();
				
		int hid = Integer.parseInt(selectedRow.hid);			
		int sfid = Integer.parseInt(selectedRow.idStudyField);			
		
					
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "result." + exportMethod;
		Object[] params = new Object[]{hid,sfid,format,rid};
		
		XmlRpcRequest<String> exportRequest = new XmlRpcRequest<String>(
				client, methodName, params, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(String result) {															
						String param = "_self";
						//ak je export do html, otvor do noveho okna
						if(format == HTML_FORMAT || format == PDF_FORMAT){
							param = "_blank";
						}							
						
						Window.open(EXPORT_PATH + result, param, "");							
					}
		});
		exportRequest.execute();
	}
	
	/**
	 * Inicializuje {@link CellList}. Vytvori novy {@link SelectionHandler}, ktory je volany 
	 * vzdy pri vybere v lavej casti menu.
	 */
	private void initCellList() {				
		/*
		 * Handler, ktory inicializuje tabulku s harmonogramami
		 */
		appManager.getHarmonogramSelectionModel().addSelectionChangeHandler(new SelectionChangeEvent.Handler() {			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {				
				initCellTable();			
			}
		});		
	}
	
	private boolean isRowEmpty(ResultCellTableRow row, boolean check){
		if(check == false){
			return false;
		}
		
		try {
			if(row.rid.isEmpty() || row.questionNumber.isEmpty() || 
					row.veduciGrade.isEmpty() || row.oponentGrade.isEmpty() || row.obhajobaGrade.isEmpty() ||
					row.overallSubjectsGrade.isEmpty() || row.overallGrade.isEmpty()){
				return true;
			}
			
			/*
			 * Kontrola ci su vyplnene vsetky predmety, funguje dovetedy dokial uzivatel neprideli hodnotenie
			 * a neodstrani ho
			 * 
			 * BUG: sposobuje nemoznost vybrat studenta aj ked ma vyplnene vsetky parametre
			 *
			if(subjectsArrayList.size() != row.subjectsHashMap.values().size()){
				return true;
			}
			*/
			
			/*
			 * Kontrola predmetov ci neobsahuje prazdny retazec
			 * BUG: nefunguje
			 *
			for(ResultSubjectModel subject : row.subjectsHashMap.values()){				
				if(subject.grade.isEmpty()) return true;
			}
			*/
			
			return false;
			
		} catch (NullPointerException e){
			
			/*
			 * Ak je hodnota nepridelena (null), tak sa vyhadzuje vynimka
			 */
			return true;
		}
	}
	
	
	/**
	 * Kontroluje  ci su zadane vsetky hodnoty
	 * 
	 * @param row
	 * 
	 * @return Ak je riadok prazdny tak vrati true, inak false
	 */
	private boolean isRowEmpty(ResultCellTableRow row) {
		//TODO: BEZ KONTROLY
		return false;
		/*
		 * Ak nema pridelene hodntenie, tak preskoc cyklus a nevyberaj
		 *											
		 *
		try {
			if(row.rid.isEmpty() || row.questionNumber.isEmpty() || 
					row.veduciGrade.isEmpty() || row.oponentGrade.isEmpty() || row.obhajobaGrade.isEmpty() ||
					row.overallSubjectsGrade.isEmpty() || row.overallGrade.isEmpty()){
				return true;
			}
			
			/*
			 * Kontrola ci su vyplnene vsetky predmety, funguje dovetedy dokial uzivatel neprideli hodnotenie
			 * a neodstrani ho
			 * 
			 * BUG: sposobuje nemoznost vybrat studenta aj ked ma vyplnene vsetky parametre
			 *
			if(subjectsArrayList.size() != row.subjectsHashMap.values().size()){
				return true;
			}
			*/
			
			/*
			 * Kontrola predmetov ci neobsahuje prazdny retazec
			 * BUG: nefunguje
			 *
			for(ResultSubjectModel subject : row.subjectsHashMap.values()){				
				Window.alert(subject.grade);
				if(subject.grade.isEmpty()) return true;
			}
			
			
			return false;
			
		} catch (NullPointerException e){
			
			/*
			 * Ak je hodnota nepridelena (null), tak sa vyhadzuje vynimka
			 *
			return true;
		}*/
	}
	
	

	/**
	 * Ulozi vsetky ostatne hodnotenia, ktore neposkytuju metody <code>saveSubjectGrade()</code> a <code>saveQuestionNumber</code>
	 * 
	 * @param object
	 * @param value
	 * @param serviceMethodName Meno XML-RPC sluzby, ktora sa ma pri ukladani hodnotenia zavolat
	 */
	private void saveGrade(
			final ResultCellTableRow object, String value, String serviceMethodName) {
		
		
		XmlRpcClient client = MyXmlRpcClient.createClient();		
		Object[] params = new Object[]{object.ID_st,value};
		
		XmlRpcRequest<Integer> request = new XmlRpcRequest<Integer>(
				client, serviceMethodName, params, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Integer result) {
						if(result > 0){
							object.setRid(String.valueOf(result));
						}						
					}
		});
		request.execute();
	}
	
	/**
	 * Ulozi cislo otazky do db
	 * 
	 * @param object
	 * @param value
	 * @param index
	 */
	private void saveQuestionNumber(final ResultCellTableRow object, final String value, final int index) {
		
		//kontrola ci sa hodnota zmenila
		if(object.questionNumber != null){
			if(object.questionNumber.equals(value)){
				return;
			}
		}
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "result.setQuestionNumber";
		Object[] params = new Object[]{object.ID_st,value};
		
		XmlRpcRequest<String> qnRequest = new XmlRpcRequest<String>(
				client, methodName, params, new AsyncCallback<String>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(String result) {
						if (result.equals("-1")) {
							Window.alert(appManager.constants.databaseInsertFailed());
						}		
						else {
							
							object.questionNumber = value;						
							if(object.rid.isEmpty()){
								object.rid = result;								
							}
						}
					}
		});
		qnRequest.execute();		
	}
	
	/**
	 * Ulozi hodnotenie z predmetu
	 * 
	 * @param idStudent
	 * @param sid
	 * @param object
	 * @param value
	 */
	private void saveSubjectGrade(int idStudent,final int sid,final ResultCellTableRow object, final String value) {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();		
		Object[] params = new Object[]{idStudent,object.rid,sid,value};
		String methodName = "result.saveSubjectGrade";
		
		XmlRpcRequest<Integer> request = new XmlRpcRequest<Integer>(
				client, methodName, params, new AsyncCallback<Integer>() {

					@Override
					public void onFailure(Throwable caught) {						
						Window.alert(caught.getMessage());					
					}

					@Override
					public void onSuccess(Integer result) {
						
						if(result > 0){				
							object.setRid(String.valueOf(result));
							
							ResultSubjectModel subject = new ResultSubjectModel(
									String.valueOf(sid), String.valueOf(result), null, null, value);
							
							object.subjectsHashMap.put(String.valueOf(sid), subject);

						}else{
							Window.alert("Známku z predmetu sa nepodarilo uložiť");
						}
						
					}
		});
		request.execute();
	}	
}