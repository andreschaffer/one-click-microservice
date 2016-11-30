MAKEFILE_DIR=$(shell dirname $(realpath $(lastword $(MAKEFILE_LIST))))
OUTPUT_DIR=${SERVICE_NAME}

generate:
	mkdir -p ${OUTPUT_DIR}
	cp -R ${MAKEFILE_DIR}/template/. ${OUTPUT_DIR}
	grep -rl '{{SERVICE_NAME}}' ${OUTPUT_DIR} | xargs --no-run-if-empty sed -i "s/{{SERVICE_NAME}}/${SERVICE_NAME}/g"
	grep -rl '{{SERVICE_PORT}}' ${OUTPUT_DIR} | xargs --no-run-if-empty sed -i "s/{{SERVICE_PORT}}/${SERVICE_PORT}/g"

ifndef SERVICE_NAME
$(error SERVICE_NAME not defined)
endif

ifndef SERVICE_PORT
$(error SERVICE_PORT not defined)
endif
