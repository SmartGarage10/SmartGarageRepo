export interface Pack {
    id: string;
    packName: string;
    totalPrice: number;
    description: string;
    services: {
        id: number;
        serviceName: string;
        serviceDescription: string;
        price: number;
    }[];
}