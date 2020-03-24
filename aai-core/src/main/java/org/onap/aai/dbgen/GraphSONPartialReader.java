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

package org.onap.aai.dbgen;

import org.apache.tinkerpop.gremlin.structure.*;
import org.apache.tinkerpop.gremlin.structure.io.GraphReader;
import org.apache.tinkerpop.gremlin.structure.io.GraphWriter;
import org.apache.tinkerpop.gremlin.structure.io.Mapper;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONMapper;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONReader;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONTokens;
import org.apache.tinkerpop.gremlin.structure.io.graphson.GraphSONWriter;
import org.apache.tinkerpop.gremlin.structure.io.gryo.GryoWriter;
import org.apache.tinkerpop.gremlin.structure.util.Attachable;
import org.apache.tinkerpop.gremlin.structure.util.Host;
import org.apache.tinkerpop.gremlin.structure.util.star.StarGraph;
import org.apache.tinkerpop.gremlin.util.function.FunctionUtils;
import org.apache.tinkerpop.gremlin.util.iterator.IteratorUtils;
import org.apache.tinkerpop.shaded.jackson.core.type.TypeReference;
import org.apache.tinkerpop.shaded.jackson.databind.JsonNode;
import org.apache.tinkerpop.shaded.jackson.databind.ObjectMapper;
import org.apache.tinkerpop.shaded.jackson.databind.node.JsonNodeType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * This is a Wrapper around the GraphsonReader class
 * The idea is to rewrite methods that are customized for A&AI
 * GraphsonReader is a final class . hence the use of the Wrapper
 * instead of inheriting-overwriting
 *
 *
 */
public final class GraphSONPartialReader implements GraphReader {
    private final ObjectMapper mapper;
    private final long batchSize;
    private boolean unwrapAdjacencyList = false;
    private final GraphSONReader reader;

    private static final Logger LOGGER = LoggerFactory.getLogger(GraphSONPartialReader.class);

    final TypeReference<Map<String, Object>> mapTypeReference = new TypeReference<Map<String, Object>>() {};

    private GraphSONPartialReader(final Builder builder) {
        mapper = builder.mapper.createMapper();
        batchSize = builder.batchSize;
        unwrapAdjacencyList = builder.unwrapAdjacencyList;
        reader = GraphSONReader.build().create();
    }

    /**
     * Read data into a {@link Graph} from output generated by any of the {@link GraphSONWriter} {@code writeVertex} or
     * {@code writeVertices} methods or by {@link GryoWriter#writeGraph(OutputStream, Graph)}.
     *
     * @param inputStream a stream containing an entire graph of vertices and edges as defined by the accompanying
     *        {@link GraphSONWriter#writeGraph(OutputStream, Graph)}.
     * @param graphToWriteTo the graph to write to when reading from the stream.
     */
    @Override
    public void readGraph(final InputStream inputStream, final Graph graphToWriteTo) throws IOException {
        // dual pass - create all vertices and store to cache the ids. then create edges. as long as we don't
        // have vertex labels in the output we can't do this single pass
        LOGGER.info("Read the Partial Graph");
        final Map<StarGraph.StarVertex, Vertex> cache = new HashMap<>();
        final AtomicLong counter = new AtomicLong(0);

        final boolean supportsTx = graphToWriteTo.features().graph().supportsTransactions();
        final Graph.Features.EdgeFeatures edgeFeatures = graphToWriteTo.features().edge();

        readVertexStrings(inputStream)
                .<Vertex>map(FunctionUtils.wrapFunction(
                        line -> readVertex(new ByteArrayInputStream(line.getBytes()), null, null, Direction.IN)))
                .forEach(vertex -> {
                    try {
                        final Attachable<Vertex> attachable = (Attachable<Vertex>) vertex;
                        cache.put((StarGraph.StarVertex) attachable.get(),
                                attachable.attach(Attachable.Method.create(graphToWriteTo)));
                        if (supportsTx && counter.incrementAndGet() % batchSize == 0)
                            graphToWriteTo.tx().commit();
                    } catch (Exception ex) {
                        LOGGER.info(String.format("Error in reading vertex from graphson%s", vertex.toString()));
                    }
                });

        cache.entrySet().forEach(kv -> kv.getKey().edges(Direction.IN).forEachRemaining(e -> {
            try {
                // can't use a standard Attachable attach method here because we have to use the cache for those
                // graphs that don't support userSuppliedIds on edges. note that outVertex/inVertex methods return
                // StarAdjacentVertex whose equality should match StarVertex.
                final Vertex cachedOutV = cache.get(e.outVertex());
                final Vertex cachedInV = cache.get(e.inVertex());

                if (cachedOutV != null && cachedInV != null) {

                    final Edge newEdge =
                            edgeFeatures.willAllowId(e.id()) ? cachedOutV.addEdge(e.label(), cachedInV, T.id, e.id())
                                    : cachedOutV.addEdge(e.label(), cachedInV);
                    e.properties().forEachRemaining(p -> newEdge.property(p.key(), p.value()));
                } else {
                    LOGGER.debug(String.format("Ghost edges from %s to %s", cachedOutV, cachedInV));

                }
                if (supportsTx && counter.incrementAndGet() % batchSize == 0)
                    graphToWriteTo.tx().commit();
            } catch (Exception ex) {
                LOGGER.info(String.format("Error in writing vertex into graph%s", e.toString()));
            }
        }));

        if (supportsTx)
            graphToWriteTo.tx().commit();
    }

