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
        maven 'maven-3.8.7'
    }

    environment {
        JAVA_HOME = tool 'jdk21'  // Extra safety
        BUILD_TOOL= 'maven' // Change to 'npm', 'go', etc. per repo or project
        GIT_REPO_URL = 'https://github.com/ALabiyb/devsecops.git'
        GIT_CREDENTIALS_ID = 'github-personal-access-token' // Jenkins credential ID for Git access
        BRANCH_NAME = 'main' // Branch to checkout
        NOTIFICATION_EMAIL = 'hackermunim@gmail.com' // Comma-separated emails
        SLACK_CHANNEL = '#ci-cd-notifications' // Optional Slack channel
    }

    stages {
        stage('Checkout and Git Info') {
            steps {
                script {
                    checkoutAndGitInfo(repo: env.GIT_REPO_URL, credentialsId: env.GIT_CREDENTIALS_ID, branch: env.BRANCH_NAME) // Uses env.GIT_REPO_URL, env.GIT_CREDENTIALS_ID, env.BRANCH_NAME automatically
                }
            }
        }

        stage('Send Start Notification') {
            steps {
                script {
                    def triggeredBy = detectBuildTrigger()
                    sendStartNotification(
                            subject: "🚀 Pipeline Started: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                            recipients: env.NOTIFICATION_EMAIL,
                            triggeredBy: triggeredBy
//                            data: [
//                                    JOB_NAME: env.JOB_NAME,
//                                    BUILD_NUMBER: env.BUILD_NUMBER,
//                                    BUILD_URL: env.BUILD_URL,
//                                    TRIGGERED_BY: triggeredBy,
//                                    BRANCH: env.BRANCH_NAME,
//                                    BUILD_STATUS: 'STARTED',
//                                    GIT_COMMIT: env.GIT_COMMIT,
//                                    GIT_AUTHOR: env.GIT_AUTHOR
//                            ]
                    )
                }
            }
        }

        stage('Build Artifact') {
            steps {
                script {
                    buildArtifact() // Uses env.BUILD_TOOL automatically // Or override: buildArtifact(buildTool: 'npm')
                    // It will auto-detect based on files (pom.xml → maven, package.json → npm, etc.)
                }
            }
        }
    }
}