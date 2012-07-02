package ps.web;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ps.ejb.PagesSearch;
import ps.ejb.PageSurferData;
import ps.database.Site;
/**
 * The action called by the servlet to get the sites
 * from the database through the session bean 
 * 
 * @author  Yakira C. Bristol
 *
 */
public class PageSurferAction extends Action {
	
	
	public ActionForward execute(ActionMapping mapping, ActionForm form, 
    		HttpServletRequest request, HttpServletResponse response) throws Exception {
		HttpSession session = request.getSession();
		
		PageSurferForm psform = (PageSurferForm) form;
		String words = psform.getSearchWord();
		//psform.setMessage(words);
		
		try{
			InitialContext ctx = new InitialContext();
			
			PageSurferData psd = (PageSurferData)session.getAttribute("searches");
			PagesSearch ps = (PagesSearch) ctx.lookup("PS/PagesSearchBean/remote");
			
			if(psd == null){
				psd = (PageSurferData) ctx.lookup("PS/PageSurferDataBean/remote");
				session.setAttribute("searches", psd);
			}
			ArrayList<Site> iresults = new ArrayList<Site>();
			if(psd.getSearches().containsKey(words)){
				iresults = psd.getSearches().get(words);
				psform.setMessage("We retrieved results from a previous search of the same words");
			}else {
				iresults = ps.getSites(words);
				psform.setMessage("");
			}
			Map<Site, HashMap<String, ArrayList<Site>>> diffSortedResults = new HashMap<Site, HashMap<String, ArrayList<Site>>>();
			diffSortedResults = ps.diffSortSites(iresults);
			
			/*for(Site s: diffSortedResults.keySet()){
				Map<String, ArrayList<Site>> csites = new HashMap<String, ArrayList<Site>>();
				csites = diffSortedResults.get(s);
				for(String cat: csites.keySet()){
					System.out.println("The category in the action is: " + cat);
					if(csites.get(cat) == null){
						System.out.println("The arraylist for this category is null");
					}else {
						System.out.println("The arraylist for this category is not null");
						for(Site t: csites.get(cat)){
							System.out.println("Site title: " + t.getSiteTitle());
						}
					}
				}
			}*/
			
			session.setAttribute("isites", iresults);
			session.setAttribute("psites", diffSortedResults);
		}catch(NamingException ne){
			ne.printStackTrace();
		}
		
		return mapping.findForward("success");
		
	}

}
