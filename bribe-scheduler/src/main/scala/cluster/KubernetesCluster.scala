package cluster

import scala.collection.mutable
import scala.collection.mutable.ArrayBuffer

import java.util.concurrent.TimeUnit

import com.google.gson.reflect.TypeToken

import io.kubernetes.client.{ApiClient, Configuration}
import io.kubernetes.client.apis.CoreV1Api
import io.kubernetes.client.models._
import io.kubernetes.client.util.Config
import io.kubernetes.client.util.Watch

import common.EventLoop
import scheduler.{Instance, InstanceId, PodInfo}

class KubernetesCluster(master: Option[String]) extends Cluster {

  // API client used to create pod - node binding
  val apiClient: ApiClient = Config.defaultClient
  apiClient.getHttpClient.setReadTimeout(0, TimeUnit.SECONDS)
  Configuration.setDefaultApiClient(apiClient)
  val api : CoreV1Api = new CoreV1Api(apiClient)

  /**
    * This method allocates a pod on a cluster node by creating a binding
    */
  override def createBinding(name: String, node: String): Boolean = {
    val body = new V1Binding()
    val target = new V1ObjectReference

    target.kind("Node")
    target.apiVersion("v1")
    target.name(node)

    val meta = new V1ObjectMeta
    meta.name(name)
    body.target(target)
    body.metadata(meta)
    try {
      api.createNamespacedPodBinding(name, "default", body, null, null, null)
    } catch {
      case e: Exception =>
        println("Create binding exception " + e + " " + name)
        return false
    }

    true
  }

  /**
    * This method deletes a binding to de-allocate a pod from a cluster node.
    */
  override def deleteBinding(name: String): Boolean = {
    import sys.process._
    
    try {
      s"/usr/bin/kubectl delete pod $name --grace-period=0 --force".! 
    } catch {
      case e: Exception => println("Delete pod exception " + e + " " + name)
    }

    true
  }

  /**
    * This method returns the list of active cluster nodes.
    */
  override val nodes: List[Instance] = {
    val instances = new ArrayBuffer[Instance]()

    api.listNode(null, null, null, null, null, null, null, null, null)
      .getItems.toArray.foreach { node =>
        val status = node.asInstanceOf[V1Node].getStatus
        val hostname = node.asInstanceOf[V1Node].getMetadata.getName
        val cpu = status.getAllocatable.get("cpu").getNumber.intValue()
        val mem = status.getAllocatable.get("memory").getNumber.doubleValue() / (1024 * 1024 * 1024)
        val name = status.getAddresses.get(0).getAddress
        val id = new InstanceId(hostname)
        if (master.isDefined && hostname == master.get) {
          println("Tainted node " + hostname + " <cpu, mem> = <" + cpu + ", " + mem + ">")
	} else {
          println("Active node " + hostname + " <cpu, mem> = <" + cpu + ", " + mem + ">")
          instances.append(new Instance(id, cpu-2, mem))
        }
      }
    instances.toList
  }

  /**
    * This method intercepts any pod event in the cluster and adds it to the
    * internal event queue.
    */
  override def intercept(eventLoop: EventLoop[PodInfo]): Unit = {
    eventLoop.start()

    val watch: Watch[V1Pod] =
      Watch.createWatch(
        apiClient,
        api.listNamespacedPodCall("default", null, null, null,
          null, null, null, null, 600000, true, null, null),
        new TypeToken[Watch.Response[V1Pod]]() {}.getType())

    import scala.collection.JavaConversions._
    try {
      watch.iterator.foreach { item =>
        val pod = item.`object`
        eventLoop.post(PodInfo(pod))
      }
    } finally {
      watch.close()
      println("Exit Kubernetes pods watcher")
    }
  }

  override def listPods: mutable.HashMap[String, String] = mutable.HashMap.empty[String, String]
}
