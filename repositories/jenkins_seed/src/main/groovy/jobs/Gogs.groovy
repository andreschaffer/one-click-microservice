package jobs

class Gogs {

  String scheme = 'http'
  String host = 'gogs'
  String port = '3000'
  String user = 'gogs_user'
  @Lazy GogsRestApi restApi = new GogsRestApi()

  private String baseUrl() {
    "$scheme://$host:$port"
  }

  private String authenticatedBaseUrl(String pass) {
    "$scheme://$user:$pass@$host:$port"
  }

  public String repoUrl(String repoName) {
    baseUrl() + "/$user/${repoName}.git"
  }

  public String authenticatedRepoUrl(String repoName, String pass) {
    authenticatedBaseUrl(pass) + "/$user/${repoName}.git"
  }


  class GogsRestApi {
    private String authenticatedApiBaseUrl(String pass) {
      authenticatedBaseUrl(pass) + "/api/v1"
    }

    public String authenticatedReposUrl(String pass) {
      authenticatedApiBaseUrl(pass) + "/user/repos"
    }

    public String authenticatedWebhooksUrl(String repoName, String pass) {
      authenticatedApiBaseUrl(pass) + "/repos/$user/$repoName/hooks"
    }
  }
}
