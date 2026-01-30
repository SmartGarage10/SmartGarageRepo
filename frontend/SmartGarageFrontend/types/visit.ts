import { User } from './user';
import { Vehicle } from './vehicle';
import { Service } from "@/types/service";
import { Pack } from "@/types/pack";

export type Visit = {
    id: string;
    client: User;
    vehicle: Vehicle;
    employee: User;
    visitDate: string;
    status: string;
    amount: number;
    currency: string;
    visitItems: VisitItem [];
};

// visit-item.ts

export enum VisitItemType {
    SERVICE = "SERVICE",
    PACK = "PACK"
}

export type VisitItem = {
    visitId?: string;
    serviceItem?: Service;
    pack?: Pack;
    itemType: VisitItemType;
    itemName?: string;
    price: number;
    quantity: number;
    subtotal?: number;
};


export enum Status {
    SCHEDULED = "SCHEDULED",
    IN_PROGRESS = "IN_PROGRESS",
    COMPLETED = "COMPLETED",
    CANCELLED = "CANCELLED"
}

export const VISIT_STATUS_OPTIONS = Object.values(Status);