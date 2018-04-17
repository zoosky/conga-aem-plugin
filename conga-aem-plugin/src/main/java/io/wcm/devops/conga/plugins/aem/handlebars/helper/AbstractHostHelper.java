package io.wcm.devops.conga.plugins.aem.handlebars.helper;

import com.github.jknack.handlebars.Options;

/**
 * Abstract base class for host helpers
 */
abstract class AbstractHostHelper {

  /**
   * Option for setting port
   */
  public static final String HASH_OPTION_PORT = "port";

  /**
   * Helper function to detect if the host already contains a port
   *
   * @param host The host
   * @return true when the host already contains a port
   */
  private Boolean hasPort(String host) {
    return host.matches("^.+:\\d+$");
  }

  /**
   * Adds a port to the incoming host when it is not the default one
   *
   * @param context The context
   * @param options Options
   * @param defaultPort The default port that should not be added
   * @return The hostname with port (when necessary)
   */
  Object addNonDefaultPort(Object context, Options options, Integer defaultPort) {
    StringBuilder sb = new StringBuilder();
    if (context == null) {
      return null;
    }
    String host = context.toString();
    // host has already a defined port
    if (this.hasPort(host)) {
      return context;
    }
    // retrieve the port, or take default one
    Integer port = options.hash(HASH_OPTION_PORT, defaultPort);

    // build result
    sb.append(host);
    if (! port.equals(defaultPort)) {
      // add port when it is not the default one
      sb.append(":").append(port);
    }
    return sb.toString();
  }

}
