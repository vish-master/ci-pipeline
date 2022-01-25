def commonPipeline

pipeline {
    agent any

    tools { nodejs "node" }

    options {
        ansiColor('xterm')
    }

    stages {
        stage("load pipeline properties") {
            steps {
                script {
                    sh 'pwd'
                    echo "Loading Jenkins files"
                    sh 'cd /'
                    sh "pwd"
                    load "./jenkins/JenkinsConstants.groovy"
                    commonPipeline = load "./jenkins/JenkinsCommon.groovy"
                }
            }
        }

        stage("run pipeline steps") {
            steps {
                script {
                    commonPipeline.infoString("Running Pipeline Steps")
                    commonPipeline.runPipelineSteps()
                }
            }

        }
    }

    post {
        always {
            echo commonPipeline.infoString("Done")
            cleanWs()
        }

        success {
            echo commonPipeline.successString("Success")
        }

        failure {
            echo commonPipeline.failureString("Failure")
        }
    }
}

