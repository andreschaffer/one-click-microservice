package jobs

class BuildMicroserviceJobName {
  String serviceName

  public String value() {
    sprintf('%1s_build', this.serviceName)
  }
}
