package io.kabanero;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
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

import static org.junit.Assert.*;

@RunWith(PowerMockRunner.class)
@PrepareForTest(KabaneroClient.class)
public class KabaneroToolManagerTest {
    KabaneroInstance kabaneroInstance;
    KabaneroToolManager kManTool;

    @Before
    public void mockKabaneroClient() throws IOException, ApiException, GeneralSecurityException {
        PowerMockito.mockStatic(KabaneroClient.class);
        Mockito.when(KabaneroClient.getInstances()).thenReturn(new ArrayList<KabaneroInstance>());

        kManTool = KabaneroToolManager.getKabaneroToolManagerInstance();
    }

    @Test
    public void addTool() {
        String id = UUID.randomUUID().toString();
        kManTool.addTool(createKabeKabaneroTool(id));

        String id2 = UUID.randomUUID().toString();
        kManTool.addTool(createKabeKabaneroTool(id2));

        assertEquals("2 kabanero tools were added", 2, kManTool.getAllTools().size());
    }

    @Test
    public void getCorrectTool() {
        String id = UUID.randomUUID().toString();
        kManTool.addTool(createKabeKabaneroTool(id));

        String id2 = UUID.randomUUID().toString();
        kManTool.addTool(createKabeKabaneroTool(id2));

        String id3 = UUID.randomUUID().toString();
        kManTool.addTool(createKabeKabaneroTool(id3));

        KabaneroTool kabToolInst = kManTool.getTool("lable" + id2);

        assertNotNull("get instance does not return null", kabToolInst);
        assertEquals("kabToolInst " + id2 + " is retrieved ", "lable" + id2, kabToolInst.getLabel());
        assertEquals("kabToolInst " + id2 + " is retrieved ", "location" + id2, kabToolInst.getLocation());
    }

    @Test
    public void getTools() {
        Collection<KabaneroTool> allTools = kManTool.getAllTools();

        assertNotNull("get all instances does not return null", allTools);
        // kManTool is a singleton so it has all the addInstances from previous tests
        assertEquals("get all instances has correct size ", 5, allTools.size());
    }

    private static KabaneroTool createKabeKabaneroTool(String id) {
        return new KabaneroTool("lable" + id, "location" + id);
    }

}