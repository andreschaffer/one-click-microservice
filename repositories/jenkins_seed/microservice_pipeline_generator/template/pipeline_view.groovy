import views.PipelineView
import jobs.BuildMicroserviceJobName

new PipelineView(
  name:'{{SERVICE_NAME}}',
  firstJob:new BuildMicroserviceJobName(serviceName:'{{SERVICE_NAME}}').value()
).build(this)
