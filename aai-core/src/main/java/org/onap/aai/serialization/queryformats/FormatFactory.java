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

package org.onap.aai.serialization.queryformats;

import jakarta.ws.rs.core.MultivaluedHashMap;
import jakarta.ws.rs.core.MultivaluedMap;

import org.onap.aai.exceptions.AAIException;
import org.onap.aai.introspection.Loader;
import org.onap.aai.serialization.db.DBSerializer;
import org.onap.aai.serialization.queryformats.exceptions.QueryParamInjectionException;
import org.onap.aai.serialization.queryformats.utils.QueryParamInjector;
import org.onap.aai.serialization.queryformats.utils.UrlBuilder;
import org.onap.aai.setup.SchemaVersions;

public class FormatFactory {

    private final Loader loader;
    private final DBSerializer serializer;
    private final UrlBuilder urlBuilder;
    private final QueryParamInjector injector;

    public FormatFactory(Loader loader, DBSerializer serializer, SchemaVersions schemaVersions, String basePath)
            throws AAIException {
        this.loader = loader;
        this.serializer = serializer;
        this.urlBuilder = new UrlBuilder(loader.getVersion(), serializer, schemaVersions, basePath);
        this.injector = QueryParamInjector.getInstance();
    }

    public FormatFactory(Loader loader, DBSerializer serializer, SchemaVersions schemaVersions, String basePath,
            String serverBase) throws AAIException {
        this.loader = loader;
        this.serializer = serializer;
        this.urlBuilder = new UrlBuilder(loader.getVersion(), serializer, serverBase, schemaVersions, basePath);
        this.injector = QueryParamInjector.getInstance();
    }

    public Formatter get(Format format) throws AAIException {
        return get(format, new MultivaluedHashMap<>());
    }

    public Formatter get(Format format, MultivaluedMap<String, String> params) throws AAIException {

        Formatter formatter = null;

        switch (format) {
            case graphson:
                formatter = new Formatter(inject(new GraphSON(), params));
                break;
            case pathed:
                formatter = new Formatter(inject(new PathedURL.Builder(loader, serializer, urlBuilder), params).build(),
                        params);
                break;
            case pathed_resourceversion:
                formatter = new Formatter(
                        inject(new PathedURL.Builder(loader, serializer, urlBuilder).includeUrl(), params).build(),
                        params);
                break;
            case id:
                formatter = new Formatter(inject(new IdURL.Builder(loader, serializer, urlBuilder), params).build(),
                        params);
                break;
            case resource:
                formatter = new Formatter(
                        inject(new Resource.Builder(loader, serializer, urlBuilder, params), params).build(), params);
                break;
            case resource_and_url:
                formatter = new Formatter(
                        inject(new Resource.Builder(loader, serializer, urlBuilder, params).includeUrl(), params)
                                .build(),
                        params);
                break;
            case raw:
                formatter = new Formatter(inject(new RawFormat.Builder(loader, serializer, urlBuilder), params).build(),
                        params);
                break;
            case simple:
                formatter = new Formatter(
                        inject(new RawFormat.Builder(loader, serializer, urlBuilder).depth(0).modelDriven(), params)
                                .build(),
                        params);
                break;
            case aggregate:
                formatter = new Formatter(
                        inject(new Aggregate.Builder(loader, serializer, urlBuilder).depth(0).modelDriven(), params)
                                .build(),
                        params);
                break;
            case console:
                formatter = new Formatter(inject(new Console(), params));
                break;
            case count:
                formatter = new Formatter(inject(new Count(), params));
                break;
            case resource_with_sot:
                formatter = new Formatter(
                        inject(new ResourceWithSoT.Builder(loader, serializer, urlBuilder), params).build(), params);
                break;
            case changes:
                formatter = new Formatter(inject(new ChangesFormat(), params));
                break;
            case state:
                formatter = new Formatter(
                        inject(new StateFormat.Builder(loader, serializer, urlBuilder), params).build(format));
                break;
            case lifecycle:
                formatter = new Formatter(
                        inject(new LifecycleFormat.Builder(loader, serializer, urlBuilder), params).build(format));
                break;
            case tree:
                formatter =
                        new Formatter(inject(new TreeFormat.Builder(loader, serializer, urlBuilder), params).build());
                break;
            default:
                break;

        }

        return formatter;
    }

    private <T> T inject(T obj, MultivaluedMap<String, String> params) throws QueryParamInjectionException {

        injector.injectParams(obj, params);
        return obj;
    }

}
