   def getDockerTag() {
        def tag = sh script: 'git rev-parse HEAD', returnStdout: true
        return tag
    }

pipeline {
    agent any
    environment{
	    Docker_tag = getDockerTag()
    }
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
                sh "mvn clean package"
            }
        }
        stage('docker') {
            steps{
                script {
                    sh 'cp -r /var/lib/jenkins/workspace/order-service-job/order-api/target .'
                    sh 'docker build . -t quangmtran36/qqd-order-service:$Docker_tag'
                    withCredentials([string(credentialsId: '6b6d3ec6-97dc-4c1c-bf02-67afd00371dc', variable: 'dockerHubPwd')]) {
                        sh 'docker login -u quangmtran36 -p ${dockerHubPwd}'
                        sh 'docker push quangmtran36/qqd-order-service:$Docker_tag'                 
                    }
                }
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