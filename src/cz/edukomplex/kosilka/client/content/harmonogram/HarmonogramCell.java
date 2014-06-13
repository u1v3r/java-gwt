package cz.edukomplex.kosilka.client.content.harmonogram;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import cz.edukomplex.kosilka.client.model.HarmonogramModel;

/**
 * 
 * @author Radovan Dvorsky
 * 
 * Predstavuje vzhlad bunky v CellList
 *
 */
public class HarmonogramCell extends AbstractCell<HarmonogramCellListRow> {


	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			HarmonogramCellListRow value, SafeHtmlBuilder sb) {
		
		if (value == null) {
			return;
		}	
		
		DateTimeFormat format = DateTimeFormat.getFormat(HarmonogramModel.OUTPUT_DATE_FORMAT);
		
		sb.appendHtmlConstant("<table>");
		sb.appendHtmlConstant("<tr><td><strong>"); 
		sb.appendEscaped(value.harmonogramName);
		sb.appendHtmlConstant("</strong></td></tr>");
		
		sb.appendHtmlConstant("<tr><td><i>");
		sb.appendEscaped(value.studyFieldName);
		sb.appendHtmlConstant("</i></td></tr>");
				
		sb.appendHtmlConstant("<tr><td><i>");
		sb.appendEscaped(format.format(value.date));
		sb.appendHtmlConstant("</i></td></tr>");		
		sb.appendHtmlConstant("</table>");
	}

}
