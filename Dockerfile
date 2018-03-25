FROM tomcat:8-slim


ARG WORK_PATH=/usr/local/tomcat/webapps


WORKDIR $WORK_PATH


RUN rm -rf * -R


COPY ./target/airapi.war $WORK_PATH/ROOT.war


EXPOSE 8080


ENTRYPOINT ["catalina.sh", "run"]
