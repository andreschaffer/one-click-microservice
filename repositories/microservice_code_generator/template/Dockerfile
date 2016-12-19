FROM python:2.7-alpine

WORKDIR /opt
COPY requirements.txt ./
RUN pip install -r requirements.txt
COPY app.py ./
EXPOSE {{SERVICE_PORT}}
CMD ["python", "app.py"]
