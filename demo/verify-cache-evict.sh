#!/usr/bin/env bash
set -euo pipefail

KEY='menuCache::1'
printf '\n1. GET để nạp cache\n'
curl -sS http://localhost:8080/menu/1
printf '\nRedis trước update:\n'
redis-cli GET "$KEY"

printf '\n2. UPDATE giá thành 75000\n'
curl -sS -X PUT http://localhost:8080/menu \
  -H 'Content-Type: application/json' \
  -d '{"id":1,"dish_name":"Phở Bò Đặc Biệt","price":75000}'
printf '\nRedis ngay sau update (phải là nil):\n'
redis-cli GET "$KEY"

printf '\n3. GET lại để lấy giá mới từ DB và nạp cache\n'
curl -sS http://localhost:8080/menu/1
printf '\nRedis sau GET mới:\n'
redis-cli GET "$KEY"
