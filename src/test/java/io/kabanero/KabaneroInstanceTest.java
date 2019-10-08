package io.kabanero;

import io.kabanero.*;
import java.util.Arrays;

import org.junit.Test;
import static org.junit.Assert.assertEquals;

public class KabaneroInstanceTest {
  KabaneroRepository kabaneroRepository = new KabaneroRepository("User repositroy name", " https://github.com/appsody/stacks/releases/latest/download/incubator-index.yaml", true);
  KabaneroCollection kabaneroCollection = new KabaneroCollection("nodejs", "2.0");
  KabaneroInstance kabaneroInstance = new KabaneroInstance("kabUser", "kabanero-instance", "10/03/2019", Arrays.asList(kabaneroRepository), "MyCluster", Arrays.asList(kabaneroCollection), "cliURL");

  @Test
  public void kabaneroInstanceNameTest() {
    assertEquals(kabaneroInstance.instanceName, "kabanero-instance");
  }

  @Test
  public void kabaneroInstanceNameDetail() {
    assertEquals(kabaneroInstance.getDetails(), kabaneroInstance.details);
  }

  @Test
  public void kabaneroInstanceEndpointTest() {
    KabaneroManager.getKabaneroManagerInstance().addInstance(kabaneroInstance);
    InstanceEndpoints instanceEndpoints = new InstanceEndpoints();
    assertEquals(instanceEndpoints.getAInstance("kabanero-instance").getStatus(), 200);
  }

}