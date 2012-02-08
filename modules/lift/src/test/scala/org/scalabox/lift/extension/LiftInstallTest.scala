package org.scalabox.lift.extension

import org.junit.runner.RunWith
import org.jboss.arquillian.junit.Arquillian
import org.junit.Test
import org.jboss.as.controller.client.ModelControllerClient
import org.jboss.as.controller.descriptions.ModelDescriptionConstants._
import org.jboss.dmr.ModelNode
import java.net.InetAddress
import org.scalabox.util.Closeable._
import org.scalabox.test.AbstractLiftTest

/**
 * // TODO: Document this
 * @author Galder Zamarreño
 * @since // TODO
 */
@RunWith(classOf[Arquillian])
class LiftInstallTest extends AbstractLiftTest {

   import AbstractLiftTest._

   @Test def testCheckInstall() {
      use(ModelControllerClient.Factory.create(
               InetAddress.getByName("localhost"), 9999)) { client =>
         val op = new ModelNode()
         op.get(OP).set(READ_RESOURCE_DESCRIPTION_OPERATION)
         op.get(OP_ADDR).add("extension", "org.scalabox.lift")
         val resp = validateResponse(client.execute(op))
         assert(!resp.asString().isEmpty)
      }
   }

}