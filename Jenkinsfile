
pipeline {
    agent {
        label 'java21'
    }

    options {
        timestamps()
        disableConcurrentBuilds()
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Verify Java') {
            steps {
                sh 'java -version'
            }
        }

        stage('Maven Build') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw clean package -DskipTests'
            }
        }

        stage('Verify Kubernetes Access') {
            steps {
                withCredentials([
                    file(
                        credentialsId: 'k3s-deployer-kubeconfig',
                        variable: 'KUBECONFIG'
                    )
                ]) {
                    sh '''
                        set -eu

                        kubectl -n microservices \
                          get deployment notifiche-service
                    '''
                }
            }
        }

        stage('Docker Build & Push GHCR') {
            when {
                tag pattern: 'v[0-9]+\\.[0-9]+\\.[0-9]+',
                    comparator: 'REGEXP'
            }

            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: 'ghcr-publish',
                        usernameVariable: 'GHCR_USER',
                        passwordVariable: 'GHCR_TOKEN'
                    )
                ]) {
                    sh '''
                        set +x
                        set -eu
                        umask 077

                        export DOCKER_CONFIG="$(mktemp -d)"
                        trap 'rm -rf "$DOCKER_CONFIG"' EXIT

                        AUTH="$(printf '%s' "$GHCR_USER:$GHCR_TOKEN" | base64 | tr -d '\\n')"

                        printf '{"auths":{"ghcr.io":{"auth":"%s"}}}\\n' \
                          "$AUTH" > "$DOCKER_CONFIG/config.json"

                        unset AUTH

                        IMAGE="ghcr.io/lucaquaglino/notifiche-service:${TAG_NAME}"

                        echo "Costruzione immagine Docker..."

                        buildctl \
                          --addr unix:///run/buildkit/buildkitd.sock \
                          build \
                          --frontend dockerfile.v0 \
                          --local context=. \
                          --local dockerfile=. \
                          --opt filename=src/main/docker/Dockerfile.jvm \
                          --opt platform=linux/amd64 \
                          --output "type=image,name=${IMAGE},push=true"

                        echo "Immagine pubblicata: ${IMAGE}"
                    '''
                }
            }
        }

        stage('Deploy Kubernetes') {
            when {
                tag pattern: 'v[0-9]+\\.[0-9]+\\.[0-9]+',
                    comparator: 'REGEXP'
            }

            steps {
                withCredentials([
                    file(
                        credentialsId: 'k3s-deployer-kubeconfig',
                        variable: 'KUBECONFIG'
                    )
                ]) {
                    sh '''
                        set -eu

                        IMAGE="ghcr.io/lucaquaglino/notifiche-service"

                        echo "Deploy Kubernetes: ${IMAGE}:${TAG_NAME}"

                        TMP_DIR="$(mktemp -d)"
                        trap 'rm -rf "$TMP_DIR"' EXIT

                        cp k8s/deployment.yaml "$TMP_DIR/deployment.yaml"

                        cat > "$TMP_DIR/kustomization.yaml" <<EOF
apiVersion: kustomize.config.k8s.io/v1beta1
kind: Kustomization

resources:
  - deployment.yaml

images:
  - name: ${IMAGE}
    newTag: ${TAG_NAME}
EOF

                        kubectl kustomize "$TMP_DIR" > "$TMP_DIR/rendered.yaml"

                        echo "Immagine selezionata:"
                        grep 'image:' "$TMP_DIR/rendered.yaml"

                        kubectl -n microservices patch \
                          deployment notifiche-service \
                          --type=strategic \
                          --patch-file "$TMP_DIR/rendered.yaml"

                        kubectl -n microservices rollout status \
                          deployment/notifiche-service \
                          --timeout=180s

                        echo "Deploy completato: ${IMAGE}:${TAG_NAME}"
                    '''
                }
            }
        }
    }

    post {
        success {
            echo 'Pipeline CI/CD completata con successo!'
        }

        failure {
            echo 'Pipeline CI/CD fallita: controllare i log Jenkins.'
        }
    }
}
