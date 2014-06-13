package cz.edukomplex.kosilka.client;

import java.util.Collections;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.view.client.SingleSelectionModel;

import cz.edukomplex.kosilka.client.content.HeaderPanel;
import cz.edukomplex.kosilka.client.content.InfoPanel;
import cz.edukomplex.kosilka.client.content.block.BlockContent;
import cz.edukomplex.kosilka.client.content.block.StudyFieldCellListRow;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramCellListRow;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramCellTableRow;
import cz.edukomplex.kosilka.client.content.harmonogram.HarmonogramContent;
import cz.edukomplex.kosilka.client.content.result.ResultContent;
import cz.edukomplex.kosilka.client.i18n.KosilkaV3Constants;
import cz.edukomplex.kosilka.client.style.MyResources;

public final class ApplicationController {
	
	public KosilkaV3Constants constants = GWT.create(KosilkaV3Constants.class);
	
	private static final ApplicationController instance = new ApplicationController();
	private static boolean isInfoPanelVisible = true;
	
	private MyResources.ApplicationResources applicationResources = MyResources.ApplicationResources.INSTANCE;
	private MyResources.CellTableResources cellTableResources = MyResources.CellTableResources.INSTANCE;
	private SingleSelectionModel<HarmonogramCellListRow> harmonogramSelectionModel = new SingleSelectionModel<HarmonogramCellListRow>();
	private SingleSelectionModel<StudyFieldCellListRow> studyFieldSelectionModel = new SingleSelectionModel<StudyFieldCellListRow>();
	private KosilkaV3 applicationParentWidget;
	private BlockContent blockContent;
	private ResultContent resultContent;	
	private HarmonogramContent harmonogramContent;
	private HeaderPanel headerPanel;

	private ApplicationController(){		
	}
	
	public static ApplicationController getInstance(){		 
        return instance;
	}
	
	public MyResources.ApplicationResources getApplicationResources() {
		return applicationResources;
	}

	public MyResources.CellTableResources getCellTableResources() {
		return cellTableResources;
	}

	public HarmonogramContent getHarmonogramContent() {
		return harmonogramContent;
	}

	public ResultContent getResultContent() {
		return resultContent;
	}

	public BlockContent getBlockContent() {
		return blockContent;
	}
	
	public void setBlockContent(BlockContent blockContent) {
		this.blockContent = blockContent;
	}

	public void setResultContent(ResultContent resultContent) {
		this.resultContent = resultContent;
	}

	public void setHarmonogramContent(HarmonogramContent harmonogramContent) {
		this.harmonogramContent = harmonogramContent;
	}

	public SingleSelectionModel<HarmonogramCellListRow> getHarmonogramSelectionModel() {
		return harmonogramSelectionModel;
	}
	
	public HarmonogramCellListRow getSelectedHarmonogram(){
		return harmonogramSelectionModel.getSelectedObject();
	}
	
	public void addRowToResultsTable(HarmonogramCellTableRow row){
		resultContent.addRowToTable(row);
	}
	
	public int getHarmonogramRowsCount(){
		return harmonogramContent.getHarmonogramRowCount();
	}
	
	public void refreshBlockContent(){		
		blockContent.clearSubjectsListBox();
		blockContent.onStudyFieldSelected();
	}

	public void refreshResultCellTable() {
		resultContent.initCellTable();		
	}
	
	public void setApplicationParentWidget(KosilkaV3 parent){
		this.applicationParentWidget = parent;
	}
	
	public KosilkaV3 getApplicationParentWidget(){
		return this.applicationParentWidget;
	}
	
	public void updateInfoPanel(){
		applicationParentWidget.infoPanel.initPanel();
	}
	
	public SingleSelectionModel<StudyFieldCellListRow> getStudyFieldSelectionModel(){
		return this.studyFieldSelectionModel;
	}
	
	public StudyFieldCellListRow getSelectedStudyField(){
		return studyFieldSelectionModel.getSelectedObject();
	}
	
