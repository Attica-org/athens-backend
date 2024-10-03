#!/bin/bash
source /usr/share/bg/mnt.sh
WAS_NAME="athens"
COMPOSE_BIN="/usr/local/bin/docker-compose"
MAX_RETRIES=10

# 네트워크 생성 (없으면 생성, 있으면 무시)
docker network create ubuntu_app-network || true

check_execute_was() {
  docker ps --filter "name=$WAS_NAME-${NEW_COLOR}" --format "{{.Status}}" | grep -q "^Up"
}

check_api_status() {
  local inspect=$(docker inspect $WAS_NAME-${NEW_COLOR} --format '{{range .NetworkSettings.Networks}}{{.IPAddress}}{{end}}')
  local endpoint="http://$inspect:8080/api/v1/open/health-check"
  curl -s -o /dev/null -w "%{http_code}" "$endpoint" | grep -q '^200'
}

CURRENT_COLOR=$(docker ps --filter "name=$WAS_NAME" --format "{{.Names}}" | cut -d'-' -f2)

if [ -z "$CURRENT_COLOR" ]; then
  NEW_COLOR="blue"
else
  NEW_COLOR=$( [ "$CURRENT_COLOR" = "blue" ] && echo "green" || echo "blue" )
fi
# 기존 컨테이너 정리
$COMPOSE_BIN down --remove-orphans

# 새 버전 배포
$COMPOSE_BIN -f docker-compose.yml -f "docker-compose.${NEW_COLOR}.yml" pull && \
$COMPOSE_BIN -f docker-compose.yml -f "docker-compose.${NEW_COLOR}.yml" up -d

attempts=0
while [ $attempts -lt $MAX_RETRIES ]; do
  if check_execute_was; then
	  sleep 2
    if check_api_status; then
      break
    fi
  fi

  attempts=$((attempts + 1))
  sleep 2
done

if [ $attempts -ge $MAX_RETRIES ]; then
  $COMPOSE_BIN -f docker-compose.yml -f "docker-compose.${NEW_COLOR}.yml" down
  exit 1
fi

if [ "$NEW_COLOR" = "blue" ]; then
  echo "mb"
  mntbg
else
  echo "mg"
  mntdg
fi

mntms

# 이전 버전 컨테이너 제거
if [ -n "$CURRENT_COLOR" ]; then
  $COMPOSE_BIN -f docker-compose.yml -f "docker-compose.${CURRENT_COLOR}.yml" down
fi

echo "Deployment completed successfully."
