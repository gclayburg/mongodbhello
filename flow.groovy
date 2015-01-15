def devQAStaging() {
    env.PATH="${tool 'Maven 3.x'}/bin:${env.PATH}"
    stage 'Dev'
    sh 'mvn -o clean package'
    archive 'target/x.war'
    stage 'QA'
    parallel(longerTests: {
        runWithServer {url ->
            sh "mvn -o -f sometests/pom.xml test -Durl=${url} -Dduration=30"
        }
    }, quickerTests: {
        runWithServer {url ->
            sh "mvn -o -f sometests/pom.xml test -Durl=${url} -Dduration=20"
        }
    })
    stage name: 'Staging', concurrency: 1
    deploy 'target/x.war', 'staging'
}
def production() {
    input message: "Does http://localhost:8080/staging/ look good?"
    try {
        checkpoint('Before production')
    } catch (NoSuchMethodError _) {
        echo 'Checkpoint feature available in Jenkins Enterprise by CloudBees.'
    }
    stage name: 'Production', concurrency: 1
    node('master') {
        sh 'curl -I http://localhost:8080/staging/'
        unarchive mapping: ['target/x.war' : 'x.war']
        deploy 'x.war', 'production'
        echo 'Deployed to http://localhost:8080/production/'
    }
}
def deploy(war, id) {
    sh "cp ${war} /tmp/webapps/${id}.war"
}
def undeploy(id) {
    sh "rm /tmp/webapps/${id}.war"
}
def runWithServer(body) {
    def id = UUID.randomUUID().toString()
    deploy 'target/x.war', id
    try {
        body.call "http://localhost:8080/${id}/"
    } finally {
        undeploy id
    }
}

def stopcoreos(instance){
    dir('docker/visualsync') {
        echo "I think I am running in a directory"
        sh "pwd"
        sh "./stopcoreos.sh $instance"
    }
}
def startcoreos(instance){
    dir('docker/visualsync') {
        echo "start: I think I am running in a directory"
        sh "pwd"
        sh "./startcoreos.sh $instance"
    }
}

def fastWar(){
    node('master'){
        unarchive mapping: ['pom.xml' : '.', 'policyconsole/' : '.', 'service-core/': '.', 'smoketest/' : '.', 'docker/' : '.', 'flow.groovy' : '.'  ]
        sh "${tool 'M3'}/bin/mvn -B -DskipTests=true clean install"
    }
}

def stopCopper() {
    node('bagley-dind') {
        unarchive mapping: [ 'docker/' : '.']
        stopcoreos('9')
    }
}

def doBuild() {

    parallel firstBranch: {
        fastWar()
    },secondBranch: {
        stopCopper()
    }
}
/*
 * VisualSync - a tool to visualize user data synchronization
 * Copyright (c) 2015 Gary Clayburg
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
 */

return this;