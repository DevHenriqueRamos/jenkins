pipeline {
  agent any
  
  environment {
    DOCKER_IMAGE = "javahenriquedev/java-api-jenkins"
    DOCKER_TAG = "latest"
    REGISTRY_CREDENTIALS = "9467788c-06ac-4ecd-9d71-9cf499fa855d"
    GIT_COMMIT = sh(script: 'git rev-parse --short HEAD', returnStdout: true).trim()
    AWS_CREDENTIALS_ID = "b338022d-618f-4731-a36b-3939201610e4"
    AWS_REGION = "us-west-2"
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

    stage('Set up AWS Credentials') {
      withCredentials([usernamePassword(credentialsId: AWS_CREDENTIALS_ID, usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY')]){
        sh '''
          aws configure set aws_access_key_id "$AWS_ACCESS_KEY_ID"
          aws configure set aws_secret_access_key "$AWS_SECRET_ACCESS_KEY"
          aws configure set region "$AWS_REGION"
        '''
      }
    }

    stage('Terraform Init') {
      steps {
        sh "cd ./terraform/env/dev/ && terraform init"
      }
    }

    stage('Terraform Validate') {
      steps {
      sh "cd ./terraform/env/dev/ && terraform validate"
      }
    }

    stage('Terraform Apply') {
      steps {
        sh 'cd ./terraform/env/dev/ && terraform apply -var="image_name_dev=${DOCKER_IMAGE}:${DOCKER_TAG}" -auto-approve'
      }
    }
  }
}
