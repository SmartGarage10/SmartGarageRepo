// components/dashboard-components/pack-cards.tsx
'use client';

import { useCallback, useEffect, useState } from "react";
import { IconTrendingUp, IconTrendingDown, IconInfoCircle } from "@tabler/icons-react";
import { toast } from "sonner";
import { XCircle } from "lucide-react";

import { Badge } from "@/components/ui/badge";
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card";
import {
    Tooltip,
    TooltipContent,
    TooltipProvider,
    TooltipTrigger,
} from "@/components/ui/tooltip";

// Define the Pack type
type Pack = {
    id: string;
    pack: string;
    description: string;
    trend?: "up" | "down";
    services: string[];
    price?: number;
}

// Sample data to use as fallback
const samplePacks: Pack[] = [
    {
        id: "1",
        pack: "Basic Pack",
        description: "Essential features for getting started",
        price: 9.99,
        services: ["Feature 1", "Feature 2", "Feature 3"],
        trend: "up"
    },
    {
        id: "2",
        pack: "Pro Pack",
        description: "Advanced features for power users",
        price: 19.99,
        services: ["Feature 1", "Feature 2", "Feature 3", "Feature 4"],
        trend: "up"
    },
    {
        id: "3",
        pack: "Enterprise Pack",
        description: "Complete solution for businesses",
        price: 49.99,
        services: ["All Features", "Priority Support", "Customization"],
        trend: "down"
    },
];

interface PackCardsProps {
    // Optional props if you want to control behavior from parent
    autoFetch?: boolean;
    showErrorToasts?: boolean;
}

export function PackCards({ autoFetch = true, showErrorToasts = true }: PackCardsProps) {
    const [data, setData] = useState<Pack[]>([]);
    const [isLoading, setIsLoading] = useState(autoFetch);
    const [error, setError] = useState<string | null>(null);

    const fetchData = useCallback(async () => {
        try {
            setIsLoading(true);
            setError(null);

            const res = await fetch("http://localhost:8080/api/packs", {
                credentials: "include",
            });

            if (!res.ok) {
                throw new Error(`HTTP error! status: ${res.status}`);
            }

            // Handle potential malformed JSON
            const text = await res.text();
            let json;

            try {
                json = JSON.parse(text);
            } catch (parseError) {
                console.error("Failed to parse JSON:", parseError);
                throw new Error("Received malformed data from server");
            }

            setData(json);
        } catch (error) {
            console.error("Fetch error:", error);
            setError(error instanceof Error ? error.message : "Unknown error");

            // Use sample data as fallback
            setData(samplePacks);

            if (showErrorToasts) {
                toast.error("Failed to load packs", {
                    className: "bg-destructive text-white",
                    description: error instanceof Error ? error.message : "Unknown error",
                    icon: <XCircle className="text-red-500" />,
                });
            }
        } finally {
            setIsLoading(false);
        }
    }, [showErrorToasts]);

    // Refetch function that can be called from parent if needed
    const refetch = useCallback(() => {
        fetchData();
    }, [fetchData]);

    useEffect(() => {
        if (autoFetch) {
            fetchData();
        }
    }, [autoFetch, fetchData]);

    if (isLoading) {
        return (
            <div className="p-6">
                <h2 className="text-2xl font-bold mb-4">Available Packs</h2>
                <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
                    {[1, 2, 3].map((i) => (
                        <Card key={i} className="animate-pulse">
                            <CardHeader>
                                <div className="h-4 bg-gray-200 rounded w-3/4 mb-2"></div>
                                <div className="h-6 bg-gray-200 rounded w-1/2 mb-4"></div>
                            </CardHeader>
                            <CardContent>
                                <div className="h-10 bg-gray-200 rounded w-full"></div>
                            </CardContent>
                            <CardFooter>
                                <div className="h-4 bg-gray-200 rounded w-1/2"></div>
                            </CardFooter>
                        </Card>
                    ))}
                </div>
            </div>
        );
    }

    return (
        <div className="p-6">
            <div className="flex justify-between items-center mb-4">
                <h2 className="text-2xl font-bold">Available Packs</h2>
                <button
                    onClick={refetch}
                    className="text-sm bg-blue-100 text-blue-700 hover:bg-blue-200 px-3 py-1 rounded-md"
                >
                    Refresh
                </button>
            </div>

            {error && (
                <div className="bg-yellow-50 border-l-4 border-yellow-400 p-4 mb-4">
                    <div className="flex">
                        <div className="ml-3">
                            <p className="text-sm text-yellow-700">
                                {error}. Showing sample data.
                            </p>
                        </div>
                    </div>
                </div>
            )}

            {data.length === 0 ? (
                <Card>
                    <CardContent className="pt-6 text-center">
                        <p className="text-muted-foreground">No packs available</p>
                    </CardContent>
                </Card>
            ) : (
                <TooltipProvider>
                    <div className="grid grid-cols-1 gap-4 @xl/main:grid-cols-2 @5xl/main:grid-cols-3">
                        {data.map((pack) => (
                            <Card key={pack.id} className="hover:shadow-lg transition-shadow">
                                <CardHeader>
                                    <CardDescription>{pack.description}</CardDescription>
                                    <CardTitle className="text-2xl font-semibold flex items-center gap-2">
                                        {pack.pack}
                                        <Tooltip>
                                            <TooltipTrigger asChild>
                                                <IconInfoCircle className="size-5 text-muted-foreground cursor-pointer" />
                                            </TooltipTrigger>
                                            <TooltipContent className="max-w-xs">
                                                <div className="flex flex-col gap-1">
                                                    <p className="font-semibold">Included Services:</p>
                                                    <ul className="list-disc list-inside text-sm">
                                                        {pack.services.map((service, i) => (
                                                            <li key={i}>{service}</li>
                                                        ))}
                                                    </ul>
                                                </div>
                                            </TooltipContent>
                                        </Tooltip>
                                    </CardTitle>
                                </CardHeader>

                                <CardContent>
                                    <Badge variant="outline" className="text-lg p-2">
                                        {pack.trend === "down" ? (
                                            <IconTrendingDown className="mr-1" />
                                        ) : (
                                            <IconTrendingUp className="mr-1" />
                                        )}
                                        {pack.price ? `$${pack.price}` : "Price not available"}
                                    </Badge>
                                </CardContent>

                                <CardFooter className="flex-col items-start gap-1.5 text-sm">
                                    <div className="flex gap-2 font-medium items-center">
                                        {pack.trend === "down" ? (
                                            <>
                                                Trending down <IconTrendingDown className="size-4" />
                                            </>
                                        ) : (
                                            <>
                                                Trending up <IconTrendingUp className="size-4" />
                                            </>
                                        )}
                                    </div>
                                    <div className="text-muted-foreground">Pack ID: {pack.id}</div>
                                </CardFooter>
                            </Card>
                        ))}
                    </div>
                </TooltipProvider>
            )}
        </div>
    );
}