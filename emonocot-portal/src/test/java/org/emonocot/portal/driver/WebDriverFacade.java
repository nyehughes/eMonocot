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
package org.emonocot.portal.driver;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PreDestroy;

import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.firefox.FirefoxBinary;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.firefox.internal.ProfilesIni;
import org.openqa.selenium.ie.InternetExplorerDriverService;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.springframework.beans.factory.FactoryBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

/**
 *
 * @author ben
 *
 */
@Component
public class WebDriverFacade implements FactoryBean<WebDriver> {

	/**
	 *
	 * @return the webdriver
	 * @throws IOException if there is a problem loading the
	 *                     properties file
	 */
	private WebDriver createWebDriver() throws IOException {
		Resource propertiesFile = new ClassPathResource(
				"META-INF/spring/application.properties");
		Properties properties = new Properties();
		properties.load(propertiesFile.getInputStream());
		String webdriverMode = properties.getProperty("selenium.webdriver.mode", "local");
		String driverName = properties.getProperty("selenium.webdriver.impl", "org.openqa.selenium.firefox.FirefoxDriver");
		WebDriverBrowserType browser = WebDriverBrowserType.fromString(driverName);
		String display = properties.getProperty("selenium.display.port", ":0");
		if (webdriverMode.equals("local")) {
			switch (browser) {
			case CHROME:
				String chromeLocation = properties
				.getProperty("selenium.webdriver.chromedriver.location");
				Map<String,String> environment = new HashMap<String,String>();
				environment.put("DISPLAY", display);
				ChromeDriverService chromeService = new ChromeDriverService.Builder()
				.usingDriverExecutable(new File(chromeLocation))
				.usingAnyFreePort().withEnvironment(environment).build();
				chromeService.start();
				return new RemoteWebDriver(chromeService.getUrl(),
						DesiredCapabilities.chrome());
			case SAFARI:
				return new SafariDriver();
			case INTERNET_EXPLORER:
				String 	internetExplorerLocation = properties
				.getProperty("selenium.webdriver.ie.location");
				InternetExplorerDriverService ieService = InternetExplorerDriverService.createDefaultService();
				ieService.start();
				return new RemoteWebDriver(ieService.getUrl(),
						DesiredCapabilities.internetExplorer());
			case FIREFOX:
			default:
				FirefoxBinary firefoxBinary = new FirefoxBinary();
				firefoxBinary.setEnvironmentProperty("DISPLAY", display);
				ProfilesIni allProfiles = new ProfilesIni();
				FirefoxProfile profile = allProfiles.getProfile("default");
				return new FirefoxDriver(firefoxBinary, profile);
			}
		} else {

			DesiredCapabilities capabilities = new DesiredCapabilities();
			switch (browser) {
			case CHROME:
				capabilities = DesiredCapabilities.chrome();
				break;
			case INTERNET_EXPLORER:
				capabilities = DesiredCapabilities.internetExplorer();
				break;
			case SAFARI:
				capabilities = DesiredCapabilities.safari();
				break;
			case FIREFOX:
			default:
				capabilities = DesiredCapabilities.firefox();
			}
			String platformName = properties.getProperty("selenium.webdriver.platformName", "LINUX");
			WebDriverPlatformType platform = WebDriverPlatformType.valueOf(platformName);
			switch (platform) {
			case MAC:
				capabilities.setPlatform(Platform.MAC);
				break;
			case WINDOWS:
				capabilities.setPlatform(Platform.WINDOWS);
				break;
			case LINUX:
			default:
				capabilities.setPlatform(Platform.LINUX);
			}
			return new RemoteWebDriver(new URL("http://build.e-monocot.org:4444/wd/hub"), capabilities);
		}
	}

	/**
	 *
	 */
	private static WebDriver browser;

	/**
	 *
	 */
	@PreDestroy
	public final void destroy() {
		if (browser != null) {
			browser.close();
			browser.quit();
			browser = null;
		}
	}

	/**
	 * @return the object
	 * @throws Exception an
	 *             exception if there is a problem creating the object
	 */
	public final WebDriver getObject() throws Exception {
		if (browser == null) {
			browser = createWebDriver();
		}
		return browser;
	}

	/**
	 *
	 * @return the web driver
	 */
	public static WebDriver getWebDriver() {
		return browser;
	}

	/**
	 * @return the type of object
	 */
	public final Class<?> getObjectType() {
		return WebDriver.class;
	}

	/**
	 * @return true if the object is a singleton
	 */
	public final boolean isSingleton() {
		return false;
	}
}
