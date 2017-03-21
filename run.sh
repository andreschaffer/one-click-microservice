#!/bin/bash
set -e

JENKINS_HOME=/tmp/jenkins_home
echo "Preparing jenkins home: $JENKINS_HOME"
if [ -d $JENKINS_HOME ]; then rm -rf $JENKINS_HOME; fi
mkdir -p $JENKINS_HOME

GOGS_HOME=/tmp/gogs
echo "Preparing gogs home: $GOGS_HOME"
if [ -d $GOGS_HOME ]; then rm -rf $GOGS_HOME; fi
mkdir -p $GOGS_HOME

REPOSITORIES_FOLDER=/tmp/repositories
echo "Preparing repositories folder: $REPOSITORIES_FOLDER"
if [ -d $REPOSITORIES_FOLDER ]; then rm -rf $REPOSITORIES_FOLDER; fi
mkdir -p $REPOSITORIES_FOLDER

echo "Starting jenkins and gogs"
DOCKER_VERSION=$(docker version --format '{{.Server.Version}}') docker-compose up -d

echo "Setting up gogs"
GOGS_PORT=3000
GOGS_HOST_PORT=localhost:${GOGS_PORT}
GOGS_USER=gogs_user
GOGS_PASS=gogs_pass

until curl -f -X POST http://${GOGS_HOST_PORT}/install \
-F "db_type=SQLite3" \
-F "db_host=127.0.0.1:3306" \
-F "db_user=root" \
-F "db_passwd=" \
-F "db_name=gogs" \
-F "ssl_mode=disable" \
-F "db_path=data/gogs.db" \
-F "app_name=Gogs: Go Git Service" \
-F "repo_root_path=/data/git/gogs-repositories" \
-F "run_user=git" \
-F "domain=localhost" \
-F "ssh_port=22" \
-F "http_port=${GOGS_PORT}" \
-F "app_url=http://${GOGS_HOST_PORT}/" \
-F "log_root_path=/app/gogs/log" \
-F "smtp_host=" \
-F "smtp_from=" \
-F "smtp_email=" \
-F "smtp_passwd=" \
-F "enable_captcha=on" \
-F "admin_name=${GOGS_USER}" \
-F "admin_passwd=${GOGS_PASS}" \
-F "admin_confirm_passwd=${GOGS_PASS}" \
-F "admin_email=${GOGS_USER}@example.com" ; do
  echo "Waiting for gogs to start..."
  sleep 3;
done

JENKINS_SEED=jenkins_seed
MICROSERVICE_CODE_GENERATOR=microservice_code_generator

echo "Creating ${JENKINS_SEED} and ${MICROSERVICE_CODE_GENERATOR} repos at gogs"
curl -f -X POST http://${GOGS_HOST_PORT}/api/v1/user/repos -d '{"name":"'${JENKINS_SEED}'"}' \
  -H 'Content-type: application/json' -u ${GOGS_USER}:${GOGS_PASS}
curl -f -X POST http://${GOGS_HOST_PORT}/api/v1/user/repos -d '{"name":"'${MICROSERVICE_CODE_GENERATOR}'"}' \
  -H 'Content-type: application/json' -u ${GOGS_USER}:${GOGS_PASS}

mkdir ${REPOSITORIES_FOLDER}/${JENKINS_SEED}
mkdir ${REPOSITORIES_FOLDER}/${MICROSERVICE_CODE_GENERATOR}

cp -R repositories/${JENKINS_SEED} ${REPOSITORIES_FOLDER}
cp -R repositories/${MICROSERVICE_CODE_GENERATOR} ${REPOSITORIES_FOLDER}

cd ${REPOSITORIES_FOLDER}/${JENKINS_SEED}
git init
git add .
git commit -m "Jenkins seed"
git remote add origin http://${GOGS_USER}:${GOGS_PASS}@${GOGS_HOST_PORT}/${GOGS_USER}/${JENKINS_SEED}.git
git push http://${GOGS_USER}:${GOGS_PASS}@${GOGS_HOST_PORT}/${GOGS_USER}/${JENKINS_SEED}.git --all

cd ${REPOSITORIES_FOLDER}/${MICROSERVICE_CODE_GENERATOR}
git init
git add .
git commit -m "Microservice code generator"
git remote add origin http://${GOGS_USER}:${GOGS_PASS}@${GOGS_HOST_PORT}/${GOGS_USER}/${MICROSERVICE_CODE_GENERATOR}.git
git push http://${GOGS_USER}:${GOGS_PASS}@${GOGS_HOST_PORT}/${GOGS_USER}/${MICROSERVICE_CODE_GENERATOR}.git --all

echo "Creating jenkins web hook for ${JENKINS_SEED} and ${MICROSERVICE_CODE_GENERATOR}"
JENKINS_PORT=8080
JENKINS_HOST_PORT=localhost:${JENKINS_PORT}

curl -f -X POST http://${GOGS_HOST_PORT}/api/v1/repos/${GOGS_USER}/${JENKINS_SEED}/hooks \
  -d '{"type":"gogs","config":{"url":"http://jenkins:'${JENKINS_PORT}'/gogs-webhook/?job=_seed","content_type":"json","secret":"dummy"},"active":true}' \
  -H "Content-type: application/json" -u ${GOGS_USER}:${GOGS_PASS}
curl -f -X POST http://${GOGS_HOST_PORT}/api/v1/repos/${GOGS_USER}/${MICROSERVICE_CODE_GENERATOR}/hooks \
  -d '{"type":"gogs","config":{"url":"http://jenkins:'${JENKINS_PORT}'/gogs-webhook/?job=microservice_code_generator_build","content_type":"json","secret":"dummy"},"active":true}' \
  -H "Content-type: application/json" -u ${GOGS_USER}:${GOGS_PASS}

echo "Creating ${GOGS_USER} secret in jenkins"
GOGS_USER_SECRET_ID=${GOGS_USER}_secret
until curl -f -X POST http://${JENKINS_HOST_PORT}/credentials/store/system/domain/_/createCredentials \
--data-urlencode 'json={
  "": "0",
  "credentials": {
    "scope": "GLOBAL",
    "id": "'${GOGS_USER_SECRET_ID}'",
    "secret": "'${GOGS_PASS}'",
    "description": "",
    "$class": "org.jenkinsci.plugins.plaincredentials.impl.StringCredentialsImpl"
  }
}' ; do
  echo "Waiting for jenkins to start..."
  sleep 3;
done

echo "All set!"
