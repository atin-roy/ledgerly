import { getAccessToken, clearAuthTokens } from "./auth";

const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL ??
  "https://ledgerly-production-4b76.up.railway.app/api";

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
      throw new ApiError(401, "No authentication token found");
    }
    requestHeaders["Authorization"] = `Bearer ${token}`;
  }

  const url = `${API_BASE_URL}${endpoint}`;
  
  console.log(`API Request: ${fetchOptions.method || 'GET'} ${url}`);

  try {
    const response = await fetch(url, {
      ...fetchOptions,
      headers: requestHeaders,
    });

    // Handle 401 Unauthorized - clear tokens and redirect to login
    if (response.status === 401) {
      clearAuthTokens();
      if (typeof window !== "undefined") {
        window.location.href = "/login";
      }
      throw new ApiError(401, "Unauthorized - please login again");
    }

    // Handle other error responses
    if (!response.ok) {
      const errorData = await response.json().catch(() => ({}));
      console.error(`API Error: ${response.status}`, errorData);
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
    console.log(`API Response:`, data);
    return data;
  } catch (error) {
    console.error("API Request Failed:", error);
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
    const user = JSON.parse(userInfo);
    return user.userId;
  } catch {
    return null;
  }
}
