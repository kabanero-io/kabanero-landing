package io.kabanero;

import java.io.IOException;
import java.lang.reflect.Field;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.kabanero.instance.KabaneroCollection;
import io.kabanero.instance.KabaneroInstance;
import io.kabanero.instance.KabaneroManager;
import io.kabanero.instance.KabaneroRepository;
import io.kubernetes.KabaneroClient;
import io.kubernetes.client.ApiException;

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KabaneroClient.class)

public class KabaneroRepositoryTest{
    KabaneroInstance kabaneroInstance;
    KabaneroManager kabaneroManager;

    @Before
    public void mockKabaneroClient() throws IOException, ApiException, GeneralSecurityException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        PowerMockito.mockStatic(KabaneroClient.class);
        Mockito.when(KabaneroClient.getInstances()).thenReturn(new ArrayList<KabaneroInstance>());

        // reset the KabaneroManager singleton before each test so they do not affect each other.
        Field instance = KabaneroManager.class.getDeclaredField("SINGLE_KABANERO_MANAGER_INSTANCE");
        instance.setAccessible(true);
        instance.set(null, null);

        kabaneroManager = KabaneroManager.getKabaneroManagerInstance();
    }

    @Test
    public void codewindURLGeneratedCorrectly() {
        KabaneroRepository kabaneroRepository = new KabaneroRepository("mock user repository name", "https://mock.com/appsody/stacks/releases/latest/download/incubator-index.yaml", true);
        KabaneroCollection kabaneroCollection = new KabaneroCollection("mock nodejs", "2.0");
        String instanceId = UUID.randomUUID().toString();
        kabaneroInstance = new KabaneroInstance("mock kabanero username", instanceId , "10/03/2019", Arrays.asList(kabaneroRepository), "mock cluster", Arrays.asList(kabaneroCollection), "https://mock-kabanero-cli-kabanero.mock.mock.ibm.com");
        assertEquals("the appsody url ending in .yaml has been correctly parsed to .json for codewind", "https://mock.com/appsody/stacks/releases/latest/download/incubator-index.json", kabaneroInstance.getDetails().getRepos().get(0).getCodewindURL());
    }
}