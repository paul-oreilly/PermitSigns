package com.oreilly.permitsigns.interactions;

import com.oreilly.common.interaction.text.Interaction;
import com.oreilly.common.interaction.text.InteractionPage;
import com.oreilly.common.interaction.text.TitledInteractionPage;
import com.oreilly.permitsigns.PermitSigns;
import com.oreilly.permitsigns.PriceRecord;


// TODO: Highlight the "Page x of y"

// views the economic data for the selected PermitAlias (via SelectPermitAlias)
public class ViewEconomicData extends TitledInteractionPage {
	
	private static final String PAGE_DATA = "view_economic_data_pages";
	private static final String PAGE_NUMBER = "view_economic_data_page_number";
	
	// allow 4 for borders, 1 for title, and 2 more for "Page x of y" at the bottom of each page
	private static final int MAX_LINES = InteractionPage.MAX_LINES - 7;
	
	
	public ViewEconomicData() {
		super();
		defaultTitle = "Sign Price Details";
	}
	
	
	@Override
	public String getDisplayText( Interaction interaction ) {
		// we may already have generated the display text (and therefore have multiple pages)
		Object displayData = interaction.context.get( PAGE_DATA );
		if ( displayData != null ) {
			String asPage = showPage( interaction, displayData );
			if ( asPage == null )
				interaction.context.remove( PAGE_DATA );
			else
				return asPage;
		}
		// otherwise, we need to generate the raw information...
		String rawInfo = generateRawInfo( interaction );
		// depending on how many lines we have, we may need to use pages...
		String[] splitDisplay = rawInfo.split( "\n" );
		if ( splitDisplay.length > MAX_LINES ) {
			return generatePagedData( interaction, splitDisplay );
		} else
			return rawInfo + "\n(type \'edit\' to change)";
	}
	
	
	protected String showPage( Interaction interaction, Object displayDataObj ) {
		// check display data is valid
		String[] displayData = null;
		if ( displayDataObj instanceof String[] )
			displayData = (String[])displayDataObj;
		else
			return null;
		// we are a page view.. therefore, we also must have a page number...
		Object pageNumberObj = interaction.context.get( PAGE_NUMBER );
		Integer pageNumber = 0;
		if ( pageNumberObj != null )
			if ( pageNumberObj instanceof Integer )
				pageNumber = (Integer)pageNumberObj;
		// range check the display data
		if ( pageNumber >= displayData.length )
			return null;
		// return the selected page
		return displayData[pageNumber];
	}
	
	
	protected String generateRawInfo( Interaction interaction ) {
		Object permitAliasObj = interaction.context.get( SelectPermitAlias.CONTEXT_SELECTED_ALIAS );
		if ( permitAliasObj != null ) {
			String permitAlias = permitAliasObj.toString();
			PriceRecord economicData = PermitSigns.instance.prices.getPriceRecord( permitAlias );
			if ( economicData != null ) {
				return economicData.toHumanString();
			} else
				return "No matching economic data found for " + permitAlias;
		}
		return "No permit alias selected, unable to find matching economic data";
	}
	
	
	protected String generatePagedData( Interaction interaction, String[] splitDisplay ) {
		// we allow 2 extra lines for adding a blank and "Page x of y"
		// we need to use pages.. find a blank line to split the page on. (Or split at half way, whichever first.
		String[] pages = new String[2];
		boolean pagesRemain = true;
		// allow 3 lines for title + border, another 2 for text border, and 2 more for "Page x of y" at the bottom
		int max_lines = InteractionPage.MAX_LINES - 7;
		while ( pagesRemain ) {
			int index = max_lines;
			while ( ( !splitDisplay[index].contentEquals( "\n" ) ) & index > max_lines / 2 )
				index--;
			String page = "";
			for ( int i = 0; i <= index; i++ )
				page += splitDisplay[i] + "\n";
			pages[pages.length] = page;
			String[] newSplitDisplay = new String[0];
			for ( int i = index + 1; i < splitDisplay.length; i++ )
				newSplitDisplay[newSplitDisplay.length] = splitDisplay[i];
			splitDisplay = newSplitDisplay;
		}
		// add a "page x of y" at the bottom of each page
		int currentPage = 1;
		int pageCount = pages.length;
		for ( @SuppressWarnings("unused")
		String value : pages ) {
			value += "\nPage " + currentPage + " of " + pageCount + " (Type \'edit\' to change)";
			currentPage++;
		}
		// add the data to our context, and return the first page.
		interaction.context.put( PAGE_DATA, pages );
		interaction.context.put( PAGE_NUMBER, Integer.valueOf( 1 ) );
		return pages[0];
	}
	
	
	@Override
	public String acceptValidatedInput( Interaction interaction, Object data ) {
		// clean data
		String universal = data.toString().toLowerCase().trim();
		// if we are paged, and input starts with "page", we wait for more input (display again)
		Object pageDataObj = interaction.context.get( PAGE_DATA );
		if ( pageDataObj != null )
			if ( pageDataObj instanceof String[] ) {
				String[] pageData = (String[])pageDataObj;
				if ( universal.startsWith( "page" ) ) {
					interaction.pageWaitingForInput = true;
					String pageNumStr = universal.replace( "page", "" ).trim();
					int pageNum = 0;
					try {
						pageNum = Integer.parseInt( pageNumStr );
					} catch ( NumberFormatException error ) {
						return pageNumStr + " is not a valid page number";
					}
					if ( pageNum > pageData.length )
						return "There are only " + pageData.length + " pages, and " + pageNum + " is not one of them";
					// otherwise.. set the current page to display, and return
					interaction.context.put( PAGE_NUMBER, Integer.valueOf( pageNum ) );
					return null;
				}
			}
		// if we are not dealing with page data...
		// "edit" will pass on to EditSignData
		if ( universal.contentEquals( "edit" ) )
			interaction.nextPage( new EditEconomicData() );
		// anything else will exit
		return null;
	}
	
}
