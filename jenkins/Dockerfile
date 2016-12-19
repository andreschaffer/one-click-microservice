FROM jenkins:2.19.3

USER root
ARG DOCKER_VERSION
RUN curl -sSL https://get.docker.com/ | sh && \
    apt-get purge -y docker-engine && \
    apt-get install -y docker-engine=${DOCKER_VERSION}-0~$( [ "${DOCKER_VERSION}" = $( (echo "${DOCKER_VERSION}"; echo "1.12.4") | sort -V | tail -n 1 ) ] && echo debian-jessie || echo jessie ) \
    make \
    python-pip
RUN pip install virtualenv
RUN usermod -aG docker jenkins
RUN usermod -aG staff jenkins

USER jenkins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh $(cat /usr/share/jenkins/plugins.txt)
COPY _seed.groovy /usr/share/jenkins/ref/init.groovy.d/_seed.groovy
