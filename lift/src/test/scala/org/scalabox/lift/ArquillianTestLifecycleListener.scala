package org.scalabox.lift

import org.jboss.arquillian.core.api.annotation.Observes
import org.jboss.arquillian.test.spi.TestClass
import org.jboss.arquillian.container.spi.event.container.{BeforeStart, AfterUnDeploy, BeforeDeploy}
import org.jboss.arquillian.core.spi.LoadableExtension
import org.jboss.arquillian.core.spi.LoadableExtension.ExtensionBuilder

/**
 * // TODO: Document this
 * @author Galder Zamarreño
 * @since // TODO
 */
class ArquillianTestLifecycleListener {

   def executeBeforeStart(@Observes event: BeforeStart) =
      TestLiftExtension.buildExtension

   def executeBeforeDeploy(@Observes event: BeforeDeploy, testClass: TestClass) =
      TestLiftExtension.installExtension

   def executeAfterUnDeploy(@Observes event: AfterUnDeploy, testClass: TestClass) =
      TestLiftExtension.uninstallExtension

}

class ArquillianTestLifecycleRegister extends LoadableExtension {

   def register(builder: ExtensionBuilder) {
      builder.observer(classOf[ArquillianTestLifecycleListener]);
   }

}