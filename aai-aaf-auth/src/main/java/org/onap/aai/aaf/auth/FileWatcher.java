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

package org.onap.aai.aaf.auth;

import java.io.File;
import java.util.TimerTask;

public abstract class FileWatcher extends TimerTask {
    private long timeStamp;
    private File file;

    /**
     * Instantiates a new file watcher.
     *
     * @param file the file
     */
    public FileWatcher(File file) {
        this.file = file;
        this.timeStamp = file.lastModified();
    }

    /**
     * runs a timer task
     *
     * @see TimerTask#run
     */
    public final void run() {
        long timeStamp = file.lastModified();

        if ((timeStamp - this.timeStamp) > 500) {
            this.timeStamp = timeStamp;
            onChange(file);
        }
    }

    /**
     * On change.
     *
     * @param file the file
     */
    protected abstract void onChange(File file);
}
