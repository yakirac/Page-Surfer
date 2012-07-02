package ps.ejb;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.ejb.Remote;
import javax.ejb.Stateless;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ps.database.CompareSites;
import ps.database.Site;
import ps.database.Word;
import ps.util.HibernateUtil;


/**
 * Retrieves the sites from the database based on the word 
 * received from the user input
 * 
 * @author Yakira C. Bristol
 *
 */


@Stateless
@Remote(PagesSearch.class)
public class PagesSearchBean implements PagesSearch {
	
	Map<String, ArrayList<Site>> catedSites = new HashMap<String, ArrayList<Site>>();
	Map<Site, HashMap<String, ArrayList<Site>>> scatedSites = new HashMap<Site, HashMap<String, ArrayList<Site>>>();
	
	String usStates = "";
	String caCountries = "";
	String saCountries = "";
	String ukCountries = "";
	String euroCountries = "";
	String asianCountries = "";
	String midEastCountries = "";
	String sports = "";
	String world ="";
	
	String strippedSWords = "";

	@Override
	public ArrayList<Site> getSites(String sw) {
		ArrayList<Site> sids = new ArrayList<Site>();
		
		Session session = HibernateUtil.getSessionFactory().openSession();
		Transaction transaction = null;
		System.out.println("We are about to get the sites");
		
		String regex = "\\b(([aA]nd)|([aA])|([tT]he)|([bB]ut)|([iI]s)|([iI]n)|([oO]n)|([oO]r)|([aA]t)|[wW]ith)\\b";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(sw);
		strippedSWords = matcher.replaceAll("");
		
		try {
			transaction = session.beginTransaction();
			Word w = new Word();
			//Get the id of the word in the word table if found
			Query wq = session.createQuery("from Word where word_name = :word");
			wq.setParameter("word", sw);
			List<?> wl = wq.list();
			if(wl.isEmpty()){
				System.out.println("We need to split up the words");
				StringTokenizer st = new StringTokenizer(sw);
				while(st.hasMoreTokens()){
					//String wd = st.nextToken();
					wq.setParameter("word", sw);
					List<?> nwl = wq.list();
					if(nwl.isEmpty()){
						continue;
					}else {
						Iterator<?> wi = nwl.iterator();
						while(wi.hasNext()){
							w = (Word) wi.next();
							Set<Site> sites = w.getSites();
							Iterator<Site> si = sites.iterator();
							while(si.hasNext()){
								boolean weHave = false;
								Site nsite = (Site) si.next();
								for(Site s: sids){
									if(nsite.getSiteURL().equals(s.getSiteURL())){
										weHave = true;
										System.out.println("In null. We already have this site");
									}
								}
								if(weHave == false){
									sids.add(nsite);
								}
							}
						}
					}
				}
			}else {
				Iterator<?> wi = wl.iterator();
				while(wi.hasNext()){
					w = (Word) wi.next();
					Set<Site> sites = w.getSites();
					Iterator<Site> si = sites.iterator();
					while(si.hasNext()){
						boolean weHave = false;
						Site nsite = (Site) si.next();
						for(Site s: sids){
							if(nsite.getSiteURL().equals(s.getSiteURL())){
								weHave = true;
								System.out.println("Not in null. We already have this site");
							}
						}
						if(weHave == false){
							sids.add(nsite);
						}
					}
				}
				
				System.out.println("We have sites but we are still going to split up the words");
				
				StringTokenizer st = new StringTokenizer(sw);
				while(st.hasMoreTokens()){
					String wd = st.nextToken();
					wq.setParameter("word", wd);
					List<?> nwl = wq.list();
					if(nwl.isEmpty()){
						continue;
					}else {
						Iterator<?> nwi = nwl.iterator();
						while(wi.hasNext()){
							w = (Word) nwi.next();
							Set<Site> sites = w.getSites();
							Iterator<Site> si = sites.iterator();
							while(si.hasNext()){
								boolean weHave = false;
								Site nsite = (Site) si.next();
								for(Site s: sids){
									if(nsite.getSiteURL().equals(s.getSiteURL())){
										weHave = true;
										System.out.println("In breaking up the words. We already have this site");
									}
								}
								if(weHave == false){
									sids.add(nsite);
								}	
							}
						}
					}
				}
			}
			
			
			
			if(sids.isEmpty()){
				Site ns = new Site();
				ns.setSiteTitle("There were no sites found");
				ns.setSiteURL("So that means no urls either");
				sids.add(ns);
			}else {
				System.out.println("We have sites");
				CompareSites cs = new CompareSites();
				Collections.sort(sids, cs);
				Collections.reverse(sids);
			}
			
			transaction.commit();
		}catch(HibernateException e){
			transaction.rollback();
			e.printStackTrace();
		}finally {
			session.close();
		}
		
		return sids;
	}

