FROM ubuntu
RUN apt-get update \
    && apt-get install tesseract-ocr -y \
    && apt-get install openjdk-8-jre -y \
    && apt-get autoclean \
    && apt-get autoremove \
    && useradd -ms /bin/bash newuser
EXPOSE 8080

USER newuser
WORKDIR /home/newuser

ADD target/openshift-jee-sample.jar openshift-jee-sample.jar

ENTRYPOINT ["java", "-Xmx1024m", "-jar", "openshift-jee-sample.jar"]
