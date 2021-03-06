package br.com.caelum.vraptor.test;

import static com.google.common.base.Objects.firstNonNull;
import static java.util.Arrays.asList;
import static junit.framework.Assert.fail;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.enterprise.inject.Vetoed;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpSession;

import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.google.common.base.Throwables;

import br.com.caelum.vraptor.Result;
import br.com.caelum.vraptor.validator.Validator;

@Vetoed
public class VRaptorTestResult {

	//private Result result;
	private Map<String, Object> values;
	private MockHttpServletResponse response;
	private MockHttpServletRequest request;
	private Throwable applicationError;
	private Boolean isValid;

	public VRaptorTestResult(Result result, MockHttpServletResponse response, 
			MockHttpServletRequest request, Validator validator) {
		super();
		//this.result = result;
		this.response = response;
		this.request = request;
		this.isValid = !validator.hasErrors();
		this.values = result.included();
	}
	
	/**
	 * 
	 * @return redirected url or jsp path
	 */
	public String getLastPath() {
		if(response.getRedirectedUrl()!=null){
			return response.getRedirectedUrl();
		}
		return response.getForwardedUrl();
	}
	
	public boolean isKeyIncluded(String key){
		return values.containsKey(key);
	}
	
	public <T> T getObject(String key){
		@SuppressWarnings("unchecked")
		T object = (T) values.get(key);
		return object;
	}

	public HttpSession getCurrentSession() {
		return request.getSession();
	}
	
	public MockHttpServletRequest getRequest() {
		return request;
	}
	
	public MockHttpServletResponse getResponse() {
		return response;
	}
	
	public String getResponseBody() {
		try {
			return response.getContentAsString();
		} catch (UnsupportedEncodingException e) {
			throw new RuntimeException(e);
		}
	}
	
	public VRaptorTestResult wasStatus(int expectedStatus) {
		int status = response.getStatus();
		if (status != expectedStatus) {
			String message = "Response status was " + status + " and not " + expectedStatus;
			if (applicationError != null) {
				String stacktrace = Throwables.getStackTraceAsString(applicationError);
				message = "Your application threw an exception: " + stacktrace;
			}
			fail(message);
		}
		return this;
	}
	
	public void setApplicationError(Throwable applicationError) {
		this.applicationError = applicationError;
	}

	public VRaptorTestResult isValid() {
		if (!isValid) {
			fail("Found validation errors");
		}
		return this;
	}
	
	public VRaptorTestResult isInvalid() {
		if (isValid) {
			fail("Validation errors not found");
		}
		return this;
	}

	public List<Cookie> getCookies() {
		return new ArrayList<>(asList(firstNonNull(getResponse().getCookies(), new Cookie[0])));
	}
	
}
