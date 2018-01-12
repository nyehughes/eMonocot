/*
 * This is eMonocot, a global online biodiversity information resource.
 *
 * Copyright © 2011–2015 The Board of Trustees of the Royal Botanic Gardens, Kew and The University of Oxford
 *
 * eMonocot is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * eMonocot is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even
 * the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * The complete text of the GNU Affero General Public License is in the source repository as the file
 * ‘COPYING’.  It is also available from <http://www.gnu.org/licenses/>.
 */
package org.emonocot.portal.logging;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.emonocot.portal.logging.ClientParameterFilter;
import org.emonocot.portal.logging.LoggingConstants;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.MDC;
import org.springframework.http.HttpStatus;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockFilterConfig;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 *
 * @author ben
 *
 */
public class ClientParameterFilterTest {

	/**
	 *
	 */
	private ClientParameterFilter clientParameterFilter = null;

	/**
	 *
	 */
	private MockHttpServletRequest servletRequest;

	/**
	 *
	 */
	private MockHttpServletResponse servletResponse;

	/**
	 * @throws ServletException if the filter cannot be initialized
	 */
	@Before
	public final void setUp() throws ServletException {
		clientParameterFilter = new ClientParameterFilter();
		MockFilterConfig filterConfig = new MockFilterConfig();
		filterConfig.addInitParameter(
				ClientParameterFilter.CONFIG_PARAMETER_NAME, "scratchpad");
		clientParameterFilter.init(filterConfig);

		servletRequest = new MockHttpServletRequest();
		servletResponse = new MockHttpServletResponse();
	}

	/**
	 * @throws ServletException if there is a problem
	 * @throws IOException if there is a problem
	 *
	 */
	@Test
	public final void testFilterWithoutParameter()
			throws IOException, ServletException {
		FilterChain filterChain = new MockFilterChain() {
			public void doFilter(final ServletRequest request,
					final ServletResponse response,
					final FilterChain filterChain) {
				fail("doFilter should not be called");
			}
		};
		assertNull("The client name should not be set before the filter",
				MDC.get(LoggingConstants.MDC_CLIENT_NAME_KEY));
		clientParameterFilter
		.doFilter(servletRequest, servletResponse, filterChain);
		assertNull("The client name should not be set after the filter",
				MDC.get(LoggingConstants.MDC_CLIENT_NAME_KEY));
		assertEquals("The response should be 400 - BAD REQUEST", HttpStatus.BAD_REQUEST.value(),
				servletResponse.getStatus());
	}

	/**
	 * @throws ServletException if there is a problem
	 * @throws IOException if there is a problem
	 *
	 */
	@Test
	public final void testFilterWithParameter()
			throws IOException, ServletException {
		servletRequest.addParameter("scratchpad", "foobar");
		FilterChain filterChain = new MockFilterChain() {
			public void doFilter(final ServletRequest request,
					final ServletResponse response,
					final FilterChain filterChain) {
				assertNotNull(
						"The client name should be set within the filter",
						MDC.get(LoggingConstants.MDC_CLIENT_NAME_KEY));
				assertEquals(
						"The client name should equal the value of the parameter",
						"foobar",
						MDC.get(LoggingConstants.MDC_CLIENT_NAME_KEY));
			}
		};
		assertNull("The client name should not be set before the filter",
				MDC.get(LoggingConstants.MDC_CLIENT_NAME_KEY));
		clientParameterFilter
		.doFilter(servletRequest, servletResponse, filterChain);
		assertNull("The client name should not be set after the filter",
				MDC.get(LoggingConstants.MDC_CLIENT_NAME_KEY));
		assertEquals("The response should be 200 - OK", HttpStatus.OK.value(),
				servletResponse.getStatus());
	}
}
