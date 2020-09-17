package scheduler

abstract class SchedulingPolicy {

  def allocate(pod: PodInfo): Boolean

  def deallocate(pod: PodInfo): Boolean
}
