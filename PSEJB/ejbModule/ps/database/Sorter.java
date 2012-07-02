package ps.database;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import ps.util.HibernateUtil;

public class Sorter {
	/*
	 * So get words from map and store them in the table. Since no
	 * word can be in the map twice probably don't have to check if 
	 * the word is in the table already. Create the word with the id 
	 * and add it to the table. Each word has a set of links.
	 * 
	 * Get links associated with the word. If we have seen the link 
	 * don't store it in the table else store it in the table. Create 
	 * the link with the id and store it in the table.
	 * 
	 * for each word in the map
	 * 		get the list of links
	 * 		for each link 
	 * 			create a new site class with the name of the link
	 * 			add it to the Set of sites
	 * 		create a new word and add the set of sites to the word
	 * 		store the site in the database
	 */
	public void storeInfo(HashMap<String, ArrayList<HashMap<String, ArrayList<String>>>> sets){
		//get the session factory
		Session session = HibernateUtil.getSessionFactory().openSession();
		//create new transaction
		Transaction trans = null;
		int count = 0;
		//begin transaction
		try {
			trans = session.beginTransaction();
			
			for(String w: sets.keySet()){
				//System.out.println("The word: " + w);
				ArrayList<HashMap<String, ArrayList<String>>> sites = sets.get(w);
				Set<Site> wsites = new HashSet<Site>();
				HashMap<String, ArrayList<String>> sSets = sites.get(0);
				String wordName = w;
				if(wordName.endsWith(".") || wordName.endsWith(",") 
						|| wordName.endsWith("/") || wordName.endsWith("!") 
						|| wordName.endsWith("{") || wordName.endsWith("}")
						|| wordName.endsWith(";") || wordName.endsWith(":")
						|| wordName.endsWith("<") || wordName.endsWith(">")
						|| wordName.endsWith("?")){
					wordName = wordName.substring(0, wordName.length() - 1);
				}
				
				for(String s: sSets.keySet()){
					//System.out.println("The url: " + s);
					ArrayList<String> wTypes = sSets.get(s);
					String url = s;
					String title = wTypes.get(0);
					//System.out.println("The title: " + title);
					String description = wTypes.get(1);
					//System.out.println("The description: " + description);
					//description = description.substring(0, 10);
					String keywords = wTypes.get(2);
					//System.out.println("The keywords: " + keywords);
					//keywords = keywords.substring(0, 10);
					Integer wFreq = Integer.valueOf(wTypes.get(3));
					//System.out.println("The freq: " + wFreq);
					//Create a new Site
					Site site = new Site(url, title, description, keywords, wFreq);
					//add site to wsites
					wsites.add(site);
				}
				//Create new Word that takes in wsites as a parameter
				Word word = new Word(wordName, wsites); 
				//Store Word in the database
				session.save(word);
				count++;
			}
			//commit the transaction
			trans.commit();
		}catch(HibernateException e){
			trans.rollback();
			e.printStackTrace();
		}finally {
			//close the session
			session.close();
		}
		
		System.out.println("The size of the keySet: " + sets.keySet().size());
		System.out.println("The number of words: " + count);
		
	}
}
