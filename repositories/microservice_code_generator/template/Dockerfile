FROM python:2.7-alpine

WORKDIR /opt
COPY app.py ./
COPY requirements.txt ./
RUN pip install -r requirements.txt
EXPOSE {{SERVICE_PORT}}
CMD ["python", "app.py"]
