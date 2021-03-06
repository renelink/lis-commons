/**
 * Copyright 2011 Link Intersystems GmbH <rene.link@link-intersystems.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.link_intersystems.util;

import static org.junit.Assert.assertEquals;

import java.util.Comparator;

import org.junit.Before;
import org.junit.Test;

public class IdentityComparatorTest {

	private Comparator<Object> comparator;
	private Object less;
	private Object greater;

	@Before
	public void setup() {
		Object o1 = new Object();
		Object o2 = new Object();
		less = System.identityHashCode(o1) < System.identityHashCode(o2) ? o1
				: o2;
		greater = System.identityHashCode(o1) > System.identityHashCode(o2) ? o1
				: o2;
		comparator = new IdentityComparator<Object>();
	}

	@Test
	public void getThroughUtilFacade() {
		Comparator<Object> identityComparator = UtilFacade.identityComparator();
		assertEquals(IdentityComparator.class, identityComparator.getClass());
	}

	@Test
	public void lessThan() {
		assertEquals(-1, comparator.compare(less, greater));
	}

	@Test
	public void greaterThan() {
		assertEquals(1, comparator.compare(greater, less));
	}

	@Test
	public void equal() {
		assertEquals(0, comparator.compare(less, less));
	}
}
