// src/api/servicesApi.ts
import { Service } from "@/types/service";

const BASE_URL = "http://localhost:8080/api";

export async function getServices(): Promise<Service[]> {
    const res = await fetch(`${BASE_URL}/services`, { credentials: "include" });
    if (!res.ok) throw new Error("Failed to fetch services");
    return res.json();
}

export async function createService(service: Omit<Service, "id">) {
    const res = await fetch(`${BASE_URL}/create-service`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(service),
    });
    if (!res.ok) throw new Error("Failed to create service");
    return res.json();
}

export async function updateService(id: number, service: Partial<Service>) {
    const res = await fetch(`${BASE_URL}/update-service/${id}`, {
        method: "PUT",
        headers: { "Content-Type": "application/json" },
        credentials: "include",
        body: JSON.stringify(service),
    });
    if (!res.ok) throw new Error("Failed to update service");
    return res.json();
}

export async function deleteService(id: number) {
    const res = await fetch(`${BASE_URL}/service/${id}`, {
        method: "DELETE",
        credentials: "include",
    });
    if (!res.ok) throw new Error("Failed to delete service");
    return true;
}
