'use client';

import { useState, useEffect } from "react";
import { Toaster } from 'sonner';
import PackCards from '@/components/dashboard-components/pack-cards';
import ServiceCards from '@/components/dashboard-components/service-card';
import { createApi } from "@/api/genericApi";

export default function Page() {
    const [packs, setPacks] = useState([]);
    const [services, setServices] = useState([]);

    const packApi = createApi("packs");
    const serviceApi = createApi("services");

    useEffect(() => {
        async function load() {
            const [packsData, servicesData] = await Promise.all([
                packApi.getAll(),
                serviceApi.getAll(),
            ]);

            setPacks(packsData);
            setServices(servicesData);
        }

        load();
    }, []);

    return (
        <div className="px-4 sm:px-6">
            <Toaster richColors position="top-center" />

            <PackCards
                packs={packs}
                setPacks={setPacks}
                services={services}
            />

            <ServiceCards
                services={services}
                setServices={setServices}
                packs={packs}
                setPacks={setPacks}
            />
        </div>
    );
}
