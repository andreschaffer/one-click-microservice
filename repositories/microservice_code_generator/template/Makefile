VERSION=latest

build: test
	docker build -t {{SERVICE_NAME}}:${VERSION} .

test: venv
	venv/bin/python app_tests.py

venv: requirements.txt
	test -d venv || virtualenv --python=python2.7 venv
	venv/bin/pip install -r requirements.txt
	touch venv/bin/activate
