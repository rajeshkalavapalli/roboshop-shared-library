def call(Map configMap) {
    pipeline {
        agent {
            node {
                label 'AGENT'
            }
        }
        environment { 
            packageVersion = ""
            nexusUrl = "172.31.34.109:8081"
        }

        options {
            timeout(time: 1, unit: 'HOURS') 
            disableConcurrentBuilds()
        }
        parameters {
            // string(name: 'PERSON', defaultValue: 'Mr Jenkins', description: 'Who should I say hello to?')

            // text(name: 'BIOGRAPHY', defaultValue: '', description: 'Enter some information about the person')

            booleanParam(name: 'Deploy', defaultValue: false, description: 'Toggle this value')

            // choice(name: 'CHOICE', choices: ['One', 'Two', 'Three'], description: 'Pick something')

            // password(name: 'PASSWORD', defaultValue: 'SECRET', description: 'Enter a password')
        }

        stages {
            stage ('get the version ') {
                steps {
                    script {
                        def packageJson = readJSON file: 'package.json'
                        packageVersion = packageJson.version
                        echo "application version : $packageVersion"
                    }
                }
            }

            stage ('install dependencies') {
                steps {
                    sh """
                        npm install 
                    """
                }
            }
            
            stage ('unit test') {
                steps {
                    sh """
                        echo "unit test will run here" 
                    """
                }
            }

            stage ('sonar scan') {
                steps{
                    sh """
                        sonar-scanner
                    """
                }
            }

            stage ('build') {
                steps {
                    sh """
                        ls -la
                        zip -q -r catalogue.zip ./* -x ".git" -x "*.zip"
                        ls -ltr
                    """
                }
            }

            stage ('publish artifact') {
                steps {
                    nexusArtifactUploader(
                        nexusVersion: 'nexus3',
                        protocol: 'http',
                        nexusUrl: "${nexusUrl}",
                        groupId: 'com.roboshop',
                        version: "${packageVersion}",
                        repository: 'catalogue',
                        credentialsId: 'nexus-auth',
                        artifacts: [
                            [artifactId: 'catalogue',
                            classifier: '',
                            file: 'catalogue.zip',
                            type: 'zip']
                        ]
                    )
                }
            }

            stage('Deploy') {
                when {
                    expression{
                        params.Deploy == 'true'
                    }
                }
                steps {
                script{
                        def params = [
                            string(name: 'version', value: "$packageVersion"),
                            string(name: 'environment', value: "dev") 
                        ]
                    build job: "catalogue-deploy", wait: true, parameters: params
                    

                }
                }
            }
        }
        // post
        post { 
            always { 
                echo 'I will always execute!'
                deleteDir()
            }
            failure { 
                echo 'I will run when there is a failure!'
            }
            success { 
                echo 'I will run when there is a success!'
            }
        }
    }
}