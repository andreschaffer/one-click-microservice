package jobs

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class DefaultJob {
  String name
  Job job

  public Job build(DslFactory dslFactory) {
    this.job = dslFactory.job(this.name) {
      logRotator {
        numToKeep(20)
      }
    }
    this.job
  }
}
