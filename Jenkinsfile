library identifier: 'jenkins-shared-library@main', retriever: modernSCM([
        $class: 'GitSCMSource',
        remote: 'http://192.168.15.85/softgepg/jenkins/jenkins-shared-library.git'
//        credentialsId: 'Gitlab-credentials-Abubakar'
])

pipeline {
    agent {
        label 'docker-host'
    }

    tools {
        maven 'maven-3.9'
    }

    environment {
        // Notification Configuration
        NOTIFICATION_EMAIL = 'aboutrika638@gmail.com,munimdevops1111@gmail.com'

        // SonarQube Configuration
        SONARQUBE_URL = 'http://62.84.183.2:9000/'
        SONARQUBE_SERVER = 'SonarQube Remote Server'
        SONAR_TOKEN = credentials('sonarqube-token-remote')

        // Project Configuration - FIXED
        PROJECT_NAME = 'notifications'
        IMAGE_NAME = 'notifications'
        IMAGE_TAG = "${env.BUILD_NUMBER ?: 'latest'}"
        BRANCH_NAME = 'main'

        // Registry Configuration
        REGISTRY_TYPE = 'harbor'
        REGISTRY_URL = '89.117.57.16'
        HARBOR_PROJECT = 'softgepg'
        REGISTRY_CREDENTIALS_ID = 'harbor-robot-secret'
        HARBOR_ROBOT_USERNAME = 'robot$jenkins'
        PRIVATE_REGISTRY_URL = '89.117.57.16'

        // Git Configuration
        GIT_REPO_URL = 'http://192.168.15.85/softgepg/notifications.git'
        GIT_CREDENTIALS_ID = 'Gitlab-credentials-Abubakar'

        // Gitea Manifest Repository Configuration
        GITEA_MANIFEST_REPO_URL = 'http://62.84.183.2:3000/SoftGePG/SoftGePG-Manifests.git'
        GITEA_MANIFEST_CREDENTIALS_ID = 'gitea-credentials-abubakar'
        GITEA_MANIFEST_BRANCH = 'main'
        MANIFEST_FILE_PATH = 'notification/notification-service.yaml'


        // Java Home - using Java 21 for general pipeline, Java 17 for SonarQube
        JAVA_HOME = '/usr/lib/jvm/java-21-openjdk-amd64'
        JAVA_17_HOME = '/usr/lib/jvm/java-17-openjdk-amd64'
    }

    stages {
        stage('Checkout & Get Git Info') {
            steps {
                script {
                    // Checkout code
                    checkout([
                            $class: 'GitSCM',
                            branches: [[name: "*/${env.BRANCH_NAME}"]],
                            extensions: [],
                            userRemoteConfigs: [[
                                                        url: env.GIT_REPO_URL,
                                                        credentialsId: env.GIT_CREDENTIALS_ID
                                                ]]
                    ])

                    // Get git commit information directly
                    env.GIT_COMMIT = sh(
                            script: 'git log -1 --pretty=%B | head -1',
                            returnStdout: true
                    ).trim()

                    env.GIT_AUTHOR = sh(
                            script: 'git log -1 --pretty=%an',
                            returnStdout: true
                    ).trim()

                    env.GIT_COMMIT_HASH = sh(
                            script: 'git rev-parse --short HEAD',
                            returnStdout: true
                    ).trim()

                    echo "Git Commit: ${env.GIT_COMMIT}"
                    echo "Git Author: ${env.GIT_AUTHOR}"
                    echo "Git Hash: ${env.GIT_COMMIT_HASH}"
                }
            }
        }

        stage('Send Start Notification') {
            steps {
                script {
                    notify([
                            subject: "🚀 Pipeline Started: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                            recipients: env.NOTIFICATION_EMAIL,
                            templateName: 'start.html',
                            data: [
                                    JOB_NAME: env.JOB_NAME,
                                    BUILD_NUMBER: env.BUILD_NUMBER,
                                    BUILD_URL: env.BUILD_URL,
                                    TRIGGERED_BY: detectBuildTrigger(),
                                    BRANCH: env.BRANCH_NAME,
                                    BUILD_STATUS: 'STARTED',
                                    GIT_COMMIT: env.GIT_COMMIT,
                                    GIT_AUTHOR: env.GIT_AUTHOR
                            ]
                    ])
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                script {
                    echo "=== Starting SonarQube Analysis ==="
                    echo "Using Java 17 for compilation (project requirement)"

                    def sonarResult = sonarQubeAnalysis([
                            projectKey: "notifications-main",
                            projectName: 'notifications Services (main branch)',
                            sonarServerName: env.SONARQUBE_SERVER,
                            sonarHostUrl: env.SONARQUBE_URL,
                            sonarToken: env.SONAR_TOKEN,
                            javaHome: env.JAVA_17_HOME,  // ← CHANGED: Use Java 17 instead of Java 21
                            mavenToolName: 'maven-3.9',
                            additionalParams: "-Dsonar.java.binaries=target/classes -Dsonar.coverage.jacoco.xmlReportPaths=target/site/jacoco/jacoco.xml"
                    ])

                    if (sonarResult) {
                        env.SONAR_ANALYSIS_SUCCESS = sonarResult.success.toString()

                        if (sonarResult.success) {
                            echo "✅ SonarQube analysis completed"
                            echo "📊 Dashboard: ${sonarResult.dashboardUrl}"
                        } else {
                            echo "⚠️ SonarQube analysis had issues: ${sonarResult.error}"
                        }
                    } else {
                        echo "❌ SonarQube analysis returned null result"
                        env.SONAR_ANALYSIS_SUCCESS = 'false'
                    }
                }
            }
        }

        stage('Build Application') {
            steps {
                script {
                    echo "=== Building Application ===="

                    def buildResult = buildAppOnly(
                            projectName: env.PROJECT_NAME,
                            imageName: "${env.HARBOR_PROJECT}/${env.IMAGE_NAME}",
                            imageTag: env.IMAGE_TAG,
                            registryUrl: env.REGISTRY_URL,
                            registryCredentialsId: env.REGISTRY_CREDENTIALS_ID,
                            dockerfilePath: './Dockerfile',
                            buildArgs: [
                                    'GIT_AUTHOR': env.GIT_AUTHOR,
                                    'GIT_COMMIT': env.GIT_COMMIT
                            ],
                            pushToRegistry: false,
                            removeAfterPush: false
                    )

                    if (buildResult.success) {
                        env.BUILT_IMAGE_NAME = buildResult.imageName
                        env.BUILD_RESULT_SUCCESS = 'true'
                        echo "✅ Build completed successfully: ${buildResult.imageName}"
                        echo "📦 Built image stored as: ${buildResult.imageName}"
                    } else {
                        env.BUILD_RESULT_SUCCESS = 'false'
                        error("Build failed: ${buildResult.error}")
                    }
                }
            }
        }

        stage('Push to Registry') {
            when {
                expression { return env.BUILD_RESULT_SUCCESS == 'true' }
            }
            steps {
                script {
                    echo "=== Pushing Image to Harbor Registry ==="
                    echo "Local Image: ${env.BUILT_IMAGE_NAME}"
                    echo "Target Image: ${env.BUILT_IMAGE_NAME}"
                    echo "Registry: ${env.PRIVATE_REGISTRY_URL}"
                    echo "Robot Account: ${env.HARBOR_ROBOT_USERNAME}"

                    // List available images for debugging
                    sh "docker images | head -10"

                    def pushResult = pushToRegistry(
                            imageName: "${env.HARBOR_PROJECT}/${env.IMAGE_NAME}",
                            imageTag: env.IMAGE_TAG,
                            registryType: env.REGISTRY_TYPE,
                            privateRegistryUrl: env.PRIVATE_REGISTRY_URL,
                            credentialsId: env.REGISTRY_CREDENTIALS_ID,
                            robotUsername: env.HARBOR_ROBOT_USERNAME,
                            localImageName: env.BUILT_IMAGE_NAME
                    )

                    if (pushResult.success) {
                        env.PUSH_RESULT_SUCCESS = 'true'
                        env.FINAL_IMAGE_NAME = pushResult.imageName
                        echo "✅ Image pushed successfully to Harbor: ${pushResult.imageName}"
                    } else {
                        env.PUSH_RESULT_SUCCESS = 'false'
                        error("Push to Harbor registry failed: ${pushResult.error}")
                    }
                }
            }
        }

        stage('Update K8s Manifest') {
            when {
                expression { return env.PUSH_RESULT_SUCCESS == 'true' }
            }
            steps {
                script {
                    echo "=== Updating Kubernetes Manifest in Gitea ==="

                    def updateResult = updateManifest(
                            repoUrl: env.GITEA_MANIFEST_REPO_URL,
                            manifestPath: env.MANIFEST_FILE_PATH,
                            branch: env.GITEA_MANIFEST_BRANCH,
                            credentialsId: env.GITEA_MANIFEST_CREDENTIALS_ID,
                            imageName: "${env.HARBOR_PROJECT}/${env.IMAGE_NAME}",
                            imageTag: env.IMAGE_TAG,
                            registryType: env.REGISTRY_TYPE,
                            registryUrl: env.REGISTRY_URL,
                            privateRegistryUrl: env.PRIVATE_REGISTRY_URL,
                            gitEmail: 'jenkins@softnet-testing.com',
                            gitName: 'Jenkins CI',
                            commitMessage: "Update ${env.IMAGE_NAME} image to ${env.IMAGE_TAG} [BUILD: ${env.BUILD_NUMBER}]"
                    )

                    if (updateResult.success) {
                        env.MANIFEST_UPDATE_SUCCESS = 'true'
                        echo "✅ Manifest updated successfully in Gitea"
                        echo "📝 Updated image: ${updateResult.finalImageName}"
                    } else {
                        env.MANIFEST_UPDATE_SUCCESS = 'false'
                        echo "⚠️ Manifest update failed: ${updateResult.error}"
                        currentBuild.result = 'UNSTABLE'
                    }
                }
            }
        }
    }

    post {
        always {
            echo "=== Pipeline Completed ==="
            echo "Build Number: ${env.BUILD_NUMBER}"
            echo "Build URL: ${env.BUILD_URL}"
            echo "SonarQube: ${env.SONARQUBE_URL}"
        }

        success {
            script {
                def finalImageName = env.FINAL_IMAGE_NAME ?: "${env.IMAGE_NAME}:${env.IMAGE_TAG}"
                notify([
                        subject: "✅ Pipeline Success: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        recipients: env.NOTIFICATION_EMAIL,
                        templateName: 'success.html',
                        data: [
                                JOB_NAME: env.JOB_NAME,
                                BUILD_NUMBER: env.BUILD_NUMBER,
                                BRANCH: env.BRANCH_NAME,
                                BUILD_URL: env.BUILD_URL,
                                TRIGGERED_BY: detectBuildTrigger(),
                                BUILD_STATUS: "SUCCESS",
                                GIT_AUTHOR: env.GIT_AUTHOR,
                                GIT_COMMIT: env.GIT_COMMIT,
                                IMAGE_NAME: finalImageName,
                                SONARQUBE_URL: "${env.SONARQUBE_URL}/dashboard?id=notifications-main",
                                MANIFEST_UPDATED: env.MANIFEST_UPDATE_SUCCESS ?: 'false'
                        ]
                ])
            }
        }

        failure {
            script {
                notify([
                        subject: "❌ Pipeline Failed: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        recipients: env.NOTIFICATION_EMAIL,
                        templateName: 'failure.html',
                        data: [
                                JOB_NAME: env.JOB_NAME,
                                BUILD_NUMBER: env.BUILD_NUMBER,
                                BRANCH: env.BRANCH_NAME,
                                BUILD_URL: env.BUILD_URL,
                                TRIGGERED_BY: detectBuildTrigger(),
                                BUILD_STATUS: "FAILED",
                                GIT_AUTHOR: env.GIT_AUTHOR ?: "Unknown",
                                GIT_COMMIT: env.GIT_COMMIT ?: "Unknown",
                                BUILD_SUCCESS: env.BUILD_RESULT_SUCCESS ?: 'false',
                                PUSH_SUCCESS: env.PUSH_RESULT_SUCCESS ?: 'false',
                                ERROR_TYPE: env.BUILD_RESULT_ERROR_TYPE ?: 'UNKNOWN_ERROR',
                                ERROR_MESSAGE: env.BUILD_RESULT_ERROR_MESSAGE ?: 'Pipeline failed - check build logs'
                        ]
                ])
            }
        }

        unstable {
            script {
                notify([
                        subject: "⚠️ Pipeline Unstable: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                        recipients: env.NOTIFICATION_EMAIL,
                        templateName: 'unstable.html',
                        data: [
                                JOB_NAME: env.JOB_NAME,
                                BUILD_NUMBER: env.BUILD_NUMBER,
                                BRANCH: env.BRANCH_NAME,
                                BUILD_URL: env.BUILD_URL,
                                TRIGGERED_BY: detectBuildTrigger(),
                                BUILD_STATUS: "UNSTABLE",
                                GIT_AUTHOR: env.GIT_AUTHOR,
                                GIT_COMMIT: env.GIT_COMMIT
                        ]
                ])
            }
        }
    }
}

/**
 * Detect build trigger source
 */
def detectBuildTrigger() {
    def triggeredBy = "Unknown"
    def causes = currentBuild.getBuildCauses()

    if (causes) {
        def cause = causes[0]
        if (cause.shortDescription && cause.shortDescription.contains("GitLab")) {
            triggeredBy = "GitLab Webhook"
        } else if (cause.userId) {
            triggeredBy = cause.userId
        } else if (cause.shortDescription && cause.shortDescription.toLowerCase().contains("scm change")) {
            triggeredBy = "SCM Change"
        } else {
            triggeredBy = cause.shortDescription ?: "Manual Trigger"
        }
    }

    return triggeredBy
}
