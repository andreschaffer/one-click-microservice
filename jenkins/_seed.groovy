import jenkins.model.*;
import hudson.model.FreeStyleProject;
import hudson.tasks.Shell;
import hudson.plugins.git.GitSCM;
import hudson.plugins.git.extensions.impl.WipeWorkspace;
import hudson.triggers.TimerTrigger;
import javaposse.jobdsl.plugin.*;

runTests = new Shell('./gradlew clean test')

executeDslScripts = new ExecuteDslScripts(
  targets:'jjdsl/**/*.groovy',
  additionalClasspath:'src/main/groovy',
  ignoreExisting:false,
  removedJobAction:RemovedJobAction.DELETE,
  removedViewAction:RemovedViewAction.DELETE,
  lookupStrategy:LookupStrategy.JENKINS_ROOT
)

project = Jenkins.instance.createProject(FreeStyleProject, '_seed')
scm = new GitSCM('http://gogs:3000/gogs_user/jenkins_seed.git')
scm.extensions.add(new WipeWorkspace())
project.setScm(scm)
project.addTrigger(new TimerTrigger('@midnight'))
project.getBuildersList().add(runTests)
project.getBuildersList().add(executeDslScripts)
project.save()
Jenkins.instance.reload()
