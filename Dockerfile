FROM openjdk:12

COPY target/pack /opt
COPY start.sh /opt/start.sh

ENTRYPOINT ["/opt/start.sh"]
