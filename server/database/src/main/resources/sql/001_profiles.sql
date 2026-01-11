BEGIN;

-- 1) create enum account_status if not exists
DO $$
BEGIN
  IF NOT EXISTS (SELECT 1 FROM pg_type WHERE typname = 'account_status') THEN
    CREATE TYPE public.account_status AS ENUM ('invited', 'active', 'revoked');
  END IF;
END $$;

-- 2) rename users -> profiles (idempotent)
DO $$
BEGIN
  IF to_regclass('public.profiles') IS NULL AND to_regclass('public.users') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE public.users RENAME TO profiles';
  END IF;
END $$;

-- 3) add status column to profiles
DO $$
BEGIN
  IF to_regclass('public.profiles') IS NOT NULL THEN
    EXECUTE 'ALTER TABLE public.profiles ADD COLUMN IF NOT EXISTS status public.account_status NOT NULL DEFAULT ''active''';
  ELSE
    RAISE EXCEPTION 'Table public.profiles not found (and public.users not found).';
  END IF;
END $$;

-- 4) backfill status from is_active (optional, but useful)
UPDATE public.profiles
SET status = CASE
  WHEN is_active = TRUE THEN 'active'::public.account_status
  ELSE 'revoked'::public.account_status
END
WHERE status IS NULL OR status <> CASE
  WHEN is_active = TRUE THEN 'active'::public.account_status
  ELSE 'revoked'::public.account_status
END;

COMMIT;
