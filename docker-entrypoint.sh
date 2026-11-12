#!/bin/bash
set -euo pipefail

if [[ "${WAIT_FOR_SQL:-true}" == "true" ]]; then
  host="${SQLSERVER_HOST:-sqlserver}"
  port="${SQLSERVER_PORT:-1433}"
  echo "Waiting for SQL Server at ${host}:${port}"
  for _ in $(seq 1 90); do
    if (echo > "/dev/tcp/${host}/${port}") >/dev/null 2>&1; then
      echo "SQL Server port is open"
      break
    fi
    sleep 2
  done
fi

exec java -jar /app/app.jar
