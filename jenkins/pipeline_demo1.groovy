pipeline {
  agent none
  tools {
      maven "Maven3.9.5"
  }
  stages {
    stage('Maven Install') {
      agent any
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