library identifier: 'jenkins-shared-library@main', retriever: modernSCM([
        $class: 'GitSCMSource',
        remote: 'https://github.com/ALabiyb/devsecops.git',
        traits: [[$class: 'jenkins.plugins.git.traits.BranchDiscoveryTrait']] // This ensures the library checks out the correct branch (main in my case)
])

pipeline {
    agent {
        label 'docker-server'
    }

    tools {
        jdk 'jdk21'
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