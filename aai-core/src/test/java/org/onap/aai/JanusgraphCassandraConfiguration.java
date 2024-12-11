package org.onap.aai;

import java.io.FileNotFoundException;

import org.apache.commons.configuration2.Configuration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.ex.ConfigurationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class JanusgraphCassandraConfiguration {

  @Value("${testcontainers.cassandra.host}")
  String cassandraHost;

  @Value("${testcontainers.cassandra.port}")
  int cassandraPort;

  @Bean
  public org.apache.commons.configuration2.Configuration getGraphProperties()
      throws FileNotFoundException, ConfigurationException {

    Configuration janusgraphConfiguration = new PropertiesConfiguration();
    janusgraphConfiguration.addProperty("storage.backend", "cql");
    janusgraphConfiguration.addProperty("storage.hostname", cassandraHost);
    janusgraphConfiguration.addProperty("storage.port", cassandraPort);
    return janusgraphConfiguration;
  }
}
