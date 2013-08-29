package net.trevize.labelme;

import java.util.Vector;

/**
 * 
 * 
 * @author Nicolas James <nicolas.james@gmail.com> [[http://njames.trevize.net]]
 * LabelMeSearcher.java - Nov 9, 2009
 */

public abstract class LabelMeSearcher {

	private String query;

	private int nbOfResults;

	private Vector<String> results;

	public LabelMeSearcher() {
		results = new Vector<String>();
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public int getNbOfResults() {
		return nbOfResults;
	}

	public void setNbOfResults(int nbOfResults) {
		this.nbOfResults = nbOfResults;
	}

	public abstract void doQuery(String query);

	public abstract LabelMeResults getResults(int idxFrom, int nbOfResults);

}
