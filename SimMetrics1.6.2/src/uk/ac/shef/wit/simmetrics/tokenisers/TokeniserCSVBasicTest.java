/**
 * SimMetrics - SimMetrics is a java library of Similarity or Distance
 * Metrics, e.g. Levenshtein Distance, that provide float based similarity
 * measures between String Data. All metrics return consistant measures
 * rather than unbounded similarity scores.
 *
 * Copyright (C) 2005 Sam Chapman - Open Source Release v1.1
 *
 * Please Feel free to contact me about this library, I would appreciate
 * knowing quickly what you wish to use it for and any criticisms/comments
 * upon the SimMetric library.
 *
 * email:       s.chapman@dcs.shef.ac.uk
 * www:         http://www.dcs.shef.ac.uk/~sam/
 * www:         http://www.dcs.shef.ac.uk/~sam/stringmetrics.html
 *
 * address:     Sam Chapman,
 *              Department of Computer Science,
 *              University of Sheffield,
 *              Sheffield,
 *              S. Yorks,
 *              S1 4DP
 *              United Kingdom,
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation; either version 2 of the License, or (at your
 * option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License
 * for more details.
 *
 * You should have received a copy of the GNU General Public License along
 * with this program; if not, write to the Free Software Foundation, Inc.,
 * 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA
 */

package uk.ac.shef.wit.simmetrics.tokenisers;

import junit.framework.TestCase;

import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA. User: Administrator Date: 23-Nov-2006 Time:
 * 12:04:10 To change this template use File | Settings | File Templates.
 */
public class TokeniserCSVBasicTest extends TestCase {

	/**
	 * internal tokeniser.
	 */
	private InterfaceTokeniser tokeniser = null;

	/**
	 * main constructor setting the name of the test case.
	 * 
	 * @param s
	 *            The name of the test
	 */
	public TokeniserCSVBasicTest(String s) {
		super(s);
	}

	/**
	 * Sets up the test fixture.
	 * 
	 * Called before every test case method.
	 */
	protected void setUp() {
		tokeniser = new TokeniserCSVBasic();
	}

	/**
	 * Tears down the test fixture.
	 * 
	 * Called after every test case method.
	 */
	protected void tearDown() {
		// release objects under test here, if necessary
	}

	/**
	 * Tests emptying the cart.
	 */
	public void testTokeniseToArrayList() {
		ArrayList results = tokeniser
				.tokenizeToArrayList("1a,2a,3a,4a\n1b,2b,3b,4b");
		assertEquals(1, results.size());
		assertEquals("1a", results.get(0));
		assertEquals("2a", results.get(1));
		assertEquals("3a", results.get(2));
		assertEquals("4a", results.get(3));
		assertEquals("1b", results.get(4));
		assertEquals("2b", results.get(5));
		assertEquals("3b", results.get(6));
		assertEquals("4b", results.get(7));
	}
}
