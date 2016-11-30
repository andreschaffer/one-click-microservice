import pipelines.MicroservicePipeline

new MicroservicePipeline(
  serviceName:'{{SERVICE_NAME}}',
  servicePort:'{{SERVICE_PORT}}'
).build(this)
