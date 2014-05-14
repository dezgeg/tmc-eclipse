package fi.helsinki.cs.plugin.tmc.model;

import static org.junit.Assert.*;

import static org.mockito.Mockito.*;

import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

import org.junit.Before;
import org.junit.Test;

public class SettingsTest {

	Preferences prefs;
	Settings settings;
	
	@Before
	public void setUp() {
		prefs = mock(Preferences.class);
		settings = new Settings(prefs);
	}
	
	@Test
	public void testSavingServerBaseUrl() {
		settings.setServerBaseUrl("http://tmc.mooc.fi");
		
		when(settings.getServerBaseUrl()).thenReturn("http://tmc.mooc.fi");
		
		assertEquals(settings.getServerBaseUrl(), "http://tmc.mooc.fi");
		
		verify(prefs).put(eq(Settings.PREF_KEY_TMC_SERVER_URL), eq("http://tmc.mooc.fi"));
	}

	@Test
	public void testSavingUsername() {
		settings.setUsername("matti.meikalainen");
		when(settings.getUsername()).thenReturn("matti.meikalainen");
		
		assertEquals(settings.getUsername(), "matti.meikalainen");
		
		verify(prefs).put(eq(Settings.PREF_KEY_USERNAME), eq("matti.meikalainen"));
	}
	
	@Test
	public void testSavingPassword() {
		settings.setPassword("password123");
		when(settings.getPassword()).thenReturn("password123");
		
		assertEquals(settings.getPassword(), "password123");
		
		verify(prefs).put(eq(Settings.PREF_KEY_PASSWORD), eq("password123"));
	}
	
	
	@Test
	public void testSavingFailure() throws BackingStoreException{
		settings.setUsername("matti.meikalainen");
		settings.setPassword("password123");
		
		doThrow(new BackingStoreException("")).when(prefs).flush();
		assertFalse(settings.save());
	}
	
	@Test
	public void testSavingSuccess() {
		settings.setUsername("matti.meikalainen");
		settings.setPassword("password123");
		
		assertTrue(settings.save());
	}
	
	@Test
	public void testDefaultSettingsSuccess(){
		assertNotNull(Settings.getDefaultSettings());
	}
}
