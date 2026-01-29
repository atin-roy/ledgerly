// Debug utilities for troubleshooting API issues

export function debugAuth() {
  if (typeof window === "undefined") {
    console.log("Running on server - no auth available");
    return;
  }

  const accessToken = localStorage.getItem("ledgerly_access_token");
  const refreshToken = localStorage.getItem("ledgerly_refresh_token");
  const userInfo = localStorage.getItem("ledgerly_user");

  console.group("🔍 Auth Debug Info");
  console.log("Access Token:", accessToken ? `${accessToken.substring(0, 20)}...` : "❌ Not found");
  console.log("Refresh Token:", refreshToken ? `${refreshToken.substring(0, 20)}...` : "❌ Not found");
  console.log("User Info:", userInfo ? JSON.parse(userInfo) : "❌ Not found");
  console.groupEnd();

  if (!accessToken) {
    console.error("⚠️ No access token found! User needs to login.");
    return false;
  }

  return true;
}

export function debugApiUrl() {
  const apiUrl = process.env.NEXT_PUBLIC_API_BASE_URL ?? "https://ledgerly-production-4b76.up.railway.app/api";
  console.group("🌐 API Configuration");
  console.log("API Base URL:", apiUrl);
  console.log("Environment:", process.env.NODE_ENV);
  console.groupEnd();
}

export function testApiConnection() {
  const apiUrl = process.env.NEXT_PUBLIC_API_BASE_URL ?? "https://ledgerly-production-4b76.up.railway.app/api";
  
  console.log("🧪 Testing API connection...");
  
  // Test health endpoint (no auth required)
  fetch(`${apiUrl.replace('/api', '')}/actuator/health`)
    .then(res => res.json())
    .then(data => {
      console.log("✅ Backend is reachable:", data);
    })
    .catch(err => {
      console.error("❌ Backend is NOT reachable:", err.message);
    });
}
