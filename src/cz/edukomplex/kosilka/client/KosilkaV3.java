package cz.edukomplex.kosilka.client;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import com.fredhat.gwt.xmlrpc.client.XmlRpcClient;
import com.fredhat.gwt.xmlrpc.client.XmlRpcRequest;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.RunAsyncCallback;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.cellview.client.CellList;
import com.google.gwt.user.cellview.client.HasKeyboardSelectionPolicy.KeyboardSelectionPolicy;
import com.google.gwt.user.client.Cookies;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SelectionChangeEvent;
import com.google.gwt.view.client.SingleSelectionModel;

import cz.edukomplex.kosilka.client.content.HeaderPanel;
import cz.edukomplex.kosilka.client.content.InfoPanel;
import cz.edukomplex.kosilka.client.content.LoginPage;
import cz.edukomplex.kosilka.client.content.block.StudyFieldCellListRow;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramAddDialog;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramCell;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramCellListRow;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramContent;
import cz.edukomplex.kosilka.client.content.result.ResultContent;
import cz.edukomplex.kosilka.client.helper.MyXmlRpcClient;
import cz.edukomplex.kosilka.client.model.HarmonogramModel;
import cz.edukomplex.kosilka.client.model.YearModel;

public class KosilkaV3 implements EntryPoint, ResizeHandler {

	interface KosilkaV3UiBinder extends UiBinder<Widget, KosilkaV3> {
	}

	private static KosilkaV3UiBinder uiBinder = GWT
			.create(KosilkaV3UiBinder.class);
	
	private ApplicationController appController = ApplicationController.getInstance();
	
	@UiField public InfoPanel infoPanel;		
	@UiField(provided = true) CellList<HarmonogramCellListRow> harmonogramsCellList;		
	@UiField DockLayoutPanel contentContainer;	
	@UiField Button addHarmonogramButton;	
	@UiField Button removeHarmonogramButton;		
	@UiField DockLayoutPanel contentWrapper;
	@UiField ScrollPanel harmonogramsScrollPanel;	
	@UiField HTMLPanel harmonogramStack;
	@UiField public ListBox yearsListBox;	
	@UiField public ListBox monthListBox;
	@UiField HeaderPanel headerPanel;
	
	public ArrayList<HarmonogramCellListRow> harmonogramsCellListArrayList = new ArrayList<HarmonogramCellListRow>();
	public ArrayList<StudyFieldCellListRow> studyFieldsArrayList = new ArrayList<StudyFieldCellListRow>();
		
	private ArrayList<String> yearsArrayList = new ArrayList<String>();
	private HashMap<String, YearModel> yearsHashMap = new HashMap<String, YearModel>();
	private TabLayoutPanel tabPanel = new TabLayoutPanel(3, Unit.EM);	
		
	
	@Override
	public void onModuleLoad() {
		
		/*
		 * ak je nastavena prihlasovacia cookie a je nastavena hodnota pernement, tak 
		 * nezobrazuj prihlasovaci dialog
		 */
		if((Cookies.getCookie("sessionID") != null) && Cookies.getCookie("pernament") != null){			
			
			/*
			 * Code-splitting 
			 * http://code.google.com/webtoolkit/doc/latest/DevGuideCodeSplitting.html
			 */
			GWT.runAsync(new RunAsyncCallback() {
				
				@Override
				public void onSuccess() {
					initApplication();
				}
				
				@Override
				public void onFailure(Throwable reason) {
					Window.alert("AJAX: Download application code failed");					
				}
			});
			
		}
		else {			
			/*
			 * Zobrazi prihlasovaci formular
			 */
			RootLayoutPanel.get().add(new LoginPage(this));			
		}
	}

	@Override
	public void onResize(ResizeEvent event) {
		refreshHarmonogramsCellList();		
	}
	
	@UiHandler("addHarmonogramButton")
	public void addHarmonogram(ClickEvent e){
		
		//code-splitting
		GWT.runAsync(new RunAsyncCallback() {
			
			@Override
			public void onSuccess() {
				new HarmonogramAddDialog().show();				
			}
			
			@Override
			public void onFailure(Throwable reason) {
				Window.alert("AJAX: Download add dialog code failed");
			}
		});
		
	}
	
	@UiHandler("monthListBox")
	public void monthListBoxChange(ChangeEvent e){
		yearListBoxChange();
	}
	
