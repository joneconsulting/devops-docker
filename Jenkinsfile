node {
    stage('Clone repository') {
        checkout scm
    }
    stage('Build image') {
        app = docker.build("devops/my-ubuntu")
    }
    stage('Push image') {
        docker.withRegistry('https://192.168.0.33', 'devops') {
            app.push("${env.BUILD_NUMBER}")
            app.push("latest")
        }
    }
}