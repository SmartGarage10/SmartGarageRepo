export interface Pack {
    id: number;
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