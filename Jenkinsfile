library identifier: 'jenkins-shared-library@main', retriever: modernSCM([
        $class: 'GitSCMSourcce',
        remote: 'https://github.com/ALabiyb/devsecops.git'
])

pipeline {
    agent {
        label 'docker-server'
    }

    stages {
        stage('Build Artifact') {
            steps {
                script {
                    buildArtifact()
                }
            }
        }
    }
}