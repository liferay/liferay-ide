./mvnw clean package -DskipTests=true && \ 
	cd build/com.liferay.ide-repository && \
	./gradlew deployToBintray
