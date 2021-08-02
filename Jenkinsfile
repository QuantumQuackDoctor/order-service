pipeline {
    agent any
    stages {
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/user-service.git'
            }
        }
        stage('build') {
            steps {
                sh "mvn clean install -X -Dspring.datasource.url=jdbc:postgresql://localhost:5432/test"
            }
        }
    }
}