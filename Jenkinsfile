pipeline {
    agent any
    stages {
        stage('Build') {
            steps {
                sh './gradlew build'
            }
        }
        stage('Docker Build') {
            steps {
                sh 'docker build -t shop-boot-demo .'
            }
        }
        stage('Deploy') {
            steps {
                sh 'docker stop shop-boot-demo || true'
                sh 'docker rm shop-boot-demo || true'
                sh 'docker run -d -p 8080:8080 --name shop-boot-demo'
            }
        }
    }
}