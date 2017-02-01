FROM $DOCKER_FROM

# Based on 'liferay.workspace.ee.docker.maintainer' in gradle.properties
MAINTAINER $DOCKER_MAINTAINER

USER $DOCKER_LIFERAY_USER

CMD [ "$DOCKER_LIFERAY_STARTUP_SCRIPT" ]

# Based on 'liferay.workspace.ee.docker.expose' in gradle.properties
EXPOSE $DOCKER_EXPOSE