	public void toggleInfoPanel(){
		/**
		 * Strasna prasacina na skrytie a zmensienie praveho infoPanelu
		 * @todo vymyslet nieco rozumnejsie 
		 */
		//widget neexistuje, tak pridaj, inak odstran
		if(isInfoPanelVisible){			
			
			applicationParentWidget.contentContainer.remove(applicationParentWidget.infoPanel);
			applicationParentWidget.contentContainer.remove(applicationParentWidget.contentWrapper);			
			applicationParentWidget.contentContainer.forceLayout();			
			applicationParentWidget.contentContainer.addEast(applicationParentWidget.infoPanel, 40);
			applicationParentWidget.contentContainer.add(applicationParentWidget.contentWrapper);
			applicationParentWidget.contentContainer.forceLayout();
			isInfoPanelVisible = false;
			applicationParentWidget.infoPanel.showPanelLabel(false);
			applicationParentWidget.infoPanel.toggleInfoPanel.setText("+");
			applicationParentWidget.infoPanel.setStyleName(applicationResources.applicationStyle().white());			
		}
		else{
			applicationParentWidget.contentContainer.remove(applicationParentWidget.infoPanel);
			applicationParentWidget.contentContainer.remove(applicationParentWidget.contentWrapper);			
			applicationParentWidget.contentContainer.forceLayout();		
			
			applicationParentWidget.contentContainer.addEast(applicationParentWidget.infoPanel, 325);
			applicationParentWidget.contentContainer.add(applicationParentWidget.contentWrapper);
			applicationParentWidget.contentContainer.forceLayout();
			isInfoPanelVisible = true;
			applicationParentWidget.infoPanel.showPanelLabel(true);
			applicationParentWidget.infoPanel.toggleInfoPanel.setText("-");
			applicationParentWidget.infoPanel.setStyleName(applicationResources.applicationStyle().black());
		}		
	}
	
	public void redrawCellList(){
		applicationParentWidget.harmonogramsCellList.redraw();
		//applicationParentWidget.studyFieldsCellList.redraw();
	}	
	
	public void setAppContent(Widget content){
		applicationParentWidget.contentWrapper.clear();//odstranenie widgetov, ak uz nejake obsahuje
		applicationParentWidget.contentWrapper.setVisible(true);//viditilne, ak nie je
		applicationParentWidget.contentWrapper.add(content);		
	}
	
	public Widget getApplicationContainer(){
		return applicationParentWidget.contentContainer;
	}
	
	/**
	 * Prida jednu bunku do harmonogramsCellList a nastavi ako vybrate
	 */
	public void addCellListRow(HarmonogramCellListRow row){		
		
		String cellListYid;
		String cellListMonth;
		
		//este nie je vybrana ziadna hodnota, resp. neexistuje ziadny harmonogram, ktory by vytvoril ak rok
		if(getApplicationParentWidget().yearsListBox.getSelectedIndex() == -1){
			cellListYid = row.yid;//ak este neexistuje ziadny harmonogram, logicky nemoze existovat ani ziadny rok
			cellListMonth = row.getMonth();
		}
		else {
			cellListYid = getApplicationParentWidget().yearsListBox.getValue(getApplicationParentWidget().yearsListBox.getSelectedIndex());
			cellListMonth = getApplicationParentWidget().monthListBox.getValue(getApplicationParentWidget().monthListBox.getSelectedIndex());
		}
				
		String newYid = row.yid;
		String newMonth = row.getMonth();
		
		//do cell listu pridavaj len harmonogramy priradene pre vybrany rok a vybrane mesiac
		if(cellListYid.equals(newYid) && cellListMonth.equals(newMonth)){
			getApplicationParentWidget().harmonogramsCellListArrayList.add(row);//pridaj do ArrayList
			Collections.sort(getApplicationParentWidget().harmonogramsCellListArrayList);//Zorad podla abecedy (nerozlisuje male a velke pismena)
			getApplicationParentWidget().refreshHarmonogramsCellList();//obnov zoznam na zaklade novej hodnoty
			getHarmonogramSelectionModel().setSelected(row, true);//vyber pridany riadok
			//tabPanel.selectTab(0);//vyber tab Studenti
		}
		else {
			Window.alert(constants.differentYidWarning());
		}
		
		getApplicationParentWidget().refreshYearsListBox();
	}

	public void setHeaderPanel(HeaderPanel headerPanel) {
		this.headerPanel = headerPanel;		
	}
	
	public void setInfoMessage(String text){
		this.headerPanel.setMesageBoxText(text);
	}
	
	public void setInfoMessage(String text,boolean error){
		if(error){			
			this.headerPanel.setMesageBoxText(text, error);
			return;
		}
		
		setInfoMessage(text);		
	}
	
	public void showLoadinMessage(){
		this.headerPanel.showLoadingMessage();
	}
	
	public void hideLoadingMessage(){
		this.headerPanel.hideLoadingMessage();
	}
}
