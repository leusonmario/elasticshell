/*
 * Licensed to Luca Cavanna (the "Author") under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. Elastic Search licenses this
 * file to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.elasticsearch.shell.client.executors.indices;

import org.elasticsearch.action.ActionFuture;
import org.elasticsearch.action.admin.cluster.state.ClusterStateRequest;
import org.elasticsearch.action.admin.cluster.state.ClusterStateResponse;
import org.elasticsearch.client.Client;
import org.elasticsearch.cluster.metadata.IndexMetaData;
import org.elasticsearch.cluster.metadata.MappingMetaData;
import org.elasticsearch.cluster.metadata.MetaData;
import org.elasticsearch.common.collect.ImmutableSet;
import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.shell.JsonSerializer;
import org.elasticsearch.shell.client.executors.AbstractRequestExecutor;

import java.io.IOException;
import java.util.Set;

/**
 * @author Luca Cavanna
 *
 * {@link org.elasticsearch.shell.client.executors.RequestExecutor} implementation for get mapping API
 */
public class GetMappingRequestExecutor<JsonInput, JsonOutput> extends AbstractRequestExecutor<ClusterStateRequest, ClusterStateResponse, JsonInput, JsonOutput> {

    private final Set<String> types;

    public GetMappingRequestExecutor(Client client, JsonSerializer<JsonInput, JsonOutput> jsonSerializer, String... types) {
        super(client, jsonSerializer);
        this.types = ImmutableSet.copyOf(types);
    }

    @Override
    protected ActionFuture<ClusterStateResponse> doExecute(ClusterStateRequest request) {
        return client.admin().cluster().state(request);
    }

    @Override
    protected XContentBuilder toXContent(ClusterStateRequest request, ClusterStateResponse response, XContentBuilder builder) throws IOException {

        MetaData metaData = response.state().metaData();

        builder.startObject();

        if (metaData.indices().isEmpty()) {
            return builder.endObject();
        }

        if (request.filteredIndices() != null && request.filteredIndices().length == 1 && types.size() == 1) {
            boolean foundType = false;
            IndexMetaData indexMetaData = metaData.iterator().next();
            for (MappingMetaData mappingMd : indexMetaData.mappings().values()) {
                if (!types.isEmpty() && !types.contains(mappingMd.type())) {
                    // filter this type out...
                    continue;
                }
                foundType = true;
                builder.field(mappingMd.type());
                builder.map(mappingMd.sourceAsMap());
            }
            if (!foundType) {
                return builder.endObject();
            }
        } else {
            for (IndexMetaData indexMetaData : metaData) {
                builder.startObject(indexMetaData.index(), XContentBuilder.FieldCaseConversion.NONE);

                for (MappingMetaData mappingMd : indexMetaData.mappings().values()) {
                    if (!types.isEmpty() && !types.contains(mappingMd.type())) {
                        // filter this type out...
                        continue;
                    }
                    builder.field(mappingMd.type());
                    builder.map(mappingMd.sourceAsMap());
                }

                builder.endObject();
            }
        }

        return builder.endObject();
    }
}