#!/bin/bash

# 색상 정의
GREEN='\033[0;32m'
BLUE='\033[0;34m'
RED='\033[0;31m'
NC='\033[0m' # No Color

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  GreenMap VM Deployment Script${NC}"
echo -e "${BLUE}========================================${NC}"

# .env 파일 존재 확인
if [ ! -f .env ]; then
    echo -e "${RED}Error: .env file not found!${NC}"
    echo -e "${RED}Please create .env file with required environment variables${NC}"
    exit 1
fi

# 최신 코드 가져오기
echo -e "\n${GREEN}1. Pulling latest code from GitHub...${NC}"
git fetch origin dev
git reset --hard origin/dev

# 기존 컨테이너 중지 및 제거
echo -e "\n${GREEN}2. Stopping and removing existing container...${NC}"
docker stop greenmap 2>/dev/null && docker rm greenmap 2>/dev/null

# 새 이미지 빌드
echo -e "\n${GREEN}3. Building new Docker image...${NC}"
# BuildKit 비활성화 (buildx 컴포넌트 없이 빌드)
export DOCKER_BUILDKIT=0
docker build -t greenmap-app .

# 환경변수 파일로 컨테이너 실행
echo -e "\n${GREEN}4. Starting new container with .env file...${NC}"
docker run -d \
  --name greenmap \
  --network host \
  --restart unless-stopped \
  --env-file .env \
  greenmap-app

# 로그 확인
echo -e "\n${GREEN}5. Showing recent container logs...${NC}"
sleep 3
docker logs --tail 70 greenmap || true

echo -e "\n${GREEN}Deployment finished successfully.${NC}"