import { User } from './user';
import { Vehicle } from './vehicle';
import { Pack } from './pack';

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
};