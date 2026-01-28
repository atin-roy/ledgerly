export interface AuthResponse {
  accessToken: string;
  refreshToken: string;
  userId: number;
  email: string;
  username: string;
  role: string;
}

const ACCESS_TOKEN_KEY = "ledgerly_access_token";
const REFRESH_TOKEN_KEY = "ledgerly_refresh_token";
const USER_INFO_KEY = "ledgerly_user";

const hasWindow = () => typeof window !== "undefined";

export function persistAuthTokens(auth: AuthResponse) {
  if (!hasWindow()) {
    return;
  }

  localStorage.setItem(ACCESS_TOKEN_KEY, auth.accessToken);
  localStorage.setItem(REFRESH_TOKEN_KEY, auth.refreshToken);
  localStorage.setItem(
    USER_INFO_KEY,
    JSON.stringify({
      userId: auth.userId,
      email: auth.email,
      username: auth.username,
      role: auth.role,
    }),
  );
}

export function getAccessToken(): string | null {
  if (!hasWindow()) {
    return null;
  }

  return localStorage.getItem(ACCESS_TOKEN_KEY);
}

export function getRefreshToken(): string | null {
  if (!hasWindow()) {
    return null;
  }

  return localStorage.getItem(REFRESH_TOKEN_KEY);
}

export function getStoredUser() {
  if (!hasWindow()) {
    return null;
  }

  const raw = localStorage.getItem(USER_INFO_KEY);
  if (!raw) {
    return null;
  }

  try {
    return JSON.parse(raw);
  } catch {
    return null;
  }
}

export function isAuthenticated() {
  return Boolean(getAccessToken());
}

export function clearAuthTokens() {
  if (!hasWindow()) {
    return;
  }

  localStorage.removeItem(ACCESS_TOKEN_KEY);
  localStorage.removeItem(REFRESH_TOKEN_KEY);
  localStorage.removeItem(USER_INFO_KEY);
}
