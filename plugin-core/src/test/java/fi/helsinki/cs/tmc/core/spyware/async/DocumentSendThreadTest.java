package fi.helsinki.cs.tmc.core.spyware.async;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.Toolkit;
import java.awt.datatransfer.StringSelection;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import fi.helsinki.cs.tmc.core.domain.Exercise;
import fi.helsinki.cs.tmc.core.domain.Project;
import fi.helsinki.cs.tmc.core.spyware.DocumentInfo;
import fi.helsinki.cs.tmc.core.spyware.async.DocumentSendThread;
import fi.helsinki.cs.tmc.core.spyware.services.EventReceiver;
import fi.helsinki.cs.tmc.core.spyware.services.LoggableEvent;
import fi.helsinki.cs.tmc.core.spyware.utility.diff_match_patch;

/*
 * If you are here for find out why the test randomly freezes, it's the clipboard function in paste event test
 * */
public class DocumentSendThreadTest {
    private DocumentSendThread thread;
    private EventReceiver receiver;
    private DocumentInfo info;
    private Project project;
    private Map<String, String> cache;
    private diff_match_patch PATCH_GENERATOR;

    @Before
    public void setUp() throws Exception {
        this.receiver = mock(EventReceiver.class);
        this.info = new DocumentInfo("a", "a", "a", "a", 0, 1);
        this.project = mock(Project.class);
        this.cache = new HashMap<String, String>();
        this.PATCH_GENERATOR = mock(diff_match_patch.class);

        when(project.getExercise()).thenReturn(new Exercise("name1", "course1"));

        this.thread = new DocumentSendThread(receiver, info, project, cache, PATCH_GENERATOR);
    }

    @Test
    public void generatePatchesTest() {
        thread.run();
        assertEquals(cache.size(), 1);
    }

    @Test
    public void insertEventTest() {
        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                LoggableEvent event = (LoggableEvent) invocation.getArguments()[0];
                assertEquals("text_insert", event.getEventType());
                return null;
            }
        }).when(receiver).receiveEvent(any(LoggableEvent.class));

        thread.run();

        verify(receiver, times(1)).receiveEvent(any(LoggableEvent.class));
    }

    @Test
    public void removeEventTest() {
        this.info = new DocumentInfo("", "", "", "", 0, 1);
        this.thread = new DocumentSendThread(receiver, info, project, cache, PATCH_GENERATOR);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                LoggableEvent event = (LoggableEvent) invocation.getArguments()[0];
                assertEquals("text_remove", event.getEventType());
                return null;
            }
        }).when(receiver).receiveEvent(any(LoggableEvent.class));

        thread.run();
        verify(receiver, times(1)).receiveEvent(any(LoggableEvent.class));
    }

    @Test
    public void pasteEventTest() {

        // travis does not have necessary components to run this test
        if ("true".equals(System.getenv("TRAVIS"))) {
            return;
        }

        /* THIS PART OCCASIONALLY FREEZES, NO IDEA WHY */
        StringSelection s = new StringSelection("a a");
        Toolkit.getDefaultToolkit().getSystemClipboard().setContents(s, s);

        this.info = new DocumentInfo("", "", "", "a a", 0, 3);
        this.thread = new DocumentSendThread(receiver, info, project, cache, PATCH_GENERATOR);

        Mockito.doAnswer(new Answer() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                LoggableEvent event = (LoggableEvent) invocation.getArguments()[0];
                assertEquals("text_paste", event.getEventType());
                return null;
            }
        }).when(receiver).receiveEvent(any(LoggableEvent.class));

        thread.run();
        verify(receiver, times(1)).receiveEvent(any(LoggableEvent.class));
    }

}
