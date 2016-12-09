@Grab(group = 'com.jayway.awaitility', module = 'awaitility', version = '1.3.5')
import com.jayway.awaitility.Duration
import com.jayway.awaitility.core.ConditionFactory
import java.util.concurrent.TimeUnit
import groovy.transform.Field
@Grab(group = 'org.codehaus.groovy.modules.http-builder', module = 'http-builder', version = '0.7.1')
import groovyx.net.http.HTTPBuilder
import groovyx.net.http.Method
import java.lang.ProcessBuilder

String jenkins = 'http://localhost:8080'
String gogs = 'http://localhost:3000'
@Field Duration defaultTimeout = new Duration(2, TimeUnit.MINUTES)
@Field Duration defaultPollInterval = new Duration(3, TimeUnit.SECONDS)
@Field ConditionFactory condition = new ConditionFactory(defaultTimeout, defaultPollInterval, defaultPollInterval, true)

shell('./run.sh')
assert isEventuallyAvailable(jenkins)
assert isEventuallyAvailable(gogs)

post("$jenkins/job/_seed/build?delay=0sec")
assert isEventuallyAvailable("$jenkins/job/_seed/lastBuild/api/json")
assert isEventuallySuccessful("$jenkins/job/_seed/lastBuild/api/json")

String serviceName = 'good_service'
String servicePort = '8000'
post("$jenkins/job/create_microservice_repo/buildWithParameters?SERVICE_NAME=$serviceName&SERVICE_PORT=$servicePort")
assert isEventuallyAvailable("$jenkins/job/create_microservice_pipeline/1/api/json")
assert isEventuallySuccessful("$jenkins/job/create_microservice_pipeline/1/api/json")
assert isEventuallySuccessful("$jenkins/job/_seed/2/api/json/")
assert isEventuallyAvailable("$jenkins/job/${serviceName}_build")

post("$jenkins/job/${serviceName}_build/build?delay=0sec")
assert isEventuallySuccessful("$jenkins/job/${serviceName}_deploy/1/api/json")

assert isEventuallyAvailable("http://localhost:$servicePort")
println('Test run finished successfully')

def void shell(cmd) {
  def process = new ProcessBuilder("$cmd").redirectErrorStream(true).start()
  process.inputStream.eachLine { println it }
}

def boolean isEventuallyAvailable(url) {
    condition.await('Waiting for successful http response').until { isAvailable(url) }
    true
}

def boolean isEventuallySuccessful(url) {
    condition.await('Waiting for successful build').until { get(url).equals('SUCCESS') }
    true
}

def boolean isAvailable(url) {
    HTTPBuilder http = new HTTPBuilder(url)
    try {
        def status = 0
        http.request(Method.GET) {
            response.success = { response, reader ->
                status = response.statusLine.statusCode
            }
        }
        println("URL $url is available! GET returned $status")
        status == 200
    } catch (Exception e) {
        println("URL $url not available")
        false
    }
}

def String get(url) {
    def buildResult = null
    HTTPBuilder http = new HTTPBuilder(url)
    http.request(Method.GET) {
        response.success = { response, reader ->
            if (reader != null) {
                buildResult = reader.result
            }
        }
        response.'404' = { response, reader ->
            buildResult = 'N/A'
        }
    }
    buildResult
}

def void post(url) {
    new HTTPBuilder(url).request(Method.POST) {}
}
