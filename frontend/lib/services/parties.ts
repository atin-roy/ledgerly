import { apiRequest, getUserIdFromToken } from "../apiClient";

export interface PartyResponse {
  id: number;
  name: string;
}

export async function getParties(): Promise<PartyResponse[]> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated - no userId found");
  }

  return apiRequest<PartyResponse[]>(`/users/${userId}/parties`);
}
