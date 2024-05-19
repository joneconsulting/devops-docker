docker network create -driver bridge my-network

docker run -d -p 13306:3306 --network my-network \
        -e MARIADB_ALLOW_EMPTY_ROOT_PASSWORD=true \
        -e MARIADB_DATABASE=mydb \
        --name my-mariadb edowon0623/my-mariadb:1.0

docker run -d -p 8088:8088 --network my-network \
        -e "spring.datasource.url=jdbc:mariadb://my-mariadb:3306/mydb" \
        --name catalog-service edowon0623/catalog-service:mariadb-demo


