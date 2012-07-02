package ps.ejb;

import java.util.ArrayList;
import java.util.Map;

import ps.database.Site;
/**
 * Interface for the Stateful session bean
 * 
 * @author  Yakira C. Bristol
 *
 */
public interface PageSurferData {
	public Map<String, ArrayList<Site>> getSearches();
	public void setSearches(String word, ArrayList<Site> sites);

}
