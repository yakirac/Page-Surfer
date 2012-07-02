package ps.web;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
/**
 * Information for the html form on the jsp page
 * 
 * @author  Yakira C. Bristol
 *
 */
public class PageSurferForm extends ActionForm {

	private static final long serialVersionUID = 1L;
	
	private String message;
	private String searchWord;
	
	
	public PageSurferForm(){
		super();
	}
	
	public ActionErrors validate(ActionMapping mapping, HttpServletRequest request){
		ActionErrors errors = new ActionErrors();
		if(getSearchWord() == null){
			errors.add("searchWord", new ActionMessage("error.searchwords.required"));
			//System.out.println("You need to enter a word");
		}
		/*if(getSearchSite() == null && getSearchWord() == null){
			errors.add("searchSite", new ActionMessage("error.site.required"));
		}*/
		
		return errors;
	}
	
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String mess){
		message = mess;
	}
	
	public String getSearchWord(){
		return searchWord;
	}
	
	public void setSearchWord(String word){
		searchWord = word;
	}

}
