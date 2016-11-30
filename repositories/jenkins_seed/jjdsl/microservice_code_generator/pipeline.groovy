import jobs.DefaultJob
import jobs.Gogs

new DefaultJob(name:'microservice_code_generator_build').build(this).with {
  scm {
    git {
      remote {
        url(new Gogs().repoUrl('microservice_code_generator'))
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
  deliveryPipelineConfiguration('Commit stage', 'Build')
  steps {
    shell('make build')
  }
}
