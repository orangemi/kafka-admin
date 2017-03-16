build-web:
	cd web && npm i && npm run build

build-api:
	gradle distTar

build: build-web build-api

build-docker: build
	docker build -t orangemi/kafka-admin .

push-docker: build-docker
	docker push orangemi/kafka-admin

.PHONY: build