package views

import javaposse.jobdsl.dsl.DslFactory
import javaposse.jobdsl.dsl.views.DeliveryPipelineView.Sorting

class PipelineView {
    String name
    String firstJob

    def build(DslFactory dslFactory) {
        dslFactory.deliveryPipelineView(this.name) {
            pipelineInstances(5)
            columns(1)
            sorting(Sorting.LAST_ACTIVITY)
            updateInterval(5)
            enableManualTriggers(true)
            showAvatars(false)
            showChangeLog(true)
            configure { node ->
              node << {
                theme('contrast')
                allowRebuild('true')
                allowPipelineStart('true')
                linkRelative('true')
              }
            }
            pipelines {
              component(this.name, this.firstJob)
            }
        }
    }
}
