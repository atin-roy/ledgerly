import { apiRequest } from "../apiClient";

export interface AdminUser {
  id: number;
  username: string;
  email: string;
  role: string;
}

export async function searchUsers(q: string): Promise<AdminUser[]> {
  return apiRequest<AdminUser[]>(`/admin/users?q=${encodeURIComponent(q)}`);
}
