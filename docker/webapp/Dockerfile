# his image is for the Africastrikes webapp

# https://hub.docker.com/_/ubuntu/
FROM ubuntu:14.04.5

MAINTAINER Lucien van Wouw <lwo@iisg.nl>

LABEL Description="This image is to build the Africastrikes webapp" Version="1.0"

# ToDo: Move to base image
ENV GOSU_VERSION=1.10 \
    locale-gen=en_US.UTF-8 \
    LC_ALL=C.UTF-8 \
    LANG=en_US.UTF-8 \
    LANGUAGE=en_US.UTF-8

# ToDo: Move to base image
RUN set -x \
    && apt-get update \
    && apt-get install -y --no-install-recommends ca-certificates curl python software-properties-common vim wget \
    && rm -rf /var/lib/apt/lists/* \
    && dpkgArch="$(dpkg --print-architecture | awk -F- '{ print $NF }')" \
    && wget -O /usr/local/bin/gosu "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch" \
    && wget -O /usr/local/bin/gosu.asc "https://github.com/tianon/gosu/releases/download/$GOSU_VERSION/gosu-$dpkgArch.asc" \
    && export GNUPGHOME="$(mktemp -d)" \
    && gpg --keyserver hkp://ha.pool.sks-keyservers.net:80 --recv-keys B42F6819007F00F88E364FD4036A9C25BF357DD4 \
    && gpg --batch --verify /usr/local/bin/gosu.asc /usr/local/bin/gosu \
    && rm -r "$GNUPGHOME" /usr/local/bin/gosu.asc \
    && chmod +x /usr/local/bin/gosu \
    && gosu nobody true \
    && wget "https://bootstrap.pypa.io/get-pip.py" && python get-pip.py && pip install ninja2 \
    && apt-get update

# Install application dependencies
RUN add-apt-repository ppa:webupd8team/java -y \
    && apt-get update \
    && /bin/echo oracle-java8-installer shared/accepted-oracle-license-v1-1 select true | /usr/bin/debconf-set-selections \
    && apt-get install oracle-java8-installer -y

ENV ADMINS="user@localhost" \
    APP_HOME="/home/africastrikes" \
    APP_PORT=8000 \
    APP_USER=africastrikes \
    APP_VERSION=1.0.0 \
    APPLY_EVOLUTIONS_DEFAULT="false" \
    ARTICLE_FILE_PATH="/data/africastrikes/" \
    DB_DEFAULT_DRIVER="com.mysql.jdbc.Driver" \
    DB_DEFAULT_URL="jdbc:mysql://localhost/africastrikes" \
    DB_DEFAULT_USERNAME="africastrikes" \
    DB_DEFAULT_PASSWORD="africastrikes" \
    PLAY_CRYPTO_SECRET="rdEzRFKFE2bC3cW3n6gKkkSH"

# Wrapper for setting config on disk
ADD ./run.sh /usr/local/bin/run.sh
ADD ./scripts /opt/scripts

RUN adduser $APP_USER --home $APP_HOME \
    && /usr/bin/wget "https://bamboo.socialhistoryservices.org/browse/AFRICA-PRODUCTION/latestSuccessful/artifact/JOB1/${APP_VERSION}/africa-strikes-${APP_VERSION}.tgz" -O "/${APP_HOME}/app.tgz" \
    && /bin/tar -xvzf "/${APP_HOME}/app.tgz" -C "${APP_HOME}" \
    && /bin/rm "/${APP_HOME}/app.tgz" \
    && chown -R $APP_USER:$APP_USER $APP_HOME

EXPOSE $APP_PORT

ENTRYPOINT ["run.sh"]

CMD ["start"]
