// src/api/apiClient.ts
export const BASE_URL = "http://localhost:8080/api";

async function request<T>(
    url: string,
    options: RequestInit = {}
): Promise<T> {
    const res = await fetch(url, {
        credentials: "include",
        headers: { "Content-Type": "application/json", ...options.headers },
        ...options,
    });

    if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || `Request failed: ${res.status}`);
    }

    // Handle empty responses (e.g., DELETE returning 204)
    if (res.status === 204) return null as T;

    return res.json();
}

export function createApi<T>(resource: string) {
    return {
        getAll: (): Promise<T[]> => request<T[]>(`${BASE_URL}/${resource}`),

        getById: (id: string | number): Promise<T> =>
            request<T>(`${BASE_URL}/${resource}/${id}`),

        create: (data: Partial<T>): Promise<T> =>
            request<T>(`${BASE_URL}/create-${resource}`, {
                method: "POST",
                body: JSON.stringify(data),
                credentials: "include",
            }),

        update: (id: string | number, data: Partial<T>): Promise<T> =>
            request<T>(`${BASE_URL}/update-${resource}/${id}`, {
                method: "PUT",
                body: JSON.stringify(data),
                credentials: "include",
            }),

        delete: (id: string | number): Promise<boolean> =>
            request(`${BASE_URL}/${resource}/${id}`, {
                method: "DELETE",
                credentials: "include",
            }).then(() => true),
    };
}
