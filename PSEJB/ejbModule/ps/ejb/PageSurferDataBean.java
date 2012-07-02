package ps.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.ejb.Remote;
import javax.ejb.Stateful;

import ps.database.Site;
/**
 * Stateful session bean that stores information about 
 * recent searches
 * 
 * @author  Yakira C. Bristol
 *
 */
@Stateful
@Remote(PageSurferData.class)
public class PageSurferDataBean implements PageSurferData{
	Map<String, ArrayList<Site>> recentSearches = new HashMap<String, ArrayList<Site>>();
	
	@Override
	public Map<String, ArrayList<Site>> getSearches() {
		
		return recentSearches;
	}

	@Override
	public void setSearches(String word, ArrayList<Site> sites) {
		recentSearches.put(word, sites);
		
	}

}