	@Override
	public Map<String, ArrayList<Site>> sortSites(ArrayList<Site> sts) {
		//HashMap<String, ArrayList<Site>> catedSites = new HashMap<String, ArrayList<Site>>();
		
		
		String category = "";
		ArrayList<String> catWords = new ArrayList<String>();
		ArrayList<Site> catSites = new ArrayList<Site>();
		ArrayList<String> categories = new ArrayList<String>();
		
		System.out.println("We are  in sortSites");
		if(sts.isEmpty()){
			System.out.println("The array list you passed in is empty");
		}else{
			for(Site s: sts){
				if(s.getSiteTitle().equals("No title") == false){
					category = s.getSiteTitle();
					System.out.println("The first title in sort sites: " + category);
					StringTokenizer st = new StringTokenizer(category);
					while(st.hasMoreTokens()){
						String nt = st.nextToken();
						catWords.add(nt);
						boolean alreadyIn = false;
	
						categories = getCategories(nt);
						
						if(categories.isEmpty()){
							catSites = catedSites.get("Other");
							if(catSites == null){
								catSites = new ArrayList<Site>();
								catSites.add(s);
							}
							catedSites.put("Other", catSites);
						}else {
						
							for(String c: categories){
								catSites = catedSites.get(c);
								if(catSites == null){
									catSites = new ArrayList<Site>();
									catSites.add(s);
								}else {
									for(Site ns: catSites){
										System.out.println("The second title in sort sites: " + category);
										if(ns.getSiteTitle().equals(s.getSiteTitle()) == false){
											System.out.println("We don't have this site yet in our catSites");
											alreadyIn = false;
										}else {
											System.out.println("They are the same site");
											alreadyIn = true;
										}
									}
									if(alreadyIn == false){
										System.out.println("We didn't have it so we added it to catSites");
										catSites.add(s);
									}
								}
								catedSites.put(c, catSites);
							}
						}
						
						
					}
					
				}else {
					catSites = catedSites.get("Other");
					if(catSites == null){
						catSites = new ArrayList<Site>();
						catSites.add(s);
					}
					catedSites.put("Other", catSites);
				}
			}
		}
		
		
		for(String ct: catedSites.keySet()){
			System.out.println("The category is: " + ct);
			if(catedSites.get(ct) == null){
				
			}else {
				for(Site s: catedSites.get(ct)){
					System.out.println("Before filterSorted. The site is: " + s.getSiteTitle());
				}
			}
		}
		
		catedSites = filterSorted(catedSites);
		
		if(catedSites == null){
			System.out.println("The cated sites are null");
		}else if(catedSites.isEmpty()){
			System.out.println("The cated sites are empty");
		}else {
			for(String ct: catedSites.keySet()){
				System.out.println("The category is: " + ct);
				for(Site s: catedSites.get(ct)){
					System.out.println("At the end of sorted. The site is: " + s.getSiteTitle());
				}
			}
		}
		
		return catedSites;
	}
	
	public Map<String, ArrayList<Site>> filterSorted(Map<String, ArrayList<Site>> ss){
    	HashMap<String, ArrayList<Site>> filtered = new HashMap<String, ArrayList<Site>>();
    	
    	for(String ct: ss.keySet()){
    		ArrayList<Site> theSites = ss.get(ct);
    		if(theSites != null){
    			System.out.println("The sites for the category are not null");
    			filtered.put(ct, theSites);
    			for(Site ts: theSites){
    				System.out.println("In filterSorted the site is: " + ts.getSiteTitle());
    			}
    		}else {
    			System.out.println("The sites for the category are null");
    			/*theSites = new ArrayList<Site>();
    			Site booSite = new Site();
				booSite.setSiteTitle("There are no sites for this category");
				booSite.setSiteURL("No sites no urls");
				theSites.add(booSite);*/
				//filtered.put(ct, theSites);
    		}
    	}
    	
    	for(String c: filtered.keySet()){
    		System.out.println("The category in filtered is: " + c);
    	}
    	
    	//catedSites = filtered;
    	
    	return filtered;
    }

