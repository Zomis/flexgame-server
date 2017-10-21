#!/usr/bin/env groovy

@Library('ZomisJenkins')
import net.zomis.jenkins.Duga

node {
    stage('Build') {
        def duga = new Duga()
        checkout scm
        def status = sh(script: 'mvn clean package', returnStatus: true)
        junit '**/target/surefire-reports/*.xml'
        duga.dugaResult("Build " + (status == 0 ? "success" : "FAILURE"))
        if (status != 0) {
            error('Maven build failed with status ' + status)
        }
    }
}
