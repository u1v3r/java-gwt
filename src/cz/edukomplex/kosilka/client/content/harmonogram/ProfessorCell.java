package cz.edukomplex.kosilka.client.content.harmonogram;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

import cz.edukomplex.kosilka.client.model.ProfessorModel;

public class ProfessorCell extends AbstractCell<ProfessorModel> {
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			ProfessorModel value, SafeHtmlBuilder sb) {
				
		sb.appendHtmlConstant("<table>");
		sb.appendHtmlConstant("<tr><td>");		
		sb.appendEscaped(value.getTitleBefore());
		sb.appendEscaped(value.getLastname());
		sb.appendEscaped(" ");
		sb.appendEscaped(value.getFirstname());
		sb.appendEscaped(value.getTitleBehind());
		sb.appendHtmlConstant("</td></tr>");				
		sb.appendHtmlConstant("</table>");
	}

}

