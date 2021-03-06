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
package com.link_intersystems.util.graph;

import java.util.Iterator;

import org.apache.commons.collections4.Closure;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.functors.NOPClosure;

/**
 * Facade to ease the use of components within the graph package.
 *
 * @author René Link <a
 *         href="mailto:rene.link@link-intersystems.com">[rene.link@link-
 *         intersystems.com]</a>
 * @since 1.2.0.0
 */
public abstract class GraphFacade {

	/**
	 * Traverses the graph using the breadth first traversal strategy. Starting
	 * at the given {@link Node} and applies the given {@link Closure} on every
	 * {@link Node} .
	 *
	 * @param start
	 *            the {@link Node} to start the traversal from.
	 * @param nodeProcessor
	 *            the closure that should be applied to every node.
	 * @see NOPClosure
	 * @since 1.2.0.0
	 */
	public static void traverseBreadthFirst(Node start, Closure nodeProcessor) {
		Iterator<Node> objectGraphIterator = new BreadthFirstNodeIterator(start);
		forAllDo(objectGraphIterator, nodeProcessor);
	}

	/**
	 * Traverses the graph using the depth first traversal strategy. Starting at
	 * the given {@link Node} and applies the given {@link Closure} on every
	 * {@link Node} .
	 *
	 * @param start
	 *            the {@link Node} to start the traversal from.
	 * @param nodeProcessor
	 *            the closure that should be applied to every node.
	 * @see NOPClosure
	 * @since 1.0.0.0
	 */
	public static void traverseDepthFirst(Node start, Closure nodeProcessor) {
		DepthFirstNodeIterator objectGraphTransformer = new DepthFirstNodeIterator(
				start);
		forAllDo(objectGraphTransformer, nodeProcessor);
	}

	static void forAllDo(Iterator<?> iterator, Closure closure) {
		while (iterator.hasNext()) {
			Object next = iterator.next();
			closure.execute(next);
		}
	}

	public enum NodeIterateStrategy {
		DEPTH_FIRST, BREADTH_FIRST;
	}

	/**
	 * Creates a predicated {@link Node} iterator that iterates the {@link Node}
	 * s per Predicate that is specified using the {@link NodeIterateStrategy}.
	 * <p>
	 * Take the following node structure for example
	 *
	 * <pre>
	 *                   A
	 *      +------------+------------+
	 *      B            C            D
	 * +----+----+       |        +---+------------+
	 * E    F    G       H        I   J            K
	 *                                      +------+------+
	 *                                      L      M      N
	 * </pre>
	 *
	 * If we assume that we have 3 {@link Predicate}s
	 * <ul>
	 * <li>The 1. {@link Predicate} matches {@link Node}s A,C,H,D,K</li>
	 * <li>The 2. {@link Predicate} matches {@link Node}s A,B,E,F</li>
	 * <li>The 3. {@link Predicate} matches {@link Node}s D,J,M,N</li>
	 * </ul>
	 * and we construct a per predicated node iterator using
	 *
	 * <pre>
	 * {@link GraphFacade#perPredicateNodeIterator(NodeIterateStrategy, Node, Predicate...) GraphFacade.perPredicatedNodeIterator(BREADTH_FIRST, startNodeA, pred1, pred2, pred3)};
	 * </pre>
	 *
	 * The resulting iterator will iterate the node structure using a breadth
	 * first strategy for every {@link Predicate} starting at startNodeA. <br/>
	 * The result will be:
	 *
	 * <pre>
	 * breadth first  breadth first   breadth first
	 *   matching       matching       matching
	 *    pred1          pred2           pred3
	 * +-----------+   +-------+       +-------+
	 *   A C D H K      A B E F         D J M N
	 *
	 * iterate order -->
	 * </pre>
	 *
	 * </p>
	 *
	 * @param nodeIterateStrategy
	 * @param startNode
	 * @param nodeIterateOrderPredicates
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static Iterator<Node> perPredicateNodeIterator(
			NodeIterateStrategy nodeIterateStrategy, Node startNode,
			Predicate... nodeIterateOrderPredicates) {
		Iterator<Node> iterator = IteratorUtils.EMPTY_ITERATOR;
		for (int i = 0; i < nodeIterateOrderPredicates.length; i++) {
			Predicate predicate = nodeIterateOrderPredicates[i];
			Iterator<Node> nodeStrategyIterator = null;
			switch (nodeIterateStrategy) {
			case BREADTH_FIRST:
				nodeStrategyIterator = new BreadthFirstNodeIterator(startNode);
				break;
			case DEPTH_FIRST:
				nodeStrategyIterator = new DepthFirstNodeIterator(startNode);
				break;
			}
			Iterator<Node> predicateFilterIterator = IteratorUtils
					.filteredIterator(nodeStrategyIterator, predicate);
			iterator = IteratorUtils.chainedIterator(iterator,
					predicateFilterIterator);
		}
		return iterator;
	}

}
