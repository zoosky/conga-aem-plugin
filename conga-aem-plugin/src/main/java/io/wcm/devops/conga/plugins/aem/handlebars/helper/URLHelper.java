/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2018 wcm.io
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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.client.utils.URIBuilder;

import com.github.jknack.handlebars.Options;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.HelperContext;

/**
 * Handlebars helper that builds and manipulates URLs
 */
public class URLHelper implements HelperPlugin<Object> {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "url";

  /**
   * Option for setting scheme
   */
  public static final String HASH_OPTION_SCHEME = "scheme";

  /**
   * Option for setting port
   */
  public static final String HASH_OPTION_PORT = "port";

  /**
   * Option for setting path
   */
  public static final String HASH_OPTION_PATH = "path";

  /**
   * Option for setting fragment
   */
  public static final String HASH_OPTION_FRAGMENT = "fragment";

  /**
   * Option for setting query
   */
  public static final String HASH_OPTION_QUERY = "query";

  /**
   * Default non ssl port
   */
  public static final Integer PORT_HTTP = 80;

  /**
   * Default ssl port
   */
  public static final Integer PORT_HTTPS = 443;

  /**
   * Http scheme
   */
  public static final String SCHEME_HTTP = "http";

  /**
   * Https scheme
   */
  public static final String SCHEME_HTTPS = "https";

  /**
   * Default scheme
   */
  public static final String DEFAULT_SCHEME = SCHEME_HTTP;

  @Override
  public Object apply(Object o, Options options, HelperContext helperContext) throws IOException {
    if (o == null) {
      return null;
    }
    // set default values
    String authority = o.toString();
    String scheme = DEFAULT_SCHEME;
    Integer port = null;
    String path = null;
    String fragment = null;
    String query = null;

    // try to extract from existing url
    try {
      URL url = new URL(authority);
      authority = url.getHost();
      scheme = url.getProtocol();
      port = url.getPort();
      path = url.getPath();
      query = url.getQuery();
      fragment = url.getRef();
    }
    catch (MalformedURLException ex) {
      // just catch, do nothing
    }

    // overwrite with values from options
    if (options.hash.containsKey(HASH_OPTION_SCHEME)) {
      scheme = options.hash(HASH_OPTION_SCHEME);
    }
    if (options.hash.containsKey(HASH_OPTION_PATH)) {
      port = options.hash(HASH_OPTION_PORT);
    }
    if (options.hash.containsKey(HASH_OPTION_PORT)) {
      port = options.hash(HASH_OPTION_PORT);
    }
    if (options.hash.containsKey(HASH_OPTION_PATH)) {
      path = options.hash(HASH_OPTION_PATH);
    }
    if (options.hash.containsKey(HASH_OPTION_FRAGMENT)) {
      fragment = options.hash(HASH_OPTION_FRAGMENT);
    }
    if (options.hash.containsKey(HASH_OPTION_QUERY)) {
      query = options.hash(HASH_OPTION_QUERY);
    }

    // build url
    URIBuilder uriBuilder = new URIBuilder()
        .setHost(authority)
        .setScheme(scheme)
        .setPath(path)
        .setFragment(fragment)
        .setCustomQuery(query);

    uriBuilder = addConditionalPort(uriBuilder, scheme, port);
    String result = uriBuilder.toString();
    // when scheme is null we have to remove //
    if (scheme == null) {
      result = result.replaceFirst("^//","");
    }

    return result;
  }

  @Override
  public String getName() {
    return NAME;
  }

  /**
   * Adds port to URL when the port is not the default scheme port.
   * @param uriBuilder Instance of the {@link URIBuilder}
   * @param scheme The scheme of the URI
   * @param port The port of the URI
   * @return {@link URIBuilder}
   */
  private URIBuilder addConditionalPort(URIBuilder uriBuilder, String scheme, Integer port) {
    if (port == null) {
      // early return when port is not defined
      return uriBuilder;
    }
    else if (scheme.equals(SCHEME_HTTP) && port.equals(PORT_HTTP)) {
      // default http port early return without change
      return uriBuilder;
    }
    else if (scheme.equals(SCHEME_HTTPS) && port.equals(PORT_HTTPS)) {
      // default https port early return without change
      return uriBuilder;
    }

    // custom port in use, add to uriBuilder
    return uriBuilder.setPort(port);
  }
}
