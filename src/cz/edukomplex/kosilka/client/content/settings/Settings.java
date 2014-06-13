package cz.edukomplex.kosilka.client.content.settings;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Widget;

import cz.edukomplex.kosilka.client.ApplicationController;
import cz.edukomplex.kosilka.client.content.block.BlockContent;

public class Settings extends Composite {

	private static SettingsUiBinder uiBinder = GWT
			.create(SettingsUiBinder.class);

	interface SettingsUiBinder extends UiBinder<Widget, Settings> {
	}
		
	@UiField BlockContent blockContent;
	
	private ApplicationController appController = ApplicationController.getInstance();
	
	public Settings() {
		initWidget(uiBinder.createAndBindUi(this));
		appController.setBlockContent(blockContent);
	}

}
