pipeline {
    agent any
    stages {
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/order-service.git'
            }
        }
        stage('build') {
            steps {
                sh "mvn clean install"
            }
        }
        stage('test') {
            steps {
                sh "mvn clean test"
            }
        }
        stage('SonarQube analysis') {
            steps {
                withSonarQubeEnv('SonarQube') {
                    sh "mvn sonar:sonar"    
                }    
            }    
        }
        stage('Quality Gate') {
            steps {
                waitForQualityGate abortPipeline= true
            }   
        }

        stage('package') {
            steps {
                echo "package"
                //sh "mvn clean test"
            }
        }
        stage('docker') {
            steps {
                echo "docker"
                //sh "mvn clean test"
            }
        }
        stage('aws') {
            steps {
                echo "aws"
                //sh "mvn clean test"
            }
        }
    }
}