#!/bin/bash

# 스크립트 불러오기
# mntdg(): 그린 버전의 Caddy 설정을 적용
# mntbg(): 블루 버전의 Caddy 설정을 적용
# mntms(): Caddy 서비스를 재시작
source /usr/share/bg/mnt.sh

# 환경 변수 설정하기
WAS_NAME="athens"  # WAS 이름
COMPOSE_BIN="/usr/local/bin/docker-compose"  # docker-compose 실행 파일 경로
MAX_RETRIES=10  # 최대 재시도 횟수
PROJECT_NAME="athens"

# 네트워크 생성 (없으면 생성, 있으면 무시)
if ! docker network inspect ubuntu_app-network >/dev/null 2>&1; then
     docker network create ubuntu_app-network
fi

# 함수 정의하기
# 새로 배포된 WAS가 실행 중인지 확인하는 함수
check_execute_was() {
  docker ps --filter "name=$WAS_NAME-${NEW_COLOR}" --format "{{.Status}}" | grep -q "^Up"
}

# API 상태를 확인하는 함수
check_api_status() {
  local inspect=$(docker inspect $WAS_NAME-${NEW_COLOR} --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}')
  local endpoint="http://$inspect:8080/api/v1/open/health-check"
  curl -s -o /dev/null -w "%{http_code}" "$endpoint" | grep -q '^200'
}

# 현재 실행 중인 WAS의 색상을 확인하기
CURRENT_COLOR=$(docker ps --filter "name=$WAS_NAME" --format "{{.Names}}" | cut -d'-' -f2)

# 새로 배포할 색상 결정하기
if [ -z "$CURRENT_COLOR" ]; then
  NEW_COLOR="blue"
else
  NEW_COLOR=$( [ "$CURRENT_COLOR" = "blue" ] && echo "green" || echo "blue" )
fi

# 기존 컨테이너 중지 및 제거
echo "Stopping and removing existing containers..."
$COMPOSE_BIN -p $PROJECT_NAME down --remove-orphans
docker rm -f athens-redis || true

# 새 버전 배포하기
echo "Deploying new version: $NEW_COLOR"
$COMPOSE_BIN -p $PROJECT_NAME -f docker-compose.yml -f "docker-compose.${NEW_COLOR}.yml" pull
$COMPOSE_BIN -p $PROJECT_NAME -f docker-compose.yml -f "docker-compose.${NEW_COLOR}.yml" up -d

# 새 버전 상태 확인하기 (최대 10번 재시도)
attempts=0
while [ $attempts -lt $MAX_RETRIES ]; do
  if check_execute_was; then
    sleep 2
    if check_api_status; then
      echo "New version is up and healthy"
      break
    fi
  fi

  attempts=$((attempts + 1))
  sleep 2
done

# 배포 실패 시 롤백하기
if [ $attempts -ge $MAX_RETRIES ]; then
  echo "Failed to deploy new version"
  exit 1
fi

# Caddy 설정 변경 & 서비스 재시작
if [ "$NEW_COLOR" = "blue" ]; then
  echo "mb"
  mntbg  # 블루 버전의 Caddy 설정 적용
else
  echo "mg"
  mntdg # 그린 버전의 Caddy 설정 적용
fi

mntms # Caddy 서비스를 재시작하여 새 설정 적용하기

# 이전 버전 컨테이너 정리하기
if [ -n "$CURRENT_COLOR" ]; then
  echo "Removing old version: $CURRENT_COLOR"
  $COMPOSE_BIN -p $PROJECT_NAME -f docker-compose.yml -f "docker-compose.${CURRENT_COLOR}.yml" down
fi

echo "Deployment completed successfully."
