/**
 * BeehiveZ is a business process model and instance management system.
 * Copyright (C) 2011  
 * Institute of Information System and Engineering, School of Software, Tsinghua University,
 * Beijing, China
 *
 * Contact: jintao05@gmail.com 
 *
 * This program is a free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation with the version of 2.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */
package cn.edu.thss.iise.beehivez.server.index.labelindex;

import java.io.IOException;
import java.util.HashSet;
import java.util.TreeSet;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.TermFreqVector;
import org.apache.lucene.search.Collector;
import org.apache.lucene.search.Scorer;

import cn.edu.thss.iise.beehivez.server.util.StringSimilarityUtil;

/**
 * @author Tao Jin
 * 
 */
public class SimilarLabelQueryResultCollector extends Collector {
	private int docBase;
	private Scorer scorer;
	private IndexReader reader;
	private HashSet<String> queryTermSet;
	private float similarity;
	private TreeSet<SimilarLabelQueryResult> queryResult = new TreeSet<SimilarLabelQueryResult>();

	public SimilarLabelQueryResultCollector(IndexReader reader,
			HashSet<String> queryTermSet, float similarity) {
		this.reader = reader;
		this.queryTermSet = queryTermSet;
		this.similarity = similarity;
	}

	@Override
	public boolean acceptsDocsOutOfOrder() {
		return true;
	}

	@Override
	public void collect(int doc) throws IOException {
		int docNum = doc + docBase;

		String label = reader.document(docNum).get(LabelDocument.FIELD_LABEL);
		TermFreqVector termFreqVector = reader.getTermFreqVector(docNum,
				LabelDocument.FIELD_LABEL);
		HashSet<String> docTermSet = new HashSet<String>();
		for (String str : termFreqVector.getTerms()) {
			docTermSet.add(str);
		}

		float score = StringSimilarityUtil.diceSemanticSimilarity(queryTermSet,
				docTermSet);

		if (score >= this.similarity) {
			queryResult.add(new SimilarLabelQueryResult(label, score));
		}
	}

	@Override
	public void setNextReader(IndexReader reader, int docBase)
			throws IOException {
		this.docBase = docBase;
	}

	@Override
	public void setScorer(Scorer scorer) throws IOException {
		this.scorer = scorer;
	}

	/**
	 * @return the queryResult
	 */
	public TreeSet<SimilarLabelQueryResult> getQueryResult() {
		return queryResult;
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
