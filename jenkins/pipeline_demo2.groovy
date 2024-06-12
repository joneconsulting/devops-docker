pipeline {
  agent none
  tools {
      maven "Maven3.9.5"
  }
  stages {
    stage('Maven Install') {
      agent any
      when {
        expression { params.MAVEN_BUILD == true }
      }
      steps {
        git branch: 'main', url: 'https://github.com/joneconsulting/cicd-web-project'
        sh 'mvn clean compile package -DskipTests=true'
      }
    }
    stage('Docker Build') {
      agent any
      when {
        expression { params.DOCKER_BUILD == true }
      }
      steps {
          script {
            if (params.DOCKER_IMAGE_TAG != "") {
                echo "[docker image tag is not null]"
                imageTag = params.DOCKER_IMAGE_TAG
            } else {
                echo "[docker image tag is null]"
                imageTag = env.BUILD_NUMBER
            }
          }
                    
          // sh 'docker build -t 192.168.0.41/devops/cicd-web-project:$BUILD_NUMBER .'
          sh 'docker build -t 192.168.0.41/devops/cicd-web-project:' + imageTag + ' .'
      }
    }
    stage('Docker Push') {
      agent any
      when {
        expression { params.DOCKER_PUSH == true }
      }
      steps {
        withDockerRegistry(credentialsId: 'harbor-user', url: 'https://192.168.0.41') {
          sh 'docker push 192.168.0.41/devops/cicd-web-project:$BUILD_NUMBER'
        }
      }
    }
  }
}