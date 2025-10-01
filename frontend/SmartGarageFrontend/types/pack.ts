import {Service} from "@/types/service";

export interface Pack {
    id: string;
    packName: string;
    amount: number;
    description: string;
    services: Service[];
}