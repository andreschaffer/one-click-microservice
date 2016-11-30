package jobs

import javaposse.jobdsl.dsl.Job

class ParameterizedTrigger {
    List<String> jobs
    String condition = "SUCCESS"
    Map<String, String> predefinedProps = [:]

    def addTo(Job job) {
        job.publishers {
            downstreamParameterized {
                trigger(this.jobs.join(", ")) {
                    condition(this.condition)
                    triggerWithNoParameters(true)
                    parameters {
                        currentBuild()
                        predefinedProps(this.predefinedProps)
                    }
                }
            }
        }
    }
}
