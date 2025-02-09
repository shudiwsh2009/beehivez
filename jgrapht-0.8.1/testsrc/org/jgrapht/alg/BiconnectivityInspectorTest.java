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
/* -------------------------
 * BiconnectivityInspectorTest.java
 * -------------------------
 * (C) Copyright 2007-2008, by France Telecom
 *
 * Original Author:  Guillaume Boulmier and Contributors.
 *
 * $Id: BiconnectivityInspectorTest.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 * 05-Jun-2007 : Initial revision (GB);
 *
 */
package org.jgrapht.alg;

import junit.framework.*;

import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;

/**
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
@SuppressWarnings("unchecked")
public class BiconnectivityInspectorTest extends TestCase {
	// ~ Methods
	// ----------------------------------------------------------------

	public void testBiconnected() {
		BiconnectedGraph graph = new BiconnectedGraph();

		BiconnectivityInspector inspector = new BiconnectivityInspector(graph);

		assertTrue(inspector.isBiconnected());
		assertEquals(0, inspector.getCutpoints().size());
		assertEquals(1, inspector.getBiconnectedVertexComponents().size());
	}

	public void testLinearGraph() {
		testLinearGraph(3);
		testLinearGraph(5);
	}

	public void testLinearGraph(int nbVertices) {
		UndirectedGraph graph = new SimpleGraph(DefaultEdge.class);

		LinearGraphGenerator generator = new LinearGraphGenerator(nbVertices);
		generator.generateGraph(graph, new ClassBasedVertexFactory<Object>(
				Object.class), null);

		BiconnectivityInspector inspector = new BiconnectivityInspector(graph);

		assertEquals(nbVertices - 2, inspector.getCutpoints().size());
		assertEquals(nbVertices - 1, inspector.getBiconnectedVertexComponents()
				.size());
	}

	public void testNotBiconnected() {
		NotBiconnectedGraph graph = new NotBiconnectedGraph();

		BiconnectivityInspector inspector = new BiconnectivityInspector(graph);

		assertEquals(2, inspector.getCutpoints().size());
		assertEquals(3, inspector.getBiconnectedVertexComponents().size());
	}
}

// End BiconnectivityInspectorTest.java
