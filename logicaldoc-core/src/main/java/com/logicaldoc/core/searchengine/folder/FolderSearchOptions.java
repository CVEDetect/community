package com.logicaldoc.core.searchengine.folder;

import java.util.ArrayList;
import java.util.List;

import com.logicaldoc.core.searchengine.SearchOptions;

/**
 * Search options specialization for the folder search.
 * 
 * @author Marco Meschieri - LogicalDOC
 * @since 6.4
 */
public class FolderSearchOptions extends SearchOptions {
	private static final long serialVersionUID = 1L;

	private List<FolderCriterion> criteria = new ArrayList<FolderCriterion>();

	/**
	 * List of order criteria eg: lastmodified asc, title desc
	 */
	private List<String> order = new ArrayList<String>();

	public List<FolderCriterion> getCriteria() {
		return criteria;
	}

	public void setCriteria(List<FolderCriterion> criteria) {
		this.criteria = criteria;
	}

	public FolderSearchOptions() {
		super(SearchOptions.TYPE_FOLDERS);
	}

	public List<String> getOrder() {
		return order;
	}

	public void setOrder(List<String> order) {
		this.order = order;
	}
}
