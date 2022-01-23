String repoName
String appVersion

def runPipelineSteps() {

    appVersion = getAppVersion()

    stage("Checkout SCM") {
        checkout(scm)
    }


    stage("build NodeJs") {
        echo infoString("Building NodeJS App")
        buildNodesJsApp()
    }

    stage("unit testing") {
        echo infoString("Testing NodeJS App")
        try {
            sh "./jenkins/scripts/start.sh"
            sh "npm test"
        } catch (Exception e) {
            throw e.getMessage()
        } finally {
            sh "./jenkins/scripts/kill.sh"
        }
    }

    stage("build Docker image") {
        echo infoString("Building Docker Image")
        dockerImage = docker.build "${APP_NAME}:${appVersion}"

        docker.withRegistry("https://${NEXUS_DOCKER_REGISTRY_URL}", NEXUS_CREDENTIAL){
            dockerImage.push("latest")
        }

    }

}

String getAppVersion() {
    def packageJson = readJSON file: 'package.json'
    appVersion = "${packageJson.version}"

    return appVersion
}


def buildNodesJsApp() {
    sh 'npm install'
    sh "chmod +x -R ${env.WORKSPACE}"
}

String infoString(String message) {
    return "\033[42m ${message} \033[0m"
}

String successString(String message) {
    return "\033[42m ${message} \033[0m"
}

String failureString(String message) {
    return "\033[41m ${message} \033[0m"
}

return this