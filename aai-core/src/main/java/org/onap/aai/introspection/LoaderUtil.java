package org.onap.aai.introspection;

import org.onap.aai.config.SpringContextAware;
import org.onap.aai.setup.SchemaVersions;

public class LoaderUtil {

    public static Loader getLatestVersion(){

        LoaderFactory loaderFactory   = SpringContextAware.getBean(LoaderFactory.class);
        SchemaVersions schemaVersions = (SchemaVersions) SpringContextAware.getBean("schemaVersions");

        return loaderFactory.createLoaderForVersion(ModelType.MOXY, schemaVersions.getDefaultVersion());
    }
}
