pipeline {
  agent none
  stages {
    stage('Maven Install') {
      agent {
        docker {
          image 'maven:3.9.5'
        }
      }
      steps {
        git branch: 'main', url: 'https://github.com/joneconsulting/cicd-web-project'
        sh 'mvn clean compile package -DskipTests=true'
      }
    }
    stage('Docker Build') {
      agent any
      steps {
        sh 'docker build -t 192.168.0.41/devops/cicd-web-project:$BUILD_NUMBER .'
      }
    }
    stage('Docker Push') {
      agent any
      steps {
        withDockerRegistry(credentialsId: 'harbor-user', url: 'https://192.168.0.41') {
          sh 'docker push 192.168.0.41/devops/cicd-web-project:$BUILD_NUMBER'
        }
      }
    }
  }
}