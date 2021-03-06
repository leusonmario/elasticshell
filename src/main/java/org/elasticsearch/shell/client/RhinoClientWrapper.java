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
package org.elasticsearch.shell.client;


import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.ResourceRegistry;
import org.elasticsearch.shell.RhinoShellTopLevel;
import org.elasticsearch.shell.ShellScope;
import org.elasticsearch.shell.json.RhinoJsonToString;
import org.elasticsearch.shell.json.RhinoStringToJson;
import org.elasticsearch.shell.scheduler.Scheduler;
import org.mozilla.javascript.NativeObject;

/**
 * @author Luca Cavanna
 *
 * Rhino implementation of {@link ClientWrapper}
 */
public class RhinoClientWrapper extends AbstractClientWrapper<RhinoClientNativeJavaObject, NativeObject, Object> {

    private final ShellScope<RhinoShellTopLevel> shellScope;

    @Inject
    RhinoClientWrapper(ShellScope<RhinoShellTopLevel> shellScope,
                       RhinoJsonToString jsonToString, RhinoStringToJson stringToJson,
                       ResourceRegistry resourceRegistry, Scheduler scheduler) {
        super(jsonToString, stringToJson, resourceRegistry, scheduler);
        this.shellScope = shellScope;
    }

    @Override
    protected RhinoClientNativeJavaObject wrapShellClient(AbstractClient<NativeObject, Object> shellClient) {
        return new RhinoClientNativeJavaObject(shellScope.get(), shellClient);
    }
}
