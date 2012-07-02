package ps.database;

import java.util.HashSet;
import java.util.Set;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;


/**
 *  This class contains word details.
 *  
 *  @author Yakira C. Bristol
 * 		
 */

@SuppressWarnings("serial")
@Entity
@Table(name = "WORD")
public class Word implements java.io.Serializable {
	private long wordId;
	private String wordName;
	private Set<Site> sites = new HashSet<Site>(0);

	public Word() {
	}

	public Word(String wordName) {
		this.wordName = wordName;
	}

	public Word(String wordName, Set<Site> sites) {
		this.wordName = wordName;
		this.sites = sites;
	}

	@Id
	@GeneratedValue
	@Column(name = "WORD_ID", nullable = false)
	public long getWordId() {
		return this.wordId;
	}

	public void setWordId(long wordId) {
		this.wordId = wordId;
	}

	@Column(name = "WORD_NAME", nullable = false, length = 4000)
	public String getWordName() {
		return this.wordName;
	}

	public void setWordName(String wordName) {
		this.wordName = wordName;
	}

	@ManyToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinTable(name = "WORD_SITES", joinColumns = { @JoinColumn(name = "WORD_ID", nullable = false, updatable = false) }, inverseJoinColumns = { @JoinColumn(name = "SITE_ID", nullable = false, updatable = false) })
	public Set<Site> getSites() {
		return this.sites;
	}

	public void setSites(Set<Site> sites) {
		this.sites = sites;
	}

}
