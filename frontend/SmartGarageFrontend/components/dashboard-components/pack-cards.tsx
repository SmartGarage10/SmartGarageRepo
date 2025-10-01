import React, { useEffect, useState } from "react";
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { HoverCard, HoverCardContent, HoverCardTrigger } from "@/components/ui/hover-card";
import { Euro, Wrench } from "lucide-react";
import {useUser} from "@/hooks/useUser";
import {Dropdown} from "@/components/custom-components/item-drop-down";
import { Pack } from "@/types/pack";
import {createApi} from "@/api/genericApi";
import {Service} from "@/types/service";
import {ServiceForm} from "@/components/forms/edit-create-service-form";
import {PackForm} from "@/components/forms/edit-create-pack-form";
import {Button} from "@/components/ui/button";

export default function PackCards() {
    const [packs, setPacks] = useState<Pack[]>([]);
    const [loading, setLoading] = useState(true);
    const [editingPack, setEditingPack] = useState<Pack | null>(null);
    const [isFormOpen, setIsFormOpen] = useState(false);

    const { user } = useUser();
    const isAdmin = user?.role?.roleName === "ADMIN";

    const packApi = createApi<Pack>("packs");

    // Load packs initially
    useEffect(() => {
        const loadPacks = async () => {
            setLoading(true);
            try {
                const data = await packApi.getAll();
                setPacks(data);
            } catch (err) {
                console.error("Failed to load packs:", err);
            } finally {
                setLoading(false);
            }
        };
        loadPacks();
    }, []);

    const handleEdit = (pack: Pack) => {
        setEditingPack(pack);
        setIsFormOpen(true);
    };

    const handleAddNew = () => {
        setEditingPack(null);
        setIsFormOpen(true);
    };

    const handleFormSuccess = async () => {
        try {
            const data = await packApi.getAll();
            setPacks(data);
        } catch (err) {
            console.error("Failed to reload packs:", err);
        } finally {
            setIsFormOpen(false);
            setEditingPack(null);
        }
    };

    const handleFormOpenChange = (open: boolean) => {
        setIsFormOpen(open);
        if (!open) setEditingPack(null);
    };

    const handleDelete = async (packId: string) => {
        if (!packId) return alert("Invalid pack ID");
        try {
            await packApi.delete(packId);
            const data = await packApi.getAll();
            setPacks(data);
        } catch (error) {
            console.error("Failed to delete pack:", error);
        }
    };

    if (loading) {
        return (
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {[1, 2, 3].map((i) => (
                    <Card key={i} className="p-5 rounded-2xl">
                        <Skeleton className="w-32 h-6 mb-3" />
                        <Skeleton className="w-20 h-5 mb-2" />
                        <Skeleton className="w-full h-4" />
                    </Card>
                ))}
            </div>
        );
    }

    return (
        <div className="relative group">
            <div className="flex items-center justify-between mb-4">
                <h2 className="scroll-m-20 py-2 text-2xl sm:text-3xl font-semibold tracking-tight first:mt-0">
                    Available Service Packs
                </h2>
                {isAdmin && (
                    <div className="md:opacity-0 md:group-hover:opacity-100 transition-opacity duration-200">
                        <Button
                            size="sm"
                            className="rounded-full text-xs sm:text-sm"
                            onClick={handleAddNew}
                        >
                            + Add New Pack
                        </Button>
                    </div>
                )}
            </div>
            <div className="grid gap-4 md:grid-cols-2 lg:grid-cols-3">
                {packs.map((pack) => (
                    <HoverCard key={pack.id} openDelay={0} closeDelay={0}>
                        <HoverCardTrigger asChild>
                            <Card className="relative overflow-hidden group p-5 rounded-2xl bg-gradient-to-br from-pink-50 to-purple-50
                             dark:from-gray-800 dark:to-gray-700 border border-gray-100 dark:border-gray-700
                             transition-shadow hover:shadow-xl flex flex-col h-full">
                                {/* Background icon */}
                                <Wrench
                                    className="absolute -right-1 -top-1 w-40 h-40 text-red-900 dark:text-red-800 opacity-20 pointer-events-none"
                                />

                                <CardContent className="p-0 flex flex-col justify-between flex-1 relative z-10">
                                    <CardHeader className="p-0 mb-2 relative z-10">
                                        <div className="flex items-center gap-2 shrink-0">
                                            <CardTitle className="text-2xl font-bold text-foreground">{pack.packName}</CardTitle>
                                            {isAdmin && (
                                                <div className="md:opacity-0 md:group-hover:opacity-100 transition-opacity duration-200">
                                                    <Dropdown
                                                        itemType="Service"
                                                        onEdit={() => {
                                                            console.log('Edit clicked for service:', pack); // Debug log
                                                            handleEdit(pack);
                                                        }}
                                                        onDelete={() => {
                                                            console.log('Delete clicked for service:', pack); // Debug log
                                                            console.log('Service ID:', pack.id, 'Type:', typeof pack.id); // Debug log
                                                            if (!pack.id) {
                                                                console.error('Pack object has no ID:', pack);
                                                                alert('Pack ID is missing');
                                                                return;
                                                            }
                                                            handleDelete(pack.id);
                                                        }}
                                                        showDuplicate={false}
                                                    />
                                                </div>
                                            )}
                                        </div>
                                    </CardHeader>
                                    <div className="flex items-center text-3xl font-semibold text-foreground">
                                        <Euro className="mr-1" />
                                        {pack.amount}
                                    </div>
                                    <CardDescription className="text-lg text-muted-foreground mt-1 line-clamp-3">
                                        {pack.description}
                                    </CardDescription>
                                </CardContent>
                            </Card>
                        </HoverCardTrigger>

                        <HoverCardContent className="w-80 p-4 space-y-2 rounded-xl shadow-lg">
                            <h4 className="text-lg font-semibold text-foreground">Services included</h4>
                            <div className="space-y-1 max-h-60 overflow-y-auto pr-1">
                                {pack.services.map((service) => (
                                    <div
                                        key={service.id}
                                        className="flex items-start justify-between rounded-md p-2 hover:bg-muted/50 transition"
                                    >
                                        <div className="flex-1">
                                            <p className="font-medium text-sm text-foreground">{service.serviceName}</p>
                                            <p className="text-xs text-muted-foreground line-clamp-2">{service.serviceDescription}</p>
                                        </div>
                                        <span className="ml-2 text-sm font-semibold text-green-600">${service.price.toFixed(2)}</span>
                                    </div>
                                ))}
                            </div>
                            <div className="flex justify-between items-center pt-2 border-t text-sm">
                                <span className="text-muted-foreground">Total</span>
                                <span className="font-bold text-foreground text-green-600">${pack.totalPrice.toFixed(2)}</span>
                            </div>
                        </HoverCardContent>
                    </HoverCard>
                ))}

                {/* Service Form Modal */}
                <PackForm
                    initialData={editingPack}
                    open={isFormOpen}
                    onOpenChange={handleFormOpenChange}
                    onSuccess={handleFormSuccess}
                />
            </div>
        </div>
    );
}
