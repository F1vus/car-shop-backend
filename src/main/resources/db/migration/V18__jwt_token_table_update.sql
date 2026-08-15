BEGIN;

DELETE FROM public.jwt_tokens jt WHERE jt.token IS NOT NULL;

ALTER TABLE public.jwt_tokens
    RENAME COLUMN token TO jti;

ALTER TABLE public.jwt_tokens
    ADD CONSTRAINT jwt_tokens_jti_unique UNIQUE (jti);

COMMIT;