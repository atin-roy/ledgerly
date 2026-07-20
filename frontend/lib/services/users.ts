import { apiRequest, getUserIdFromToken } from "../apiClient";

export interface PasswordChangeRequest {
  currentPassword: string;
  newPassword: string;
}

export async function changePassword(
  data: PasswordChangeRequest,
): Promise<void> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<void>(`/users/${userId}/password`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function deleteAccount(currentPassword: string): Promise<void> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<void>(`/users/${userId}/account-deletion`, {
    method: "POST",
    body: JSON.stringify({ currentPassword }),
  });
}
