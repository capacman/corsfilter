package com.globalmaksimum.web.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class JsonpForwarderFilter implements Filter {
	private static final Logger LOGGER = LoggerFactory
			.getLogger(JsonpForwarderFilter.class);

	public void init(FilterConfig filterConfig) throws ServletException {
		// TODO Auto-generated method stub

	}

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		String callback = req.getParameter("callback");
		String timeValue = req.getParameter("_");
		System.out.println("callback " + callback + " timeValue " + timeValue);
		if (callback != null) {
			MyWrapper wrappedResponse = new MyWrapper(res);
			chain.doFilter(request, wrappedResponse);
			String locationValue = wrappedResponse.getLocationValue();
			if (locationValue != null) {
				System.out.println("location value found " + locationValue);
				if (locationValue.contains("ticket")) {
					System.out.println("location value contains ticket");
					String newLocation = locationValue+"&callback="+callback+"&_="+timeValue;
					System.out.println("newLocationValue "+newLocation);
					res.sendRedirect(newLocation);
				}
			}
		} else {
			chain.doFilter(request, response);
		}
	}

	public void destroy() {
		// TODO Auto-generated method stub

	}

	static public class MyWrapper extends HttpServletResponseWrapper {

		private String locationValue = null;

		public MyWrapper(HttpServletResponse response) {
			super(response);
		}

		@Override
		public void setHeader(String name, String value) {
			if (name.equalsIgnoreCase("Location"))
				this.locationValue = value;
			super.setHeader(name, value);
		}

		public String getLocationValue() {
			return locationValue;
		}

		@Override
		public void sendRedirect(String location) throws IOException {
			this.locationValue = location;
			//prevent server from actually send redirect
		}

	}

}
