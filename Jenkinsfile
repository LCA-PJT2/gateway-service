#!/usr/bin/env groovy
def APP_NAME
def APP_VERSION
def DOCKER_IMAGE_NAME

pipeline {
    agent {
        node {
            label 'master'
        }
    }

    parameters {
        gitParameter branch: '',
                    branchFilter: '.*',
                    defaultValue: 'origin/main',
                    description: '', listSize: '0',
                    name: 'TAG',
                    quickFilterEnabled: false,
                    selectedValue: 'DEFAULT',
                    sortMode: 'DESCENDING_SMART',
                    tagFilter: '*',
                    type: 'PT_BRANCH_TAG'

        booleanParam defaultValue: false, description: '', name: 'RELEASE'
    }

    environment {
        GIT_URL = "https://github.com/LCA-PJT2/gateway-service.git"
        GITHUB_CREDENTIAL = "github-token"
        ARTIFACTS = "build/libs/**"
        DOCKER_REGISTRY = "alswn26"
        DOCKERHUB_CREDENTIAL = 'dockerhub-token'
    }

    options {
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: "30", artifactNumToKeepStr: "30"))
        timeout(time: 120, unit: 'MINUTES')
    }

    tools {
        gradle 'Gradle 8.14.2'
        jdk 'OpenJDK 17'
        dockerTool 'Docker'
    }

    stages {
        stage('Set Version') {
            steps {
                script {
                    APP_NAME = sh (
                            script: "gradle -q getAppName",
                            returnStdout: true
                    ).trim()
                    APP_VERSION = sh (
                            script: "gradle -q getAppVersion",
                            returnStdout: true
                    ).trim()

                    DOCKER_IMAGE_NAME = "${DOCKER_REGISTRY}/${APP_NAME}:${APP_VERSION}"

                    sh "echo IMAGE_NAME is ${APP_NAME}"
                    sh "echo IMAGE_VERSION is ${APP_VERSION}"
                    sh "echo DOCKER_IMAGE_NAME is ${DOCKER_IMAGE_NAME}"
                }
            }
        }

        stage('Build & Test Application') {
            steps {
                sh "gradle clean build"
            }
        }

        stage('Push Docker Image') {
            steps {
                script {
                    docker.withRegistry("", DOCKERHUB_CREDENTIAL) {
                        sh "docker buildx build --platform linux/amd64,linux/arm64 -t ${DOCKER_IMAGE_NAME} --push ."
                    }
                    sh "docker rmi ${DOCKER_IMAGE_NAME}"
                }
            }
        }

        // --- 여기서부터 수정 필요 ---
        stage('Update Deployment Manifest & Git Push') {
            steps {
                script {
                    withCredentials([string(credentialsId: 'github-pat', variable: 'GITHUB_TOKEN')]) {
                        sh """
                        # 1. 저장소 clone
                        rm -rf app-config
                        git clone https://${GITHUB_TOKEN}@github.com/LCA-PJT2/app-config.git
                        cd app-config

                        # 2. image 태그 업데이트
                        sed -i 's|image: ${DOCKER_REGISTRY}/${APP_NAME}:.*|image: ${DOCKER_REGISTRY}/${APP_NAME}:${APP_VERSION}|' gateway-service/prd/gateway-service-deploy.yml

                        # 3. Git 설정 및 커밋
                        git config user.name "minju26"
                        git config user.email "mjalswn26@gmail.com"
                        git add gateway-service/prd/gateway-service-deploy.yml
                        git commit -m "chore(gateway): 버전 업데이트 ${APP_VERSION}"
                        git push origin main
                        """
                    }
                }
            }
        }
    }
}
