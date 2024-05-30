##### Container Image Security
```
docker scout quickview edowon0623/docker:latest
```

```
docker scout recommendations <container images>
```

```
docker pull aquasec/trivy:0.18.3
```

```
docker run --rm -v /home/ec2-user/.cache/:/root/.cache/ aquasec/trivy:0.18.3 <container images>
```

##### Container Isolation
```
docker network create --driver bridge isolated_network
```

```
docker run --network=isolated_network <container images>
```

##### Runtime Security
```
docker run -it first-image:0.1 bash
```
```
# apt-get update
```

```
docker run -it --cap-drop=ALL first-image:0.1 bash
```
```
# apt-get update
```

```
docker run -it run --cap-drop=ALL --cap-add=SETGID --cap-add=SETUID --cap-add=CHOWN --cap-add=DAC_OVERRIDE --cap-add=FOWNER first-image:0.1 bash
```
```
# apt-get update
```

##### Secrets Management
```
docker swarm init
echo "MySecretData" | docker secret create my_secret -
echo qwer1234 | docker secret create my_db_password -
```

```
docker secret ls
docker secret inspect my_secret
docker sevice create --name my-redis --secret my_secret redis:alpine
```

```
docker exec -it <container_id> bash
# redis-cli SET my_secret_key "$(cat /run/secrets/my_secret)"
# redis-cli
127.0.0.1:6379> GET my_secret_key
```

```
docker service create --name my-db --replicas 1 \
        --secret source=my_db_password,target=mariadb_root_password \
        -e MARIADB_ROOT_PASSWORD_FILE="/run/secrets/mariadb_root_password" \
        -e MARIADB_DATABASE="mydb" mariadb:latest
```
```
docker exec -it [container_id] bash
```
```
mariadb1] mariadb -h127.0.0.1 -uroot -p
```
