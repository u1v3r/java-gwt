package cz.edukomplex.kosilka.client.content.result;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class ResultCell extends AbstractCell<ResultCellListRow> {

	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			ResultCellListRow value, SafeHtmlBuilder sb) {
		
		sb.appendHtmlConstant("<table>");
		sb.appendHtmlConstant("<tr><td><strong>");
		sb.appendEscaped(value.harmonogramName);
		sb.appendHtmlConstant("</strong></tr></td>");
		sb.appendHtmlConstant("<tr><td>");
		if(value.date != null ) sb.appendEscaped(value.date);
		sb.appendHtmlConstant("</tr></td>");
		sb.appendHtmlConstant("</table>");
	}

}
