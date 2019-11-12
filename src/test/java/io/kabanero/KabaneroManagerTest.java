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
public class KabaneroManagerTest {
    KabaneroInstance kabaneroInstance;
    KabaneroManager kMan;

    @Before
    public void mockKabaneroClient() throws IOException, ApiException, GeneralSecurityException, NoSuchFieldException, SecurityException, IllegalArgumentException, IllegalAccessException {
        PowerMockito.mockStatic(KabaneroClient.class);
        Mockito.when(KabaneroClient.getInstances()).thenReturn(new ArrayList<KabaneroInstance>());

        // reset the KabaneroManager singleton before each test so they do not affect each other.
        Field instance = KabaneroManager.class.getDeclaredField("SINGLE_KABANERO_MANAGER_INSTANCE");
        instance.setAccessible(true);
        instance.set(null, null);

        kMan = KabaneroManager.getKabaneroManagerInstance();
    }

    @Test
    public void addInstance() {
        String id = UUID.randomUUID().toString();
        kMan.addInstance(createKabInstance(id));
        assertEquals("only 1 instance is added", 1, kMan.getAllKabaneroInstances().size());
    }

    @Test
    public void getCorrectInstance() {
        String id = UUID.randomUUID().toString();
        kMan.addInstance(createKabInstance(id));

        String id2 = UUID.randomUUID().toString();
        kMan.addInstance(createKabInstance(id2));

        String id3 = UUID.randomUUID().toString();
        kMan.addInstance(createKabInstance(id3));
        
        KabaneroInstance kabInst = kMan.getKabaneroInstance(id2);

        assertNotNull("get instance does not return null", kabInst);
        assertEquals("kabInst " + id2 + " is retrieved ", id2, kabInst.getInstanceName());
    }

    @Test
    public void getAllInstances() {
      
        String id2 = UUID.randomUUID().toString();
        kMan.addInstance(createKabInstance(id2));

        String id3 = UUID.randomUUID().toString();
        kMan.addInstance(createKabInstance(id3));

        Collection<KabaneroInstance> allInsts = kMan.getAllKabaneroInstances();

        assertNotNull("get all instances does not return null", allInsts);
        assertEquals("get all instances has correct size ", 2, allInsts.size());
    }

    private static KabaneroInstance createKabInstance(String id){
        KabaneroRepository kabaneroRepository = new KabaneroRepository("mock user repository name", " https://mock.com/appsody/stacks/releases/latest/download/incubator-index.yaml", true);
        KabaneroCollection kabaneroCollection = new KabaneroCollection("mock nodejs", "2.0");
        return new KabaneroInstance("mock kabanero username", id , "10/03/2019", Arrays.asList(kabaneroRepository), "mock cluster", Arrays.asList(kabaneroCollection), "https://mock-kabanero-cli-kabanero.mock.mock.ibm.com");
    }

}
