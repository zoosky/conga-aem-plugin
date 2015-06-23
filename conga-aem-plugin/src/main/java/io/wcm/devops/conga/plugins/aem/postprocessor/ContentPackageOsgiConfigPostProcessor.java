/*
 * #%L
 * wcm.io
 * %%
 * Copyright (C) 2015 wcm.io
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
package io.wcm.devops.conga.plugins.aem.postprocessor;

import io.wcm.devops.conga.generator.GeneratorException;
import io.wcm.devops.conga.generator.spi.PostProcessorPlugin;
import io.wcm.devops.conga.generator.spi.context.FileContext;
import io.wcm.devops.conga.generator.spi.context.PostProcessorContext;
import io.wcm.devops.conga.plugins.sling.util.ConfigConsumer;
import io.wcm.devops.conga.plugins.sling.util.ProvisioningUtil;
import io.wcm.tooling.commons.contentpackagebuilder.ContentPackage;
import io.wcm.tooling.commons.contentpackagebuilder.ContentPackageBuilder;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Dictionary;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.CharEncoding;
import org.apache.felix.cm.file.ConfigurationHandler;
import org.apache.sling.provisioning.model.Model;
import org.slf4j.Logger;

import com.google.common.collect.ImmutableList;

/**
 * Transforms a Sling Provisioning file into OSGi configurations (ignoring all other provisioning contents)
 * and then packages them up in an AEM content package to be deployed via CRX package manager.
 */
public class ContentPackageOsgiConfigPostProcessor implements PostProcessorPlugin {

  /**
   * Plugin name
   */
  public static final String NAME = "aem-contentpackage-osgiconfig";

  /**
   * Root path for content package
   */
  public static final String PROPERTY_PACKAGE_ROOT_PATH = "contentPackageRootPath";

  /**
   * Group name for content package
   */
  public static final String PROPERTY_PACKAGE_GROUP = "contentPackageGroup";

  /**
   * Package name for content package
   */
  public static final String PROPERTY_PACKAGE_NAME = "contentPackageName";

  @Override
  public String getName() {
    return NAME;
  }

  @Override
  public boolean accepts(FileContext file, PostProcessorContext context) {
    return ProvisioningUtil.isProvisioningFile(file);
  }

  @Override
  public List<FileContext> apply(FileContext fileContext, PostProcessorContext context) {
    File file = fileContext.getFile();
    Logger logger = context.getLogger();
    Map<String, Object> options = context.getOptions();

    try {
      // generate OSGi configurations
      Model model = ProvisioningUtil.getModel(fileContext);

      // create AEM content package with configurations
      File zipFile = new File(file.getParentFile(), FilenameUtils.getBaseName(file.getName()) + ".zip");
      logger.info("Generate " + zipFile.getCanonicalPath());

      ContentPackageBuilder builder = new ContentPackageBuilder()
      .rootPath(getProp(options, PROPERTY_PACKAGE_ROOT_PATH))
      .group(getProp(options, PROPERTY_PACKAGE_GROUP))
      .name(getProp(options, PROPERTY_PACKAGE_NAME));

      try (ContentPackage contentPackage = builder.build(zipFile)) {
        generateOsgiConfigurations(model, contentPackage, logger);
      }

      // delete provisioning file after transformation
      file.delete();

      return ImmutableList.of(new FileContext().file(zipFile));
    }
    catch (IOException ex) {
      throw new GeneratorException("Unable to post-process sling provisioning OSGi configurations.", ex);
    }
  }

  private String getProp(Map<String, Object> options, String key) {
    Object value = options.get(key);
    if (value instanceof String) {
      return (String)value;
    }
    throw new GeneratorException("Missing post processor option '" + key + "' for post processor '" + NAME + "'.");
  }

  /**
   * Generate OSGi configuration for all feature and run modes.
   * @param model Provisioning Model
   * @param contentPackage Content package
   * @param logger Logger
   * @throws IOException
   */
  private void generateOsgiConfigurations(Model model, ContentPackage contentPackage, Logger logger) throws IOException {
    ProvisioningUtil.visitOsgiConfigurations(model, new ConfigConsumer<Void>() {
      @Override
      public Void accept(String path, Dictionary<String, Object> properties) throws IOException {
        String contentPath = contentPackage.getRootPath() + "/" + path;
        logger.info("  Include " + contentPath);

        // write configuration to byte array
        byte[] configData;
        try (ByteArrayOutputStream os = new ByteArrayOutputStream()) {
          ConfigurationHandler.write(os, properties);
          configData = os.toByteArray();
        }

        // write configuration to content package
        try (ByteArrayInputStream is = new ByteArrayInputStream(configData)) {
          contentPackage.addFile(contentPath, is, "text/plain;charset=" + CharEncoding.UTF_8);
        }
        return null;
      }
    });
  }

}
