#### Windows (Windows 10 OK, Windows 11 NG)
````
$ docker run --privileged --name jenkins-server -itd -p 10022:22 -p 8080:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker:latest /usr/sbin/init

````

#### Windows (ip-tables 관련 오류 발생 시, Windows 11 OK)
````
$ docker run --privileged --name jenkins-server -itd -p 10022:22 -p 8080:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker:no_iptables /usr/sbin/init

$ docker run --privileged --name myapp -itd -p 20022:22 -p 8082:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker:no_iptables /usr/sbin/init

````

#### Apple m1 chip
````
$ docker run --privileged --name jenkins-server -itd -p 10022:22 -p 8080:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker-server:m1 /usr/sbin/init

$ docker run --privileged --name myapp -itd -p 20022:22 -p 8082:8080 -e container=docker -v /sys/fs/cgroup:/sys/fs/cgroup:rw --cgroupns=host edowon0623/docker-server:m1 /usr/sbin/init

````
