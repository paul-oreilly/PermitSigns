package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.interfaces.HasTitle;
import com.oreilly.common.interaction.text.pages.MenuPage;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.records.EconomicData;


public class EditEconomicData extends MenuPage implements HasTitle {
	
	public EditEconomicData() {
		super();
	}
	
	
	@Override
	public String getTitle() {
		// TODO Auto-generated method stub
		return "PermitSigns Economic Editing Assistant";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		Object permitAliasObj = interaction.context.get( SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAliasObj != null ) {
			String permitAlias = permitAliasObj.toString();
			EconomicData economicData = PermitSigns.instance.economy.getEconomicData( permitAlias );
			if ( economicData != null ) {
				return "Economic data for " + permitAlias + ":\n\n" +
						economicData.toHumanString( false, "" ) + "\n\n" +
						"Please select an option to edit (or exit to quit)\n";
				// TODO: Continue...
				
			} else
				return "No matching economic data found for " + permitAlias;
		}
		return "No permit alias selected, unable to find matching economic data";
	}
	
}
