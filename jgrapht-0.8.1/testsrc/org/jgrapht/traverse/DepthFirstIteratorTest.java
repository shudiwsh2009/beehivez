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
/* ---------------------------
 * DepthFirstIteratorTest.java
 * ---------------------------
 * (C) Copyright 2003-2008, by Liviu Rau and Contributors.
 *
 * Original Author:  Liviu Rau
 * Contributor(s):   Barak Naveh
 *
 * $Id: DepthFirstIteratorTest.java 645 2008-09-30 19:44:48Z perfecthash $
 *
 * Changes
 * -------
 * 30-Jul-2003 : Initial revision (LR);
 * 06-Aug-2003 : Test traversal listener & extract a shared superclass (BN);
 *
 */
package org.jgrapht.traverse;

import java.util.*;

import org.jgrapht.*;
import org.jgrapht.graph.*;

/**
 * Tests for the {@link DepthFirstIteratorTest} class.
 * 
 * <p>
 * NOTE: This test uses hard-coded expected ordering isn't really guaranteed by
 * the specification of the algorithm. This could cause false failures if the
 * traversal implementation changes.
 * </p>
 * 
 * @author Liviu Rau
 * @since Jul 30, 2003
 */
public class DepthFirstIteratorTest extends AbstractGraphIteratorTest {
	// ~ Methods
	// ----------------------------------------------------------------

	String getExpectedStr1() {
		return "1,3,6,5,7,9,4,8,2";
	}

	String getExpectedStr2() {
		return "1,3,6,5,7,9,4,8,2,orphan";
	}

	String getExpectedFinishString() {
		return "6:4:9:2:8:7:5:3:1:orphan:";
	}

	AbstractGraphIterator<String, DefaultEdge> createIterator(
			DirectedGraph<String, DefaultEdge> g, String vertex) {
		AbstractGraphIterator<String, DefaultEdge> i = new DepthFirstIterator<String, DefaultEdge>(
				g, vertex);
		i.setCrossComponentTraversal(true);

		return i;
	}

	/**
	 * See <a href="http://sf.net/projects/jgrapht">Sourceforge bug 1169182</a>
	 * for details.
	 */
	public void testBug1169182() {
		DirectedGraph<String, DefaultEdge> dg = new DefaultDirectedGraph<String, DefaultEdge>(
				DefaultEdge.class);

		String a = "A";
		String b = "B";
		String c = "C";
		String d = "D";
		String e = "E";
		String f = "F";
		String g = "G";
		String h = "H";
		String i = "I";
		String j = "J";
		String k = "K";
		String l = "L";

		dg.addVertex(a);
		dg.addVertex(b);
		dg.addVertex(c);
		dg.addVertex(d);
		dg.addVertex(e);
		dg.addVertex(f);
		dg.addVertex(g);
		dg.addVertex(h);
		dg.addVertex(i);
		dg.addVertex(j);
		dg.addVertex(k);
		dg.addVertex(l);

		dg.addEdge(a, b);
		dg.addEdge(b, c);
		dg.addEdge(c, j);
		dg.addEdge(c, d);
		dg.addEdge(c, e);
		dg.addEdge(c, f);
		dg.addEdge(c, g);
		dg.addEdge(d, h);
		dg.addEdge(e, h);
		dg.addEdge(f, i);
		dg.addEdge(g, i);
		dg.addEdge(h, j);
		dg.addEdge(i, c);
		dg.addEdge(j, k);
		dg.addEdge(k, l);

		Iterator<String> dfs = new DepthFirstIterator<String, DefaultEdge>(dg);
		String actual = "";
		while (dfs.hasNext()) {
			String v = dfs.next();
			actual += v;
		}

		String expected = "ABCGIFEHJKLD";
		assertEquals(expected, actual);
	}
}

// End DepthFirstIteratorTest.java
