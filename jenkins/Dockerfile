FROM jenkins:2.46.3

USER root
RUN curl -sSL https://get.docker.com/ | sh \
    && apt-get install -y \
    make \
    python-pip \
    && rm -rf /var/lib/apt/lists/*
RUN pip install virtualenv
RUN usermod -aG docker,staff jenkins

USER jenkins
COPY plugins.txt /usr/share/jenkins/plugins.txt
RUN /usr/local/bin/install-plugins.sh $(cat /usr/share/jenkins/plugins.txt)
COPY _seed.groovy /usr/share/jenkins/ref/init.groovy.d/_seed.groovy
