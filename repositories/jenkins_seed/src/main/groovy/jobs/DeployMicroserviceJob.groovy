package jobs

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job

class DeployMicroserviceJob {
  String serviceName
  String servicePort
  Job job

  public Job build(DslFactory dslFactory) {
    this.job = new DefaultJob(name:jobName()).build(dslFactory)
    this.job.with {
      parameters {
        stringParam('VERSION','','Version to deploy')
      }
      deliveryPipelineConfiguration('Deploy', 'Deploy')
      wrappers {
        preBuildCleanup()
      }
      steps {
        shell(
          """\
          docker stop $serviceName || true && docker rm $serviceName || true
          docker run -d --name $serviceName -p $servicePort:$servicePort $serviceName:\${VERSION}
          """.stripIndent()
        )
      }
    }
    this.job
  }

  private String jobName() {
    sprintf('%1s_deploy', this.serviceName)
  }
}
