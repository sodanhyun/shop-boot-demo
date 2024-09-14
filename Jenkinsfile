pipeline {
    agent any

    environment {
        timestamp = "${System.currentTimeMillis() / 1000L}"
    }

    stages {
        stage('Prepare') {
            steps {
                script {
                    // Get the ID of the sbb:latest image
                    def oldImageId = sh(script: "docker images shop-boot-demo:latest -q", returnStdout: true).trim()
                    env.oldImageId = oldImageId
                }

                git branch: 'main',
                    url: 'https://github.com/sodanhyun/shop-boot-demo'
            }

            post {
                success {
                    sh 'echo "Successfully Cloned Repository"'
                }
                failure {
                    sh 'echo "Fail Cloned Repository"'
                }
            }
        }

        stage('Build Gradle') {
            steps {
                dir('.') {
                    sh """
                    chmod +x gradlew
                    """
                }

                dir('.') {
                    sh """
                    ./gradlew clean build
                    """
                }
            }

            post {
                success {
                    sh 'echo "Successfully Build Gradle Test"'
                }
                 failure {
                    sh 'echo "Fail Build Gradle Test"'
                }
            }
        }

        stage('Build Docker Image') {
            steps {
                script {
                    sh "docker build -t shop-boot-demo:${timestamp} ."
                }
            }
        }

        stage('Run Docker Container') {
            steps {
                script {
                    // Check if the container is already running
                    def isRunning = sh(script: "docker ps -q -f name=shop-boot-demo", returnStdout: true).trim()

                    if (isRunning) {
                        sh "docker rm -f shop-boot-demo"
                    }

                    // Run the new container
                    try {
                        sh """
                        docker run \
                          --name=shop-boot-demo \
                          -p 8080:8080 \
                          -v /docker_projects/shop-boot-demo/volumes/gen:/gen \
                          --restart unless-stopped \
                          -e TZ=Asia/Seoul \
                          -d \
                          shop-boot-demo:${timestamp}
                        """
                    } catch (Exception e) {
                        // If the container failed to run, remove it and the image
                        isRunning = sh(script: "docker ps -q -f name=shop-boot-demo", returnStdout: true).trim()

                        if (isRunning) {
                            sh "docker rm -f shop-boot-demo"
                        }

                        def imageExists = sh(script: "docker images -q shop-boot-demo:${timestamp}", returnStdout: true).trim()

                        if (imageExists) {
                            sh "docker rmi shop-boot-demo:${timestamp}"
                        }

                        error("Failed to run the Docker container.")
                    }

                    // If there's an existing 'latest' image, remove it
                    def latestExists = sh(script: "docker images -q shop-boot-demo:latest", returnStdout: true).trim()

                    if (latestExists) {
                        sh "docker rmi shop-boot-demo:latest"

                        if(!oldImageId.isEmpty()) {
                        	sh "docker rmi ${oldImageId}"
                        }
                    }

                    // Tag the new image as 'latest'
                    sh "docker tag shop-boot-demo:${env.timestamp} shop-boot-demo:latest"
                }
            }
        }
    }
}