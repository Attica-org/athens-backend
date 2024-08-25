pipeline {
    agent any

    environment {
        DOCKER_HUB_CREDENTIALS = 'docker-hub'
    }

    stages {
        stage('Clone Repository') {
            steps {
                checkout scm
            }
        }

        stage('Build Gradle') {
            steps {
                script {
                    docker.withRegistry("https://registry.hub.docker.com", DOCKER_HUB_CREDENTIALS) {
                        sh "./gradlew jib -Djib.to.image=${env.DOCKER_REPOSITORY}/${env.DOCKER_IMAGE_NAME}"
                    }
                }
            }
        }

        stage('Deploy application') {
            steps {
                script {
                    sshPublisher(publishers: [
                        sshPublisherDesc(
                            configName: 'application-instance',
                            transfers: [
                                sshTransfer(
                                    sourceFiles: 'docker-compose.green.yml, docker-compose.blue.yml, deploy-prod.sh',
                                    execCommand:  '''
                                        chmod +x deploy-prod.sh && ./deploy-prod.sh
                                    ''',
                                    execTimeout: 200000
                                )
                            ],
                            verbose: true
                        )
                    ])
                }
            }
        }
    }
}
