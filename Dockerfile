FROM adoptopenjdk:11-jre-hotspot
EXPOSE 8888
ARG JAR_FILE=target/monitor-1.0.0-SNAPSHOT-fat.jar
ADD ${JAR_FILE} app.jar
ENTRYPOINT ["java","-jar","/app.jar"]