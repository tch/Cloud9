/*
 * Cloud9: A MapReduce Library for Hadoop
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0 
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package edu.umd.cloud9.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Random;

import junit.framework.JUnit4TestAdapter;

import org.junit.Test;

public class ArrayListOfIntsTest {

	@Test
	public void testBasic1() {
		int size = 100000;
		Random r = new Random();
		int[] ints = new int[size];

		ArrayListOfInts list = new ArrayListOfInts();
		for (int i = 0; i < size; i++) {
			int k = r.nextInt(size);
			list.add(k);
			ints[i] = k;
		}

		for (int i = 0; i < size; i++) {
			int v = list.get(i);

			assertEquals(ints[i], v);
		}

	}

	@Test
	public void testUpdate() {
		int size = 100000;
		Random r = new Random();
		int[] ints = new int[size];

		ArrayListOfInts list = new ArrayListOfInts();
		for (int i = 0; i < size; i++) {
			int k = r.nextInt(size);
			list.add(k);
			ints[i] = k;
		}

		assertEquals(size, list.size());

		for (int i = 0; i < size; i++) {
			list.set(i, ints[i] + 1);
		}

		assertEquals(size, list.size());

		for (int i = 0; i < size; i++) {
			int v = list.get(i);

			assertEquals(ints[i] + 1, v);
		}

	}

	@Test
	public void testTrim1() {
		int size = 89;
		Random r = new Random();
		int[] ints = new int[size];

		ArrayListOfInts list = new ArrayListOfInts();
		for (int i = 0; i < size; i++) {
			int k = r.nextInt(size);
			list.add(k);
			ints[i] = k;
		}

		for (int i = 0; i < size; i++) {
			int v = list.get(i);

			assertEquals(ints[i], v);
		}

		int[] rawArray = list.getArray();
		int lenBefore = rawArray.length;

		list.trimToSize();
		int[] rawArrayAfter = list.getArray();
		int lenAfter = rawArrayAfter.length;

		assertEquals(89, lenAfter);
		assertTrue(lenBefore > lenAfter);
	}

	@Test
	public void testClone() {
		int size = 100000;
		Random r = new Random();
		int[] ints = new int[size];

		ArrayListOfInts list1 = new ArrayListOfInts();
		for (int i = 0; i < size; i++) {
			int k = r.nextInt(size);
			list1.add(k);
			ints[i] = k;
		}

		ArrayListOfInts list2 = list1.clone();

		assertEquals(size, list1.size());
		assertEquals(size, list2.size());

		for (int i = 0; i < size; i++) {
			list2.set(i, ints[i] + 1);
		}

		// values in old list should not have changed
		assertEquals(size, list1.size());
		for (int i = 0; i < size; i++) {
			assertEquals(ints[i], list1.get(i));
		}

		// however, values in new list should have changed
		assertEquals(size, list1.size());
		for (int i = 0; i < size; i++) {
			assertEquals(ints[i] + 1, list2.get(i));
		}
	}

	@Test
	public void testShift() {
		int size = 100;
		int shift = 10;

		ArrayListOfInts list = new ArrayListOfInts();
		for (int i = 0; i < size; i++)
			list.add(i);
		list.shiftLastNToTop(shift);

		for (int i = 0; i < list.size(); i++) {
			assertEquals(size - shift + i, list.get(i));
		}
		list.add(size);
		assertEquals(shift + 1, list.size());
		assertEquals(size, list.get(shift));
	}

	public static junit.framework.Test suite() {
		return new JUnit4TestAdapter(ArrayListOfIntsTest.class);
	}

}