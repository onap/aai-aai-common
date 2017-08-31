package org.onap.aaiutils.oxm;

import org.junit.Test;

import static org.junit.Assert.*;

public class OxmModelLoaderTest {
    @Test
    public void loadModels() throws Exception {
        OxmModelLoader.loadModels();
        assertTrue( OxmModelLoader.getVersionContextMap().size() > 0);
    }
}