pipeline {
    agent any
    
    environment {
        DOCKER_COMPOSE_FILE = 'docker/docker-compose.yml'
        AWS_REGION = 'eu-west-1'
        ECR_REGISTRY = '614441038924.dkr.ecr.eu-west-1.amazonaws.com'
        ECR_REPO_BACKEND = 'sportscenter-backend'
        ECR_REPO_FRONTEND = 'sportscenter-frontend'
        EC2_HOST = '34.240.77.92'
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
                withCredentials([[
                    $class: 'AmazonWebServicesCredentialsBinding',
                    credentialsId: 'aws-credentials'
                ]]) {
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
                sshagent(['ec2-ssh-key']) {
                    sh '''
                        # Upload docker-compose file and data
                        scp -o StrictHostKeyChecking=no \
                            docker/docker-compose.ec2.yml \
                            ${EC2_USER}@${EC2_HOST}:~/docker-compose.yml
                        
                        scp -o StrictHostKeyChecking=no \
                            docker/data.sql \
                            ${EC2_USER}@${EC2_HOST}:~/data.sql
                        
                        # Deploy on EC2
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << 'ENDSSH'
                            # Login to ECR
                            aws ecr get-login-password --region eu-west-1 | \
                                docker login --username AWS --password-stdin 614441038924.dkr.ecr.eu-west-1.amazonaws.com
                            
                            # Pull latest images
                            docker pull 614441038924.dkr.ecr.eu-west-1.amazonaws.com/sportscenter-backend:latest
                            docker pull 614441038924.dkr.ecr.eu-west-1.amazonaws.com/sportscenter-frontend:latest
                            
                            # Stop old containers
                            docker-compose down || true
                            
                            # Clean up old images
                            docker image prune -f
                            
                            # Start new containers
                            docker-compose up -d
                            
                            # Show running containers
                            docker-compose ps
ENDSSH
                    '''
                }
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Checking EC2 service health status...'
                script {
                    def maxRetries = 15
                    def retryCount = 0
                    def backendHealthy = false
                    def frontendHealthy = false
                    
                    // Check backend on EC2
                    while (retryCount < maxRetries && !backendHealthy) {
                        try {
                            sh "curl -f http://${EC2_HOST}:8081/api/products?PageSize=1"
                            backendHealthy = true
                            echo 'Backend health check passed on EC2'
                        } catch (Exception e) {
                            retryCount++
                            echo "Backend health check failed, retrying ${retryCount}/${maxRetries}..."
                            sleep(15)
                        }
                    }
                    
                    if (!backendHealthy) {
                        error("Backend health check failed on EC2")
                    }
                    
                    // Check frontend on EC2
                    retryCount = 0
                    while (retryCount < maxRetries && !frontendHealthy) {
                        try {
                            sh "curl -f http://${EC2_HOST}/"
                            frontendHealthy = true
                            echo 'Frontend health check passed on EC2'
                        } catch (Exception e) {
                            retryCount++
                            echo "Frontend health check failed, retrying ${retryCount}/${maxRetries}..."
                            sleep(10)
                        }
                    }
                    
                    if (!frontendHealthy) {
                        error("Frontend health check failed on EC2")
                    }
                }
            }
        }
        
        stage('Deployment Report') {
            steps {
                sh """
                    echo "=========================================="
                    echo "Deployment to EC2 Successful!"
                    echo "=========================================="
                    echo "Frontend URL: http://${EC2_HOST}"
                    echo "Backend API:  http://${EC2_HOST}:8081/api"
                    echo "=========================================="
                    echo "Build Number: ${BUILD_NUMBER}"
                    echo "ECR Images:"
                    echo "  - ${ECR_REGISTRY}/${ECR_REPO_BACKEND}:${BUILD_NUMBER}"
                    echo "  - ${ECR_REGISTRY}/${ECR_REPO_FRONTEND}:${BUILD_NUMBER}"
                    echo "=========================================="
                """
                
                sshagent(['ec2-ssh-key']) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << 'ENDSSH'
                            echo ""
                            echo "Running containers on EC2:"
                            docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
                            echo ""
                            echo "Disk usage:"
                            df -h / | tail -1
ENDSSH
                    '''
                }
            }
        }
    }
    
    post {
        success {
            echo 'CI/CD pipeline executed successfully!'
            echo "Application deployed to: http://${EC2_HOST}"
        }
        failure {
            echo 'CI/CD pipeline execution failed!'
            script {
                try {
                    echo "Checking EC2 container logs..."
                    sshagent(['ec2-ssh-key']) {
                        sh '''
                            ssh -o StrictHostKeyChecking=no ${EC2_USER}@${EC2_HOST} << 'ENDSSH'
                                echo "=========================================="
                                echo "Container logs from EC2:"
                                echo "=========================================="
                                docker-compose logs --tail=100 || echo "Failed to get container logs"
ENDSSH
                        '''
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
