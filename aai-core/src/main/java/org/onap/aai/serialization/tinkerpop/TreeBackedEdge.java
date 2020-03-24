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

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.util.detached.DetachedEdge;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;

import java.util.Iterator;

/**
 * Represents a {@link Edge} that is disconnected from a {@link Graph} however,
 * traversals are supported as they are backed by a Tree with saturated {@link Vertex} and {@link Edge} objects.
 * These objects are not mutable and can only be used to read information out.
 *
 */
public class TreeBackedEdge extends DetachedEdge implements Edge {

    private static final long serialVersionUID = 5419650145562077538L;
    private TreeBackedVertex inVertex;
    private TreeBackedVertex outVertex;

    public TreeBackedEdge(Edge edge, TreeBackedVertex inVertex, TreeBackedVertex outVertex) {
        super(edge, true);
        this.inVertex = inVertex;
        this.outVertex = outVertex;
    }

    @Override
    public Vertex inVertex() {
        return this.inVertex;
    }

    @Override
    public Vertex outVertex() {
        return this.outVertex;
    }

    @Override
    public Iterator<Vertex> bothVertices() {
        return this.vertices(Direction.BOTH);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        switch (direction) {
            case OUT:
                return IteratorUtils.of(this.outVertex);
            case IN:
                return IteratorUtils.of(this.inVertex);
            default:
                return IteratorUtils.of(this.outVertex, this.inVertex);
        }
    }

}
