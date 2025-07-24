"use client";

import { useRef, useState } from "react";
import { useReactToPrint } from "react-to-print";
import html2canvas from "html2canvas-pro";
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

export default function InvoicePage() {
  // Print function
  const contentRef = useRef<HTMLDivElement>(null);
  const reactToPrintFn = useReactToPrint({ contentRef });

  // PDF function
  const handleDownloadPdf = async () => {
    if (!contentRef.current) return;

    try {
      // Clone the element to avoid modifying visible DOM
      const clone = contentRef.current.cloneNode(true) as HTMLElement;

      // Append clone offscreen so it can render properly
      clone.style.position = "absolute";
      clone.style.left = "-9999px";
      clone.style.top = "0";
      document.body.appendChild(clone);

      // Use html2canvas-pro to take screenshot
      const canvas = await html2canvas(clone, {
        scale: 2,
        useCORS: true,
        logging: false,
      });

      const imgData = canvas.toDataURL("image/jpeg", 0.98);

      const pdf = new jsPDF({
        unit: "in",
        format: "a4",
        orientation: "portrait",
      });

      // Calculate width and height to maintain aspect ratio on A4 size (8.27 x 11.69 inches)
      const pdfWidth = 8.27;
      const pdfHeight = (canvas.height * pdfWidth) / canvas.width;

      pdf.addImage(imgData, "JPEG", 0, 0, pdfWidth, pdfHeight);
      pdf.save("invoice.pdf");

      // Cleanup
      document.body.removeChild(clone);
    } catch (error) {
      console.error("Error generating PDF:", error);
    }
  };

  const [selectedVehicle, setSelectedVehicle] = useState("");
  const [selectedVisit, setSelectedVisit] = useState("");

  const vehicles = [
    {
      licensePlate: "ABC-1234",
      vin: "1HGBH41JXMN109186",
      make: "Toyota",
      model: "Camry",
      year: 2018,
      user: {
        id: 1,
        name: "Bob Smith",
        email: "bob@example.com",
        address: "1234 Main St.",
        cityStateZip: "Springfield, IL 62701",
      },
      visits: [
        {
          date: "2024-01-15",
          paymentMethod: "Credit Card",
          servicePack: {
            name: "Basic Pack",
            services: [
              { item: "Oil Change", quantity: 1, price: 80 },
              { item: "Brake Pads", quantity: 4, price: 100 },
              { item: "Tire Rotation", quantity: 1, price: 50 },
            ],
          },
        },
        {
          date: "2024-03-10",
          paymentMethod: "Cash",
          servicePack: {
            name: "Premium Pack",
            services: [
              { item: "Oil Change", quantity: 1, price: 80 },
              { item: "Brake Pads", quantity: 4, price: 100 },
              { item: "Tire Rotation", quantity: 1, price: 50 },
              { item: "Engine Tune-Up", quantity: 1, price: 200 },
            ],
          },
        },
      ],
    },
    {
      licensePlate: "XYZ-5678",
      vin: "2HGBH41JXMN109187",
      make: "Honda",
      model: "Civic",
      year: 2020,
      user: {
        id: 2,
        name: "Alice Johnson",
        email: "alice@example.com",
        address: "5678 Oak St.",
        cityStateZip: "Springfield, IL 62702",
      },
      visits: [
        {
          date: "2024-02-05",
          paymentMethod: "Credit Card",
          servicePack: {
            name: "Standard Pack",
            services: [
              { item: "Oil Change", quantity: 1, price: 80 },
              { item: "Tire Rotation", quantity: 1, price: 50 },
            ],
          },
        },
      ],
    },
  ];

  // Vehicle options for first select
  const vehicleOptions = vehicles.map((v) => ({
    value: v.licensePlate,
    label: `${v.make} ${v.model} (${v.licensePlate})`,
  }));

  // Find selected vehicle object
  const currentVehicle = vehicles.find((v) => v.licensePlate === selectedVehicle);

  // Visit options based on selected vehicle
  const visitOptions = currentVehicle
    ? currentVehicle.visits.map((visit) => ({
        value: visit.date,
        label: visit.date,
      }))
    : [];

  // Find selected visit object
  const currentVisit = currentVehicle?.visits.find((v) => v.date === selectedVisit);

  // Date format with suffix
  function formatDateWithOrdinal(dateString: string) {
    const date = new Date(dateString);
    const month = date.toLocaleString("default", { month: "long" });
    const day = date.getDate();
    const year = date.getFullYear();
    const suffix = getOrdinalSuffix(day);

    return (
      <>
        {month} {day}
        <sup>{suffix}</sup>, {year}
      </>
    );
  }
  function getOrdinalSuffix(day: number) {
    if (day > 3 && day < 21) return "th"; // 11th, 12th, 13th special case
    switch (day % 10) {
      case 1:
        return "st";
      case 2:
        return "nd";
      case 3:
        return "rd";
      default:
        return "th";
    }
  }

  return (
    <div className="p-4 bg-gray-50 min-h-screen">
      <div className="flex flex-col lg:flex-row gap-4">
        {/* Invoice Card (printable) */}
        <Card
          id="invoice"
          ref={contentRef}
          className="w-full max-w-full lg:max-w-[794px] lg:h-[1123px] p-4 sm:p-6 border border-gray-300 shadow bg-invoice-background print:p-0 print:shadow-none rounded-none"
        >
          <CardHeader className="flex flex-col sm:flex-row sm:items-start sm:justify-between pb-4 gap-2">
            <div>
              <CardTitle className="text-2xl sm:text-3xl">Invoice #10928</CardTitle>
              <CardDescription>
                <Typography size="sm" as="p" className="text-gray-500">
                  {currentVisit ? formatDateWithOrdinal(currentVisit.date) : "N/A"}
                </Typography>
              </CardDescription>
            </div>
          </CardHeader>

          <CardContent className="flex flex-col justify-between h-full space-y-6 sm:space-y-8">
            <div className="space-y-8">
              {/* Payee & Payor */}
              <div className="grid grid-cols-1 sm:grid-cols-2 gap-8">
                <div>
                  <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                    Recipient
                  </Typography>
                  <Typography weight="bold">{currentVehicle?.user.name ?? "N/A"}</Typography>
                  <Typography>{currentVehicle?.user.address ?? "N/A"}</Typography>
                  <Typography>{currentVehicle?.user.cityStateZip ?? "N/A"}</Typography>
                </div>
                <div>
                  <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                    Supplier
                  </Typography>
                  <Typography weight="bold">Acme Inc.</Typography>
                  <Typography>1234 Main St.</Typography>
                  <Typography>Springfield, IL 62701</Typography>
                  <Typography>(000) 123-4567</Typography>
                  <Typography>billing@acme.com</Typography>
                  <Typography>acme.com</Typography>
                </div>
              </div>

              {/* Vehicles Info Table */}
              <div>
                <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                  Vehicles Info
                </Typography>
                <div className="overflow-x-auto">
                  <Table className="min-w-[600px] w-full border border-gray-200 rounded-md overflow-hidden">
                    <TableHeader>
                      <TableRow className="bg-gray-50">
                        <TableHead className="text-left px-4 py-2">License Plate</TableHead>
                        <TableHead className="text-left px-4 py-2">VIN</TableHead>
                        <TableHead className="text-left px-4 py-2">Make</TableHead>
                        <TableHead className="text-left px-4 py-2">Model</TableHead>
                        <TableHead className="text-left px-4 py-2">Year</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {selectedVehicle && currentVehicle ? (
                        <TableRow key={currentVehicle.licensePlate}>
                          <TableCell className="px-4 py-2">{currentVehicle.licensePlate}</TableCell>
                          <TableCell className="px-4 py-2">{currentVehicle.vin}</TableCell>
                          <TableCell className="px-4 py-2">{currentVehicle.make}</TableCell>
                          <TableCell className="px-4 py-2">{currentVehicle.model}</TableCell>
                          <TableCell className="px-4 py-2">{currentVehicle.year}</TableCell>
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
                <Typography>{currentVisit?.servicePack.name ?? "N/A"}</Typography>
              </div>

              {/* Service Details Table */}
              <div>
                <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                  Details
                </Typography>
                <div className="overflow-x-auto">
                  <Table className="min-w-[600px] w-full border border-gray-200 rounded-md overflow-hidden">
                    <TableHeader>
                      <TableRow className="bg-gray-50">
                        <TableHead className="text-left px-4 py-2">Item</TableHead>
                        <TableHead className="text-right px-4 py-2">Quantity</TableHead>
                        <TableHead className="text-right px-4 py-2">Price</TableHead>
                        <TableHead className="text-right px-4 py-2">Total</TableHead>
                      </TableRow>
                    </TableHeader>
                    <TableBody>
                      {currentVisit?.servicePack.services.length ? (
                        currentVisit.servicePack.services.map((service, i) => (
                          <TableRow key={i}>
                            <TableCell className="text-left px-4 py-2">{service.item}</TableCell>
                            <TableCell className="text-right px-4 py-2">{service.quantity}</TableCell>
                            <TableCell className="text-right px-4 py-2">
                              ${service.price.toFixed(2)}
                            </TableCell>
                            <TableCell className="text-right px-4 py-2">
                              ${(service.price * service.quantity).toFixed(2)}
                            </TableCell>
                          </TableRow>
                        ))
                      ) : (
                        <TableRow>
                          <TableCell colSpan={4} className="text-center py-4 text-gray-500">
                            No visit selected.
                          </TableCell>
                        </TableRow>
                      )}
                    </TableBody>
                    <TableFooter>
                      <TableRow className="bg-gray-50 font-semibold border-t">
                        <TableCell className="px-4 py-2 text-left" colSpan={3}>
                          Total
                        </TableCell>
                        <TableCell className="px-4 py-2 text-right">
                          $
                          {currentVisit
                            ? currentVisit.servicePack.services
                                .reduce((sum, s) => sum + s.price * s.quantity, 0)
                                .toFixed(2)
                            : "0.00"}
                        </TableCell>
                      </TableRow>
                    </TableFooter>
                  </Table>
                </div>
              </div>
            </div>

            {/* Signatures */}
            <div className="space-y-8 pt-4">
              <div>
                <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-2">
                  Payment Information
                </Typography>
                <Typography>Payment Method: {currentVisit?.paymentMethod ?? "N/A"}</Typography>
                <Typography>Transaction ID: {currentVisit ? "1234567890" : "N/A"}</Typography>
              </div>

              <div className="flex flex-col sm:flex-row justify-between mt-12 gap-6 sm:gap-0">
                <div className="w-full sm:w-1/2 sm:pr-4">
                  <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-6">
                    Client Signature
                  </Typography>
                </div>
                <div className="w-full sm:w-1/2 sm:pl-4">
                  <Typography size="xs" weight="bold" className="uppercase text-gray-500 mb-6">
                    Creator Signature
                  </Typography>
                </div>
              </div>
            </div>
          </CardContent>
        </Card>

        {/* Sidebar - Search & Payment Info */}
        <div className="w-full max-w-md space-y-6">
          <Card className="space-y-6 p-6">
            <div className="space-y-2">
              <InputSelect
                value={selectedVehicle}
                onValueChange={(val) => {
                  setSelectedVehicle(val);
                  setSelectedVisit(""); // reset visit when vehicle changes
                }}
                placeholder="Search vehicles..."
                clearable
                options={vehicleOptions}
              >
                {(selectProps) => <InputSelectTrigger {...selectProps} className="w-full" />}
              </InputSelect>

              <InputSelect
                value={selectedVisit}
                onValueChange={setSelectedVisit}
                placeholder="Select visit date..."
                clearable
                disabled={!selectedVehicle}
                options={visitOptions}
              >
                {(selectProps) => <InputSelectTrigger {...selectProps} className="w-full" />}
              </InputSelect>
            </div>

            <div className="flex flex-col gap-1">
              <Button
                onClick={reactToPrintFn}
                disabled={!selectedVehicle || !selectedVisit}
                className="w-full"
                size="lg"
              >
                <IconPrinter /> Print Invoice
              </Button>

              <Button
                onClick={handleDownloadPdf}
                disabled={!selectedVehicle || !selectedVisit}
                className="w-full"
                size="lg"
              >
                <DownloadIcon /> Download Invoice
              </Button>
            </div>
          </Card>
        </div>
      </div>
    </div>
  );
}
