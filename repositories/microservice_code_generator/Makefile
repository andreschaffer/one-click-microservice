MAKEFILE_DIR=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
OUTPUT_DIR=${SERVICE_NAME}

generate:
	test -n "${SERVICE_NAME}" || (echo "SERVICE_NAME not defined" && exit 1)
	test -n "${SERVICE_PORT}" || (echo "SERVICE_PORT not defined" && exit 1)
	mkdir -p ${OUTPUT_DIR}
	cp -R ${MAKEFILE_DIR}/template/. ${OUTPUT_DIR}
	grep -rl '{{SERVICE_NAME}}' ${OUTPUT_DIR} | xargs --no-run-if-empty sed -i "s/{{SERVICE_NAME}}/${SERVICE_NAME}/g"
	grep -rl '{{SERVICE_PORT}}' ${OUTPUT_DIR} | xargs --no-run-if-empty sed -i "s/{{SERVICE_PORT}}/${SERVICE_PORT}/g"

build: test

test: SERVICE_NAME:=test_service
test: SERVICE_PORT:=9876
test: OUTPUT_DIR:=$(shell mktemp -u)
test: VERSION:=test_version
test: generate
	cd ${OUTPUT_DIR}; make build VERSION=${VERSION}; rm -r ${OUTPUT_DIR};
	docker rmi ${SERVICE_NAME}:${VERSION}
