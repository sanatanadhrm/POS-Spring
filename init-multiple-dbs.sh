#!/bin/bash
set -e

# Otomatis membuat database ketiga microservice saat pertama kali Postgres dibuat
psql -v ON_ERROR_STOP=1 --username "$POSTGRES_USER" <<-EOSQL
    CREATE DATABASE cashier_auth;
    CREATE DATABASE cashier_product;
    CREATE DATABASE cashier_order;
EOSQL