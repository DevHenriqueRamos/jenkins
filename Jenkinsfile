pipeline {
  agent {
    dockerImage { image 'maven:3.9.9-eclipse-temurin-21-alpine' }
  }
  stages {
    stage('Tests') {
      steps {
        sh 'mvn test'
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
  }
}