	public void refreshHarmonogramsCellList(){
		
		harmonogramsCellList.setPageSize(harmonogramsCellListArrayList.size());
		harmonogramsCellList.setRowCount(harmonogramsCellListArrayList.size(),true);
		harmonogramsCellList.setRowData(0, harmonogramsCellListArrayList);
		
		//Nastavenie vysky laveho panelu		
		int windowHeight = Window.getClientHeight();		
		int contentPosition = contentContainer.getAbsoluteTop();
		int height = (windowHeight - contentPosition);
		contentContainer.setHeight(height + "px");
		infoPanel.setHeight(height + "px");
		
		
		int scrollPanelHeight = windowHeight - harmonogramsCellList.getAbsoluteTop();
		harmonogramsScrollPanel.setHeight((int)scrollPanelHeight + "px");
		//studyFieldsScrollPanel.setHeight((int)(0.45 * scrollPanelHeight) + "px");
		
		//skryje informaciu o nacitani stranky
		appController.hideLoadingMessage();		
	}

	public void refreshMonthListBox() {
		refreshMonthListBox(
				yearsListBox.getValue(yearsListBox.getSelectedIndex()), 
				Integer.valueOf(monthListBox.getValue(monthListBox.getSelectedIndex()))
		);		
	}
	
	public void refreshMonthListBox(String yid, final int selectMonth) {
		
		monthListBox.clear();
		
		String method = "harmonogram.getHarmonogramsCount";
		Object[] params = new Object[]{yid};
		
		XmlRpcRequest<HashMap<String,Integer>> request = new XmlRpcRequest<HashMap<String,Integer>>(
				MyXmlRpcClient.createClient(), method, params, new AsyncCallback<HashMap<String,Integer>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
				
			}
			
			@Override
			public void onSuccess(HashMap<String,Integer> result) {
				
				//12 mesiacov
				for (int i = 1; i <= 12; i++) {
					//vklada do listobxu mesiac vo formate [mesiac]. ([pocet harmonogramov v mesiaci])
					monthListBox.addItem(i + ". (" + result.get(String.valueOf(i)) + ")", String.valueOf(i));					
				}		
				
				//vyberie aktualny mesiac
				monthListBox.setSelectedIndex(selectMonth - 1);//-1 lebo inex zacina od 0 a mesiace od 1
			}
		});
		
		request.execute();
				
		
	}

	public void refreshYearsListBox(){
		
		yearsListBox.clear();
		yearsHashMap.clear();
		yearsArrayList.clear();
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String yearMethod = "year.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> yearRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(client, yearMethod, null, 
				new AsyncCallback<ArrayList<HashMap<String, String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(ArrayList<HashMap<String, String>> result) {
				
				for (HashMap<String,String> hashMap : result) {
					yearsArrayList.add(hashMap.get("yid"));
					yearsHashMap.put(hashMap.get("year_name"), new YearModel(hashMap.get("yid"), hashMap.get("year_name")));
					yearsListBox.addItem(hashMap.get("year_name"), hashMap.get("yid"));
				}	
				
				//vyber v listobxe vyber hodnotu na zaklade vybrateho harmonogramu			
				yearsListBox.setSelectedIndex(yearsArrayList.indexOf(appController.getSelectedHarmonogram().yid));
			}
		});
		yearRequest.execute();		
	}
	
	@UiHandler("removeHarmonogramButton")
	public void removeHarmonogram(ClickEvent event){
		
		try{
			
			String harmonogramName = appController.getSelectedHarmonogram().harmonogramName;
			
			//Pre istotu dva potvrdzovacie dialogy :)
			if(!Window.confirm("Naozaj si prajete odstrániť harmonogram " +
					harmonogramName + "?")){
				return;	
			}					
			
			boolean confirm = Window.confirm("Odstrením harmonogramu dôjde aj" +
					" k odstráneniu všetkých pridelených známok. Naozaj si prajete " +
					"odstrániť harmonogram " + harmonogramName + "? ");
			
			if(confirm){
				removeSelectedHarmonogram();		
			}
		} catch(NullPointerException e){
			Window.alert(appController.constants.harmonogramNotSelected());
		}
	}

	@UiHandler("yearsListBox")
	public void yearsListBoxChange(ChangeEvent e){
		yearListBoxChange();	
		
	}
	
	private void fetchHarmonograms(){
		
		harmonogramsCellListArrayList.clear();
		
		XmlRpcClient client = MyXmlRpcClient.createClient();		
		String method = "harmonogram.fetchAll";	
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> cellListRequest = new XmlRpcRequest<ArrayList<HashMap<String, String>>>(
				client, method,null, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(ArrayList<HashMap<String, String>> result) {
				parseHarmonogramsCellListXMLResponse(result);				
			}			
		});
		cellListRequest.execute();	
	}
	
	private void fetchHarmonograms(String yid,int month){
					
		harmonogramsCellListArrayList.clear();
		
		XmlRpcClient client = MyXmlRpcClient.createClient();		
		String method = "harmonogram.fetchAll";	
		Object[] params = new Object[]{yid,month};
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> cellListRequest = new XmlRpcRequest<ArrayList<HashMap<String, String>>>(
				client, method,params, new AsyncCallback<ArrayList<HashMap<String, String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());
			}

			@Override
			public void onSuccess(ArrayList<HashMap<String, String>> result) {
				parseHarmonogramsCellListXMLResponse(result);				
			}			
		});
		
		cellListRequest.execute();	
	}
	

	private String getAcademicYear(int year,int month){
		
		String academicYear = null;
		
		if(month >= 1 && month <= 8){
			academicYear = (year - 1) + "/" + year;
		}
		else {
			academicYear = year + "/" + (year + 1);
		}
		
		return academicYear;
	}
	
	
	/**
	 * Vracia akutalny mesiac od 1-12
	 * 
	 * @return mesiac v rozsahu 1-12
	 */
	private int getActualMonth(){
		return new Date().getMonth() + 1;//normalne metoda vracia hodnoty v rozsahu 0-11, preto +1
	}
	
	/**
	 * Vrati aktualny rok
	 * 
	 * @return aktualny rok
	 */
	private int getActualYear() {
		return new Date().getYear() + 1900;
	}
	
	private String getYidFromYear(int year, int month) {
							
		return yearsHashMap.get(getAcademicYear(year, month)).getYid();
	}

	private void initApplication(){	
				
		/*
		 * Inicializacie ktore musia byt este pred createAndBindUi()
		 */		
		harmonogramsCellList = new CellList<HarmonogramCellListRow>(new HarmonogramCell());
		
		DockLayoutPanel container = (DockLayoutPanel) uiBinder.createAndBindUi(this);			
		RootLayoutPanel.get().add(container);		
	
		//nastavenia pre ApplicationController
		appController.setHarmonogramContent(new HarmonogramContent());
		appController.setResultContent(new ResultContent());
		appController.setHeaderPanel(headerPanel);
		appController.setApplicationParentWidget(this);
		
		//zobrazi na vrchole stranku spravu o nacitani
		appController.showLoadinMessage();
		
		//Vytvorenie tabov Studenti a Vysledky
		tabPanel.add(appController.getResultContent(), appController.constants.resultsTab());
		tabPanel.add(appController.getHarmonogramContent(), appController.constants.studentsTab());	
		
		//trieda si v metode onResize sama meni velkost widgetov v zavislosti na velkosti okna
		Window.addResizeHandler(this);

		//vlozi css do DOM stranky, inak by sa css uvedene v cz.edukomplex.kosilka.style neaplikovalo
		appController.getApplicationResources().applicationStyle().ensureInjected();			
		
		//nacita ak. roky do listobxu, sucasne inicializuje aj harmonogramy a listbox s mesiacami
		initYearListBox();
		
		//skryje info panel
		appController.toggleInfoPanel();
	}
	

	
	private void initHarmonogramsCellList() {
		
		SingleSelectionModel<HarmonogramCellListRow> harmonogramSelectionModel = appController.getHarmonogramSelectionModel();
		
		harmonogramsCellList.setKeyboardSelectionPolicy(KeyboardSelectionPolicy.ENABLED);
		
		harmonogramSelectionModel.addSelectionChangeHandler(new SelectionChangeEvent.Handler() {
			
			@Override
			public void onSelectionChange(SelectionChangeEvent event) {					
							
				//vlozenie panelu
				appController.setAppContent(tabPanel);
				
				/*if(studyFieldSelectionModel.getSelectedObject() != null){
					studyFieldSelectionModel.setSelected(studyFieldSelectionModel.getSelectedObject(), false);
				}*/
			}
		});
		harmonogramsCellList.setSelectionModel(harmonogramSelectionModel);
		//Vyberie prvy harmonogram v CellListe a automaticky ho nacita
		try{
			harmonogramSelectionModel.setSelected(harmonogramsCellListArrayList.get(0), true);
		} catch(Exception e){}
		refreshHarmonogramsCellList();		
	}
	
	
	private void initYearListBox() {
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String yearMethod = "year.fetchAll";
		
		XmlRpcRequest<ArrayList<HashMap<String, String>>> yearRequest = new XmlRpcRequest<ArrayList<HashMap<String,String>>>(client, yearMethod, null, 
				new AsyncCallback<ArrayList<HashMap<String, String>>>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert(caught.getMessage());				
			}

			@Override
			public void onSuccess(ArrayList<HashMap<String, String>> result) {
								
				//kontrola ci uz existuje nejaky rok
				if(result.size() > 0){
					for (HashMap<String,String> hashMap : result) {						
						yearsArrayList.add(hashMap.get("yid"));
						yearsHashMap.put(hashMap.get("year_name"), new YearModel(hashMap.get("yid"), hashMap.get("year_name")));
						yearsListBox.addItem(hashMap.get("year_name"), hashMap.get("yid"));
					}								
					
					//inicializacia mesiacov
					refreshMonthListBox(
							yearsHashMap.get(getAcademicYear(getActualYear(), getActualMonth())).getYid(),
							getActualMonth()
					);
					

					
					//vyber v listobxe vyber hodnotu v ktorom roku sa uzivatel nachadza				
					yearsListBox.setSelectedIndex(yearsArrayList.indexOf(							
							yearsHashMap.get(getAcademicYear(getActualYear(), getActualMonth())).getYid())
					);
					
					//vrati len harmonogramy z posledneho roka
					fetchHarmonograms(
							getYidFromYear(getActualYear(),getActualMonth()),
							getActualMonth()
					);
					
				}
				//neskor mozno odstranit
				else {
					fetchHarmonograms(); //vrati vsetky harmonogramy
				}							
			}
		});
		yearRequest.execute();		
	}
	
	
	private void parseHarmonogramsCellListXMLResponse(ArrayList<HashMap<String, String>> result){
		
		DateTimeFormat fomat = DateTimeFormat.getFormat(HarmonogramModel.DATABASE_DATE_FORMAT);
		
		for (int i = 0; i < result.size(); i++) {
			HarmonogramCellListRow harmonogramCellListRow = new HarmonogramCellListRow(
					result.get(i).get("hid"),  
					result.get(i).get("harmonogram_name"), 
					result.get(i).get("sfid"),  
					result.get(i).get("studyfield_name"), 
					fomat.parse(result.get(i).get("date")),
					result.get(i).get("yid"),
					result.get(i).get("month")
			);
						
			harmonogramsCellListArrayList.add(harmonogramCellListRow);
		}
	
		initHarmonogramsCellList();
	}
	
	
	private void removeSelectedHarmonogram() {
		
		String hid = appController.getSelectedHarmonogram().hid;		
		
		XmlRpcClient client = MyXmlRpcClient.createClient();
		String methodName = "harmonogram.remove";
		Object[] params = new Object[]{hid};
		XmlRpcRequest<Boolean> removeHarmonogramRequest = new XmlRpcRequest<Boolean>(
				client, methodName, params, new AsyncCallback<Boolean>() {

					@Override
					public void onFailure(Throwable caught) {
						Window.alert(caught.getMessage());
					}

					@Override
					public void onSuccess(Boolean result) {						
						if (result.equals(true)) {
							harmonogramsCellListArrayList.remove(appController.getSelectedHarmonogram());
							refreshHarmonogramsCellList();
							refreshMonthListBox();
							appController.getHarmonogramSelectionModel().setSelected(harmonogramsCellListArrayList.get(0), true);
							
						}
					}
		});
		removeHarmonogramRequest.execute();		
	}
	
	
	private void yearListBoxChange() {
		String yid = yearsListBox.getValue(yearsListBox.getSelectedIndex());
		int month = Integer.valueOf(monthListBox.getValue(monthListBox.getSelectedIndex()));		
		refreshMonthListBox(yid,month);
		fetchHarmonograms(yid,month);
	}
	
}