	@Override
	public Map<Site, HashMap<String, ArrayList<Site>>> diffSortSites(
			ArrayList<Site> rsites) {
		
		
		//String regex = "\\b(([aA]nd)|([aA])|([tT]he)|([bB]ut)|([iI]s)|([iI]n)|([oO]n)|([oO]r)|([aA]t)|[wW]ith)\\b";
		//Pattern pattern = Pattern.compile(regex);
		ArrayList<Site> relatedSites;
		HashMap<String, ArrayList<Site>> rSiteCats;
		for(Site ns: rsites){
			relatedSites = new ArrayList<Site>();
			rSiteCats = new HashMap<String, ArrayList<Site>>();
			//Matcher matcher = pattern.matcher(ns.getSiteTitle());
			//String nsTitle = matcher.replaceAll("");
			//boolean alreadyHave = false;
			createCategories();
			System.out.println("The first site title: " + ns.getSiteTitle());
			for(Site ins: rsites){
				System.out.println("The second site title: " + ins.getSiteTitle());
				//check if sites comparing are equal to each other
				//if so move on to the next site
				if(ins.getSiteTitle().equals(ns.getSiteTitle())){
					System.out.println("The sites have the same title. Moving on.");
					//continue;
				}else {
					System.out.println("Before checking sites info. Site titles are: ");
					System.out.println("Site 1: " + ns.getSiteTitle());
					System.out.println("Site 2: " + ins.getSiteTitle());
					checkTitle(ns, ins, relatedSites);
					checkKeywords(ns, ins, relatedSites);
					checkDescription(ns, ins, relatedSites);
					
					
				}
			}
			
			if(relatedSites.isEmpty() == false){
				//sortSites(relatedSites);
				//rSiteCats = (HashMap<String, ArrayList<Site>>) catedSites;
				//rSiteCats.put("Other", relatedSites);
				rSiteCats = (HashMap<String, ArrayList<Site>>) sortSites(relatedSites);
				HashMap<String, ArrayList<Site>> refiltered = new HashMap<String, ArrayList<Site>>();
				refiltered = (HashMap<String, ArrayList<Site>>) filterSorted(rSiteCats);
				scatedSites.put(ns, refiltered);
			}else {
				System.out.println("The related sites was empty");
				Site booSite = new Site();
				booSite.setSiteTitle("There are no related sites for this site");
				booSite.setSiteURL("No sites no urls");
				relatedSites.add(booSite);
				rSiteCats.put("Other", relatedSites);
				scatedSites.put(ns, rSiteCats);
			}
		}
		
		return scatedSites;

	}
	
