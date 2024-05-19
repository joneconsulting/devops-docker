docker run -d -p 13306:3306 \
        -e MARIADB_ALLOW_EMPTY_ROOT_PASSWORD=true \
        -e MARIADB_DATABASE=mydb  \
        --name mariadb1 \
        mariadb:latest