/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.serialization.tinkerpop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.tinkerpop.gremlin.process.traversal.step.util.Tree;
import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedVertex;

/**
 * Represents a {@link Vertex} that is disconnected from a {@link Graph} however,
 * traversals are supported as they are backed by a Tree with saturated {@link Vertex} and {@link Edge} objects.
 * These objects are not mutable and can only be used to read information out.
 *
 */

public class TreeBackedVertex extends DetachedVertex implements Vertex {

    private static final long serialVersionUID = -976854460992756953L;
    private final Tree<Element> tree;
    private final Vertex self;

    public TreeBackedVertex(Vertex v, Tree<Element> tree) {
        super(v, true);
        this.self = v;
        this.tree = tree;
    }

    @Override
    public Iterator<Edge> edges(final Direction direction, final String... edgeLabels) {
        final List<Element> edges = tree.getObjectsAtDepth(2);
        final List<Tree<Element>> trees = tree.getTreesAtDepth(2);
        final List<Tree<Element>> vTrees = tree.getTreesAtDepth(3);
        return edges.stream().map(ele -> (Edge) ele).filter(e -> {
            if (Direction.IN.equals(direction)) {
                return e.inVertex().equals(self);
            } else if (Direction.OUT.equals(direction)) {
                return e.outVertex().equals(self);
            } else {
                return true;
            }
        }).filter(e -> {
            boolean result = false;
            if (edgeLabels.length == 0) {
                return true;
            }
            for (String label : edgeLabels) {
                if (label.equals(e.label())) {
                    result = true;
                    break;
                }
            }
            return result;
        }).map(e -> {
            Tree<Element> eTree = new Tree<>();
            for (Tree<Element> tree : trees) {
                if (tree.keySet().contains(e)) {
                    eTree = tree;
                    break;
                }
            }
            TreeBackedVertex in = null;
            TreeBackedVertex out = null;
            if (e.inVertex().equals(self)) {
                in = this;
                out = this.createForVertex(e.outVertex(), vTrees);
            } else if (e.outVertex().equals(self)) {
                out = this;
                in = this.createForVertex(e.inVertex(), vTrees);
            }
            return (Edge) new TreeBackedEdge(e, in, out);
        }).iterator();

    }

    private TreeBackedVertex createForVertex(Vertex v, List<Tree<Element>> trees) {
        Tree<Element> vTree = new Tree<>();
        for (Tree<Element> tree : trees) {
            if (tree.keySet().contains(v)) {
                vTree = tree;
                break;
            }
        }

        return new TreeBackedVertex((Vertex) vTree.keySet().iterator().next(), vTree);
    }

    @Override
    public Iterator<Vertex> vertices(final Direction direction, final String... labels) {
        final List<Tree<Element>> vertexElements = tree.getTreesAtDepth(3);
        final List<Element> edgeElements = tree.getObjectsAtDepth(2);
        return edgeElements.stream().map(ele -> (Edge) ele).filter(e -> {
            boolean result = false;
            if (labels.length == 0) {
                return true;
            }
            for (String label : labels) {
                if (label.equals(e.label())) {
                    result = true;
                    break;
                }
            }
            return result;
        }).filter(e -> {
            if (Direction.IN.equals(direction) && e.inVertex().equals(self)) {
                return true;
            } else if (Direction.OUT.equals(direction) && e.outVertex().equals(self)) {
                return true;
            } else if (Direction.BOTH.equals(direction)) {
                return true;
            } else {
                return false;
            }
        }).map(e -> {
            final List<Vertex> list;
            if (Direction.IN.equals(direction)) {
                list = Collections.singletonList(e.outVertex());
            } else if (Direction.OUT.equals(direction)) {
                list = Collections.singletonList(e.inVertex());
            } else {
                list = new ArrayList<>();
                Iterator<Vertex> itr = e.bothVertices();
                while (itr.hasNext()) {
                    list.add(itr.next());
                }
            }
            return list;

        }).flatMap(list -> list.stream()).map(v -> {
            Tree<Element> vTree = new Tree<Element>();
            for (Tree<Element> tree : vertexElements) {
                if (tree.keySet().contains(v)) {
                    vTree = tree;
                    break;
                }
            }

            return (Vertex) new TreeBackedVertex(v, vTree);
        }).iterator();
    }

}
