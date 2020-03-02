package tools;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.UUID;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;


import io.kabanero.tools.KabaneroTool;
import io.kabanero.tools.KabaneroToolManager;
import io.kubernetes.KabaneroClient;


@RunWith(PowerMockRunner.class)
@PrepareForTest(KabaneroClient.class)
public class KabaneroToolManagerTest {
    KabaneroToolManager kToolMan;

    @Before
    public void mockKabaneroClient() throws Exception {
        PowerMockito.mockStatic(KabaneroClient.class);
        PowerMockito.doNothing().when(KabaneroClient.class);
        KabaneroClient.discoverTools(kToolMan);


        // reset the KabaneroToolManager singleton before each test so they do not affect each other.
        Field instance = KabaneroToolManager.class.getDeclaredField("SINGLE_TOOL_MANAGER_INSTANCE");
        instance.setAccessible(true);
        instance.set(null, null);

        kToolMan = KabaneroToolManager.getKabaneroToolManagerInstance();
    }

    @Test
    public void addTool() {
        String id = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id));

        assertEquals("1 kabanero tool was added", 1, kToolMan.getAllTools().size());
    }

    @Test
    public void getCorrectTools() {
        String id = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id));

        String id2 = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id2));

        String id3 = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id3));

        KabaneroTool kabTool = kToolMan.getTool(id2);
 
        assertNotNull("get tool does not return null", kabTool);
        assertEquals("kabTool " + id2 + " is retrieved ", id2, kabTool.getName());
        assertEquals("kabTool " + id2 + " is retrieved ", id2, kabTool.getLocation());
    }

    @Test
    public void getAllToolsHasCorrectSize() {
        String id = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id));

        String id2 = UUID.randomUUID().toString();
        kToolMan.addTool(createKabeKabaneroTool(id2));

        Collection<KabaneroTool> allTools = kToolMan.getAllTools();

        assertNotNull("get all tools does not return null", allTools);
        assertEquals("get all tools has correct size ", 2, allTools.size());
    }

    private static KabaneroTool createKabeKabaneroTool(String id) {
        return new KabaneroTool(id, id);
    }

}