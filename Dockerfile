FROM bluerain/java-10-buildpack:tomcat


ARG WORK_PATH=/home/app


WORKDIR $WORK_PATH


COPY ./target/airapi.war $WORK_PATH/ROOT.war


EXPOSE 8080


ENTRYPOINT ["catalina.sh", "run"]
