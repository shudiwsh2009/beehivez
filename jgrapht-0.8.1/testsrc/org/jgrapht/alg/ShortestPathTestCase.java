/* ==========================================
 * JGraphT : a free Java graph-theory library
 * ==========================================
 *
 * Project Info:  http://jgrapht.sourceforge.net/
 * Project Creator:  Barak Naveh (http://sourceforge.net/users/barak_naveh)
 *
 * (C) Copyright 2003-2008, by Barak Naveh and Contributors.
 *
 * This library is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation; either version 2.1 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Lesser General Public
 * License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this library; if not, write to the Free Software Foundation,
 * Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.
 */
/* ------------------------------
 * ShortestPathTestCase.java
 * ------------------------------
 * (C) Copyright 2003-2008, by John V. Sichi and Contributors.
 *
 * Original Author:  John V. Sichi
 * Contributor(s):   -
 *
 * $Id: ShortestPathTestCase.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 * 14-Jan-2006 : Factored out of DijkstraShortestPathTest (JVS);
 *
 */
package org.jgrapht.alg;

import java.util.*;

import junit.framework.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

/**
 * .
 * 
 * @author John V. Sichi
 */
public abstract class ShortestPathTestCase extends TestCase {
	// ~ Static fields/initializers
	// ---------------------------------------------

	static final String V1 = "v1";
	static final String V2 = "v2";
	static final String V3 = "v3";
	static final String V4 = "v4";
	static final String V5 = "v5";

	// ~ Instance fields
	// --------------------------------------------------------

	DefaultWeightedEdge e12;
	DefaultWeightedEdge e13;
	DefaultWeightedEdge e15;
	DefaultWeightedEdge e24;
	DefaultWeightedEdge e34;
	DefaultWeightedEdge e45;

	// ~ Methods
	// ----------------------------------------------------------------

	/**
	 * .
	 */
	public void testPathBetween() {
		List path;
		Graph<String, DefaultWeightedEdge> g = create();

		path = findPathBetween(g, V1, V2);
		assertEquals(Arrays.asList(new DefaultEdge[] { e12 }), path);

		path = findPathBetween(g, V1, V4);
		assertEquals(Arrays.asList(new DefaultEdge[] { e12, e24 }), path);

		path = findPathBetween(g, V1, V5);
		assertEquals(Arrays.asList(new DefaultEdge[] { e12, e24, e45 }), path);

		path = findPathBetween(g, V3, V4);
		assertEquals(Arrays.asList(new DefaultEdge[] { e13, e12, e24 }), path);
	}

	protected abstract List findPathBetween(
			Graph<String, DefaultWeightedEdge> g, String src, String dest);

	protected Graph<String, DefaultWeightedEdge> create() {
		return createWithBias(false);
	}

	protected Graph<String, DefaultWeightedEdge> createWithBias(boolean negate) {
		Graph<String, DefaultWeightedEdge> g;
		double bias = 1;
		if (negate) {
			// negative-weight edges are being tested, so only a directed graph
			// makes sense
			g = new SimpleDirectedWeightedGraph<String, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);
			bias = -1;
		} else {
			// by default, use an undirected graph
			g = new SimpleWeightedGraph<String, DefaultWeightedEdge>(
					DefaultWeightedEdge.class);
		}

		g.addVertex(V1);
		g.addVertex(V2);
		g.addVertex(V3);
		g.addVertex(V4);
		g.addVertex(V5);

		e12 = Graphs.addEdge(g, V1, V2, bias * 2);

		e13 = Graphs.addEdge(g, V1, V3, bias * 3);

		e24 = Graphs.addEdge(g, V2, V4, bias * 5);

		e34 = Graphs.addEdge(g, V3, V4, bias * 20);

		e45 = Graphs.addEdge(g, V4, V5, bias * 5);

		e15 = Graphs.addEdge(g, V1, V5, bias * 100);

		return g;
	}
}

// End ShortestPathTestCase.java
