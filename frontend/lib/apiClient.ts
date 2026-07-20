import { getAccessToken, getRefreshToken, clearAuthTokens, persistAuthTokens, type AuthResponse } from "./auth";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ?? "http://localhost:8080/api";

export { API_BASE_URL };

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public data?: unknown
  ) {
    super(message);
    this.name = "ApiError";
  }
}


/** Server validation details, flattened to one human sentence. */
export function extractApiError(err: unknown): string {
  if (err && typeof err === "object" && "data" in err) {
    const data = (err as { data?: { details?: unknown; message?: string } }).data;
    const details = data?.details;
    if (Array.isArray(details) && details.length > 0) {
      const first = details[0] as { message?: string };
      if (first?.message) return first.message;
    } else if (details && typeof details === "object") {
      const first = Object.values(details as Record<string, string>)[0];
      if (typeof first === "string") return first;
    }
    if (data?.message) return data.message;
  }
  return err instanceof Error && err.message
    ? err.message
    : "Something went wrong. Try again.";
}

interface RequestOptions extends RequestInit {
  requiresAuth?: boolean;
}

function redirectToLogin() {
  clearAuthTokens();
  if (typeof window !== "undefined") {
    window.location.href = "/login";
  }
}

async function tryRefreshToken(): Promise<string | null> {
  const refreshToken = getRefreshToken();
  if (!refreshToken) return null;

  try {
    const response = await fetch(`${API_BASE_URL}/auth/refresh`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ refreshToken }),
    });

    if (!response.ok) return null;

    const data = await response.json() as AuthResponse;
    persistAuthTokens(data);
    return data.accessToken;
  } catch {
    return null;
  }
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestOptions = {}
): Promise<T> {
  if (typeof window === "undefined") {
    throw new ApiError(0, "API calls can only be made from the browser");
  }

  const { requiresAuth = true, headers = {}, ...fetchOptions } = options;

  const requestHeaders: Record<string, string> = {
    "Content-Type": "application/json",
    ...(headers as Record<string, string>),
  };

  if (requiresAuth) {
    const token = getAccessToken();
    if (!token) {
      redirectToLogin();
      throw new ApiError(401, "No authentication token found - please login");
    }
    requestHeaders["Authorization"] = `Bearer ${token}`;
  }

  const url = `${API_BASE_URL}${endpoint}`;

  try {
    const response = await fetch(url, {
      ...fetchOptions,
      headers: requestHeaders,
    });

    if (response.status === 401 && requiresAuth) {
      const newToken = await tryRefreshToken();
      if (newToken) {
        const retryResponse = await fetch(url, {
          ...fetchOptions,
          headers: { ...requestHeaders, Authorization: `Bearer ${newToken}` },
        });

        if (retryResponse.status === 401) {
          redirectToLogin();
          throw new ApiError(401, "Session expired - please login again");
        }

        if (!retryResponse.ok) {
          const errorData = await retryResponse.json().catch(() => ({}));
          throw new ApiError(
            retryResponse.status,
            (errorData as { message?: string }).message || `HTTP error ${retryResponse.status}`,
            errorData
          );
        }

        if (retryResponse.status === 204) return null as T;
        return await retryResponse.json();
      }

      redirectToLogin();
      throw new ApiError(401, "Session expired - please login again");
    }

    if (response.status === 403) {
      redirectToLogin();
      throw new ApiError(403, "Access denied");
    }

    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      throw new ApiError(
        response.status,
        (errorData as { message?: string }).message || `HTTP error ${response.status}`,
        errorData
      );
    }

    if (response.status === 204) {
      return null as T;
    }

    return await response.json();
  } catch (error) {
    if (error instanceof ApiError) {
      throw error;
    }
    throw new ApiError(0, error instanceof Error ? error.message : "Network error");
  }
}

export function getUserIdFromToken(): number | null {
  if (typeof window === "undefined") {
    return null;
  }

  const userInfo = localStorage.getItem("ledgerly_user");
  if (!userInfo) {
    return null;
  }

  try {
    const user = JSON.parse(userInfo) as { userId?: number };
    return user.userId ?? null;
  } catch {
    return null;
  }
}
