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
package com.link_intersystems.lang;

import static junit.framework.Assert.assertEquals;

import org.apache.commons.lang3.ArrayUtils;
import org.junit.Test;

public class PrimitiveArrayCallbackTest {

	@Test(expected = IllegalArgumentException.class)
	public void nullConstructorArg() {
		new PrimitiveArrayCallback(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void notAnArrayConstructorArg() {
		new PrimitiveArrayCallback(new Object());
	}

	@Test(expected = IllegalArgumentException.class)
	public void notAnPrimitiveArrayConstructorArg() {
		new PrimitiveArrayCallback(ArrayUtils.EMPTY_OBJECT_ARRAY);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void setIndexIsOutOfUpperBounds() {
		int[] arr = new int[10];
		PrimitiveArrayCallback callback = new PrimitiveArrayCallback(arr);
		callback.setIndex(10);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void setIndexIsOutOfLowerBounds() {
		int[] arr = new int[10];
		PrimitiveArrayCallback callback = new PrimitiveArrayCallback(arr);
		callback.setIndex(-1);
	}

	@Test
	public void getIndex() {
		int[] arr = new int[10];
		PrimitiveArrayCallback callback = new PrimitiveArrayCallback(arr);
		callback.setIndex(6);
		int index = callback.getIndex();
		assertEquals(6, index);

	}

	@Test
	public void autoincrement() {
		int[] arr = new int[10];
		PrimitiveArrayCallback callback = new PrimitiveArrayCallback(arr);
		callback.setIndex(8);
		assertEquals(0, arr[8]);
		callback.primitive(13);
		assertEquals(13, arr[8]);
		callback.primitive(15);
		assertEquals(15, arr[9]);
	}
}