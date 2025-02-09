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
 * BlockCutpointGraphTest.java
 * -------------------------
 * (C) Copyright 2007-2008, by France Telecom
 *
 * Original Author:  Guillaume Boulmier and Contributors.
 *
 * $Id: BlockCutpointGraphTest.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 * 05-Jun-2007 : Initial revision (GB);
 *
 */
package org.jgrapht.alg;

import java.util.*;

import junit.framework.*;

import org.jgrapht.*;
import org.jgrapht.generate.*;
import org.jgrapht.graph.*;

/**
 * @author Guillaume Boulmier
 * @since July 5, 2007
 */
@SuppressWarnings("unchecked")
public class BlockCutpointGraphTest extends TestCase {
	// ~ Methods
	// ----------------------------------------------------------------

	public void testBiconnected() {
		BiconnectedGraph graph = new BiconnectedGraph();

		BlockCutpointGraph blockCutpointGraph = new BlockCutpointGraph(graph);
		testGetBlock(blockCutpointGraph);

		assertEquals(0, blockCutpointGraph.getCutpoints().size());
		int nbBiconnectedComponents = blockCutpointGraph.vertexSet().size()
				- blockCutpointGraph.getCutpoints().size();
		assertEquals(1, nbBiconnectedComponents);
	}

	public void testGetBlock(BlockCutpointGraph blockCutpointGraph) {
		for (Iterator iter = blockCutpointGraph.vertexSet().iterator(); iter
				.hasNext();) {
			UndirectedGraph component = (UndirectedGraph) iter.next();
			if (!component.edgeSet().isEmpty()) {
				for (Iterator iterator = component.vertexSet().iterator(); iterator
						.hasNext();) {
					Object vertex = iterator.next();
					if (!blockCutpointGraph.getCutpoints().contains(vertex)) {
						assertEquals(component, blockCutpointGraph
								.getBlock(vertex));
					}
				}
			} else {
				assertTrue(blockCutpointGraph.getCutpoints().contains(
						component.vertexSet().iterator().next()));
			}
		}
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

		BlockCutpointGraph blockCutpointGraph = new BlockCutpointGraph(graph);
		testGetBlock(blockCutpointGraph);

		assertEquals(nbVertices - 2, blockCutpointGraph.getCutpoints().size());
		int nbBiconnectedComponents = blockCutpointGraph.vertexSet().size()
				- blockCutpointGraph.getCutpoints().size();
		assertEquals(nbVertices - 1, nbBiconnectedComponents);
	}

	public void testNotBiconnected() {
		UndirectedGraph graph = new NotBiconnectedGraph();

		BlockCutpointGraph blockCutpointGraph = new BlockCutpointGraph(graph);
		testGetBlock(blockCutpointGraph);

		assertEquals(2, blockCutpointGraph.getCutpoints().size());
		int nbBiconnectedComponents = blockCutpointGraph.vertexSet().size()
				- blockCutpointGraph.getCutpoints().size();
		assertEquals(3, nbBiconnectedComponents);
	}
}

// End BlockCutpointGraphTest.java
