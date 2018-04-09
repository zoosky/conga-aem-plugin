/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2017 wcm.io
 * %%
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * #L%
 */
package io.wcm.devops.conga.plugins.aem.handlebars.helper;

import static io.wcm.devops.conga.plugins.aem.handlebars.helper.TestUtils.executeHelper;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.junit.Before;
import org.junit.Test;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.util.PluginManagerImpl;

public class URLHelperTest {

  private HelperPlugin<Object> helper;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    helper = new PluginManagerImpl().get(URLHelper.NAME, HelperPlugin.class);
  }

  @Test
  public void testNull() throws Exception {
    Object uri = executeHelper(helper, null, new MockOptions());
    assertNull(uri);
  }

  @Test
  public void testDefaults() throws Exception {
    Object uri = executeHelper(helper, "localhost", new MockOptions());
    assertEquals("http://localhost", uri);
  }

  @Test
  public void testFtpScheme() throws Exception {
    MockOptions mockOptions = new MockOptions().withHash(URLHelper.HASH_OPTION_SCHEME, "ftp");
    Object uri = executeHelper(helper, "localhost", mockOptions);
    assertEquals("ftp://localhost", uri);
  }

  @Test
  public void testDefaultSchemeWithCustomPort() throws Exception {
    MockOptions mockOptions = new MockOptions().withHash(URLHelper.HASH_OPTION_PORT, 1234);
    Object uri = executeHelper(helper, "localhost", mockOptions);
    assertEquals("http://localhost:1234", uri);
  }

  @Test
  public void testHttpStandardPort() throws Exception {
    Object uri = executeHelper(helper, "localhost", new MockOptions());
    assertEquals("http://localhost", uri);
  }

  @Test
  public void testHttpCustomPort() throws Exception {
    MockOptions mockOptions = new MockOptions().withHash(URLHelper.HASH_OPTION_PORT, 5678);
    Object uri = executeHelper(helper, "localhost", mockOptions);
    assertEquals("http://localhost:5678", uri);
  }

  @Test
  public void testHttpsDefaultPort() throws Exception {
    MockOptions mockOptions = new MockOptions().withHash(URLHelper.HASH_OPTION_SCHEME, "https");
    Object uri = executeHelper(helper, "localhost", mockOptions);
    assertEquals("https://localhost", uri);
  }

  @Test
  public void testHttpsWithExplicitDefaultPort() throws Exception {
    MockOptions mockOptions = new MockOptions().withHash(URLHelper.HASH_OPTION_SCHEME, "https").withHash(URLHelper.HASH_OPTION_PORT, 443);
    Object uri = executeHelper(helper, "localhost", mockOptions);
    assertEquals("https://localhost", uri);
  }

  @Test
  public void testHttpsCustomPort() throws Exception {
    MockOptions mockOptions = new MockOptions().withHash(URLHelper.HASH_OPTION_SCHEME, "https").withHash(URLHelper.HASH_OPTION_PORT, 2345);
    Object uri = executeHelper(helper, "localhost", mockOptions);
    assertEquals("https://localhost:2345", uri);
  }

  @Test
  public void testWithAllOptionsSet() throws Exception {
    MockOptions mockOptions = new MockOptions()
        .withHash(URLHelper.HASH_OPTION_PORT, 4567)
        .withHash(URLHelper.HASH_OPTION_SCHEME, "customscheme")
        .withHash(URLHelper.HASH_OPTION_PATH, "/my/custom/path.extension")
        .withHash(URLHelper.HASH_OPTION_QUERY, "firstParam=value1&secondParam=value2")
        .withHash(URLHelper.HASH_OPTION_FRAGMENT, "customFragment");
    Object uri = executeHelper(helper, "customhost", mockOptions);
    assertEquals("customscheme://customhost:4567/my/custom/path.extension?firstParam=value1&secondParam=value2#customFragment", uri);
  }

  @Test
  public void testManipulatePortOfExistingUrl() throws Exception {
    MockOptions mockOptions = new MockOptions()
        .withHash(URLHelper.HASH_OPTION_PORT, 5678);
    Object uri = executeHelper(helper, "http://localhost/path?query=value#fragment", mockOptions);
    assertEquals("http://localhost:5678/path?query=value#fragment", uri);
  }

  @Test
  public void testUrlWithNoOptions() throws Exception {
    MockOptions mockOptions = new MockOptions();
    Object uri = executeHelper(helper, "http://localhost/path?query=value#fragment", mockOptions);
    assertEquals("http://localhost/path?query=value#fragment", uri);
  }

  @Test
  public void testUrlWithAllOptions() throws Exception {
    MockOptions mockOptions = new MockOptions()
        .withHash(URLHelper.HASH_OPTION_PORT, 5678)
        .withHash(URLHelper.HASH_OPTION_SCHEME, "customscheme")
        .withHash(URLHelper.HASH_OPTION_PATH, "/my/custom/path.extension")
        .withHash(URLHelper.HASH_OPTION_QUERY, "firstParam=value1&secondParam=value2")
        .withHash(URLHelper.HASH_OPTION_FRAGMENT, "customFragment");
    Object uri = executeHelper(helper, "http://localhost:1234/path?query=value#fragment", mockOptions);
    assertEquals("customscheme://localhost:5678/my/custom/path.extension?firstParam=value1&secondParam=value2#customFragment", uri);
  }

  @Test
  public void testUnset() throws Exception {
    MockOptions mockOptions = new MockOptions()
      .withHash(URLHelper.HASH_OPTION_PORT, null)
      .withHash(URLHelper.HASH_OPTION_SCHEME, null)
      .withHash(URLHelper.HASH_OPTION_PATH, null)
      .withHash(URLHelper.HASH_OPTION_QUERY, null)
      .withHash(URLHelper.HASH_OPTION_FRAGMENT, null);
    Object uri = executeHelper(helper, "https://localhost/custom/path?param1=value1&param2=value2#deeplink", mockOptions);
    assertEquals("localhost", uri);
  }
}
