package pipelines

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.Job
import jobs.BuildMicroserviceJob
import jobs.DeployMicroserviceJob
import jobs.ParameterizedTrigger

class MicroservicePipeline {
  String serviceName
  String servicePort
  Job build
  Job deploy

  def build(DslFactory dslFactory) {
    this.build = new BuildMicroserviceJob(serviceName:this.serviceName).build(dslFactory)
    this.deploy = new DeployMicroserviceJob(
      serviceName:this.serviceName, servicePort:this.servicePort).build(dslFactory)
    new ParameterizedTrigger(jobs:[this.deploy.name], predefinedProps:['VERSION':'${VERSION}']).addTo(this.build)
  }
}