	public void createCategories(){
		/*
		 * Categories:
		 * North America: US and Canada
		 * United States: all 50
		 * Mexico
		 * South America
		 * Europe: Germany, Italy, Spain, France, Portugal
		 * UK: England, Scotland, Ireland
		 * Asia: China, Japan
		 * Russia
		 * Australia
		 * New Zealand
		 * Sports: soccer, basketball 
		 * News
		 * Entertainment
		 * Music
		 * Travel
		 * Health
		 * Other
		 */
		catedSites.put("News", null);
		catedSites.put("Sports", null);
		catedSites.put("Entertainment", null);
		catedSites.put("Music", null);
		catedSites.put("Health", null);
		catedSites.put("Other", null);
		
		catedSites.put("US", null);
		catedSites.put("Canada", null);
		catedSites.put("Mexico", null);
		catedSites.put("Central America", null);
		catedSites.put("South America", null);
		catedSites.put("UK", null);
		catedSites.put("Europe", null);
		catedSites.put("Asia", null);
		catedSites.put("Middle East", null);
		catedSites.put("Australia", null);
		catedSites.put("New Zealand", null);
		
		usStates = "\\b((Alabama)|(Alaska)|(Arizona)|(Arkansas)|(California)|(Colorado)|(Connecticut)|(Delaware)|" +
				"(Florida)|(Georgia)|(Hawai'i)|(Idaho)|(Illinois)|(Indiana)|(Iowa)|(Kansas)|(Kentucky)|(Louisiana)|(Maine)|" +
				"(Maryland)|(Massachusetts)|(Michigan)|(Minnesota)|(Mississippi)|(Missouri)|(Montana)|(Nebraska)|(Nevada)|" +
				"(New Hampshire)|(New Jersey)|(New Mexico)|(New York)|(NY)|(N.Y.)|(North Carolina)|(North Dakota)|(Ohio)|" +
				"(Oklahoma)|(Oregon)|(Pennsylvania)|(Rhode Island)|(South Carolina)|(South Dakota)|(Tennessee)|(Texas)|(Utah)|" +
				"(Vermont)|(Virginia)|(Washington)|(West Virginia)|(Wisconsin)|(Wyoming))\\b";
		caCountries = "\\b((Belize)|(Costa Rica)|(El Salvador)|(Guatemala)|(Honduras)|(Nicaragua)|(Panama))\\b";
		saCountries = "\\b((Argentina)|(Bolivia)|(Brazil)|(Chile)|(Colombia)|(Ecuador)|(French Guiana)|(Guyana)|(Paraguay)|" +
				"(Peru)|(Suriname)|(Uraguay)|(Venezuela))\\b";
		ukCountries = "\\b((Northern Ireland)|(Scotland)|(Wales)|(England)|(Great Britain))\\b";
		euroCountries = "\\b((Belarus)|(Bulgaria)|(Czech Republic)|(Hungary)|(Moldova)|" +
				"(Poland)|(Romania)|(Russian Federation)|(Russia)|(Slovakia)|(Ukraine)|" +
				"(Denmark)|(Estonia)|(Finland)|(Ireland)|(Latvia)|(Lithuania)|(Northern Ireland)|" +
				"(Norway)|(Sweden)|(United Kingdom)|(UK)|(U.K.)|(Wales)|(Albania)|(Andorra)|(Bosnia and Herzegovina)|" +
				"(Bosnia)|(Croatia)|(Cyprus)|(Greece)|(Vatican City State)|(Italy)|(Macedonia)|" +
				"(Malta)|(Montenegro)|(Portugal)|(San Marino)|(Serbia)|(Slovenia)|(Spain)|(Turkey)|" +
				"(Austria)|(Belgium)|(France)|(Germany)|(Liechtenstein)|(Luxembourg)|(Monaco)|" +
				"(Netherlands)|(Switzerland))\\b";
		asianCountries = "\\b((China)|(Japan)|(Korea)|(North Korea)|(South Korea)|(Mongolia)|(Taiwan)|(Bangladesh)|(India)|" +
				"(Nepal)|(Pakistan)|(Sri Lanka)|(Cambodia)|(Indonesia)|(Malaysia)|(Philippines)|(Singapore)|(Thailand)|(Vietnam))\\b";
		midEastCountries = "\\b((Afghanistan)|(Iran)|(Iraq)|(Israel)|(Jordan)|(Kuwait)|(Lebanon)|(Palestine)|(Syria)|(Oman)|" +
				"(Qatar)|(Saudi Arabia)|(United Arab Emirates)|(UAE)|(Yemen))\\b";
		sports = "\\b(([bB]asketball)|([fF]ootball)|([sS]occer)|([nN]ba)|(NBA)|([mM]lb)|(MLB))\\b";
		world = "\\b((United States)|(US)|(U.S.)|(USA)|(Mexico)|(South America)|(Europe)|(UK)|" +
				"(United Kingdom)|(Africa)|(Asia)|(Australia)|(AUS)|([aA]us)|(New Zealand)|(NZ)|([nN]z))\\b";
		
	}
	
	public ArrayList<String> getCategories(String word){
		ArrayList<String> catgs = new ArrayList<String>(); 
		
		Pattern pattern = Pattern.compile(usStates);
		Matcher matcher = pattern.matcher(word);

		if(matcher.find() == true){
			catgs.add("US");
		}
		
		pattern = Pattern.compile(caCountries);
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Central America");
		}
		
