'use client';

import React, { useMemo, useCallback, useState } from "react";
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
    CardDescription
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { HoverCard, HoverCardContent, HoverCardTrigger } from "@/components/ui/hover-card";
import { Euro, Wrench } from "lucide-react";
import { useUser } from "@/hooks/useUser";
import { Dropdown } from "@/components/custom-components/item-drop-down";
import { Button } from "@/components/ui/button";
import { PackForm } from "@/components/forms/edit-create-pack-form";
import { createApi } from "@/api/genericApi";
import type { Pack } from "@/types/pack";
import type { Service } from "@/types/service";
import type { Option } from "@/components/ui/multiple-selector";

export default function PackCards({ packs, setPacks, services }) {
    const [editingPack, setEditingPack] = useState<Pack | null>(null);
    const [isFormOpen, setIsFormOpen] = useState(false);

    const { user } = useUser();
    const isAdmin = user?.role?.roleName === "ADMIN";

    const packApi = createApi<Pack>("packs");

    // Convert services → select options
    const serviceOptions: Option[] = useMemo(
        () =>
            services.map((s) => ({
                label: s.serviceName,
                value: s.id,
            })),
        [services]
    );

    // Convert editingPack.services → select options
    const selectedServiceOptions: Option[] = useMemo(() => {
        if (!editingPack) return [];
        return editingPack.services.map((s) => ({
            label: s.serviceName,
            value: s.id,
        }));
    }, [editingPack]);

    // Calculate amount
    const calculateAmount = useCallback(
        (ids: number[]) => {
            const selected = services.filter((s) => ids.includes(Number(s.id)));
            const raw = selected.reduce((sum, s) => sum + s.price, 0);
            const discounted = raw * 0.9;
            return discounted > 0 ? Math.round(discounted / 5) * 5 - 0.01 : 0;
        },
        [services]
    );

    // When user selects services in form
    const handleServicesChange = (options: Option[]) => {
        setEditingPack(prev => {
            if (!prev) return prev;

            const ids = options.map(o => Number(o.value));
            const amount = calculateAmount(ids);

            return {
                ...prev,
                services: services.filter(s => ids.includes(Number(s.id))),
                amount,
            };
        });
    };

    // Open form for edit/create
    const handleData = (pack: Pack | null) => {
        if (pack) {
            setEditingPack(pack);
        } else {
            setEditingPack({
                id: "",
                packName: "",
                description: "",
                amount: 0,
                services: [],
                totalPrice: 0,
            });
        }
        setIsFormOpen(true);
    };

    // Submit pack
    const handleSubmit = useCallback(
        async (packData: Pack) => {
            try {
                const isEdit = !!packData.id;

                const ids = packData.services.map(s => Number(s.id));
                const amount = calculateAmount(ids);

                const endpoint = isEdit
                    ? `http://localhost:8080/api/update-pack/${packData.id}`
                    : "http://localhost:8080/api/create-pack";

                const method = isEdit ? "PUT" : "POST";

                const payload = {
                    id: packData.id,
                    packName: packData.packName,
                    description: packData.description,
                    amount,
                    serviceIds: ids
                };

                const res = await fetch(endpoint, {
                    method,
                    headers: { "Content-Type": "application/json" },
                    body: JSON.stringify(payload),
                    credentials: "include",
                });

                if (!res.ok) {
                    const err = await res.json().catch(() => ({}));
                    throw new Error(err.message || "Failed to save pack");
                }

                // 🔥 AUTO REFRESH PACKS
                setPacks(await packApi.getAll());

                setIsFormOpen(false);
                setEditingPack(null);
            } catch (err) {
                console.error("Error saving pack:", err);
            }
        },
        [packApi, calculateAmount, setPacks]
    );

    // Delete pack
    const handleDelete = async (id: number) => {
        await packApi.delete(id);
        setPacks(await packApi.getAll());
    };

    return (
        <div className="relative group">
            <div className="flex items-center justify-between mb-6">
                <h2 className="text-2xl sm:text-3xl font-semibold tracking-tight">
                    Available Service Packs
                </h2>

                {isAdmin && (
                    <div className="md:opacity-0 md:group-hover:opacity-100 transition-opacity">
                        <Button size="sm" onClick={() => handleData(null)}>
                            + Add New Pack
                        </Button>
                    </div>
                )}
            </div>

            <div className="grid gap-8 sm:grid-cols-2 lg:grid-cols-3 auto-rows-[180px]">
                {packs.map((pack) => (
                    <HoverCard key={pack.id}>
                        <div className="relative">
                            <HoverCardTrigger asChild>
                                <div onClick={() => handleData(pack)}>
                                    <Card
                                        className="relative h-[180px] p-5 rounded-3xl
                                            bg-gradient-to-br from-white/70 to-white/20
                                            dark:from-gray-900/60 dark:to-gray-800/40
                                            backdrop-blur-xl border border-white/20
                                            dark:border-gray-700/40 shadow-lg hover:shadow-2xl
                                            transition-all duration-300 flex flex-col"
                                    >
                                        <Wrench className="absolute right-3 top-3 w-16 h-16 opacity-[0.06]" />

                                        <CardContent className="p-0 flex flex-col">
                                            <CardHeader className="p-0 mb-1">
                                                <CardTitle className="text-lg font-semibold tracking-tight leading-tight">
                                                    {pack.packName}
                                                </CardTitle>
                                            </CardHeader>

                                            <div className="flex items-center text-3xl font-bold mb-1">
                                                <Euro className="mr-1 w-5 h-5" />
                                                {pack.amount}
                                            </div>

                                            <CardDescription className="text-sm opacity-80 leading-snug line-clamp-3">
                                                {pack.description}
                                            </CardDescription>
                                        </CardContent>
                                    </Card>
                                </div>
                            </HoverCardTrigger>

                            {isAdmin && (
                                <div className="absolute top-4 right-4 z-50">
                                    <Dropdown
                                        itemType="Pack"
                                        onEdit={() => handleData(pack)}
                                        onDelete={() => handleDelete(pack.id)}
                                        showDuplicate={false}
                                    />
                                </div>
                            )}
                        </div>

                        <HoverCardContent
                            className="w-80 p-5 rounded-2xl shadow-xl
                                bg-white/80 dark:bg-gray-900/80 backdrop-blur-xl border border-white/20"
                        >
                            <h4 className="text-lg font-semibold mb-2">Services included</h4>

                            <div className="space-y-2 max-h-60 overflow-y-auto pr-1">
                                {pack.services.map((s) => (
                                    <div
                                        key={s.id}
                                        className="flex justify-between items-start p-2 rounded-lg
                                            bg-gray-50 dark:bg-gray-800/40"
                                    >
                                        <div>
                                            <p className="font-medium">{s.serviceName}</p>
                                            <p className="text-xs text-muted-foreground">
                                                {s.serviceDescription}
                                            </p>
                                        </div>
                                        <span className="font-semibold text-green-600">
                                            €{s.price.toFixed(2)}
                                        </span>
                                    </div>
                                ))}
                            </div>

                            <div className="flex justify-between pt-3 border-t mt-3 text-sm">
                                <span>Total</span>
                                <span className="font-bold text-green-600">
                                    €{pack.totalPrice.toFixed(2)}
                                </span>
                            </div>
                        </HoverCardContent>
                    </HoverCard>
                ))}

                {editingPack && (
                    <PackForm
                        open={isFormOpen}
                        onOpenChange={setIsFormOpen}
                        initialData={editingPack}
                        serviceOptions={serviceOptions}
                        selectedServiceOptions={selectedServiceOptions}
                        onServicesChange={handleServicesChange}
                        amount={editingPack.amount}
                        onSubmit={handleSubmit}
                    />
                )}
            </div>
        </div>
    );
}
