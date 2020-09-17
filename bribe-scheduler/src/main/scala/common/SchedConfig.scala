package common

import com.typesafe.config.ConfigFactory

object SchedConfig {

  val conf = ConfigFactory.load()

  val KUBERNETES_MASTER_ADDR =
    conf.getString("kubernetes.master.address")

  val CAPRI_PARAM_FILE =
    conf.getString("capri.parameters.file")
}
