import { User } from './user';
import { Vehicle } from './vehicle';
import { Pack } from './pack';
import {Service} from "@/types/service";

export type Visit = {
    id: string;
    client: User;
    vehicle: Vehicle;
    employee: User;
    visitDate: string;
    status: string;
    amount: number;
    currency: string;
    pack ?: Pack;
    visitServices ?: Service[];
};

export enum Status {
    SCHEDULED = "SCHEDULED",
    IN_PROGRESS = "IN_PROGRESS",
    COMPLETED = "COMPLETED",
    CANCELLED = "CANCELLED"
}

export const VISIT_STATUS_OPTIONS = Object.values(Status);