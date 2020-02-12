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

package org.onap.aai.util;

/*
 * logging to hbase encountered hotspotting issues, so per
 * http://archive.cloudera.com/cdh5/cdh/5/hbase-0.98.6-cdh5.3.8/book/rowkey.design.html
 * we decided to salt the rowkeys
 * as these keys are generated in a couple places, I made a class to contain that logic
 */
public class HbaseSaltPrefixer {
    private int NUM_REGION_BUCKETS = 3; // the number of hbase region servers per cluster

    private static class SingletonHolder {
        private static final HbaseSaltPrefixer INSTANCE = new HbaseSaltPrefixer();
    }

    /**
     * Instantiates a new hbase salt prefixer.
     */
    private HbaseSaltPrefixer() {
    }

    /**
     * Gets the single instance of HbaseSaltPrefixer.
     *
     * @return single instance of HbaseSaltPrefixer
     */
    public static HbaseSaltPrefixer getInstance() {
        return SingletonHolder.INSTANCE;
    }

    /**
     * Prepend salt.
     *
     * @param key the key
     * @return the string
     */
    public String prependSalt(String key) {
        int salt = key.hashCode() % NUM_REGION_BUCKETS;
        return salt + "-" + key;
    }
}
