package org.onap.aai;

import java.io.FileNotFoundException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.janusgraph.core.JanusGraphProperty;
import org.janusgraph.core.schema.JanusGraphConfiguration;
import org.onap.aai.dbmap.AAIGraphConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JanusgraphCassandraConfiguration {

  @Value("${testcontainers.cassandra.port}")
  int cassandraPort;

  @Bean
  public org.apache.commons.configuration2.Configuration getGraphProperties()
      throws FileNotFoundException, ConfigurationException {

    Configuration smth = new PropertiesConfiguration();
    smth.addProperty("storage.backend", "cql");
    smth.addProperty("storage.hostname", "localhost");
    smth.addProperty("storage.port", cassandraPort);
    return smth;
  }
}
