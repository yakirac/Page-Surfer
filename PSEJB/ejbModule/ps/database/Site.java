package ps.database;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 *  This class contains site details. 
 *  
 *  @author Yakira C. Bristol
 * 		
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "SITE")
public class Site implements java.io.Serializable {
	private long siteId;
	private String siteURL;
	private String siteTitle;
	private String siteDescription;
	private String siteKeywords;
	private Integer wordFreq;

	public Site() {
	}

	public Site(String siteURL, String siteTitle, String siteDescription,
			String siteKeywords, Integer wordFreq) {
		this.siteURL = siteURL;
		this.siteTitle = siteTitle;
		this.siteDescription = siteDescription;
		this.siteKeywords = siteKeywords;
		this.wordFreq = wordFreq;
	}

	@Id
	@GeneratedValue
	@Column(name = "SITE_ID", nullable = false)
	public long getSiteId() {
		return this.siteId;
	}

	public void setSiteId(long siteId) {
		this.siteId = siteId;
	}

	@Column(name = "SITE_URL", nullable = false, length = 100000)
	public String getSiteURL() {
		return this.siteURL;
	}

	public void setSiteURL(String siteURL) {
		this.siteURL = siteURL;
	}

	@Column(name = "SITE_TITLE", nullable = false, length = 100000)
	public String getSiteTitle() {
		return this.siteTitle;
	}

	public void setSiteTitle(String siteTitle) {
		this.siteTitle = siteTitle;
	}

	@Column(name = "SITE_DESCRIPTION", nullable = false, length = 100000)
	public String getSiteDescription() {
		return this.siteDescription;
	}

	public void setSiteDescription(String siteDescription) {
		this.siteDescription = siteDescription;
	}

	@Column(name = "SITE_KEYWORDS", nullable = false, length = 100000)
	public String getSiteKeywords() {
		return this.siteKeywords;
	}

	public void setSiteKeywords(String siteKeywords) {
		this.siteKeywords = siteKeywords;
	}

	@Column(name = "WORD_FREQ", nullable = false)
	public Integer getWordFreq() {
		return this.wordFreq;
	}

	public void setWordFreq(Integer wordFreq) {
		this.wordFreq = wordFreq;
	}

}
