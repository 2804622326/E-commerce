pipeline {
    agent any
    
    tools {
        maven 'Maven-3.9.0'
        nodejs 'NodeJS-18.17.0'
    }
    
    environment {
        APP_DIR = '/home/ec2-user/ecommerce/E-commerce'
        DOCKER_COMPOSE_FILE = 'docker/docker-compose.yml'
    }
    
    stages {
        stage('Checkout') {
            steps {
                echo 'Pulling latest code from repository...'
                git branch: 'main', 
                    credentialsId: 'github-credentials',
                    url: 'https://github.com/2804622326/E-commerce.git'
            }
        }
        
        stage('Build Backend') {
            steps {
                echo 'Building Spring Boot backend...'
                sh '''
                    chmod +x ./mvnw
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
        
        stage('Build Docker Images') {
            steps {
                echo 'Building Docker images...'
                sh '''
                    # Load environment variables
                    export $(cat ${APP_DIR}/.env | xargs)
                    
                    # Build Docker images
                    docker-compose -f ${DOCKER_COMPOSE_FILE} build --no-cache
                '''
            }
        }
        
        stage('Deploy') {
            steps {
                echo 'Stopping old containers and starting new ones...'
                sh '''
                    cd ${APP_DIR}
                    
                    # Load environment variables
                    export $(cat .env | xargs)
                    
                    # Stop and remove old containers
                    docker-compose -f ${DOCKER_COMPOSE_FILE} down || true
                    
                    # Start new containers
                    docker-compose -f ${DOCKER_COMPOSE_FILE} up -d
                    
                    # Clean up unused images
                    docker image prune -f
                '''
            }
        }
        
        stage('Health Check') {
            steps {
                echo 'Checking service health status...'
                script {
                    def maxRetries = 10
                    def retryCount = 0
                    def backendHealthy = false
                    def frontendHealthy = false
                    
                    // Check backend
                    while (retryCount < maxRetries && !backendHealthy) {
                        try {
                            sh 'curl -f http://localhost:8081/api/products?PageSize=1'
                            backendHealthy = true
                            echo 'Backend health check passed'
                        } catch (Exception e) {
                            retryCount++
                            echo "Backend health check failed, retrying ${retryCount}/${maxRetries}..."
                            sleep(10)
                        }
                    }
                    
                    if (!backendHealthy) {
                        error("Backend health check failed")
                    }
                    
                    // Check frontend
                    retryCount = 0
                    while (retryCount < maxRetries && !frontendHealthy) {
                        try {
                            sh 'curl -f http://localhost:80/'
                            frontendHealthy = true
                            echo 'Frontend health check passed'
                        } catch (Exception e) {
                            retryCount++
                            echo "Frontend health check failed, retrying ${retryCount}/${maxRetries}..."
                            sleep(5)
                        }
                    }
                    
                    if (!frontendHealthy) {
                        error("Frontend health check failed")
                    }
                }
            }
        }
        
        stage('Deployment Report') {
            steps {
                sh '''
                    echo "=========================================="
                    echo "Deployment Successful!"
                    echo "=========================================="
                    echo "Frontend URL: http://$(curl -s ifconfig.me)"
                    echo "Backend API: http://$(curl -s ifconfig.me):8081/api"
                    echo "Jenkins: http://$(curl -s ifconfig.me):8080"
                    echo "=========================================="
                    echo ""
                    echo "Running containers:"
                    docker ps --format "table {{.Names}}\t{{.Status}}\t{{.Ports}}"
                    echo ""
                    echo "Disk usage:"
                    df -h / | tail -1
                '''
            }
        }
    }
    
    post {
        success {
            echo 'CI/CD pipeline executed successfully!'
        }
        failure {
            echo 'CI/CD pipeline execution failed!'
            sh '''
                echo "=========================================="
                echo "Deployment failed, checking container logs:"
                echo "=========================================="
                docker-compose -f ${DOCKER_COMPOSE_FILE} logs --tail=50
            '''
        }
        always {
            echo 'Cleaning up workspace...'
            cleanWs()
        }
    }
}
