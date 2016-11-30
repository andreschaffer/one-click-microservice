import groovy.io.FileType
import javaposse.jobdsl.dsl.DslScriptLoader
import javaposse.jobdsl.dsl.JobManagement
import javaposse.jobdsl.dsl.MemoryJobManagement
import spock.lang.Specification
import spock.lang.Unroll

class JjdslScriptsSpec extends Specification {

  @Unroll
  void 'test script #file.path'(File file) {
    given:
    JobManagement jm = new MemoryJobManagement()

    when:
    new DslScriptLoader(jm).runScript(file.text)

    then:
    noExceptionThrown()

    where:
    file << jobFiles
  }

  private List<File> getJobFiles() {
    List<File> files = []
    new File('jjdsl').eachFileRecurse(FileType.FILES) {
        if (it.name.endsWith('.groovy')) {
            files << it
        }
    }
    files
  }
}
