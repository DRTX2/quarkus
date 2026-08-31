-- =====================================================
-- V2: Tabla de experimentos de ingeniería del caos y AIOps
-- =====================================================

CREATE TABLE chaos_experiments (
    id VARCHAR(36) PRIMARY KEY,
    target_namespace VARCHAR(100) NOT NULL,
    target_deployment VARCHAR(100) NOT NULL,
    chaos_type VARCHAR(50) NOT NULL,
    status VARCHAR(50) NOT NULL,
    executed_by VARCHAR(100) NOT NULL,
    execution_logs TEXT,
    resilience_report TEXT,
    resilience_score INT,
    recovered_successfully BOOLEAN,
    recovery_time_ms BIGINT,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    completed_at TIMESTAMP
);

CREATE INDEX idx_chaos_experiments_namespace ON chaos_experiments(target_namespace);
CREATE INDEX idx_chaos_experiments_deployment ON chaos_experiments(target_deployment);
CREATE INDEX idx_chaos_experiments_type ON chaos_experiments(chaos_type);
CREATE INDEX idx_chaos_experiments_status ON chaos_experiments(status);
CREATE INDEX idx_chaos_experiments_created_at ON chaos_experiments(created_at);
