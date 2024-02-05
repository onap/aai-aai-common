/**
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright © 2017-2018 AT&T Intellectual Property. All rights reserved.
 * ================================================================================
 * Modifications Copyright © 2024 Deutsche Telekom.
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

 package org.onap.aai.query.builder;

 import java.util.Collections;
 import java.util.HashMap;
 import java.util.List;
 import java.util.Map;
 import java.util.Optional;
 import java.util.stream.Collectors;
 
 import org.apache.commons.lang3.tuple.ImmutablePair;
 import org.apache.tinkerpop.gremlin.process.traversal.Step;
 import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
 import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversal;
 import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
 import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
 import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
 import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
 import org.apache.tinkerpop.gremlin.structure.Vertex;
 import org.onap.aai.db.props.AAIProperties;
 import org.onap.aai.introspection.Introspector;
 import org.onap.aai.introspection.Loader;
 import org.onap.aai.schema.enums.ObjectMetadata;
 
 public class TraversalURIOptimizedQuery<E> extends TraversalQuery<E> {
 
     protected Map<Integer, String> stepToAaiUri = new HashMap<>();
 
     public TraversalURIOptimizedQuery(Loader loader, GraphTraversalSource source) {
         super(loader, source);
         optimize = true;
     }
 
     public TraversalURIOptimizedQuery(Loader loader, GraphTraversalSource source, GraphTraversal<Vertex, Vertex> traversal) {
         super(loader, source);
         optimize = true;
     }
 
     public TraversalURIOptimizedQuery(Loader loader, GraphTraversalSource source, Vertex start) {
         super(loader, source, start);
         optimize = true;
     }
 
     protected TraversalURIOptimizedQuery(GraphTraversal traversal, Loader loader, GraphTraversalSource source,
             GraphTraversalBuilder graphTraversalBuilder) {
         super(traversal, loader, source, graphTraversalBuilder);
         optimize = true;
     }
 
     protected TraversalURIOptimizedQuery(GraphTraversal traversal, Loader loader, GraphTraversalSource source,
             GraphTraversalBuilder graphTraversalBuilder, Map<Integer, String> stepToAaiUri) {
         super(traversal, loader, source, graphTraversalBuilder);
         optimize = graphTraversalBuilder.optimize;
         this.stepToAaiUri = stepToAaiUri;
     }
 
     @Override
     protected void executeQuery() {
 
         this.completeTraversal = this.traversal.asAdmin().clone();
 
         if (this.optimize) {
             this.completeTraversal = this.pivotTraversal(this.completeTraversal);
         }
 
         if (start == null) {
             Traversal.Admin<Vertex,Vertex> admin = source.V().asAdmin();
             TraversalHelper.insertTraversal(admin.getEndStep(), completeTraversal, admin);
 
             this.completeTraversal = (Traversal.Admin<Vertex, E>) admin;
 
         }
 
     }
 
     private Traversal.Admin<Vertex, E> pivotTraversal(Traversal.Admin<Vertex, E> traversalAdmin) {
         // if we do not have an index or other conditions do no optimization
         if (stepToAaiUri.isEmpty()) {
             return traversalAdmin;
         }
 
         Traversal.Admin<Vertex, E> traversalAdminStart = traversalAdmin.clone();
         
         List<Step> steps = traversalAdmin.getSteps();
         // clean up traversal steps
         for (int i = 0; i < steps.size(); i++) {
             traversalAdminStart.removeStep(0);
         }
         
         int lastURIStepIndex = getLastURIStepIndex();
         ((GraphTraversal<Vertex, E>) traversalAdminStart).has(AAIProperties.AAI_URI, stepToAaiUri.get(lastURIStepIndex));
 
         ImmutablePair<Integer, Integer> indexAndStepCountTuple = getHasContainerAdjustedIndexAndSplitPosition(steps, lastURIStepIndex);
         int adjustedIndex = indexAndStepCountTuple.getKey();
         for (int i = adjustedIndex; i < steps.size(); i++) {
             Step step = steps.get(i);
             boolean isFirstStep = i == adjustedIndex;
             if (isFirstStep && step instanceof HasStep) {
                 int splitPosition = indexAndStepCountTuple.getValue();
                 List<HasContainer> newContainers = ((HasStep<?>) step).getHasContainers().stream()
                     .skip(splitPosition)
                     .collect(Collectors.toList());
                 traversalAdminStart.addStep(new HasStep<Vertex>(traversalAdminStart, newContainers.toArray(new HasContainer[0])));
                 i++;
             }
             traversalAdminStart.addStep(steps.get(i));
         }
 
         return traversalAdminStart;
     }
 
     /**
      * Adjust lastURIStepIndex by the number of steps that are in hasContainers.
      * A HasContainer can contain multiple steps, which skews the original index.
      * Returns the step index and split position inside the hasContainer
      * @param steps the list of steps to go through
      * @param lastURIStepIndex the list index to adjust
      * @return a Tuple<Integer, Integer> of the form (index, splitPosition)
      */
     private ImmutablePair<Integer,Integer> getHasContainerAdjustedIndexAndSplitPosition(List<Step> steps, int lastURIStepIndex) {
         int stepCount = 0;
         for (int j = 0; j <= lastURIStepIndex; j++) {
             Step step = steps.get(j);
             if (step instanceof HasStep) {
                 stepCount += ((HasStep<?>) step).getHasContainers().size();
             } else {
                 stepCount++;
             }
             if(stepCount == lastURIStepIndex) {
                 int splitPosition = stepCount + 1 - lastURIStepIndex;
                 return new ImmutablePair<>(j + 1, splitPosition);
             } else if(stepCount > lastURIStepIndex) {
                 int splitPosition = stepCount + 1 - lastURIStepIndex;
                 return new ImmutablePair<>(j, splitPosition);
             }
         }
         return new ImmutablePair<>(lastURIStepIndex, lastURIStepIndex);
     }
 
     @Override
     public QueryBuilder<Vertex> createKeyQuery(Introspector obj) {
         super.createKeyQuery(obj);
 
         if (shouldAddStepUri(obj)) {
             Optional<String> uri = getStepUriFromIntrospector(obj);
             if (uri.isPresent()) {
                 if (stepToAaiUri.isEmpty()) {
                     stepToAaiUri.put(stepIndex + 1, uri.get());
                 } else {
                     stepToAaiUri.put(stepIndex, uri.get());
                 }
             }
         } else {
             optimize = false;
             stepToAaiUri = new HashMap<>();
         }
         return (QueryBuilder<Vertex>) this;
     }
 
     private boolean shouldAddStepUri(Introspector obj) {
         boolean shouldOptimize = optimize;
 
         if (shouldOptimize && start != null) {
             shouldOptimize = false;
         }
 
         if (shouldOptimize && stepToAaiUri.isEmpty() && !obj.isTopLevel()) {
             shouldOptimize = false;
         }
 
         if (shouldOptimize && obj.getMetadata(ObjectMetadata.ABSTRACT) != null) {
             shouldOptimize = false;
         }
 
         return shouldOptimize;
     }
 
     private Optional<String> getStepUriFromIntrospector(Introspector obj) {
         String uri = "";
         try {
             uri = obj.getURI();
         } catch (Exception e) {
         }
 
         if ("".equals(uri)) {
             return Optional.empty();
         }
 
         if (!stepToAaiUri.isEmpty()) {
             uri = stepToAaiUri.get(getLastURIStepIndex()) + uri;
         }
 
         return Optional.of(uri);
     }
 
     protected int getLastURIStepIndex() {
         return Collections.max(stepToAaiUri.keySet());
     }
 
     private Map<Integer, String> getStepToAaiUriWithoutStepGreaterThan(final int index) {
         return stepToAaiUri.entrySet().stream().filter(kv -> kv.getKey() <= index)
                 .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
     }
 
     @Override
     protected QueryBuilder<E> cloneQueryAtStep(int index) {
         GraphTraversal.Admin<Vertex, E> cloneAdmin = getCloneAdmin(index);
         return new TraversalURIOptimizedQuery<>(cloneAdmin, loader, source, this,
                 getStepToAaiUriWithoutStepGreaterThan(index));
     }
 
 }
 