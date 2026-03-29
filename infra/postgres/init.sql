-- TradeSphere — PostgreSQL initialization
-- Creates one database per service. Runs on first container boot.

CREATE DATABASE tradesphere_identity;
CREATE DATABASE tradesphere_account;
CREATE DATABASE tradesphere_market;
CREATE DATABASE tradesphere_order;
CREATE DATABASE tradesphere_portfolio;

GRANT ALL PRIVILEGES ON DATABASE tradesphere_identity  TO tradesphere;
GRANT ALL PRIVILEGES ON DATABASE tradesphere_account   TO tradesphere;
GRANT ALL PRIVILEGES ON DATABASE tradesphere_market    TO tradesphere;
GRANT ALL PRIVILEGES ON DATABASE tradesphere_order     TO tradesphere;
GRANT ALL PRIVILEGES ON DATABASE tradesphere_portfolio TO tradesphere;
