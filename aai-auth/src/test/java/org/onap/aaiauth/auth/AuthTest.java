/*
 * ============LICENSE_START=======================================================
 * org.onap.aai
 * ================================================================================
 * Copyright (C) 2026
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

package org.onap.aaiauth.auth;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.Test;

public class AuthTest {

    @Test
    public void authorizesCaseInsensitiveUserForAllMethodsWhenMethodsAreOmitted() throws Exception {
        Auth auth = new Auth(writeAuthFile("{\"roles\":[{\"name\":\"reader\",\"users\":[{\"username\":\"Alice\"}],\"functions\":[{\"name\":\"inventory\"}]}]}"));

        assertTrue(auth.authBasic("alice", "GET:inventory"));
        assertTrue(auth.authBasic("alice", "PATCH:inventory"));
        assertFalse(auth.authBasic("alice", "HEAD:inventory"));
    }

    @Test
    public void reloadsCachedUsersWhenTheConfigurationChanges() throws Exception {
        Path authFile = Files.createTempFile("auth", ".json");
        Files.writeString(authFile, "{\"roles\":[{\"name\":\"reader\",\"users\":[{\"username\":\"alice\"}],\"functions\":[{\"name\":\"inventory\",\"methods\":[{\"name\":\"GET\"}]}]}]}", StandardCharsets.UTF_8);
        AuthCore authCore = new AuthCore(authFile.toString());

        assertTrue(authCore.authorize("alice", "GET:inventory"));
        Files.writeString(authFile, "{\"roles\":[{\"name\":\"writer\",\"users\":[{\"username\":\"alice\"}],\"functions\":[{\"name\":\"inventory\",\"methods\":[{\"name\":\"POST\"}]}]}]}", StandardCharsets.UTF_8);
        authCore.loadUsers(authFile.toString());

        assertFalse(authCore.authorize("alice", "GET:inventory"));
        assertTrue(authCore.authorize("alice", "POST:inventory"));
    }

    @Test
    public void rejectsRequestsWithMissingUserOrAction() throws Exception {
        Auth auth = new Auth(writeAuthFile("{\"roles\":[]}"));

        assertFalse(auth.validateRequest(null, "GET:inventory"));
        assertFalse(auth.validateRequest("alice", null));
    }

    private String writeAuthFile(String content) throws Exception {
        Path authFile = Files.createTempFile("auth", ".json");
        Files.writeString(authFile, content, StandardCharsets.UTF_8);
        return authFile.toString();
    }
}