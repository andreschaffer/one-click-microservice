package jobs

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class BuildMicroserviceJob {
  String serviceName
  Job job

  public Job build(DslFactory dslFactory) {
    this.job = new DefaultJob(name:jobName()).build(dslFactory)
    this.job.with {
      environmentVariables {
        groovy('[VERSION: new Date().format("1.0.yyyyMMddHHmmss")]')
      }
      deliveryPipelineConfiguration('Commit stage', 'Build')
      scm {
        git {
          remote {
            url(new Gogs().repoUrl(this.serviceName))
          }
          branches('master')
          extensions {
            wipeOutWorkspace()
          }
        }
        triggers {
          scm('@weekly')
        }
      }
      steps {
        shell('make build VERSION=${VERSION}')
      }
    }
    this.job
  }

  private String jobName() {
    new BuildMicroserviceJobName(serviceName:this.serviceName).value()
  }
}
