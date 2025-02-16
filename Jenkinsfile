pipeline {
  agent any
  
  environment {
    DOCKER_IMAGE = "javahenriquedev/java-api-jenkins"
    DOCKER_TAG = "latest"
    REGISTRY_CREDENTIALS = "9467788c-06ac-4ecd-9d71-9cf499fa855d"
  }

  stages {
    stage('Tests') {
      steps {
        sh 'mvn clean test -B'
      }
      post {
        always {
          junit 'target/surefire-reports/*.xml'
        }
      }
    }

    stage('Build') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }

    stage('Build Docker Image') {
      steps {
        script {
          sh "docker build -t ${DOCKER_IMAGE}:${DOCKER_TAG} ."
        }
      }
    }

    stage('Login to Docker Hub') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: REGISTRY_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh "echo ${DOCKER_PASS} | docker login -u ${DOCKER_USER} --password-stdin"
          }
        }
      }
    }

    stage('Push to Docker Hub') {
      steps {
        script {
          sh "docker push ${DOCKER_IMAGE}:${DOCKER_TAG}"
        }
      }
    }

    stage('Clean Up') {
      steps {
        sh "docker rmi ${DOCKER_IMAGE}:${DOCKER_TAG}"
      }
    }
  }
}