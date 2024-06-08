#### Windows (Windows 10 OK, Windows 11 NG)
````
$ docker run --privileged --name my-harbor -itd -p 10022:22 -p 8081:8080 -p 80:80 -p 443:443 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker:latest /usr/sbin/init

$ docker run --privileged --name myapp -itd -p 20022:22 -p 8082:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker:latest /usr/sbin/init

````

#### Windows (ip-tables 관련 오류 발생 시, Windows 11 OK)
![NO_IPTABLES](../images/iptables_error.png)
````
$ docker run --privileged --name my-harbor -itd -p 10022:22 -p 8081:8080 -p 80:80 -p 443:443 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker:no_iptables /usr/sbin/init

$ docker run --privileged --name myapp -itd -p 20022:22 -p 8082:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker:no_iptables /usr/sbin/init

````

#### Apple m1 chip
````
$ docker run --privileged --name my-harbor -itd -p 10022:22 -p 8081:8080 -p 80:80 -p 443:443 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker-server:m1 /usr/sbin/init

$ docker run --privileged --name myapp -itd -p 20022:22 -p 8082:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker-server:m1 /usr/sbin/init

````

#### Dind 에서 registry 사용 시 (manager, worker01, worker02)
- /etc/docker/daemon.json 파일에 아래 내용 추가
````
{
    "insecure-registries": ["registry:5000"]
}
````

#### Container에서 docker-compose 설치 
````
$ curl -L "https://github.com/docker/compose/releases/download/v2.6.0/docker-compose-$(uname -s)-$(uname -m)" -o /usr/local/bin/docker-compose

$ chmod +x /usr/local/bin/docker-compose

$ docker-compose --version
````