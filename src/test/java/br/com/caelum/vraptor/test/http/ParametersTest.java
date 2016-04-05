package br.com.caelum.vraptor.test.http;

import org.junit.Test;

public class ParametersTest {

	@Test
	public void testSetContentOneTime() {
		new Parameters().setContent("2134".getBytes());
	}

	@Test(expected = IllegalStateException.class)
	public void testSetContentTwice() {
		new Parameters().setContent("123".getBytes()).setContent("abc".getBytes());
	}

}
