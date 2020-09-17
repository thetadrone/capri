package scheduler

/**
  * A wrapper for the string representing the Kubernetes pod ID.
  */
class InstanceId(id: String) {

  def getId: String = this.id

  def canEqual(other: Any): Boolean =
    other.isInstanceOf[InstanceId]

  override def equals(that: Any): Boolean =
    that match {
      case that: InstanceId => this.hashCode == that.hashCode
      case _ => false
    }

  override def hashCode(): Int = id.hashCode()
}
