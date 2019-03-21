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
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ============LICENSE_END=========================================================
 */

package org.onap.aai.util.genxsd;

import java.util.Set;
import java.util.TreeSet;

public class DeleteFootnoteSet {
    protected Set<String> footnotes = new TreeSet<>();
    protected String targetNode = "<NodeType>";

    public DeleteFootnoteSet(String targetNode) {
        super();
        this.targetNode = targetNode == null ? "" : targetNode;
    }

    public void add(String s) {
        String fullnote = null;
        if ("(1)".equals(s)) {
            fullnote = s + " IF this " + targetNode.toUpperCase()
                + " node is deleted, this FROM node is DELETED also";
        } else if ("(2)".equals(s)) {
            fullnote = s + " IF this " + targetNode.toUpperCase()
                + " node is deleted, this TO node is DELETED also";
        } else if ("(3)".equals(s)) {
            fullnote = s + " IF this FROM node is deleted, this " + targetNode.toUpperCase()
                + " is DELETED also";
        } else if ("(4)".equals(s)) {
            fullnote = s + " IF this TO node is deleted, this " + targetNode.toUpperCase()
                + " is DELETED also";
        } else if (s.contains(targetNode.toUpperCase())) {
            fullnote = s;
        } else {
            return;
        }
        footnotes.add(fullnote);
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (footnotes.size() > 0)
            sb.append("\n      -");
        sb.append(String.join("\n      -", footnotes) + "\n");
        return sb.toString();
    }
}
