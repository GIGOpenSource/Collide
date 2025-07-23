FROM openjdk:8-jdk-alpine3.7

LABEL "author"="javajianghu"

COPY entrypoint.sh /sentinel/
ADD https://github.com/alibaba/Sentinel/releases/download/1.8.8/sentinel-dashboard-1.8.8.jar /sentinel/sentinel-dashboard-1.8.8.jar

# 设置时区，默认为UTC
RUN ln -sf /usr/share/zoneinfo/Asia/Shanghai /etc/localtime
RUN echo 'Asia/Shanghai' > /etc/timezone
ENTRYPOINT ["sh","/sentinel/entrypoint.sh"]
