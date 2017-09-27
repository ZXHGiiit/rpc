package org.rpc.sample.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by xinghang on 17/9/27.
 */
public class Slf4jDemo {
  private static final Logger LOG = LoggerFactory.getLogger(Slf4jDemo.class);

  public static void main(String[] args) {
    LOG.error("Error Message!");
    LOG.warn("Warn Message!");
    LOG.info("Info Message!");
    LOG.debug("Debug Message!");
    LOG.trace("Trace Message!");
  }
}
