package cz.edukomplex.kosilka.client.style;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.resources.client.ImageResource.ImageOptions;
import com.google.gwt.user.cellview.client.CellTable;


/**
 * Rozhranie pomocou ktoreho je mozne pristupovat ku vsetkym zdrojom 
 * (obrazkom, css suborom prip. inym druhom suborov).
 *   
 * 
 * @author Radovan Dvorsky
 *
 */
public interface MyResources {	
	
	public interface ApplicationResources extends ClientBundle{
		public final static ApplicationResources INSTANCE = GWT.create(ApplicationResources.class);
		
		@Source("cz/edukomplex/kosilka/client/style/Application.css")
		ApplicationStyle applicationStyle();	
		
		@Source("img/small/delete.png")
		ImageResource delete();
	}

	
	public interface CellTableResources extends CellTable.Resources{
		public final static CellTableResources INSTANCE = GWT.create(CellTableResources.class);
		
		@Source({CellTable.Style.DEFAULT_CSS,"cz/edukomplex/kosilka/client/style/MyCellTable.css"})
		CellTableStyle cellTableStyle();
		
		@ImageOptions(height = 15,width = 15)
		@Source("img/go-bottom.png")		
		ImageResource goBottom();
		
		@ImageOptions(height = 15,width = 15)
		@Source("img/go-top.png")		
		ImageResource goTop();	
		
	}	
	
	/**
	 * Poskytuje rozhranie pre zakladny CSS styl aplikacie
	 * 
	 */
	public interface ApplicationStyle extends CssResource{
		String brightColor();
		String darkColor();
		String borderColor();
		String actionPanel();
		String scrollPanel();
		String cellTableHead();
		String cellListPanel();
		String white();
		String black();
		String border();
		String borderRight();
		String borderLeft();
		String alignRight();
		String alignLeft();
		String blockContentHeadHeight();
		String brightColorStyle();
		String blockContentBorder();
		String errorMessageBox();
		String infoMessageBox();
	}	
	
	/**
	 * Rozhranie upravuje vzhlad {@link CellTable}
	 *
	 */
	public interface CellTableStyle extends CellTable.Style{	
		String cellTableNavigationColumn();
	}
	
}
