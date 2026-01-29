import { apiRequest, getUserIdFromToken } from "../apiClient";

export interface PotResponse {
  id: number;
  name: string;
  target: number;
  saved: number;
}

export interface CreatePotRequest {
  name: string;
  target: number;
  saved?: number;
}

export interface UpdatePotRequest {
  name?: string;
  target?: number;
  saved?: number;
}

export async function getPots(): Promise<PotResponse[]> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<PotResponse[]>(`/users/${userId}/pots`);
}

export async function getPot(potId: number): Promise<PotResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<PotResponse>(`/users/${userId}/pots/${potId}`);
}

export async function createPot(data: CreatePotRequest): Promise<PotResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<PotResponse>(`/users/${userId}/pots`, {
    method: "POST",
    body: JSON.stringify(data),
  });
}

export async function updatePot(
  potId: number,
  data: UpdatePotRequest
): Promise<PotResponse> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<PotResponse>(`/users/${userId}/pots/${potId}`, {
    method: "PUT",
    body: JSON.stringify(data),
  });
}

export async function deletePot(potId: number): Promise<void> {
  const userId = getUserIdFromToken();
  if (!userId) {
    throw new Error("User not authenticated");
  }

  return apiRequest<void>(`/users/${userId}/pots/${potId}`, {
    method: "DELETE",
  });
}
