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
package org.elasticsearch.shell.command;

import java.io.PrintStream;
import java.util.Iterator;

import org.elasticsearch.common.inject.Inject;
import org.elasticsearch.shell.console.Console;

/**
 * Command that allows to display the entries available in the history (if supported)
 *
 * @author Luca Cavanna
 */
@ExecutableCommand(aliases = "history")
public class HistoryCommand extends Command {

    @Inject
    protected HistoryCommand(Console<PrintStream> console) {
        super(console);
    }

    @SuppressWarnings("unused")
    public void execute() {
        int i = 1;
        Iterator<CharSequence> entries = console.getHistoryEntries();
        while (entries.hasNext()) {
            int digits = String.valueOf(i).length();
            StringBuilder messageBuilder = new StringBuilder();
            for (int j = 0; j < 5 - digits; j++) {
                messageBuilder.append(" ");
            }
            messageBuilder.append(i++).append("  ").append(entries.next());
            console.println(messageBuilder.toString());
        }
    }
}