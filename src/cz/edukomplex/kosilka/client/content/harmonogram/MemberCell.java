package cz.edukomplex.kosilka.client.content.harmonogram;

import com.google.gwt.cell.client.AbstractCell;
import com.google.gwt.safehtml.shared.SafeHtmlBuilder;
import com.google.gwt.user.client.ui.AbstractImagePrototype;

import cz.edukomplex.kosilka.client.model.ProfessorModel;
import cz.edukomplex.kosilka.client.style.MyResources;

public class MemberCell extends AbstractCell<ProfessorModel>{
	
	private final String imageHtml;
	
	public MemberCell(){
		MyResources.ApplicationResources resources = MyResources.ApplicationResources.INSTANCE;
		this.imageHtml = AbstractImagePrototype.create(resources.delete()).getHTML();
	}
	
	@Override
	public void render(com.google.gwt.cell.client.Cell.Context context,
			ProfessorModel value, SafeHtmlBuilder sb) {
		
		sb.appendHtmlConstant("<table>");
		sb.appendHtmlConstant("<tr><td>");		
		sb.appendEscaped(value.getTitleBefore());
		sb.appendEscaped(value.getLastname());
		sb.appendEscaped(" ");
		sb.appendEscaped(value.getFirstname());
		sb.appendEscaped(" ");
		sb.appendEscaped(value.getTitleBehind());
		sb.appendHtmlConstant("</td>");
		sb.appendHtmlConstant("<td>");		
		sb.appendHtmlConstant(imageHtml);
		sb.appendHtmlConstant("</td></tr>");
		sb.appendHtmlConstant("</table>");
	}

}
