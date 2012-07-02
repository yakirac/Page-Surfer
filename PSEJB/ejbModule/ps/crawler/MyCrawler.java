package ps.crawler;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;


public class MyCrawler extends WebCrawler{

	private final static Pattern FILTERS = Pattern.compile(".*(\\.(css|js|bmp|gif|jpe?g" + "|png|tiff?|mid|mp2|mp3|mp4"
            + "|wav|avi|mov|mpeg|ram|m4v|pdf" + "|rm|smil|wmv|swf|wma|zip|rar|gz))$");

	Map<String, Integer> wor = new HashMap<String, Integer>();
	int count = 0;
	String title = "";
	String description = "";
	String keywords = "";
	String url = "";
	
	HashMap<String, ArrayList<HashMap<String, ArrayList<String>>>> crazy = new HashMap<String, ArrayList<HashMap<String, ArrayList<String>>>>();;
	
	/**
	* You should implement this function to specify whether
	* the given url should be crawled or not (based on your
	* crawling logic).
	*/
	@Override
	public boolean shouldVisit(WebURL url) {
	    String href = url.getURL().toLowerCase();
	    return !FILTERS.matcher(href).matches() && href.startsWith("http://");
	}
	
	/**
	* This function is called when a page is fetched and ready 
	* to be processed by your program.
	*/
	@Override
	public void visit(Page page) {
	    int docid = page.getWebURL().getDocid();
	    url = page.getWebURL().getURL();
	    int parentDocid = page.getWebURL().getParentDocid();
	
	    System.out.println("Docid: " + docid);
	    System.out.println("URL: " + url);
	    System.out.println("Docid of parent page: " + parentDocid);
	
	    if (page.getParseData() instanceof HtmlParseData) {
	            HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
	            String text = htmlParseData.getText();
	            String html = htmlParseData.getHtml();
	            title = htmlParseData.getTitle();
	            if(title == null){
	            	title = "No title";
	            	System.out.println("No title whaaat?!");
	            }
	            
	            String dmtag = "<meta name=\"description\" content=\"";
	            int begin = html.indexOf(dmtag);
	            int end = html.indexOf(">", begin);
	            //String description = "";
	            if(begin < 0){
	            	description = "No description";
	            }else {
	            	description = html.substring(begin + dmtag.length(), end);
	            }
	            
	            //System.out.println("This is the index of the description: beginning = " + begin + ", end = " + end);
	            //System.out.println("This is the result: " + description);
	            
	            String kmtag = "<meta name=\"keywords\" content=\"";
	            int kbegin = html.indexOf(kmtag);
	            int kend = html.indexOf(">", kbegin);
	            //String keywords = "";
	            if(kbegin < 0){
	            	keywords = "No keywords";
	            }else {
	            	keywords = html.substring(kbegin + kmtag.length(), kend);
	            }
	            
	            setUpWords(description);
	            setUpWords(keywords);
	            setUpWords(text);
	            
	            
	            
	            
	            //List<WebURL> links = htmlParseData.getOutgoingUrls();
	            
	    }
	
	    System.out.println("=============");
	}
	
	@Override
	public void onBeforeExit(){
		
		CrawlController cc = this.getMyController();
		cc.setCustomData(crazy);
		
	}
	
	public void setUpWords(String wList){
		Map<String, ArrayList<String>> sites = new HashMap<String, ArrayList<String>>();
	    ArrayList<String> types = new ArrayList<String>();
	    ArrayList<HashMap<String, ArrayList<String>>> siteSets = new ArrayList<HashMap<String, ArrayList<String>>>();
		
		StringTokenizer st = new StringTokenizer(wList, " \t\n\r\f)(><:;,!#?}{][|&*^%$@\\/");
	    while(st.hasMoreTokens()){
	    	String next = st.nextToken().toLowerCase().trim();
	    	if(next.endsWith(".") || next.endsWith(",") 
					|| next.endsWith("/") || next.endsWith("!") 
					|| next.endsWith("{") || next.endsWith("}")
					|| next.endsWith(";") || next.endsWith(":")
					|| next.endsWith("<") || next.endsWith(">")
					|| next.endsWith("?") || next.startsWith(".") 
					|| next.startsWith(",") || next.startsWith("/") 
					|| next.startsWith("!") || next.startsWith("{") 
					|| next.startsWith("}") || next.startsWith(";") 
					|| next.startsWith(":") || next.startsWith("<") 
					|| next.startsWith(">") || next.startsWith("?") 
					|| next.startsWith("\"") || next.startsWith("'")){
	    		next = next.substring(0, next.length() - 1);
			}
	    	System.out.println("This is the word: " + next);
	    	//System.out.println("This is the frequency: " + freq);
	    	Integer freq = 1;
	    	//String frq = null;
	    	siteSets = crazy.get(next);
	    	if(siteSets == null){
	    		siteSets = new ArrayList<HashMap<String, ArrayList<String>>>();
	    		sites = new HashMap<String, ArrayList<String>>();
	    		types = new ArrayList<String>();
	    		
	    		types.add(title);
	            types.add(description);
	            types.add(keywords);
	            //System.out.println("The frequecy about to be added: " + freq);
	            types.add(freq.toString());
	            
	            sites.put(url, types);
	            siteSets.add((HashMap<String, ArrayList<String>>) sites);
	            crazy.put(next, siteSets);
	    		//frq = String.valueOf(freq);
	    		System.out.println("It's null so everything is all new");
	    	}else {
	    		sites = siteSets.get(0);
	    		types = sites.get(url);
	    		if(types == null){
	    			types = new ArrayList<String>();
	    			types.add(title);
		            types.add(description);
		            types.add(keywords);
		            System.out.println("Types was null so we have a new types arrayList");
		            types.add(freq.toString());
		            
		            sites.put(url, types);
		            siteSets.set(0, (HashMap<String, ArrayList<String>>) sites);
		            crazy.put(next, siteSets);
	    		}else {
	    			if(types.get(3) == null){
	    				freq = 1;
	    			}else {
	    				freq = Integer.parseInt(types.get(3));
	        			//System.out.println("The current frequecy: " + freq);
	            		freq++;
	            		//System.out.println("The updated frequecy: " + freq);
	    			}
	        		types.set(3, freq.toString());
	    		}
	    		
	    		
	    	}
	    	
	    }
	}

}
