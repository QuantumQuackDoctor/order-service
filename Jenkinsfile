pipeline {
    agent any
    stages {
        stage('git') {
            steps {
                git branch: 'dev', url: 'https://github.com/QuantumQuackDoctor/order-service.git'
            }
        }
        stage('package') {
            steps {
                sh "mvn clean package"
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
        stage('ECR Push') {
            steps{
                script {
                    sh 'docker build -t order-service .'
                    sh "aws ecr get-login-password --region us-east-2 | docker login --username AWS --password-stdin 644684002839.dkr.ecr.us-east-2.amazonaws.com"
                    sh 'docker tag order-service:latest 644684002839.dkr.ecr.us-east-2.amazonaws.com/order-service:latest'
                    sh 'docker push 644684002839.dkr.ecr.us-east-2.amazonaws.com/order-service:latest'               
                }
            }
        }
    }
    post {
        success {
            script {
                sh 'docker rmi $(docker images -a | grep aws | awk '{print $3}')'
            }
        }
    }
}