    /**
     * Read {@link Vertex} objects from output generated by any of the {@link GraphSONWriter} {@code writeVertex} or
     * {@code writeVertices} methods or by {@link GraphSONWriter#writeGraph(OutputStream, Graph)}.
     *
     * @param inputStream a stream containing at least one {@link Vertex} as defined by the accompanying
     *        {@link GraphWriter#writeVertices(OutputStream, Iterator, Direction)} or
     *        {@link GraphWriter#writeVertices(OutputStream, Iterator)} methods.
     * @param vertexAttachMethod a function that creates re-attaches a {@link Vertex} to a {@link Host} object.
     * @param edgeAttachMethod a function that creates re-attaches a {@link Edge} to a {@link Host} object.
     * @param attachEdgesOfThisDirection only edges of this direction are passed to the {@code edgeMaker}.
     */
    @Override
    public Iterator<Vertex> readVertices(final InputStream inputStream,
            final Function<Attachable<Vertex>, Vertex> vertexAttachMethod,
            final Function<Attachable<Edge>, Edge> edgeAttachMethod, final Direction attachEdgesOfThisDirection)
            throws IOException {
        return reader.readVertices(inputStream, vertexAttachMethod, edgeAttachMethod, attachEdgesOfThisDirection);

    }

    /**
     * Read a {@link Vertex} from output generated by any of the {@link GraphSONWriter} {@code writeVertex} or
     * {@code writeVertices} methods or by {@link GraphSONWriter#writeGraph(OutputStream, Graph)}.
     *
     * @param inputStream a stream containing at least a single vertex as defined by the accompanying
     *        {@link GraphWriter#writeVertex(OutputStream, Vertex)}.
     * @param vertexAttachMethod a function that creates re-attaches a {@link Vertex} to a {@link Host} object.
     */
    @Override
    public Vertex readVertex(final InputStream inputStream,
            final Function<Attachable<Vertex>, Vertex> vertexAttachMethod) throws IOException {
        return reader.readVertex(inputStream, vertexAttachMethod);
    }

    /**
     * Read a {@link Vertex} from output generated by any of the {@link GraphSONWriter} {@code writeVertex} or
     * {@code writeVertices} methods or by {@link GraphSONWriter#writeGraph(OutputStream, Graph)}.
     *
     * @param inputStream a stream containing at least one {@link Vertex} as defined by the accompanying
     *        {@link GraphWriter#writeVertices(OutputStream, Iterator, Direction)} method.
     * @param vertexAttachMethod a function that creates re-attaches a {@link Vertex} to a {@link Host} object.
     * @param edgeAttachMethod a function that creates re-attaches a {@link Edge} to a {@link Host} object.
     * @param attachEdgesOfThisDirection only edges of this direction are passed to the {@code edgeMaker}.
     */
    @Override
    public Vertex readVertex(final InputStream inputStream,
            final Function<Attachable<Vertex>, Vertex> vertexAttachMethod,
            final Function<Attachable<Edge>, Edge> edgeAttachMethod, final Direction attachEdgesOfThisDirection)
            throws IOException {

        return reader.readVertex(inputStream, vertexAttachMethod, edgeAttachMethod, attachEdgesOfThisDirection);
    }

    /**
     * Read an {@link Edge} from output generated by {@link GraphSONWriter#writeEdge(OutputStream, Edge)} or via
     * an {@link Edge} passed to {@link GraphSONWriter#writeObject(OutputStream, Object)}.
     *
     * @param inputStream a stream containing at least one {@link Edge} as defined by the accompanying
     *        {@link GraphWriter#writeEdge(OutputStream, Edge)} method.
     * @param edgeAttachMethod a function that creates re-attaches a {@link Edge} to a {@link Host} object.
     */
    @Override
    public Edge readEdge(final InputStream inputStream, final Function<Attachable<Edge>, Edge> edgeAttachMethod)
            throws IOException {
        return reader.readEdge(inputStream, edgeAttachMethod);
    }

