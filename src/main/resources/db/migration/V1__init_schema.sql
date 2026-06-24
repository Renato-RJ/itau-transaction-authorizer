-- V0: Initialize database schema and set search path
-- Ensure public schema exists (it should exist by default in PostgreSQL)
CREATE SCHEMA IF NOT EXISTS public;

-- Set default search path to public
SET search_path TO public;

-- Ensure we have the necessary extensions
CREATE EXTENSION IF NOT EXISTS "uuid-ossp";

