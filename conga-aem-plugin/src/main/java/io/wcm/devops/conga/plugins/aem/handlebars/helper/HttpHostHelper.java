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

import com.github.jknack.handlebars.Options;

import io.wcm.devops.conga.generator.spi.handlebars.HelperPlugin;
import io.wcm.devops.conga.generator.spi.handlebars.context.HelperContext;

/**
 * Handlebars helper that adds a port to the host when it is not the default one
 */
public class HttpHostHelper extends AbstractHostHelper implements HelperPlugin<Object> {

  /**
   * Plugin/Helper name
   */
  public static final String NAME = "httpHost";

  /**
   * Plugin/Helper name
   */
  public static final Integer DEFAULT_PORT = 80;


  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public Object apply(Object context, Options options, HelperContext pluginContext) throws IOException {
    return this.addNonDefaultPort(context, options, DEFAULT_PORT);
  }

}
