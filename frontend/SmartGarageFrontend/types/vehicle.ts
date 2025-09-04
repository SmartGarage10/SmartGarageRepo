import { User } from "@/types/user";

export interface Vehicle {
    id: string;
    vehiclePlate: string;
    vin: string;
    client: User;
    brand: string;
    model: string;
    year: string;
}