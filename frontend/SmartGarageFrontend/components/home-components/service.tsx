import React from "react";
import {
  Wrench,
  Gauge,
  BatteryFull,
  Brush,
  Settings,
  Car,
  ArrowRight,
} from "lucide-react";
import {
  Card,
  CardContent,
  CardDescription,
  CardTitle,
  CardFooter,
} from "@/components/ui/card";

const services = [
  {
    title: "Routine Maintenance",
    description:
      "Oil changes, tire rotations, brake inspections, and all essential upkeep to extend your vehicle’s lifespan.",
    icon: Wrench,
  },
  {
    title: "Engine Diagnostics & Repair",
    description:
      "Advanced diagnostics to detect issues early and precision repairs to keep your engine performing flawlessly.",
    icon: Settings,
  },
  {
    title: "Electrical System Repair",
    description:
      "Battery replacement, wiring repairs, and electrical troubleshooting to keep your vehicle powered and safe.",
    icon: BatteryFull,
  },
  {
    title: "Brake Services",
    description:
      "Inspection, repair, and replacement to ensure your brakes perform safely and reliably under all conditions.",
    icon: Gauge,
  },
  {
    title: "Transmission Repair",
    description:
      "Expert transmission diagnostics and repair services to keep your ride smooth and responsive.",
    icon: Car,
  },
  {
    title: "Auto Body & Paint",
    description:
      "Collision repairs, paint jobs, and detailing services to restore your vehicle’s look and value.",
    icon: Brush,
  },
];
const iconSize = 40;
export default function service() {
  return (
    <section className="py-32 w-full px-20">
      <div className="text-center mb-12">
        <h2 className="text-3xl font-bold mb-5 pb-5 relative after:content-[''] after:absolute after:left-0 after:right-0 after:bottom-0 after:w-[60px] after:h-[3px] after:bg-[var(--accent-color)] after:mx-auto before:content-[''] before:absolute before:left-0 before:right-0 before:bottom-[4px] before:w-[160px] before:h-[1px] before:bg-[color-mix(in_srgb,var(--default-color),transparent_60%)] before:mx-auto">
          Services
        </h2>
        <p className="text-gray-300 max-w-xl mx-auto leading-relaxed">
          Comprehensive care to keep your vehicle running at its best
        </p>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-6">
        {services.map(({ title, description, icon: Icon }, index) => (
          <Card key={index} className="border-0 rounded-2xl">
            <CardContent className="flex gap-8 items-start">
              {/* Icon */}
              <div className=" flex-shrink-0 p-4 bg-indigo-500 rounded-xl text-white grid">
                <Icon size={iconSize} />
              </div>
              {/* Text content */}
              <div>
                <CardTitle className="text-2xl font-semibold text-gray-300">
                  {title}
                </CardTitle>
                <CardDescription className="text-base text-gray-400 leading-relaxed">
                  {description}
                </CardDescription>
                <CardFooter className="p-0">
                  <a
                    href="#"
                    className="text-indigo-500 hover:text-indigo-400 flex items-center gap-2"
                  >
                    Learn More
                    <ArrowRight size={16} />
                  </a>
                </CardFooter>
              </div>
            </CardContent>
          </Card>
        ))}
      </div>
    </section>
  );
}
