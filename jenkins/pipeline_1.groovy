pipeline {
    agent {
        kubernetes {
            yaml '''
apiVersion: v1
kind: Pod
metadata:
    name: jenkins-pod
    labels:
        some-label: pod
    namespace: dev
spec:
    containers:
        - name: jnlp
          image: harbor.joneconsulting.co.kr/library/jenkins/inbound-agent:4.11-1-jdk11
        - name: maven-jdk
          image: harbor.joneconsulting.co.kr/workspace/maven:3.8.7-openjdk-21
          command:
            - cat
          tty: true
        - name: docker
          image: harbor.joneconsulting.co.kr/workspace/docker-dind-awscli:lateset
          volumeMounts:
            - name: dockersock
              mountPath: "/var/run/docker.sock"
          command:
            - cat
          tty: true
        - name: kubectl
          iamge:  harbor.joneconsulting.co.kr/workspace/k8s-kubectl:latest 
          command:
            - cat
          tty: true
        - name: app-java-baseimage
          image:  harbor.joneconsulting.co.kr/workspace/app-java-baseimage:maven-jdk17
          command:
            - cat
          tty: true
    imagePullSecrets:
    - name: dev-secret-harbot
    volumes:
      - name: dockersock
        hostPath:
          path: /var/run/docker.sock
    affinity:
      nodeAffinity:
        requiredDuringSchedulingIgnoreDuringExecution:
          nodeSelectorTerms:
        - matchExpressions:
          - key: node
            operator: In
            values:
            - mgm

            '''
        }
    }

    environment {
        REGISTRY_HOST = "harbor.joneconsulting.co.kr"
        DEPLOY_ENV = "dev"
        AWS_CREDENTIAL_NAME = 'aws_credentail'
        ECR_PATH='12333.dkr.ecr.ap-northeast-2.amazonaws.com'
        REGION='ap-northeast-2'
        AWS_ACCESS_KEY_ID="~"
        AWS_SECRET_ACCESS_KEY="~~"
        ACW_ECR_API="https://~.api.ecr.ap-northeast-2.vpce.amazonaws.com"
        AWS_ECR_USERNAME='AWS'
        AWS_ECR_REGISTRY="12333.dkr.ecr.ap-northeast-2.amazonaws.com"
        AWS_PROFILE="mydata-prdJenkins"
        ARGOCD_AUTH_TOKEN = "..."
    }

    stages {
        stage("git clone") {
            steps {
                git clone: '${BRANCH}', credentialsId: 'credential_gitlab', url: 'http://gitlab.com/project/test.git'

                script {
                    gitTag = sh(returnStdout: true, script: 'git tag | tail -n 1').trim()
                    latestHash = sh(returnStdout: true, script: 'git rev-parse HEAD').trim()
                    latestHashShort = sh(returnStdout: true, script: 'git rev-parse --short HEAD').trim()

                    if (gitTag == null || gitTag == "") {
                        latestTagHash = latestHash
                    } else {
                        latestTagHash = sh(returnStdout: true, script: 'git rev-list -n 1' + gitTag).trim()
                    }
                }
            }
        }
        stage("build") {
            when {
                express { params.MAVEN_BUILD == true }
            }
            steps {
                configFileProvider([configFile(fileId: 'maven-settings.xml', variable: 'MAVEN_SETTINGS')]) {
                  container('app-java-baseimage') {
                    sh 'echo [start maven build]'
                    sh 'mvn --version'
                    sh 'java --version'
                    sh 'pwd'
                    sh 'ls -al'

                    sh 'sed -i "s/<activeProfile>dev/<activeProfile>$DEPLOY_ENV/g" $MAVEN_SETTGINS'
                    sh 'mvn -e -U -s $MAVEN_SETTINGS clean package -DskipTests=true -Dmaven.test.failure.ignore=true'
                    sh 'ls ./target'
                  }
                }
            }
        }
        stage("dockerize") {
            when {
                express { params.MAVEN_BUILD == true && params.DOCKER_IMAGE_BUILD == true }
            }
            steps {
                container('docker') {
                  script {
                    echo "[start docker image build]"

                    if (env.DOCKER_IMAGE_TAG != "") {
                      echo "docker image tag is not null"
                      imageTag = env.DOCKER_IMAGE_TAG
                      
                    } else {
                      echo "docker image tag is null"
                      if (latestHash != null) {
                        if (gitTag != null && latestTagHash == latestHash) {
                          if (gitTag == "") {
                            imageTag = latestHashShort
                          } else {
                            imageTag = gitTag
                          }
                        } else {
                          imageTag = latestHashShort
                        }
                      } else {
                        imageTag = latestHashShort
                      }
                    }

                    script {
                      withCredentials(
                        $class: "UsernamePasswordMultiBinding",
                        credentialsId: "harbor_credentail".
                        usernameVariable: 'username',
                        passwordVariable: 'password'
                      ) {
                        sh 'echo ${password} | docker login http://harbor.joneconsulting.co.kr -u ${username} --password-stdin'
                        sh 'docker pull harbor.joneconsulting.co.kr/app-java-runtime:open-jdk17.0.2-slim-addon'
                        sh 'docker build -t 12333.dkr.ecr.ap-northeast-2.amazonaws.com/dev-app-api:' + imageTag + '-$BUILD_NUMBER -f ${DOCKERFILE_NAME} .'
                      }
                    }

                    script {
                      withCredentials(
                        $class: "AmazonWebServicesCredentialsBiding",
                        credentialsId: "aws_credentail".
                        accessKeyVariable: 'AWS_ACCESS_KEY_ID',
                        secretKeyVariable: 'AWS_SECRET_ACCESS_KEY'
                      ) {
                        sh 'aws configure set aws_access_key_id ${AWS_ACCESS_KEY_ID}'
                        sh 'aws configure set aws_secret_access_key ${AWS_SECRET_ACCESS_KEY}'
                        sh 'aws ecr get-login-password --region ap-northeast-2 --endpoint-url ${AWS_ECR_API} | docker login --username ${AWS_ECR_USERNAME} --passwoprd-stdin ${AWS_ECR_REGISTRY}'
                        sh 'docker push 12333.dkr.ecr.ap-northeast-2.amazonaws.com/dev-app-api:' + iamgeTag + '-$BUILD_NUMBER'
                      }
                    }

                    sh '''
                    if [[ ~ -d ~/.aws]]; then
                      mkdir ~/.aws
                    fi
                    cat <<-EOF > ~/.aws/credentials
                    [mydata-prdJenkins]
                    aws_access_key_id=$AWS_ACCESS_KEY_ID
                    aws_secret_access_key=$AWS_SECRET_ACCESS_KEY
                    '''
                    sh '''
                    cat <<-EOF > ~/.aws/config
                    [default]
                    region=ap-northeast-2
                    output-json
                    '''

                    sh 'aws ecr get-login-password --profile ${AWS_PROFILE} --region ap-northeast-2 --endpoint-url ${AWS_ECR_API} | docker login --username ${AWS_ECR_USERNAME} --password-stdin ${AWS_ECR_REGISTRY}'
                    echo '[dockerize] end'
                  }
                }
            }
        }
        stage("updte k8s manifest file") {
            when {
                express { params.DOCKER_IMAGE_BUILD == true && params.ARGOCD == true }
            }
            steps {
                container('docker') {
                  script {
                    sh 'echo [strat change an image tag of the docker image]'
                    sh 'mkdir -p k8s_files'
                    dir ("k8s_fiels") {
                      git branch: 'dev', credentialsId: 'gitlab_credential', url: 'http://gitlab.com/app-cd.git'
                      sh 'pwd'
                      sh 'ls'
                    }

                    dir("kus_fiels/dev/app-api") {
                      sh "pwd"
                      sh "ls"
                      resultText = sh(returnStdout: true, script: '''
                        chmod 777 dev-api-deploy.yml
                        CURRENTTAG=$(awk -F: \'{if (match($1, "image")!=0) {if (match($2, "aifc-dev")!=0) {printf ":%s",$3}}}\' dev-api-deploy.yml)
                        REPLACETAG=":'''" + imageTag + '''-$BUILD_NUMBER"
                        sed -i "s|$CURRENTTAG|$REPLACETAG|g" dev-api-deploy.yml
                      ''')
                      sh 'cat dev-api-deploy.yml | grep image:'
                      sh 'chmod 644 dev-api-deploy.yml'

                      withCredentials([gitUsernamePassword(credentialsId: 'gitlab_credentialsId', gitToolName: 'Default')]) {
                        sh '''
                          ls -al
                          git config user.name "dowon"
                          git config user.email "test@test.com"
                          git clean -fd
                          git add .
                          git commit -m "[project-api] changed a iamge tag of the K8s deployment file by Jenkins (tag: ''' + imageTag + '''-$BUILD_NUMBER)"
                          git push http://gitlab.com/project-app-cd.git
                        '''
                      }
                    }
                  }
                }
            }
        }
        stage("argocd sync") {
            when {
                express { params.ARGOCD == true }
            }
            steps {
                container('kubectl') {
                    sh 'echo "[start argocd sync]"'
                    sh """ curl -X POST "http://argocd/api/v1/applications/app-name-api/sync" -H "Authorization: Bearer ${ARGOCD_AUTH_TOEKN}" """
                }
            }
        }
    }
}