    /**
     * Read a {@link VertexProperty} from output generated by
     * {@link GraphSONWriter#writeVertexProperty(OutputStream, VertexProperty)} or via an {@link VertexProperty} passed
     * to {@link GraphSONWriter#writeObject(OutputStream, Object)}.
     *
     * @param inputStream a stream containing at least one {@link VertexProperty} as written by the accompanying
     *        {@link GraphWriter#writeVertexProperty(OutputStream, VertexProperty)} method.
     * @param vertexPropertyAttachMethod a function that creates re-attaches a {@link VertexProperty} to a
     *        {@link Host} object.
     */
    @Override
    public VertexProperty readVertexProperty(final InputStream inputStream,
            final Function<Attachable<VertexProperty>, VertexProperty> vertexPropertyAttachMethod) throws IOException {
        return reader.readVertexProperty(inputStream, vertexPropertyAttachMethod);
    }

    /**
     * Read a {@link Property} from output generated by {@link GraphSONWriter#writeProperty(OutputStream, Property)} or
     * via an {@link Property} passed to {@link GraphSONWriter#writeObject(OutputStream, Object)}.
     *
     * @param inputStream a stream containing at least one {@link Property} as written by the accompanying
     *        {@link GraphWriter#writeProperty(OutputStream, Property)} method.
     * @param propertyAttachMethod a function that creates re-attaches a {@link Property} to a {@link Host} object.
     */
    @Override
    public Property readProperty(final InputStream inputStream,
            final Function<Attachable<Property>, Property> propertyAttachMethod) throws IOException {
        return reader.readProperty(inputStream, propertyAttachMethod);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <C> C readObject(final InputStream inputStream, final Class<? extends C> clazz) throws IOException {
        return mapper.readValue(inputStream, clazz);
    }

    private Stream<String> readVertexStrings(final InputStream inputStream) throws IOException {
        if (unwrapAdjacencyList) {
            final JsonNode root = mapper.readTree(inputStream);
            final JsonNode vertices = root.get(GraphSONTokens.VERTICES);
            if (!vertices.getNodeType().equals(JsonNodeType.ARRAY))
                throw new IOException(String.format("The '%s' key must be an array", GraphSONTokens.VERTICES));
            return IteratorUtils.stream(vertices.elements()).map(Object::toString);
        } else {
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            return br.lines();
        }

    }

    public static Builder build() {
        return new Builder();
    }

    public static final class Builder implements ReaderBuilder<GraphSONPartialReader> {
        private long batchSize = 10000;

        private Mapper<ObjectMapper> mapper = GraphSONMapper.build().create();
        private boolean unwrapAdjacencyList = false;

        private Builder() {
        }

        /**
         * Number of mutations to perform before a commit is executed when using
         * {@link GraphSONPartialReader#readGraph(InputStream, Graph)}.
         */
        public Builder batchSize(final long batchSize) {
            this.batchSize = batchSize;
            return this;
        }

        /**
         * Override all of the {@link GraphSONMapper} builder
         * options with this mapper. If this value is set to something other than null then that value will be
         * used to construct the writer.
         */
        public Builder mapper(final Mapper<ObjectMapper> mapper) {
            this.mapper = mapper;
            return this;
        }

        /**
         * If the adjacency list is wrapped in a JSON object, as is done when writing a graph with
         * {@link GraphSONWriter.Builder#wrapAdjacencyList} set to {@code true}, this setting needs to be set to
         * {@code true} to properly read it. By default, this value is {@code false} and the adjacency list is
         * simply read as line delimited vertices.
         * <p/>
         * By setting this value to {@code true}, the generated JSON is no longer "splittable" by line and thus not
         * suitable for OLAP processing. Furthermore, reading this format of the JSON with
         * {@link GraphSONPartialReader#readGraph(InputStream, Graph)} or
         * {@link GraphSONPartialReader#readVertices(InputStream, Function, Function, Direction)} requires that the
         * entire JSON object be read into memory, so it is best saved for "small" graphs.
         */
        public Builder unwrapAdjacencyList(final boolean unwrapAdjacencyList) {
            this.unwrapAdjacencyList = unwrapAdjacencyList;
            return this;
        }

        public GraphSONPartialReader create() {
            return new GraphSONPartialReader(this);
        }
    }
}
