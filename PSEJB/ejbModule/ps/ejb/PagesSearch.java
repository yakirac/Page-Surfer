package ps.ejb;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import ps.database.Site;

/**
 * Interface for the Stateless session bean
 * 
 * @author  Yakira C. Bristol
 *
 */

public interface PagesSearch {
	public ArrayList<Site> getSites(String sw);
	public Map<String, ArrayList<Site>> sortSites(ArrayList<Site> sts);
	public Map<Site, HashMap<String, ArrayList<Site>>> diffSortSites(ArrayList<Site> rsites);

}
