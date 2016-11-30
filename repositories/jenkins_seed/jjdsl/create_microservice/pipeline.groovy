import javaposse.jobdsl.dsl.Job
import jobs.DefaultJob
import jobs.BuildMicroserviceJobName
import jobs.ParameterizedTrigger
import jobs.Gogs
import system.Credentials

parameters = {
  stringParam('SERVICE_NAME','','Name of the microservice')
  stringParam('SERVICE_PORT','','Port of the microservice')
}

Gogs gogs = new Gogs()

Job createRepo = new DefaultJob(name:'create_microservice_repo').build(this)
createRepo.with {
  parameters this.parameters
  wrappers {
    preBuildCleanup()
    credentialsBinding {
        string('GOGS_PASS', Credentials.GOGS_USER_SECRET_ID)
    }
  }
  deliveryPipelineConfiguration('Repository', 'Create repository')
  steps {
    shell("""\
      set -e
      MICROSERVICE_CODE_GENERATOR=microservice_code_generator
      git clone ${gogs.repoUrl("\${MICROSERVICE_CODE_GENERATOR}")}

      curl -f -X POST ${gogs.restApi.authenticatedReposUrl("\${GOGS_PASS}")} \\
        -d "{\\"name\\":\\"\${SERVICE_NAME}\\"}" -H "Content-type: application/json"
      NEW_SERVICE_REPO_URL=${gogs.authenticatedRepoUrl("\${SERVICE_NAME}", "\${GOGS_PASS}")}
      git clone \${NEW_SERVICE_REPO_URL}

      make -f \${MICROSERVICE_CODE_GENERATOR}/Makefile generate SERVICE_NAME=\${SERVICE_NAME} SERVICE_PORT=\${SERVICE_PORT}

      cd \${SERVICE_NAME}
      git add .
      git -c "user.name=Jenkins" -c "user.email=jenkins@example.com" commit -m "Initial commit"
      git push \${NEW_SERVICE_REPO_URL} --all

      NEW_SERVICE_BUILD_JOB_NAME=${new BuildMicroserviceJobName(serviceName:"\${SERVICE_NAME}").value()}
      curl -f -X POST ${gogs.restApi.authenticatedWebhooksUrl("\${SERVICE_NAME}", "\${GOGS_PASS}")} \\
        -d "{\\"type\\":\\"gogs\\",\\"config\\":{\\"url\\":\\"http://jenkins:8080/gogs-webhook/?job=\${NEW_SERVICE_BUILD_JOB_NAME}\\",\\"content_type\\":\\"json\\"},\\"active\\":true}" \\
        -H "Content-type: application/json"
      """.stripIndent()
    )
  }
}

Job createPipeline = new DefaultJob(name:'create_microservice_pipeline').build(this)
createPipeline.with {
  parameters this.parameters
  wrappers {
    preBuildCleanup()
    credentialsBinding {
        string('GOGS_PASS', Credentials.GOGS_USER_SECRET_ID)
    }
  }
  deliveryPipelineConfiguration('Pipeline', 'Create pipeline')
  steps {
    shell("""\
      set -e
      MICROSERVICE_PIPELINE_GENERATOR=microservice_pipeline_generator
      JENKINS_SEED=jenkins_seed
      JENKINS_SEED_REPO_URL=${gogs.authenticatedRepoUrl("\${JENKINS_SEED}", "\${GOGS_PASS}")}
      git clone \${JENKINS_SEED_REPO_URL}

      cd \${JENKINS_SEED}
      make -f \${MICROSERVICE_PIPELINE_GENERATOR}/Makefile generate SERVICE_NAME=\${SERVICE_NAME} SERVICE_PORT=\${SERVICE_PORT} OUTPUT_DIR=jjdsl/\${SERVICE_NAME}

      git add .
      git -c "user.name=Jenkins" -c "user.email=jenkins@example.com" commit -m "New microservice pipeline: \${SERVICE_NAME}"
      git push \${JENKINS_SEED_REPO_URL} --all
      """.stripIndent()
    )
  }
}

new ParameterizedTrigger(jobs:[createPipeline.name]).addTo(createRepo)
