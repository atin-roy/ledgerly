import { getAccessToken, clearAuthTokens } from "./auth";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "http://localhost:8080/api";

export class ApiError extends Error {
  constructor(
    public status: number,
    message: string,
    public data?: any
  ) {
    super(message);
    this.name = "ApiError";
  }
}

interface RequestOptions extends RequestInit {
  requiresAuth?: boolean;
}

export async function apiRequest<T>(
  endpoint: string,
  options: RequestOptions = {}
): Promise<T> {
  // Only run on client side
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
      console.error("❌ No authentication token found in localStorage");
      clearAuthTokens();
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
      throw new ApiError(401, "No authentication token found - please login");
    }
    requestHeaders["Authorization"] = `Bearer ${token}`;
  }

  const url = `${API_BASE_URL}${endpoint}`;
  
  console.log(`🔗 API Request: ${fetchOptions.method || 'GET'} ${url}`, {
    hasAuth: !!requestHeaders["Authorization"],
  });

  try {
    const response = await fetch(url, {
      ...fetchOptions,
      headers: requestHeaders,
    });

    // Handle 401 Unauthorized or 403 Forbidden - both indicate auth failure
    // Spring Security returns 403 for unauthenticated requests (expired/invalid token)
    if (response.status === 401 || response.status === 403) {
      const errorData = await response.json().catch(() => ({}));
      console.error(`❌ API returned ${response.status} - clearing tokens and redirecting to login`, errorData);
      clearAuthTokens();
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
      throw new ApiError(response.status, "Session expired - please login again");
    }

    // Handle other error responses
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      console.error(`❌ API Error: ${response.status}`, errorData);
      throw new ApiError(
        response.status,
        errorData.message || `HTTP error ${response.status}`,
        errorData
      );
    }

    // Handle 204 No Content
    if (response.status === 204) {
      return null as T;
    }

    const data = await response.json();
    console.log(`✅ API Response:`, data);
    return data;
  } catch (error) {
    console.error("❌ API Request Failed:", error);
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
    console.warn("⚠️ No user info found in localStorage");
    return null;
  }

  try {
    const user = JSON.parse(userInfo);
    if (!user.userId) {
      console.warn("⚠️ No userId in stored user info:", user);
      return null;
    }
    return user.userId;
  } catch (err) {
    console.error("❌ Error parsing user info from localStorage:", err);
    return null;
  }
}
