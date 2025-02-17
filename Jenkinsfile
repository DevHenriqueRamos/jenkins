pipeline {
  agent any
  
  environment {
    DOCKER_IMAGE = "javahenriquedev/java-api-jenkins"
    DOCKER_TAG = "latest"
    REGISTRY_CREDENTIALS = "66c2de5c-d0b3-4bce-8713-6308a3315410"
    GIT_COMMIT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
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
          sh "docker build -t ${DOCKER_IMAGE}:${GIT_COMMIT} ."
        }
      }
    }

    stage('Login to Docker Hub') {
      steps {
        script {
          withCredentials([usernamePassword(credentialsId: REGISTRY_CREDENTIALS, usernameVariable: 'DOCKER_USER', passwordVariable: 'DOCKER_PASS')]) {
            sh ''' 
              echo "$DOCKER_PASS" | docker login -u "$DOCKER_USER" --password-stdin
            '''
          }
        }
      }
    }

    stage('Push to Docker Hub') {
      steps {
        script {
          sh "docker tag ${DOCKER_IMAGE}:${GIT_COMMIT} ${DOCKER_IMAGE}:${DOCKER_TAG}"
          sh "docker push ${DOCKER_IMAGE}:${GIT_COMMIT}"
          sh "docker push ${DOCKER_IMAGE}:${GIT_COMMIT}"
        }
      }
    }

    stage('Clean Up') {
      steps {
        sh "docker system prune -af"
      }
    }
  }
}
