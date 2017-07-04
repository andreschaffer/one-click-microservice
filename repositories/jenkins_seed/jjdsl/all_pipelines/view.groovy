deliveryPipelineView('all_pipelines') {
    pipelineInstances(1)
    columns(2)
    sorting(Sorting.FAILED_FIRST)
    updateInterval(5)
    enableManualTriggers(true)
    showAvatars(false)
    showChangeLog(false)
    configure { node ->
      node << {
        theme('contrast')
        allowRebuild('true')
        allowPipelineStart('true')
        linkRelative('true')
      }
    }
    pipelines {
      component('_seed', '_seed')
      component('create_microservice', 'create_microservice_repo')
      regex(/(.+)_build$/)
    }
}
