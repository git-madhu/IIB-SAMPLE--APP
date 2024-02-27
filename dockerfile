FROM iibcom/iib:latest

WORKDIR /home/aceuser/ace-server/

ADD http://localhost:8082/artifactory/example-repo-local/sample.bar

EXPOSE 7801


:


