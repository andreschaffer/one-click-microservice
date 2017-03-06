[![Build Status](https://travis-ci.org/andreschaffer/one-click-microservice.svg?branch=master)](https://travis-ci.org/andreschaffer/one-click-microservice)
# One Click Microservice

# Background
Having a way of rapidly creating and deploying microservices is a prerequisite if you want to do serious microservices. It should be easy, so that we can focus on business, the thing that makes us unique. Talking about unique, we hope you enjoyed the quite unique movie [Inception](https://en.wikipedia.org/wiki/Inception), as we'll see some inception techniques going through this solution :)

# Pieces involved
The central pieces of this solution are [Jenkins](https://jenkins.io/), [the Jenkins Job-DSL plugin](https://wiki.jenkins-ci.org/display/JENKINS/Job+DSL+Plugin), [Gogs](https://gogs.io/) and [Docker](https://www.docker.com/).  

We use Docker to run Jenkins and Gogs, and while running Jenkins we provide a way for it to run Docker itself (first inception...) with a Docker-outside-of-Docker approach (we map the host's Docker socket, so that containers started by Jenkins will be siblings).  

Within Gogs, we create two git repositories: the microservice_code_generator, which is self-explanatory, and the jenkins_seed, where the Jenkins Job-DSL definitions will live. The Jenkins Job-DSL plugin allows for having pipelines as code, in case you were not familiar with it.  

Now, (our second inception...) using a seed job in Jenkins with the jenkins_seed repository as remote url (origin), we create the other jobs involved in this solution. Nice! The seed job's duty does not end here though, as it's responsible for keeping Jenkins pipelines updated as things evolve.  

From that point, we already have a create_microservice pipeline that fulfills the promise and allows us to create microservices with.one.click! Really cool. How it does it, well, you can bet there's more inception coming... and you'd win. :)  

The create_microservice pipeline has two jobs: the first one creates a new git repository in Gogs and fills it with the microservice_code_generator generated content; the second one uses the microservice_pipeline_generator, which lives inside the jenkins_seed repository, to generate a new pipeline in the jenkins_seed repository itself and pushes those changes, triggering the seed job and thus updating Jenkins with our brand new microservice pipeline.  

That one was probably not the easiest one to follow, but let's move on because we are not done with the inceptionism yet (omg)... in order to have a complete solution we need to deploy the microservice somewhere, right? And in this case somewhere happens to be our own machine.

Phew. Now we are done :)  
Let's see how to put it in action next!

# Prerequisites
- Make sure you have [Git](https://git-scm.com/book/en/v2/Getting-Started-Installing-Git) (to be able to clone this repo), [Docker](https://docs.docker.com/engine/installation/) & [Docker Compose](https://docs.docker.com/compose/install/) installed

# Action
- Clone this repository  
- Run ./run.sh (the first time it will take a couple of minutes since it needs to build the Jenkins Docker image)  
- Now you got Jenkins and Gogs running (the git repositories on Gogs can be explored at [http://localhost:3000/explore/repos](http://localhost:3000/explore/repos) if you'd like)
- Browse to Jenkins at [http://localhost:8080](http://localhost:8080)
- Run the seed job (the first time it will take a few seconds because it downloads libs and runs tests before effectively updating the jobs)  
![alt text](https://github.com/andreschaffer/one-click-microservice/blob/master/docs/images/run_seed_job.png "Run the seed job")  

- Go to the all_pipelines view (or create_microservice view, but all_pipelines is especially good because you can see everything that will happen)  
![alt text](https://github.com/andreschaffer/one-click-microservice/blob/master/docs/images/go_to_all_pipelines_view.png "Go to the all_pipelines view")  

- Run the create_microservice pipeline (provide a service name, i.e. good_service and the port that it shall listen to, i.e. 8000)  
![alt text](https://github.com/andreschaffer/one-click-microservice/blob/master/docs/images/run_create_microservice_pipeline.png "Run the create_microservice pipeline")  

- Wait for the pipeline to finish and for the seed job to run again  
- Voila! You have a brand new pipeline for your microservice. Trigger it to get your microservice up and running  
![alt text](https://github.com/andreschaffer/one-click-microservice/blob/master/docs/images/run_microservice_pipeline.png "Run the microservice pipeline")  

- After the deployment is done, you can check your microservice echo Hello, World! (i.e. [http://localhost:8000](http://localhost:8000))   
![alt text](https://github.com/andreschaffer/one-click-microservice/blob/master/docs/images/microservice_deployment_finished.png "Microservice deployment finished")

- Enjoy and maybe create some more microservices :)

# Going forward
Now that you have a solution for easily creating microservices, one thing will happen: you WILL create lots of microservices. And in order to be able to maintain them, you'll need the proper mindset. A few tips:  
- Grow your system with convention in mind (i.e. for builds, deployments, monitoring, etc)  
- Adopt a test strategy that scales (there is one here that you can follow: https://github.com/andreschaffer/microservices-testing-examples)  
- Put effort into identifying the right microservices boundaries (strategic [Domain-Driven Design](https://en.wikipedia.org/wiki/Domain-driven_design) can take you a long way)  
- Design for failure (because shit happens)  

# That's it! We hope you enjoyed it!
Adapt the pieces (i.e. git server, microservice_code_generator, deployment) to suit your needs and make your own inception dream! Remember to carry your [totem](http://inception.wikia.com/wiki/Totem) with you and have fun! :)

# How to contribute
This project follows the following contribution guidelines:  
https://guides.github.com/activities/contributing-to-open-source/#contributing

# License
This project is licensed under the [MIT License](https://opensource.org/licenses/MIT) and affects all files in this source code repository.

# Who do I talk to?
* Ping [André Schaffer](https://github.com/andreschaffer) or [Tommy Tynjä](https://github.com/tommysdk)
