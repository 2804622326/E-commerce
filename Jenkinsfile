pipeline {
    agent any
    
    environment {
        DOCKER_COMPOSE_FILE = 'docker/docker-compose.yml'
        AWS_REGION = 'eu-west-1'
        ECR_REGISTRY = '614441038924.dkr.ecr.eu-west-1.amazonaws.com'
        ECR_REPO_BACKEND = 'sportscenter-backend'
        ECR_REPO_FRONTEND = 'sportscenter-frontend'
        EC2_INSTANCE_ID = 'i-07871d98b4e6dec77'
        EC2_USER = 'ec2-user'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling latest code from repository...'
                git branch: 'feature/sort-optimization', 
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/2804622326/E-commerce.git'
            }
        }
        
        stage('Build Backend') {
            steps {
                echo 'Building Spring Boot backend...'
                sh '''
                    chmod +x ./mvnw
                    # Use Maven wrapper - no external Maven needed
                    ./mvnw clean package -DskipTests
                '''
            }
            post {
                success {
                    echo 'Backend build successful'
                }
                failure {
                    echo 'Backend build failed'
                }
            }
        }
        
        stage('Test Backend') {
            steps {
                echo 'Running backend unit tests...'
                sh '''
                    ./mvnw test
                '''
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
                success {
                    echo 'Backend tests passed'
                }
                failure {
                    echo 'Backend tests failed'
                }
            }
        }
        
        stage('Build Frontend') {
            steps {
                echo 'Building React frontend...'
                dir('client') {
                    sh '''
                        npm ci
                        npm run build
                    '''
                }
            }
            post {
                success {
                    echo 'Frontend build successful'
                }
                failure {
                    echo 'Frontend build failed'
                }
            }
        }
        
        stage('Test Frontend') {
            steps {
                echo 'Running frontend unit tests...'
                dir('client') {
                    sh '''
                        npm run test -- --run
                    '''
                }
            }
            post {
                success {
                    echo 'Frontend tests passed'
                }
                failure {
                    echo 'Frontend tests failed'
                }
            }
        }
        
        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images for AMD64 architecture...'
                sh '''
                    # Create and use buildx builder for multi-platform builds
                    docker buildx create --name multiplatform --use || docker buildx use multiplatform
                    docker buildx inspect --bootstrap
                    
                    # Build backend image for AMD64
                    docker buildx build \
                        --platform linux/amd64 \
                        --file docker/Dockerfile.backend \
                        --tag sportscenter-backend:latest \
                        --tag ${ECR_REGISTRY}/${ECR_REPO_BACKEND}:latest \
                        --tag ${ECR_REGISTRY}/${ECR_REPO_BACKEND}:${BUILD_NUMBER} \
                        --load \
                        .
                    
                    # Build frontend image for AMD64
                    docker buildx build \
                        --platform linux/amd64 \
                        --file docker/Dockerfile.frontend \
                        --tag sportscenter-frontend:latest \
                        --tag ${ECR_REGISTRY}/${ECR_REPO_FRONTEND}:latest \
                        --tag ${ECR_REGISTRY}/${ECR_REPO_FRONTEND}:${BUILD_NUMBER} \
                        --load \
                        .
                '''
            }
        }
        
        stage('Push to ECR') {
            steps {
                echo 'Pushing Docker images to Amazon ECR...'
                withCredentials([
                    [$class: 'UsernamePasswordMultiBinding', credentialsId: 'aws-credentials', 
                     usernameVariable: 'AWS_ACCESS_KEY_ID', passwordVariable: 'AWS_SECRET_ACCESS_KEY']
                ]) {
                    sh '''
                        # Login to ECR
                        aws ecr get-login-password --region ${AWS_REGION} | \
                            docker login --username AWS --password-stdin ${ECR_REGISTRY}
                        
                        # Push backend images
                        docker push ${ECR_REGISTRY}/${ECR_REPO_BACKEND}:latest
                        docker push ${ECR_REGISTRY}/${ECR_REPO_BACKEND}:${BUILD_NUMBER}
                        
                        # Push frontend images
                        docker push ${ECR_REGISTRY}/${ECR_REPO_FRONTEND}:latest
                        docker push ${ECR_REGISTRY}/${ECR_REPO_FRONTEND}:${BUILD_NUMBER}
                        
                        echo "Images pushed successfully!"
                        echo "Backend: ${ECR_REGISTRY}/${ECR_REPO_BACKEND}:${BUILD_NUMBER}"
                        echo "Frontend: ${ECR_REGISTRY}/${ECR_REPO_FRONTEND}:${BUILD_NUMBER}"
                    '''
                }
            }
        }
        
        stage('Deploy to EC2') {
            steps {
                echo 'Deploying application to EC2 instance...'
                script {
                    def ec2Host = sh(
                        script: "aws ec2 describe-instances --instance-ids ${EC2_INSTANCE_ID} --region ${AWS_REGION} --query 'Reservations[0].Instances[0].PublicIpAddress' --output text",
                        returnStdout: true
                    ).trim()
                    
                    echo "EC2 Public IP: ${ec2Host}"
                    
                    sshagent(['ec2-ssh-key']) {
                        sh """
                            scp -o StrictHostKeyChecking=no \
                                docker/docker-compose.ec2.yml \
                                ${EC2_USER}@${ec2Host}:~/docker-compose.yml
                            
                            scp -o StrictHostKeyChecking=no \
                                docker/data.sql \
                                ${EC2_USER}@${ec2Host}:~/data.sql
                            
                            cat > /tmp/deploy.sh << 'EOF'
#!/bin/bash
set -e

echo "=== Starting deployment ==="

# Login to ECR
echo "Logging in to ECR..."
aws ecr get-login-password --region eu-west-1 | docker login --username AWS --password-stdin 614441038924.dkr.ecr.eu-west-1.amazonaws.com

# Pull latest images
echo "Pulling images..."
docker pull 614441038924.dkr.ecr.eu-west-1.amazonaws.com/sportscenter-backend:latest
docker pull 614441038924.dkr.ecr.eu-west-1.amazonaws.com/sportscenter-frontend:latest

# Stop old containers
echo "Stopping old containers..."
docker-compose down || true

# Clean up old images
echo "Cleaning up..."
docker image prune -f

# Start new containers and WAIT for them to be healthy
echo "Starting new containers..."
docker-compose up -d

# Wait for backend to be healthy (up to 3 minutes)
echo "Waiting for backend to become healthy..."
RETRIES=18
for i in $(seq 1 $RETRIES); do
    if docker inspect --format='{{.State.Health.Status}}' sportscenter-backend 2>/dev/null | grep -q "healthy"; then
        echo "Backend is healthy!"
        break
    fi
    if [ $i -eq $RETRIES ]; then
        echo "ERROR: Backend failed to become healthy"
        docker logs sportscenter-backend --tail=50
        exit 1
    fi
    echo "Waiting for backend... attempt $i/$RETRIES"
    sleep 10
done

# Wait for frontend to start (it depends on backend being healthy)
echo "Waiting for frontend to start..."
sleep 5
RETRIES=12
for i in $(seq 1 $RETRIES); do
    STATUS=$(docker inspect --format='{{.State.Status}}' sportscenter-frontend 2>/dev/null || echo "not found")
    if [ "$STATUS" = "running" ]; then
        echo "Frontend is running!"
        break
    fi
    if [ $i -eq $RETRIES ]; then
        echo "ERROR: Frontend failed to start"
        docker logs sportscenter-frontend --tail=50
        exit 1
    fi
    echo "Waiting for frontend... attempt $i/$RETRIES (status: $STATUS)"
    sleep 5
done

# Show running containers
echo "=== Deployment complete ==="
docker-compose ps
EOF

                            scp -o StrictHostKeyChecking=no /tmp/deploy.sh ${EC2_USER}@${ec2Host}:~/deploy.sh
                            ssh -o StrictHostKeyChecking=no ${EC2_USER}@${ec2Host} "chmod +x ~/deploy.sh && ~/deploy.sh"
                        """
                    }
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Checking EC2 service health status...'
                script {
                    def ec2Host = sh(
                        script: "aws ec2 describe-instances --instance-ids ${EC2_INSTANCE_ID} --region ${AWS_REGION} --query 'Reservations[0].Instances[0].PublicIpAddress' --output text",
                        returnStdout: true
                    ).trim()
                    
                    def maxRetries = 25
                    def retryCount = 0
                    def backendHealthy = false
                    def frontendHealthy = false
                    
                    echo 'Waiting for backend to be ready...'
                    while (retryCount < maxRetries && !backendHealthy) {
                        try {
                            sh "curl -f http://${ec2Host}:8081/api/products?PageSize=1"
                            backendHealthy = true
                            echo 'Backend health check passed on EC2'
                        } catch (Exception e) {
                            retryCount++
                            echo "Backend health check failed, retrying ${retryCount}/${maxRetries}..."
                            sleep(20)
                        }
                    }
                    
                    if (!backendHealthy) {
                        error("Backend health check failed on EC2 after ${maxRetries} attempts")
                    }
                    
                    echo 'Waiting for frontend to be ready...'
                    retryCount = 0
                    while (retryCount < maxRetries && !frontendHealthy) {
                        try {
                            sh "curl -f http://${ec2Host}/"
                            frontendHealthy = true
                            echo 'Frontend health check passed on EC2'
                        } catch (Exception e) {
                            retryCount++
                            echo "Frontend health check failed, retrying ${retryCount}/${maxRetries}..."
                            sleep(15)
                        }
                    }
                    
                    if (!frontendHealthy) {
                        error("Frontend health check failed on EC2 after ${maxRetries} attempts")
                    }
                }
            }
        }
        
        stage('Deployment Report') {
            steps {
                script {
                    def ec2Host = sh(
                        script: "aws ec2 describe-instances --instance-ids ${EC2_INSTANCE_ID} --region ${AWS_REGION} --query 'Reservations[0].Instances[0].PublicIpAddress' --output text",
                        returnStdout: true
                    ).trim()
                    
                    sh """
                        echo "=========================================="
                        echo "Deployment to EC2 Successful!"
                        echo "=========================================="
                        echo "Frontend URL: http://${ec2Host}"
                        echo "Backend API:  http://${ec2Host}:8081/api"
                        echo "=========================================="
                        echo "Build Number: ${BUILD_NUMBER}"
                        echo "ECR Images:"
                        echo "  - ${ECR_REGISTRY}/${ECR_REPO_BACKEND}:${BUILD_NUMBER}"
                        echo "  - ${ECR_REGISTRY}/${ECR_REPO_FRONTEND}:${BUILD_NUMBER}"
                        echo "=========================================="
                    """
                    
                    sshagent(['ec2-ssh-key']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${EC2_USER}@${ec2Host} "echo '' && echo 'Running containers on EC2:' && docker ps --format 'table {{.Names}}\t{{.Status}}\t{{.Ports}}' && echo '' && echo 'Disk usage:' && df -h / | tail -1"
                        """
                    }
                }
            }
        }
    }
    
    post {
        success {
            echo 'CI/CD pipeline executed successfully!'
            script {
                def ec2Host = sh(
                    script: "aws ec2 describe-instances --instance-ids ${EC2_INSTANCE_ID} --region ${AWS_REGION} --query 'Reservations[0].Instances[0].PublicIpAddress' --output text",
                    returnStdout: true
                ).trim()
                echo "Application deployed to: http://${ec2Host}"
            }
        }
        failure {
            echo 'CI/CD pipeline execution failed!'
            script {
                try {
                    def ec2Host = sh(
                        script: "aws ec2 describe-instances --instance-ids ${EC2_INSTANCE_ID} --region ${AWS_REGION} --query 'Reservations[0].Instances[0].PublicIpAddress' --output text",
                        returnStdout: true
                    ).trim()
                    
                    echo "Checking EC2 container logs..."
                    sshagent(['ec2-ssh-key']) {
                        sh """
                            ssh -o StrictHostKeyChecking=no ${EC2_USER}@${ec2Host} "echo '==========================================' && echo 'Container logs from EC2:' && echo '==========================================' && docker-compose logs --tail=50 || echo 'Failed to get container logs'"
                        """
                    }
                } catch (Exception e) {
                    echo "Could not retrieve EC2 container logs: ${e.message}"
                }
            }
        }
        always {
            echo 'Pipeline execution completed.'
        }
    }
}
