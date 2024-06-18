0. Make a dir
```
mkdir ~/certs
mkdir ~/harbor
```

1. CA Certificates
```
openssl genrsa -out ca.key 4096
openssl req -x509 -new -nodes -sha512 -days 365 -key ca.key -out ca.crt
```

2. Server Certificates
```
openssl genrsa -out server.key 4096
openssl req -new -sha512 -key server.key -out server.csr
```

3. SAN
```
vi v3ext.cnf
```
```
subjectAltName = IP:192.168.0.33, IP:127.0.0.1
```
```
openssl x509 -req -sha512 -days 365 -extfile v3ext.cnf -CA ca.crt -CAkey ca.key -CAcreateserial -in server.csr -out server.crt
openssl x509 -inform PEM -in server.crt -out server.cert
```

4. Docker Engine
mkdir -p /etc/docker/certs.d/server
cp server.cert /etc/docker/certs.d/server/
cp server.key /etc/docker/certs.d/server/
cp ca.crt /etc/docker/certs.d/server/