		pattern = Pattern.compile(saCountries);
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("South America");
		}
		
		pattern = Pattern.compile(ukCountries);
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("UK");
		}
		
		pattern = Pattern.compile(euroCountries);
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Europe");
		}
		
		pattern = Pattern.compile(asianCountries);
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Asia");
		}
		
		pattern = Pattern.compile(midEastCountries);
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Middle East");
		}
		
		pattern = Pattern.compile(sports);
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Sports");
		}
		
		pattern = Pattern.compile("\\b[cC]anada\\b");
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Canada");
		}
		
		pattern = Pattern.compile("\\b[mM]exico\\b");
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Mexico");
		}
		
		pattern = Pattern.compile("\\b(([nN]ews)|([pP]olitics)|([pP]olitical)|([gG]overnment)|)\\b");
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("News");
		}
		
		pattern = Pattern.compile("\\b(([eE]ntertainment)|([tT]v))\\b");
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Entertainment");
		}
		
		pattern = Pattern.compile("\\b[mM]usic\\b");
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Music");
		}
		
		pattern = Pattern.compile("\\b(([hH]ealth)|(disease)|(cure)|(cancer)|([dD]rugs)|([dD]rug))\\b");
		matcher = pattern.matcher(word);
		
		if(matcher.find() == true){
			catgs.add("Health");
		}
		
		return catgs;
	}

	public void checkTitle(Site fs, Site ss, ArrayList<Site> rs){
		if(!fs.getSiteTitle().equals("No title") && !ss.getSiteTitle().equals("No title")){
			String regex = "\\b(([aA]nd)|([aA])|([tT]he)|([bB]ut)|([iI]s)|([iI]n)|([oO]n)|([oO]r)|([aA]t)|[wW]ith)\\b";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(fs.getSiteTitle());
			String fTitle = matcher.replaceAll("");
			boolean alreadyHave = false;
			
			matcher = pattern.matcher(ss.getSiteTitle());
			String sTitle = matcher.replaceAll("");
			//check the words in this sites title to the main site's title
			StringTokenizer tt = new StringTokenizer(sTitle);
			while(tt.hasMoreTokens()){
				String w = tt.nextToken();
				//if the main site's title contains this word
				//check if the relatedSites array already contains the site
				//if not add it
				if(fTitle.contains(w)){
					System.out.println("We have a relation in the title");
					if(rs.isEmpty() == false){
						for(Site r: rs){
							if(r.getSiteTitle().equals(ss.getSiteTitle())){
								System.out.println("In checktitle. We already have this site");
								alreadyHave = true;
							}else{
								alreadyHave = false;
							}
						}
						if(alreadyHave == false){
							System.out.println("Not empty.Site added.");
							rs.add(ss);
						}
					}else{
						System.out.println("Empty.Site added.");
						rs.add(ss);
					}
				}
			}
			
			if(rs.isEmpty()){
				System.out.println("The list is empty in checkTitle");
			}
		}
	}
	
	public void checkKeywords(Site fs, Site ss, ArrayList<Site> rs){
		if(!fs.getSiteKeywords().equals("No keywords") && !ss.getSiteKeywords().equals("No keywords")){
			String regex = "\\b(([aA]nd)|([aA])|([tT]he)|([bB]ut)|([iI]s)|([iI]n)|([oO]n)|([oO]r)|([aA]t)|[wW]ith)\\b";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(fs.getSiteKeywords());
			String fKeywords = matcher.replaceAll("");
			boolean alreadyHave = false;
			
			matcher = pattern.matcher(ss.getSiteTitle());
			String sKeywords = matcher.replaceAll("");
			//check the words in this sites title to the main site's title
			StringTokenizer tt = new StringTokenizer(sKeywords, ",");
			while(tt.hasMoreTokens()){
				String w = tt.nextToken();
				//if the main site's title contains this word
				//check if the relatedSites array already contains the site
				//if not add it
				if(fKeywords.contains(w)){
					System.out.println("We have a relation in the keywords");
					if(rs.isEmpty() == false){
						for(Site r: rs){
							if(r.getSiteTitle().equals(ss.getSiteTitle())){
								System.out.println("In checkkeywords. We already have this site");
								alreadyHave = true;
							}else{
								alreadyHave = false;
							}
						}
						if(alreadyHave == false){
							System.out.println("Not empty.Site added.");
							rs.add(ss);
						}
					}else {
						System.out.println("Empty.Site added.");
						rs.add(ss);
					}
				}
			}
			
			if(rs.isEmpty()){
				System.out.println("The list is empty in checkKeywords");
			}
		}
	}
	
	public void checkDescription(Site fs, Site ss, ArrayList<Site> rs){
		if(!fs.getSiteDescription().equals("No description") && !ss.getSiteDescription().equals("No description")){
			String regex = "\\b(([aA]nd)|([aA])|([tT]he)|([bB]ut)|([iI]s)|([iI]n)|([oO]n)|([oO]r)|([aA]t)|[wW]ith)\\b";
			Pattern pattern = Pattern.compile(regex);
			Matcher matcher = pattern.matcher(fs.getSiteDescription());
			String fDescription = matcher.replaceAll("");
			boolean alreadyHave = false;
			
			matcher = pattern.matcher(ss.getSiteTitle());
			String sDescription = matcher.replaceAll("");
			//check the words in this sites title to the main site's title
			StringTokenizer tt = new StringTokenizer(sDescription, " :,;");
			while(tt.hasMoreTokens()){
				String w = tt.nextToken();
				//if the main site's title contains this word
				//check if the relatedSites array already contains the site
				//if not add it
				if(fDescription.contains(w)){
					System.out.println("We have a relation in the description");
					if(rs.isEmpty() == false){
						for(Site r: rs){
							if(r.getSiteTitle().equals(ss.getSiteTitle())){
								System.out.println("In checkdescription. We already have this site");
								alreadyHave = true;
							}else{
								alreadyHave = false;
							}
						}
						if(alreadyHave == false){
							System.out.println("Not empty.Site added.");
							rs.add(ss);
						}
					}else{
						System.out.println("Empty.Site added.");
						rs.add(ss);
					}
				}
			}
			
			if(rs.isEmpty()){
				System.out.println("The list is empty in checkDescription");
			}
		}
	}


}
