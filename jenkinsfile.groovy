pipeline {
    agent any
    environment {
        Bar_Creation = "/mnt/d/iib/ace-12.0.2.0/tools/mqsicreatebar"
        Bar_Store = "/mnt/d/iib"
        Job_Name = "Sample"
        Jfrog_Repository = "http://localhost:8082/artifactory/example-repo-local/"
        Artifactory_Repo_Source = "example-repo-local"
        Artifactory_Repo_Destination = "example-repo-local-destination"
        Artifactory_URL = "http://localhost:8082/artifactory"
        Artifactory_Auth = credentials('7b8040db-fdb2-43dd-a480-2edcbe2e343f')
        Artifact_Name = "Sample.bar"
    }
    stages {
        stage('git login') {
            steps {
                git branch: 'main', credentialsId: 'Git', url: 'https://github.com/git-madhu/IIB-SAMPLE--APP.git'
            }
        }
        stage('bar build') {
            steps {
                script {
                    def xvfb = [$class: 'Xvfb', additionalOptions: '-nolisten tcp -screen 0 1024x768x24', autoDisplayName: true, timeout: 60, display_name: '0.1']
                    wrap(xvfb) {
                        // sh 'export DISPLAY=:99'
                        sh "$Bar_Creation -data $WORKSPACE -b $Bar_Store/Sample.bar -a $Job_Name"
                        // sh "chmod +r $Bar_Store/$Job_Name.bar"
                    }
                }
            }
        }
        stage('Bar Store') {
            steps {
                withCredentials([usernamePassword(credentialsId: '7b8040db-fdb2-43dd-a480-2edcbe2e343f', passwordVariable: 'Jfrog_Password', usernameVariable: 'Jfrog_Username')]) {
                    sh "curl -v -u$Jfrog_Username:$Jfrog_Password --upload-file $Bar_Store/Sample.bar $Jfrog_Repository"
                }
            }
        }
//        stage('Move Artifact in Artifactory') {
//            steps {
//                script {
//                    def moveEndpoint = "${Artifactory_URL}/${Artifactory_Repo_Source}/${Artifact_Name}?to=${Artifactory_Repo_Destination}/${Artifact_Name}"
//                    sh "curl -u ${Artifactory_Auth} -X POST ${moveEndpoint}"
//                }
//            }
//        }
    }
}
