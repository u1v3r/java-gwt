package cz.edukomplex.kosilka.client.content.block;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;

public class StudyFieldCell extends AbstractCell<StudyFieldCellListRow> {
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			StudyFieldCellListRow value, SafeHtmlBuilder sb) {
		sb.appendHtmlConstant("<br>");
		sb.appendEscaped(value.name);
		sb.appendHtmlConstant("<br>&nbsp;");
	}

}
