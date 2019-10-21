package io.kabanero;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Collection;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import io.kubernetes.KabaneroClient;
import io.kubernetes.client.ApiException;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KabaneroClient.class)
public class KabaneroToolManagerTest {
    KabaneroInstance kabaneroInstance;
    KabaneroToolManager kToolMan;

    @Before
    public void mockKabaneroClient() throws IOException, ApiException, GeneralSecurityException {
        PowerMockito.mockStatic(KabaneroClient.class);
       // Mockito.when(KabaneroClient.discoverTools(kToolMan).thenReturn(kToolMan);

        kToolMan = KabaneroToolManager.getKabaneroToolManagerInstance();
    }

    @Test
    public void addTool() {
        String id = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id));

        assertEquals("2 kabanero tools were added", 1, kToolMan.getAllTools().size());
    }

    @Test
    public void getCorrectTools() {
        String id = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id));

        String id2 = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id2));

        String id3 = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id3));

        KabaneroTool kabTool = kToolMan.getTool("lable" + id2);

        assertNotNull("get tool does not return null", kabTool);
        assertEquals("kabTool " + id2 + " is retrieved ", "lable" + id2, kabTool.getLabel());
        assertEquals("kabTool " + id2 + " is retrieved ", "location" + id2, kabTool.getLocation());
    }

    @Test
    public void getAllToolsHasCorrectSize() {
        Collection<KabaneroTool> allTools = kToolMan.getAllTools();

        assertNotNull("get all tools does not return null", allTools);
        // kToolMan is a singleton so it has all the addInstances from previous tests
        assertEquals("get all tools has correct size ", 4, allTools.size());
    }

    private static KabaneroTool createKabeKabaneroTool(String id) {
        return new KabaneroTool("lable" + id, "location" + id);
    }

}