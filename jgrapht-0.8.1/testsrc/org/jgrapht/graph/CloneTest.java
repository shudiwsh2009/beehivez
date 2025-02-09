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
/* --------------
 * CloneTest.java
 * --------------
 * (C) Copyright 2003-2008, by John V. Sichi and Contributors.
 *
 * Original Author:  John V. Sichi
 * Contributor(s):   -
 *
 * $Id: CloneTest.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 * 06-Oct-2003 : Initial revision (JVS);
 *
 */
package org.jgrapht.graph;

import org.jgrapht.*;

/**
 * A unit test for a cloning bug, adapted from a forum entry from Linda Buisman.
 * 
 * @author John V. Sichi
 * @since Oct 6, 2003
 */
public class CloneTest extends EnhancedTestCase {
	// ~ Constructors
	// -----------------------------------------------------------

	/**
	 * @see junit.framework.TestCase#TestCase(java.lang.String)
	 */
	public CloneTest(String name) {
		super(name);
	}

	// ~ Methods
	// ----------------------------------------------------------------

	/**
	 * Test graph cloning.
	 */
	@SuppressWarnings("unchecked")
	public void testCloneSpecificsBug() {
		SimpleGraph<String, DefaultEdge> g1 = new SimpleGraph<String, DefaultEdge>(
				DefaultEdge.class);
		String one = "1";
		String two = "2";
		String three = "3";
		g1.addVertex(one);
		g1.addVertex(two);
		g1.addVertex(three);
		g1.addEdge(one, two);
		g1.addEdge(two, three);

		SimpleGraph<String, DefaultEdge> g2 = (SimpleGraph<String, DefaultEdge>) g1
				.clone(); // Type-safty
		// warning OK with
		// clone
		assertEquals(2, g2.edgeSet().size());
		assertNotNull(g2.getEdge(one, two));
		assertTrue(g2.removeEdge(g2.getEdge(one, two)));
		assertNotNull(g2.removeEdge("2", "3"));
		assertTrue(g2.edgeSet().isEmpty());
	}

	/**
	 * Tests usage of {@link ParanoidGraph} for detecting broken vertex
	 * implementations.
	 */
	public void testParanoidGraph() {
		BrokenVertex v1 = new BrokenVertex(1);
		BrokenVertex v2 = new BrokenVertex(2);
		BrokenVertex v3 = new BrokenVertex(1);

		SimpleGraph<BrokenVertex, DefaultEdge> g = new SimpleGraph<BrokenVertex, DefaultEdge>(
				DefaultEdge.class);
		ParanoidGraph<BrokenVertex, DefaultEdge> pg = new ParanoidGraph<BrokenVertex, DefaultEdge>(
				g);
		pg.addVertex(v1);
		pg.addVertex(v2);
		try {
			pg.addVertex(v3);

			// should not get here
			assertFalse();
		} catch (IllegalArgumentException ex) {
			// expected, swallow
		}
	}

	// ~ Inner Classes
	// ----------------------------------------------------------

	private class BrokenVertex {
		private int x;

		BrokenVertex(int x) {
			this.x = x;
		}

		public boolean equals(Object other) {
			if (!(other instanceof BrokenVertex)) {
				return false;
			}
			return x == ((BrokenVertex) other).x;
		}
	}
}

// End CloneTest.java
