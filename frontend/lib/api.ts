const DEFAULT_API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "http://localhost:8080/api";

const normalizeBaseUrl = (url: string) => url.replace(/\/+$/, "");

const API_BASE_URL = normalizeBaseUrl(DEFAULT_API_BASE_URL);

export const AUTH_LOGIN_URL = `${API_BASE_URL}/auth/login`;
export const AUTH_REGISTER_URL = `${API_BASE_URL}/auth/register`;
