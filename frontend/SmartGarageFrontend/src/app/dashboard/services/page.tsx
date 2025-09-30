'use client';

import { Toaster } from 'sonner';
import PackCards from '@/components/dashboard-components/pack-cards';
import React, { useEffect, useState } from 'react';
import { Service } from '@/types/service';
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Euro } from "lucide-react";
import { Dropdown } from '@/components/custom-components/item-drop-down';

import { useUser } from "@/hooks/useUser";

import { getServices, deleteService } from "@/api/serviceApi";
import { ServiceForm } from '@/components/forms/edit-create-service-form';

export default function Page() {
    const [services, setServices] = useState<Service[]>([]);
    const [loading, setLoading] = useState(true);
    const [editingService, setEditingService] = useState<Service | null>(null);
    const [isFormOpen, setIsFormOpen] = useState(false);

    const { user } = useUser();
    const isAdmin = user?.role?.roleName === "ADMIN";

    // Load services initially
    useEffect(() => {
        const loadServices = async () => {
            setLoading(true);
            try {
                const data = await getServices();
                console.log('Loaded services:', data); // Debug log
                setServices(data);
            } catch (err) {
                console.error('Failed to load services:', err);
            } finally {
                setLoading(false);
            }
        };

        loadServices();
    }, []);

    const handleEdit = (service: Service) => {
        console.log('Editing service:', service); // Debug log
        setEditingService(service);
        setIsFormOpen(true);
    };

    const handleAddNew = () => {
        setEditingService(null);
        setIsFormOpen(true);
    };

    const handleFormSuccess = async () => {
        try {
            // Reload services after form success
            const data = await getServices();
            setServices(data);
        } catch (err) {
            console.error('Failed to reload services:', err);
        } finally {
            setIsFormOpen(false);
            setEditingService(null);
        }
    };

    const handleFormOpenChange = (open: boolean) => {
        setIsFormOpen(open);
        if (!open) {
            setEditingService(null);
        }
    };

    const handleDelete = async (serviceId: string) => {
        console.log('Deleting service ID:', serviceId, 'Type:', typeof serviceId); // Debug log

        if (!serviceId || serviceId <= 0) {
            console.error('Invalid service ID:', serviceId);
            alert('Invalid service ID');
            return;
        }

        try {
            await deleteService(serviceId);
            // Reload services after delete
            const data = await getServices();
            setServices(data);
        } catch (error) {
            console.error('Failed to delete service:', error);
        }
    };

    return (
        <div className="px-4 sm:px-6">
            <Toaster richColors position="top-center" toastOptions={{ className: 'font-sans' }} />

            {/* Service Form Modal */}
            <ServiceForm
                initialData={editingService}
                open={isFormOpen}
                onOpenChange={handleFormOpenChange}
                onSuccess={handleFormSuccess}
            />

            {/* Packs Section */}
            <div className="relative group">
                <div className="flex items-center justify-between mb-4">
                    <h2 className="scroll-m-20 py-2 text-2xl sm:text-3xl font-semibold tracking-tight first:mt-0">
                        Available Service Packs
                    </h2>
                    {isAdmin && (
                        <div className="md:opacity-0 md:group-hover:opacity-100 transition-opacity duration-200">
                            <Button size="sm" className="rounded-full text-xs sm:text-sm">
                                + Add New Pack
                            </Button>
                        </div>
                    )}
                </div>
                <PackCards />
            </div>

            {/* Services Section */}
            <div className="relative mt-8 group">
                <div className="flex items-center justify-between mb-4">
                    <h2 className="scroll-m-20 py-2 text-2xl sm:text-3xl font-semibold tracking-tight first:mt-0">
                        Services
                    </h2>
                    {isAdmin && (
                        <div className="md:opacity-0 md:group-hover:opacity-100 transition-opacity duration-200">
                            <Button
                                size="sm"
                                className="rounded-full text-xs sm:text-sm"
                                onClick={handleAddNew}
                            >
                                + Add New Service
                            </Button>
                        </div>
                    )}
                </div>

                <div className="grid gap-4 sm:gap-6 sm:grid-cols-1 md:grid-cols-2 xl:grid-cols-3">
                    {loading ? (
                        Array.from({ length: 3 }).map((_, i) => (
                            <Card key={i} className="rounded-2xl p-4 sm:p-6">
                                <Skeleton className="h-6 w-28 mb-4" />
                                <Skeleton className="h-4 w-full mb-2" />
                                <Skeleton className="h-4 w-3/4" />
                                <div className="mt-4 flex justify-between">
                                    <Skeleton className="h-6 w-16" />
                                    <Skeleton className="h-8 w-20 rounded-md" />
                                </div>
                            </Card>
                        ))
                    ) : (
                        services.map((s) => (
                            <Card
                                key={s.serviceId}
                                className="relative group rounded-2xl p-4 sm:p-6 transition-all border hover:shadow-xl md:hover:scale-[1.02] flex flex-col justify-between"
                            >
                                <CardHeader className="p-0 mb-1">
                                    <div className="flex items-start justify-between gap-2 flex-wrap">
                                        <div className="min-w-0 flex-1">
                                            <CardTitle className="text-xl sm:text-2xl font-semibold break-words leading-tight">
                                                {s.serviceName}
                                            </CardTitle>
                                        </div>

                                        <div className="flex items-center gap-2 shrink-0">
                                            <Badge variant="secondary" className="rounded-full px-2 sm:px-3 py-1 text-xs sm:text-sm whitespace-nowrap">
                                                New
                                            </Badge>

                                            {isAdmin && (
                                                <div className="md:opacity-0 md:group-hover:opacity-100 transition-opacity duration-200">
                                                    <Dropdown
                                                        itemType="Service"
                                                        onEdit={() => {
                                                            console.log('Edit clicked for service:', s); // Debug log
                                                            handleEdit(s);
                                                        }}
                                                        onDelete={() => {
                                                            console.log('Delete clicked for service:', s); // Debug log
                                                            console.log('Service ID:', s.serviceId, 'Type:', typeof s.serviceId); // Debug log
                                                            if (!s.serviceId) {
                                                                console.error('Service object has no ID:', s);
                                                                alert('Service ID is missing');
                                                                return;
                                                            }
                                                            handleDelete(s.serviceId);
                                                        }}
                                                        showDuplicate={false}
                                                    />
                                                </div>
                                            )}
                                        </div>
                                    </div>
                                </CardHeader>

                                <CardContent className="p-0 flex flex-col flex-1 justify-between">
                                    <p className="text-sm text-muted-foreground line-clamp-3 mt-2">
                                        {s.serviceDescription}
                                    </p>
                                    <div className="mt-4 flex items-center justify-end">
                                        <span className="flex items-center font-bold text-primary text-sm sm:text-base">
                                            <Euro className="h-3 w-3 sm:h-4 sm:w-4 mr-1" />
                                            {s.price.toFixed(2)}
                                        </span>
                                    </div>
                                </CardContent>
                            </Card>
                        ))
                    )}
                </div>
            </div>
        </div>
    );
}