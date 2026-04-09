"use client";

import { useEffect, useRef, useState } from "react";
import { useReactToPrint } from "react-to-print";
import jsPDF from "jspdf";

import { Button } from "@/components/ui/button";
import {
    Card,
    CardContent,
    CardHeader,
    CardTitle,
    CardDescription,
} from "@/components/ui/card";
import {
    Table,
    TableHeader,
    TableRow,
    TableHead,
    TableBody,
    TableCell,
    TableFooter,
} from "@/components/ui/table";
import { Typography } from "@/lib/typography";
import { InputSelect, InputSelectTrigger } from "@/components/ui/input-select";
import { IconPrinter } from "@tabler/icons-react";
import { DownloadIcon } from "lucide-react";

import { createApi } from "@/api/genericApi";
import { Visit, VisitItem, VisitItemType } from "@/types/visit";
import { Vehicle } from "@/types/vehicle";

export default function InvoicePage() {
    const contentRef = useRef<HTMLDivElement>(null);
    const [visits, setVisits] = useState<Visit[]>([]);
    const [selectedVehicle, setSelectedVehicle] = useState("");
    const [selectedVisit, setSelectedVisit] = useState("");
    const [loading, setLoading] = useState(false);

    const visitApi = createApi<Visit>("visits");

    useEffect(() => {
        async function loadData() {
            try {
                const data = await visitApi.getAll();
                setVisits(data);
            } catch (error) {
                console.error("Error loading visits:", error);
            }
        }
        loadData();
    }, []);

    // Get unique vehicles from visits
    const vehiclesMap = new Map<string, Vehicle>();
    visits.forEach((visit) => {
        if (visit.vehicle && !vehiclesMap.has(visit.vehicle.vehiclePlate)) {
            vehiclesMap.set(visit.vehicle.vehiclePlate, visit.vehicle);
        }
    });
    const vehicles = Array.from(vehiclesMap.values());

    const vehicleOptions = vehicles.map((v) => ({
        value: v.vehiclePlate,
        label: `${v.brand} ${v.model} (${v.vehiclePlate})`,
    }));

    const currentVehicle = vehicles.find((v) => v.vehiclePlate === selectedVehicle);

    // Filter visits for selected vehicle
    const visitOptions = visits
        .filter((v) => v.vehicle?.vehiclePlate === selectedVehicle)
        .map((v) => ({
            value: v.id,
            label: new Date(v.visitDate).toLocaleDateString('en-US', {
                year: 'numeric',
                month: 'short',
                day: 'numeric',
                hour: '2-digit',
                minute: '2-digit'
            }),
        }));

    const currentVisit = visits.find((v) => v.id === selectedVisit);

    // Get visit items properly
    const getVisitItems = (): VisitItem[] => {
        if (!currentVisit) return [];
        return currentVisit.visitItems || [];
    };

    const visitItems = getVisitItems();

    // Calculate totals
    const calculateSubtotals = () => {
        return visitItems.map(item => ({
            ...item,
            subtotal: item.price * item.quantity
        }));
    };

    const itemsWithSubtotals = calculateSubtotals();
    const totalAmount = itemsWithSubtotals.reduce((sum, item) => sum + (item.subtotal || 0), 0);

    // react-to-print setup
    const reactToPrintFn = useReactToPrint({
        contentRef: contentRef,
        documentTitle: `Invoice_${currentVehicle?.vehiclePlate || 'invoice'}`,
    });

    // Helper to safely slice ID string
    const getInvoiceNumber = () => {
        if (!currentVisit?.id) return '00000';
        const idString = String(currentVisit.id);
        return idString.slice(-8);
    };

    const getTransactionId = () => {
        if (!currentVisit?.id) return 'N/A';
        const idString = String(currentVisit.id);
        return idString.slice(-12);
    };

    function formatDateWithOrdinal(dateString: string) {
        const date = new Date(dateString);
        const month = date.toLocaleString("default", { month: "long" });
        const day = date.getDate();
        const year = date.getFullYear();
        const suffix = getOrdinalSuffix(day);

        return `${month} ${day}${suffix}, ${year}`;
    }

    function getOrdinalSuffix(day: number) {
        if (day > 3 && day < 21) return "th";
        switch (day % 10) {
            case 1: return "st";
            case 2: return "nd";
            case 3: return "rd";
            default: return "th";
        }
    }

    // Helper to get item name based on type
    const getItemName = (item: VisitItem): string => {
        if (item.itemType === VisitItemType.SERVICE && item.serviceItem) {
            return item.serviceItem.serviceName || "Service";
        } else if (item.itemType === VisitItemType.PACK && item.pack) {
            return item.pack.packName || "Pack";
        }
        return item.itemName || "Item";
    };

    // Get pack name if any items are packs
    const getPackName = (): string => {
        const packItem = visitItems.find(item => item.itemType === VisitItemType.PACK && item.pack);
        return packItem?.pack?.packName || "CUSTOM SERVICES";
    };

    // NEW: Create PDF using jsPDF directly (no html2canvas)
    const handleDownloadPdf = () => {
        if (!currentVisit || !currentVehicle) {
            console.error("No visit or vehicle selected");
            return;
        }

        setLoading(true);
        try {
            const pdf = new jsPDF({
                unit: 'mm',
                format: 'a4',
                orientation: 'portrait'
            });

            // Set font
            pdf.setFont("helvetica");

            // Colors
            const darkColor = [51, 51, 51]; // Dark gray
            const lightColor = [107, 114, 128]; // Light gray
            const accentColor = [59, 130, 246]; // Blue
            const borderColor = [229, 231, 235]; // Border gray

            // Margins
            const margin = 15;
            let yPos = margin;
            const pageWidth = 210;
            const contentWidth = pageWidth - (2 * margin);

            // Header
            pdf.setFontSize(24);
            pdf.setTextColor(...darkColor);
            pdf.text(`Invoice #${getInvoiceNumber()}`, margin, yPos);
            yPos += 8;

            pdf.setFontSize(11);
            pdf.setTextColor(...lightColor);
            pdf.text(formatDateWithOrdinal(currentVisit.visitDate), margin, yPos);
            yPos += 15;

            // Two column layout for Client and Provider
            const col1X = margin;
            const col2X = margin + (contentWidth / 2);

            // Client Information
            pdf.setFontSize(10);
            pdf.setTextColor(...lightColor);
            pdf.text("CLIENT INFORMATION", col1X, yPos);
            yPos += 5;

            pdf.setFontSize(11);
            pdf.setTextColor(...darkColor);
            pdf.text(currentVehicle?.client?.name || currentVisit?.client?.name || "N/A", col1X, yPos);
            yPos += 5;
            pdf.text(currentVehicle?.client?.address || currentVisit?.client?.address || "N/A", col1X, yPos);
            yPos += 5;
            pdf.text(currentVehicle?.client?.phone || currentVisit?.client?.phone || "N/A", col1X, yPos);
            yPos += 5;
            pdf.text(currentVehicle?.client?.email || currentVisit?.client?.email || "N/A", col1X, yPos);
            yPos += 10;

            // Reset y for Service Provider
            let providerY = yPos - 25;

            // Service Provider
            pdf.setFontSize(10);
            pdf.setTextColor(...lightColor);
            pdf.text("SERVICE PROVIDER", col2X, providerY);
            providerY += 5;

            pdf.setFontSize(11);
            pdf.setTextColor(...darkColor);
            pdf.text("AutoCare Pro", col2X, providerY);
            providerY += 5;
            pdf.text("1234 Service Street", col2X, providerY);
            providerY += 5;
            pdf.text("Auto City, AC 12345", col2X, providerY);
            providerY += 5;
            pdf.text("(555) 123-4567", col2X, providerY);
            providerY += 5;
            pdf.text("info@autocarepro.com", col2X, providerY);

            // Vehicle Information Header
            yPos += 10;
            pdf.setFontSize(10);
            pdf.setTextColor(...lightColor);
            pdf.text("VEHICLE INFORMATION", margin, yPos);
            yPos += 8;

            // Vehicle Table
            const tableTop = yPos;
            const colWidths = [30, 40, 25, 25, 20];
            let xPos = margin;

            // Table Headers
            pdf.setFontSize(9);
            pdf.setTextColor(...lightColor);
            pdf.text("License Plate", xPos, yPos);
            xPos += colWidths[0];
            pdf.text("VIN", xPos, yPos);
            xPos += colWidths[1];
            pdf.text("Make", xPos, yPos);
            xPos += colWidths[2];
            pdf.text("Model", xPos, yPos);
            xPos += colWidths[3];
            pdf.text("Year", xPos, yPos);

            yPos += 5;

            // Draw header line
            pdf.setDrawColor(...borderColor);
            pdf.line(margin, yPos, margin + contentWidth, yPos);
            yPos += 3;

            // Vehicle Data
            pdf.setFontSize(10);
            pdf.setTextColor(...darkColor);
            xPos = margin;
            pdf.text(currentVehicle.vehiclePlate, xPos, yPos);
            xPos += colWidths[0];
            pdf.text(currentVehicle.vin, xPos, yPos);
            xPos += colWidths[1];
            pdf.text(currentVehicle.brand, xPos, yPos);
            xPos += colWidths[2];
            pdf.text(currentVehicle.model, xPos, yPos);
            xPos += colWidths[3];
            pdf.text(String(currentVehicle.year), xPos, yPos);

            yPos += 15;

            // Service Summary
            pdf.setFontSize(10);
            pdf.setTextColor(...lightColor);
            pdf.text("SERVICE SUMMARY", margin, yPos);
            yPos += 5;

            pdf.setFontSize(14);
            pdf.setTextColor(...darkColor);
            pdf.text(getPackName(), margin, yPos);
            yPos += 10;

            // Service Details Header
            pdf.setFontSize(10);
            pdf.setTextColor(...lightColor);
            pdf.text("SERVICE DETAILS", margin, yPos);
            yPos += 8;

            // Service Details Table
            const serviceTableTop = yPos;
            const serviceColWidths = [70, 25, 20, 25, 30];
            xPos = margin;

            // Table Headers
            pdf.setFontSize(9);
            pdf.setTextColor(...lightColor);
            pdf.text("Item", xPos, yPos);
            xPos += serviceColWidths[0];
            pdf.text("Type", xPos, yPos);
            xPos += serviceColWidths[1];
            pdf.text("Qty", xPos, yPos);
            xPos += serviceColWidths[2];
            pdf.text("Price", xPos, yPos);
            xPos += serviceColWidths[3];
            pdf.text("Total", xPos, yPos);

            yPos += 5;

            // Draw header line
            pdf.setDrawColor(...borderColor);
            pdf.line(margin, yPos, margin + contentWidth, yPos);
            yPos += 3;

            // Service Items
            pdf.setFontSize(10);
            itemsWithSubtotals.forEach((item, index) => {
                if (yPos > 270) { // Check if we need a new page
                    pdf.addPage();
                    yPos = margin;
                }

                xPos = margin;
                pdf.setTextColor(...darkColor);
                pdf.text(getItemName(item), xPos, yPos);
                xPos += serviceColWidths[0];

                pdf.setTextColor(...accentColor);
                pdf.text(item.itemType, xPos, yPos);
                xPos += serviceColWidths[1];

                pdf.setTextColor(...darkColor);
                pdf.text(String(item.quantity), xPos, yPos);
                xPos += serviceColWidths[2];

                pdf.text(`$${item.price.toFixed(2)}`, xPos, yPos);
                xPos += serviceColWidths[3];

                pdf.text(`$${(item.subtotal || 0).toFixed(2)}`, xPos, yPos);

                yPos += 7;
            });

            // Draw footer line
            pdf.setDrawColor(...borderColor);
            pdf.line(margin, yPos, margin + contentWidth, yPos);
            yPos += 5;

            // Totals
            xPos = margin + serviceColWidths[0] + serviceColWidths[1] + serviceColWidths[2];

            pdf.setFontSize(10);
            pdf.setTextColor(...darkColor);
            pdf.text("Subtotal", xPos, yPos);
            xPos += serviceColWidths[3];
            pdf.text(`$${totalAmount.toFixed(2)}`, xPos, yPos);
            yPos += 6;

            if (currentVisit?.currency) {
                xPos = margin + serviceColWidths[0] + serviceColWidths[1] + serviceColWidths[2];
                pdf.text("Currency", xPos, yPos);
                xPos += serviceColWidths[3];
                pdf.text(currentVisit.currency, xPos, yPos);
                yPos += 6;
            }

            // Total Amount
            pdf.setFontSize(12);
            pdf.setFont("helvetica", "bold");
            pdf.setTextColor(...darkColor);
            xPos = margin + serviceColWidths[0] + serviceColWidths[1] + serviceColWidths[2];
            pdf.text("Total Amount", xPos, yPos);
            xPos += serviceColWidths[3];
            pdf.text(`$${totalAmount.toFixed(2)} ${currentVisit?.currency || 'USD'}`, xPos, yPos);

            yPos += 15;

            // Payment Information
            pdf.setFontSize(10);
            pdf.setFont("helvetica", "normal");
            pdf.setTextColor(...lightColor);
            pdf.text("PAYMENT INFORMATION", margin, yPos);
            yPos += 5;

            pdf.setFontSize(11);
            pdf.setTextColor(...darkColor);
            pdf.text(`Payment Method: Cash`, margin, yPos);
            yPos += 5;
            pdf.text(`Transaction ID: ${getTransactionId()}`, margin, yPos);
            yPos += 5;
            pdf.text(`Visit Date: ${new Date(currentVisit.visitDate).toLocaleDateString()}`, margin, yPos);

            yPos += 15;

            // Signatures
            const signatureY = yPos;
            pdf.setFontSize(10);
            pdf.setTextColor(...lightColor);
            pdf.text("CLIENT SIGNATURE", margin, signatureY);
            pdf.text("SERVICE ADVISOR SIGNATURE", margin + (contentWidth / 2), signatureY);

            yPos += 5;

            // Signature lines
            pdf.setDrawColor(...borderColor);
            pdf.line(margin, yPos, margin + (contentWidth / 2) - 10, yPos);
            pdf.line(margin + (contentWidth / 2) + 10, yPos, margin + contentWidth, yPos);

            yPos += 8;

            // Advisor name
            pdf.setFontSize(10);
            pdf.setTextColor(...darkColor);
            pdf.text(currentVisit?.employee?.name || 'Service Advisor', margin + (contentWidth / 2) + 10, yPos);

            // Save PDF
            const fileName = `invoice_${currentVehicle.vehiclePlate}_${new Date().getTime()}.pdf`;
            pdf.save(fileName);

        } catch (error) {
            console.error("Error generating PDF:", error);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="p-6 bg-gray-50 min-h-screen flex flex-col lg:flex-row gap-6">
            {/* Invoice Section for Print */}
            <div ref={contentRef} className="w-full lg:max-w-3xl">
                <Card className="p-6 border border-gray-300 shadow-lg bg-white rounded-lg">
                    {/* Header */}
                    <CardHeader className="pb-4">
                        <div className="flex flex-col sm:flex-row sm:items-start sm:justify-between gap-2">
                            <div>
                                <CardTitle className="text-2xl sm:text-3xl">
                                    Invoice #{getInvoiceNumber()}
                                </CardTitle>
                                <CardDescription>
                                    <Typography size="sm" as="p" className="text-gray-500">
                                        {currentVisit ? formatDateWithOrdinal(currentVisit.visitDate) : "Select a visit"}
                                    </Typography>
                                </CardDescription>
                            </div>
                            <div className="text-right">
                                <Typography size="sm" className="text-gray-600">
                                    Status: <span className={`font-semibold ${currentVisit?.status === 'COMPLETED' ? 'text-green-600' : 'text-amber-600'}`}>
                                        {currentVisit?.status || 'N/A'}
                                    </span>
                                </Typography>
                                <Typography size="sm" className="text-gray-600">
                                    Employee: {currentVisit?.employee?.name || 'N/A'}
                                </Typography>
                            </div>
                        </div>
                    </CardHeader>

                    {/* Content */}
                    <CardContent className="space-y-6">
                        {/* Recipient & Supplier */}
                        <div className="grid grid-cols-1 sm:grid-cols-2 gap-6">
                            <div>
                                <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                                    Client Information
                                </Typography>
                                <Typography weight="bold">{currentVehicle?.client?.name || currentVisit?.client?.name || "N/A"}</Typography>
                                <Typography>{currentVehicle?.client?.address || currentVisit?.client?.address || "N/A"}</Typography>
                                <Typography>{currentVehicle?.client?.phone || currentVisit?.client?.phone || "N/A"}</Typography>
                                <Typography>{currentVehicle?.client?.email || currentVisit?.client?.email || "N/A"}</Typography>
                            </div>
                            <div>
                                <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                                    Service Provider
                                </Typography>
                                <Typography weight="bold">AutoCare Pro</Typography>
                                <Typography>1234 Service Street</Typography>
                                <Typography>Auto City, AC 12345</Typography>
                                <Typography>(555) 123-4567</Typography>
                                <Typography>info@autocarepro.com</Typography>
                            </div>
                        </div>

                        {/* Vehicle Info Table */}
                        <div>
                            <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                                Vehicle Information
                            </Typography>
                            <div className="overflow-x-auto">
                                <Table className="w-full border border-gray-200">
                                    <TableHeader>
                                        <TableRow className="bg-gray-50">
                                            <TableHead className="px-3 py-2">License Plate</TableHead>
                                            <TableHead className="px-3 py-2">VIN</TableHead>
                                            <TableHead className="px-3 py-2">Make</TableHead>
                                            <TableHead className="px-3 py-2">Model</TableHead>
                                            <TableHead className="px-3 py-2">Year</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {selectedVehicle && currentVehicle ? (
                                            <TableRow key={currentVehicle.vehiclePlate}>
                                                <TableCell className="px-3 py-2">{currentVehicle.vehiclePlate}</TableCell>
                                                <TableCell className="px-3 py-2">{currentVehicle.vin}</TableCell>
                                                <TableCell className="px-3 py-2">{currentVehicle.brand}</TableCell>
                                                <TableCell className="px-3 py-2">{currentVehicle.model}</TableCell>
                                                <TableCell className="px-3 py-2">{currentVehicle.year}</TableCell>
                                            </TableRow>
                                        ) : (
                                            <TableRow>
                                                <TableCell colSpan={5} className="text-center py-4 text-gray-500">
                                                    No vehicle selected.
                                                </TableCell>
                                            </TableRow>
                                        )}
                                    </TableBody>
                                </Table>
                            </div>
                        </div>

                        {/* Service Summary */}
                        <div>
                            <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                                Service Summary
                            </Typography>
                            <Typography className="text-lg font-semibold">
                                {getPackName()}
                            </Typography>
                            {currentVisit?.description && (
                                <Typography className="text-gray-600 mt-1">
                                    {currentVisit.description}
                                </Typography>
                            )}
                        </div>

                        {/* Service Details Table */}
                        <div>
                            <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                                Service Details
                            </Typography>
                            <div className="overflow-x-auto">
                                <Table className="w-full border border-gray-200">
                                    <TableHeader>
                                        <TableRow className="bg-gray-50">
                                            <TableHead className="px-3 py-2">Item</TableHead>
                                            <TableHead className="px-3 py-2">Type</TableHead>
                                            <TableHead className="px-3 py-2 text-right">Quantity</TableHead>
                                            <TableHead className="px-3 py-2 text-right">Price</TableHead>
                                            <TableHead className="px-3 py-2 text-right">Total</TableHead>
                                        </TableRow>
                                    </TableHeader>
                                    <TableBody>
                                        {itemsWithSubtotals.length ? (
                                            itemsWithSubtotals.map((item, i) => (
                                                <TableRow key={i}>
                                                    <TableCell className="px-3 py-2">
                                                        <div className="font-medium">{getItemName(item)}</div>
                                                        {(item.itemType === VisitItemType.PACK && item.pack?.description) && (
                                                            <div className="text-sm text-gray-500 mt-1">
                                                                {item.pack.description}
                                                            </div>
                                                        )}
                                                        {(item.itemType === VisitItemType.SERVICE && item.serviceItem?.serviceDescription) && (
                                                            <div className="text-sm text-gray-500 mt-1">
                                                                {item.serviceItem.serviceDescription}
                                                            </div>
                                                        )}
                                                    </TableCell>
                                                    <TableCell className="px-3 py-2">
                                                        <span className={`inline-block px-2 py-1 rounded text-xs ${item.itemType === VisitItemType.PACK ? 'bg-blue-100 text-blue-800' : 'bg-green-100 text-green-800'}`}>
                                                            {item.itemType}
                                                        </span>
                                                    </TableCell>
                                                    <TableCell className="px-3 py-2 text-right">{item.quantity}</TableCell>
                                                    <TableCell className="px-3 py-2 text-right">
                                                        ${item.price.toFixed(2)}
                                                    </TableCell>
                                                    <TableCell className="px-3 py-2 text-right">
                                                        ${(item.subtotal || 0).toFixed(2)}
                                                    </TableCell>
                                                </TableRow>
                                            ))
                                        ) : (
                                            <TableRow>
                                                <TableCell colSpan={5} className="text-center py-4 text-gray-500">
                                                    No services or packs added to this visit.
                                                </TableCell>
                                            </TableRow>
                                        )}
                                    </TableBody>
                                    <TableFooter>
                                        <TableRow className="bg-gray-50 font-semibold border-t">
                                            <TableCell colSpan={4} className="px-3 py-2">Subtotal</TableCell>
                                            <TableCell className="px-3 py-2 text-right">
                                                ${totalAmount.toFixed(2)}
                                            </TableCell>
                                        </TableRow>
                                        {currentVisit?.currency && (
                                            <TableRow className="bg-gray-50">
                                                <TableCell colSpan={4} className="px-3 py-2">Currency</TableCell>
                                                <TableCell className="px-3 py-2 text-right">
                                                    {currentVisit.currency}
                                                </TableCell>
                                            </TableRow>
                                        )}
                                        <TableRow className="bg-gray-800 text-white font-bold hover:text-gray-800!">
                                            <TableCell colSpan={4} className="px-3 py-2">Total Amount</TableCell>
                                            <TableCell className="px-3 py-2 text-right">
                                                {totalAmount.toFixed(2)} {currentVisit?.currency || 'USD'}
                                            </TableCell>
                                        </TableRow>
                                    </TableFooter>
                                </Table>
                            </div>
                        </div>

                        {/* Payment Information & Signatures */}
                        <div className="pt-4">
                            <div className="mb-6">
                                <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                                    Payment Information
                                </Typography>
                                <Typography>Payment Method: Cash</Typography>
                                <Typography>Transaction ID: {getTransactionId()}</Typography>
                                <Typography>Visit Date: {currentVisit ? new Date(currentVisit.visitDate).toLocaleDateString() : 'N/A'}</Typography>
                            </div>

                            <div className="flex justify-between border-t pt-6 mt-6">
                                <div className="w-1/2 pr-4">
                                    <div className="h-12 border-b border-gray-300"></div>
                                    <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-4">
                                        Client Signature
                                    </Typography>
                                </div>
                                <div className="w-1/2 pl-4">
                                    <div className="h-12 border-b border-gray-300"></div>
                                    <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-4">
                                        Service Advisor Signature
                                    </Typography>
                                </div>
                            </div>
                        </div>
                    </CardContent>
                </Card>
            </div>

            {/* Sidebar */}
            <div className="w-full max-w-md">
                <Card className="p-6 shadow-xl rounded-2xl bg-gradient-to-br from-white to-gray-100 border border-gray-200">
                    <div className="text-center mb-4">
                        <Typography size="lg" weight="bold" className="text-gray-700">Invoice Generator</Typography>
                        <Typography size="sm" className="text-gray-500">Select vehicle & visit date</Typography>
                    </div>

                    <div className="space-y-4">
                        <div>
                            <Typography size="sm" weight="medium" className="mb-2 text-gray-700">Select Vehicle</Typography>
                            <InputSelect
                                value={selectedVehicle}
                                onValueChange={(val) => { setSelectedVehicle(val); setSelectedVisit(""); }}
                                placeholder="Search vehicles..."
                                clearable
                                options={vehicleOptions}
                            >
                                {(props) => <InputSelectTrigger {...props} className="w-full border-gray-300 shadow-sm rounded-lg" />}
                            </InputSelect>
                        </div>

                        <div>
                            <Typography size="sm" weight="medium" className="mb-2 text-gray-700">Select Visit</Typography>
                            <InputSelect
                                value={selectedVisit}
                                onValueChange={setSelectedVisit}
                                placeholder="Select visit date..."
                                clearable
                                disabled={!selectedVehicle}
                                options={visitOptions}
                            >
                                {(props) => <InputSelectTrigger {...props} className="w-full border-gray-300 shadow-sm rounded-lg" />}
                            </InputSelect>
                        </div>
                    </div>

                    {currentVisit && (
                        <div className="mt-6 p-4 bg-blue-50 rounded-lg border border-blue-100">
                            <Typography size="sm" weight="bold" className="text-blue-800 mb-2">Visit Summary</Typography>
                            <div className="space-y-1">
                                <Typography size="sm" className="flex justify-between">
                                    <span className="text-gray-600">Items:</span>
                                    <span>{visitItems.length}</span>
                                </Typography>
                                <Typography size="sm" className="flex justify-between">
                                    <span className="text-gray-600">Total Amount:</span>
                                    <span className="font-semibold">${totalAmount.toFixed(2)}</span>
                                </Typography>
                                <Typography size="sm" className="flex justify-between">
                                    <span className="text-gray-600">Status:</span>
                                    <span className={`font-semibold ${currentVisit.status === 'COMPLETED' ? 'text-green-600' : 'text-amber-600'}`}>
                                        {currentVisit.status}
                                    </span>
                                </Typography>
                            </div>
                        </div>
                    )}

                    <div className="mt-6 space-y-3">
                        <Button
                            onClick={reactToPrintFn}
                            disabled={!selectedVehicle || !selectedVisit || loading}
                            className="w-full flex items-center justify-center gap-2 bg-blue-600 hover:bg-blue-700 text-white shadow-lg rounded-lg transition-all duration-200"
                        >
                            <IconPrinter /> {loading ? 'Processing...' : 'Print Invoice'}
                        </Button>

                        <Button
                            onClick={handleDownloadPdf}
                            disabled={!selectedVehicle || !selectedVisit || loading}
                            variant="outline"
                            className="w-full flex items-center justify-center gap-2 bg-white hover:bg-gray-50 shadow rounded-lg border border-gray-200 transition-all duration-200"
                        >
                            <DownloadIcon /> {loading ? 'Generating PDF...' : 'Download PDF'}
                        </Button>
                    </div>
                </Card>
            </div>
        </div>
    );
}